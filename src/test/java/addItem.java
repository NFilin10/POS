import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.controllers.StockController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


public class addItem {

    @Mock
    private SalesSystemDAO dao;

    @Mock
    private ShoppingCart shoppingCart;

    @Before
    public void setup() {
        dao = mock(SalesSystemDAO.class);
        shoppingCart = mock(ShoppingCart.class);
    }

    @Test
    public void testAddNewProductTransactionLifecycle() {
        StockController stockController = new StockController(dao, shoppingCart);

        StockItem readyItem = new StockItem(12345L, "TestItem", "", 5.0, 10);

        stockController.addNewProduct(readyItem);

        InOrder inOrder = inOrder(dao);
        inOrder.verify(dao, times(1)).beginTransaction();
        inOrder.verify(dao, times(1)).commitTransaction();

    }
}
