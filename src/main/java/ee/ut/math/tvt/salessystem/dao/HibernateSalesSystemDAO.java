package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO () {
        // if you get ConnectException / JDBCConnectionException then you
        // probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("pos");
        em = emf.createEntityManager ();

        saveStockItem(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        saveStockItem(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        saveStockItem(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        saveStockItem(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
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
        String hql = "FROM StockItem WHERE name = :itemName";
        Query query = em.createQuery(hql, StockItem.class);
        query.setParameter("itemName", name);
        return (StockItem) query.getSingleResult();
    }

    @Override
    public List<SoldItem> getSoldItemList() {
        return em.createQuery("from SoldItem", SoldItem.class).getResultList();
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        beginTransaction();
        StockItem merge = em.merge(stockItem);
        em.persist(merge);
        commitTransaction();
    }

    @Override
    public void deleteStockItem(StockItem stockItem) {
        beginTransaction();
        em.remove(stockItem);
        commitTransaction();
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        beginTransaction();
        SoldItem merge = em.merge(item);
        em.persist(merge);
        commitTransaction();
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
        beginTransaction();
        Purchase merge = em.merge(purchase);
        em.persist(merge);
        commitTransaction();
    }

    @Override
    public List<Purchase> getPurchaseList() {
        return em.createQuery("from Purchase", Purchase.class).getResultList();
    }
}
