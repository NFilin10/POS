package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO () {
        // if you get ConnectException / JDBCConnectionException then you
        // probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("pos");
        em = emf.createEntityManager ();

        saveStockItem(new StockItem(46456646L, "gugi", "fefwewrw", 3.87, 5));
        saveStockItem(new StockItem(64564645L, "fgfd", "rftrjry", 7.54, 8));
    }
    // TODO implement missing methods
    public void close () {
        em.close ();
        emf.close ();
    }

    @Override
    public List<StockItem> findStockItems() {
        return em.createQuery("from StockItem", StockItem.class).getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return em.find(StockItem.class, id);
    }

    @Override
    public StockItem findStockItem(String name) {
        return em.find(StockItem.class, name);
    }

    @Override
    public List<SoldItem> getSoldItemList() {
        return em.createQuery("from SoldItem", SoldItem.class).getResultList();
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        beginTransaction();
        em.persist(stockItem);
        commitTransaction();
        close();
    }

    @Override
    public void deleteStockItem(StockItem stockItem) {
        beginTransaction();
        em.remove(stockItem);
        commitTransaction();
        close();
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        beginTransaction();
        em.persist(item);
        commitTransaction();
        close();
    }

    @Override
    public void beginTransaction () {
        em.getTransaction (). begin ();
    }
    @Override
    public void rollbackTransaction () {
        em.getTransaction (). rollback ();
    }
    @Override
    public void commitTransaction () {
        em.getTransaction (). commit ();
    }

    @Override
    public void savePurchase(Purchase purchase) {

    }

    @Override
    public List<Purchase> getPurchaseList() {
        return null;
    }
}
