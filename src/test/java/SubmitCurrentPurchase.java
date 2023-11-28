import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SubmitCurrentPurchase {

    private ShoppingCart cart;
    private SalesSystemDAO dao;

    private Warehouse warehouse;

    private User loggedInUser;


    @Before
    public void setUp() {
        dao = Mockito.mock(SalesSystemDAO.class);
        cart = new ShoppingCart(dao);
        warehouse = new Warehouse(dao);
        loggedInUser = new User();

    }

    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addItem(soldItem);

        User dummyUser = new User();
        cart.submitCurrentPurchase(dummyUser);

        assertEquals(3, stockItem.getQuantity());
    }



    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        User dummyUser = new User();

        cart.submitCurrentPurchase(dummyUser);

        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao, times(1)).beginTransaction();
        inOrder.verify(dao, times(1)).commitTransaction();
    }

    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addItem(soldItem);

        User dummyUser = new User();
        cart.submitCurrentPurchase(dummyUser);

        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(dao).savePurchase(purchaseCaptor.capture());

        Purchase savedPurchase = purchaseCaptor.getValue();
        assertNotNull("Purchase should not be null", savedPurchase);

        List<SoldItem> savedItems = savedPurchase.getItems();
        assertTrue("Saved items should contain soldItem1", savedItems.contains(soldItem));
    }

    @Test
    //FAILED
    public void testSubmittingCurrentOrderSavesCorrectTime() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addItem(soldItem);

        // Create a User with an empty purchase list
        User dummyUser = new User();
        dummyUser.setPurchases(new ArrayList<>());

        // Set up behavior for the dao to save the purchase
        doAnswer(invocation -> {
            Purchase purchase = invocation.getArgument(0);
            // Add the purchase to the user's purchase list
            dummyUser.getPurchases().add(purchase);
            return null;
        }).when(dao).savePurchase(any(Purchase.class));

        cart.submitCurrentPurchase(dummyUser);

        // Check if the user has purchases
        assertNotNull(dummyUser.getPurchases());
        assertFalse(dummyUser.getPurchases().isEmpty());

        // Get the saved purchase time
        LocalTime savedTime = dummyUser.getPurchases().get(0).getTime();

        // Get the current time
        LocalTime currentTime = LocalTime.now();

        // Define an acceptable time window (for example, within 1 second)
        Duration acceptableTimeWindow = Duration.ofSeconds(1);

        // Check if the saved time is within the acceptable time window of the current time
        assertTrue(savedTime.isAfter(currentTime.minus(acceptableTimeWindow)) &&
                savedTime.isBefore(currentTime.plus(acceptableTimeWindow)));
    }

    @Test
    public void testCancellingOrder() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 2);
        SoldItem soldItem1 = new SoldItem(stockItem, 3);
        cart.addItem(soldItem);

        cart.cancelCurrentPurchase();
        cart.addItem(soldItem1);

        User dummyUser = new User();
        cart.submitCurrentPurchase(dummyUser);

        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(dao).savePurchase(purchaseCaptor.capture());

        Purchase savedPurchase = purchaseCaptor.getValue();

        List<SoldItem> savedItems = savedPurchase.getItems();
        assertTrue("Saved items should contain soldItem1", savedItems.contains(soldItem1));
        assertFalse("Purchase should not contain soldItem", savedItems.contains(soldItem));
    }

    @Test
    //FAILED
    public void testCancellingOrderQuanititiesUnchanged() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addItem(soldItem);

        cart.cancelCurrentPurchase();

        int quantity = stockItem.getQuantity();
        assertEquals(5, quantity);
    }

}
