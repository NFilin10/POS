import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class addItem {

    private Warehouse warehouse;
    private SalesSystemDAO dao;

    private Warehouse warehouse1;

    private InMemorySalesSystemDAO dao1;

    @Before
    public void setUp() {
        dao = mock(SalesSystemDAO.class);
        warehouse = new Warehouse(dao);
    }

    @Test
    public void testAddingItemBeginsAndCommitsTransaction() {
        warehouse.addNewProductToWarehouse("12345", 10, "Product", 20.0);

        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao).beginTransaction();
        inOrder.verify(dao).commitTransaction();

    }

    @Test
    public void testAddingNewItem() {

        dao1 = new InMemorySalesSystemDAO();
        warehouse1 = new Warehouse(dao1);

        String barcode = "7";
        int quantity = 10;
        String name = "New Product";
        double price = 20;

        warehouse1.addNewProductToWarehouse(barcode, quantity, name, price);

        StockItem addedItem = dao1.findStockItem(Long.parseLong(barcode));
        System.out.println(addedItem);
        assertEquals(barcode, String.valueOf(addedItem.getId()));
        assertEquals(quantity, addedItem.getQuantity());
        assertEquals(name, addedItem.getName());
        assertEquals(price, addedItem.getPrice(), 0.001);
    }
}
