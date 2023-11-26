import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.NegativePriceException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.InOrder;

import java.time.LocalTime;

import static org.junit.Assert.*;
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
    public void testSubmittingCurrentOrderCreatesPurchase() throws NegativePriceException, ApplicationException {
        StockItem stockItem = new StockItem(5L, "Burger", "fmefoaenwoinawi",  15.00, 1);
        warehouse.addNewProductToWarehouse("5", 1, "Burger", 15.0);
        SoldItem soldItem = new SoldItem(stockItem, 3);
        cart.addSoldItem(soldItem);
        User user = new User("User", "Customer", "user", "123" );
        cart.submitCurrentPurchase(user);
        verify(dao).findPurchase(soldItem, user);
    }

    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime() throws NegativePriceException, ApplicationException {
        StockItem stockItem = new StockItem(5L, "Burger", "fmefoaenwoinawi",  15.00, 1);
        warehouse.addNewProductToWarehouse("5", 1, "Burger", 15.0);
        SoldItem soldItem = new SoldItem(stockItem, 3);
        cart.addSoldItem(soldItem);
        User user = new User("User", "Customer", "user", "123" );
        cart.submitCurrentPurchase(user);
        verify(dao).getPurchaseList(user).get(0).getTime();
        assertEquals(dao.getPurchaseList(user).get(0).getTime().getHour(), LocalTime.now().getHour());
        assertEquals(dao.getPurchaseList(user).get(0).getTime().getMinute(), LocalTime.now().getMinute());
        assertEquals(dao.getPurchaseList(user).get(0).getTime().getSecond(), LocalTime.now().getSecond(), 1);
    }

    @Test
    public void testCancellingOrder() throws NegativePriceException, ApplicationException {
        StockItem stockItem1 = new StockItem(6L, "Hot Dog", "ergeri",  8.00, 1);
        warehouse.addNewProductToWarehouse("6", 1, "Hot Dog", 8.0);
        SoldItem soldItem1 = new SoldItem(stockItem1, 3);
        cart.addSoldItem(soldItem1);
        cart.cancelCurrentPurchase();

        StockItem stockItem = new StockItem(5L, "Burger", "fmefoaenwoinawi",  15.00, 1);
        warehouse.addNewProductToWarehouse("5", 1, "Burger", 15.0);
        SoldItem soldItem = new SoldItem(stockItem, 3);
        cart.addSoldItem(soldItem);
        User user = new User("User", "Customer", "user", "123" );
        cart.submitCurrentPurchase(user);

        assertEquals(dao.getPurchaseList(user).get(0).getItems().get(0), soldItem);
    }

    @Test
    public void testCancellingOrderQuanititiesUnchanged() throws NegativePriceException, ApplicationException {
        int quantity = 3;

        StockItem stockItem1 = new StockItem(6L, "Hot Dog", "ergeri",  8.00, quantity);
        warehouse.addNewProductToWarehouse("6", quantity, "Hot Dog", 8.0);
        SoldItem soldItem1 = new SoldItem(stockItem1, 3);
        cart.addSoldItem(soldItem1);
        cart.cancelCurrentPurchase();

        assertEquals(quantity, dao.findStockItem(6L).getQuantity());
    }
}
