import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class submitCurrentPurchase {

    private ShoppingCart cart;
    private SalesSystemDAO dao;

    private Warehouse warehouse;

    @Before
    public void setUp() {
        dao = Mockito.mock(SalesSystemDAO.class);
        cart = new ShoppingCart(dao);
        warehouse = new Warehouse(dao);
    }

    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity() {
        // Create a stock item and add it to the DAO's database
        StockItem stockItem = new StockItem(1L, "Test Item", "Description", 10.0, 5);
        when(dao.findStockItem(1L)).thenReturn(stockItem);

        // Create a sold item based on the stock item and add it to the shopping cart
        SoldItem soldItem = new SoldItem(stockItem, 2);
        cart.addSoldItem(soldItem);

        // Submit the current purchase
        User dummyUser = new User(); // Assuming you have a User class
        cart.submitCurrentPurchase(dummyUser);

        // Verify that the stock item quantity has decreased
        assertEquals(3, stockItem.getQuantity());
    }



    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        // Arrange
        User dummyUser = new User(); // Assuming you have a User class

        // Act
        cart.submitCurrentPurchase(dummyUser);

        // Assert
        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao, times(1)).beginTransaction();
        inOrder.verify(dao, times(1)).commitTransaction();
    }

    @Test
    public void testSubmittingCurrentOrderCreatesPurchase() {
       User dummyUser = new User();
       cart.submitCurrentPurchase(dummyUser);
       InOrder inOrder = inOrder(dao);
       inOrder.verify(dao, times(1)).getPurchaseList(dummyUser);
    }

    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime() {
        User dummyUser = new User();
        cart.submitCurrentPurchase(dummyUser);
        assertEquals(LocalTime.now(),  dao.getLast10Purchases(dummyUser).get(0).getTime());
    }

    @Test
    public void testCancellingOrder() throws ApplicationException {
        User dummyUser = new User();
        SoldItem nullItem = new SoldItem();
        cart.addItem(nullItem);
        cart.cancelCurrentPurchase();
        SoldItem otherNullItem = new SoldItem();
        cart.addItem(otherNullItem);
        cart.submitCurrentPurchase(dummyUser);
        assertEquals(otherNullItem, dummyUser.getPurchases().get(0).getItems().get(0));
    }

    @Test
    public void testCancellingOrderQuanititiesUnchanged() throws ApplicationException {
        StockItem nullItem = new StockItem();
        nullItem.setQuantity(1);
        SoldItem soldItem = new SoldItem(nullItem, 1);
        cart.addItem(soldItem);
        cart.cancelCurrentPurchase();
        assertEquals(1, nullItem.getQuantity());
    }
}
