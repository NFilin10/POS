import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.NegativePriceException;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

public class addItem {

    private Warehouse warehouse;
    private SalesSystemDAO dao;

//    private Warehouse warehouse1;

    private InMemorySalesSystemDAO dao1;

    @Before
    public void setUp() {
        dao = mock(SalesSystemDAO.class);
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
    public void testAddingNewItem() throws ApplicationException, NegativePriceException {

        String barcode = "7";
        int quantity = 10;
        String name = "New Product";
        double price = 20;

        warehouse.addNewProductToWarehouse(barcode, quantity, name, price);

        // Verify that the mocked DAO is called correctly
        ArgumentCaptor<StockItem> stockItemCaptor = ArgumentCaptor.forClass(StockItem.class);
        verify(dao).saveStockItem(stockItemCaptor.capture());

        StockItem addedItem = stockItemCaptor.getValue();
        System.out.println(addedItem);

        assertEquals(barcode, String.valueOf(addedItem.getBarcode()));
        assertEquals(quantity, addedItem.getQuantity());
        assertEquals(name, addedItem.getName());
        assertEquals(price, addedItem.getPrice(), 0.001);
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
    public void testAddingItemWithNegativeQuantity() throws ApplicationException, NegativePriceException {
        String barcode = "123";
        int quantity = -5;
        String name = "Test Product";
        double price = 10.0;

        warehouse.addNewProductToWarehouse(barcode, quantity, name, price);
    }
}





