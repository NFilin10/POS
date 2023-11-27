import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class submitCurrentPurchase {

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
    public void testSubmittingCurrentOrderSavesCorrectTime() throws ApplicationException {
        User dummyUser = new User();
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addItem(soldItem);
        cart.submitCurrentPurchase(dummyUser);
        assertEquals(LocalTime.now(),  dummyUser.getPurchases().get(0).getTime());
    }

    @Test
    public void testCancellingOrder() throws ApplicationException {
        User dummyUser = new User();
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        SoldItem nullItem = new SoldItem(stockItem, 2);
        cart.addItem(nullItem);
        cart.cancelCurrentPurchase();
        SoldItem otherNullItem = new SoldItem(stockItem, 3);
        cart.addItem(otherNullItem);
        cart.submitCurrentPurchase(dummyUser);
        assertEquals(otherNullItem, dummyUser.getPurchases().get(0).getItems().get(0));
    }

    @Test
    public void testCancellingOrderQuanititiesUnchanged() throws ApplicationException {
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        SoldItem soldItem = new SoldItem(stockItem, 1);
        cart.addItem(soldItem);
        cart.cancelCurrentPurchase();
        assertEquals(5, stockItem.getQuantity());
    }

}
