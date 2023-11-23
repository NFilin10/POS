import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.NegativePriceException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static junit.framework.TestCase.assertEquals;

public class submitCurrentPurchase {
    private ShoppingCart cart;
    private Warehouse warehouse;
    private SalesSystemDAO dao;

    @Before
    public void setUp() {
        dao = mock(SalesSystemDAO.class);
        warehouse = new Warehouse(dao);
        cart = new ShoppingCart(dao);
    }

    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity() throws NegativePriceException, ApplicationException {
        int quantity = 40;

        StockItem stockItem = new StockItem(5L, "Burger", "fmefoaenwoinawi",  15.00, quantity);
        warehouse.addNewProductToWarehouse("5", quantity, "Burger", 15.0);
        SoldItem soldItem = new SoldItem(stockItem, 3);
        cart.addSoldItem(soldItem);
        User user = new User("User", "Customer", "user", "123" );
        cart.submitCurrentPurchase(user);
        //List<SoldItem> list = new ArrayList<>();
        //list.add(soldItem);
        //Purchase purchase = new Purchase(45.0, LocalDate.now(), LocalTime.now(), list);
        ArgumentCaptor<Purchase> purchaseArgumentCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(dao).savePurchase(purchaseArgumentCaptor.capture());

        Purchase purchase1 = purchaseArgumentCaptor.getValue();
        System.out.println(purchase1);

        assertEquals(quantity - 3, dao.findStockItem("Burger").getQuantity());
    }

    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() throws NegativePriceException, ApplicationException {
        StockItem stockItem = new StockItem(5L, "Burger", "fmefoaenwoinawi",  15.00, 1);
        warehouse.addNewProductToWarehouse("5", 1, "Burger", 15.0);
        SoldItem soldItem = new SoldItem(stockItem, 3);
        cart.addSoldItem(soldItem);
        User user = new User("User", "Customer", "user", "123" );
        cart.submitCurrentPurchase(user);
        verify(dao).beginTransaction();
        verify(dao).commitTransaction();
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
