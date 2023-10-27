import static org.mockito.Mockito.*;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class addItem {

    private Warehouse warehouse;
    private SalesSystemDAO dao;

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
}
