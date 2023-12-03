import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.NegativePriceException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class AddItem {

    private Warehouse warehouse;
    private SalesSystemDAO dao;

    private ShoppingCart cart;

    @Before
    public void setUp() {
        dao = mock(SalesSystemDAO.class);
        cart = new ShoppingCart(dao);
        warehouse = new Warehouse(dao);
    }

    @Test
    public void testAddingItemBeginsAndCommitsTransaction() throws ApplicationException, NegativePriceException {
        warehouse.addNewProductToWarehouse("12345", 10, "Product", 20.0);

        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao).beginTransaction();
        inOrder.verify(dao).commitTransaction();

    }

    @Test
    public void testAddingNewItem() {
        StockItem stockItem = new StockItem(123L, "Test Product", "Description", 10.0, 5);
        SoldItem soldItem = new SoldItem(stockItem, 4);

        when(dao.findStockItem(soldItem.getBarcode())).thenReturn(stockItem);

        try {
            cart.addItem(soldItem);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

        assertTrue("The item should be added to the cart", cart.getAll().contains(soldItem));
        assertEquals("The quantity of the item in the cart should be 4", 4, soldItem.getQuantity().intValue());
    }


    @Test
    public void testAddingExistingItem() throws NegativePriceException {
        StockItem existingItem = new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5);
        when(dao.findStockItem(existingItem.getBarcode())).thenReturn(existingItem);

        warehouse.updateItem(existingItem, "1", "8", "Lays chips", "11.0");

        verify(dao, never()).saveStockItem(any());

        assertEquals(8, existingItem.getQuantity());
    }


    @Test(expected = ApplicationException.class)
    public void testAddingItemWithNegativeQuantity() throws ApplicationException {
        StockItem stockItem = new StockItem(123L, "Test Product", "", 5, 7);
        when(dao.findStockItem(123L)).thenReturn(stockItem);
        SoldItem  soldItem = new SoldItem(stockItem, -4);
        cart.addItem(soldItem);
    }


    @Test(expected = ApplicationException.class)
    public void testAddingItemWithQuantityTooLarge() throws ApplicationException {
        StockItem stockItem = new StockItem(123L, "Test Product", "Description", 23.0, 3);
        when(dao.findStockItem(123L)).thenReturn(stockItem);

        SoldItem soldItem = new SoldItem(stockItem, 4);

        cart.addItem(soldItem);
    }


    @Test(expected = ApplicationException.class)
    public void testAddingItemWithQuantitySumTooLarge() throws ApplicationException {
        StockItem stockItem = new StockItem(123L, "Test Product", "Description", 20.0, 5);
        when(dao.findStockItem(123L)).thenReturn(stockItem);

        SoldItem firstSoldItem = new SoldItem(stockItem, 3);
        cart.addItem(firstSoldItem);

        SoldItem secondSoldItem = new SoldItem(stockItem, 3);

        cart.addItem(secondSoldItem);
    }
}





