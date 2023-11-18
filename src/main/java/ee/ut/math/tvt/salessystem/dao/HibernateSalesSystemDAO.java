package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.TeamMember;

import javax.persistence.*;
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

        addTeamMember(new TeamMember("Artjom", "Shishkov", "artjom.siskov@ut.ee"));
        addTeamMember(new TeamMember("Nikita", "Filin", "nikita.filin@ut.ee"));
        addTeamMember(new TeamMember("Kaisa", "Kumpas", "kaisa.kumpas@ut.ee"));
        addTeamMember(new TeamMember("Alina", "Gudkova", "alina.gudkova@ut.ee"));
    }
    public void close () {
        em.close ();
        emf.close ();
    }
    @Override
    public List<TeamMember> getListOfTeamMembers() {
        return em.createQuery("from TeamMember", TeamMember.class).getResultList();
    }

    @Override
    public TeamMember getTeamMember(String name) {
        String hql = "FROM TeamMember WHERE firstname = :firstName";
        Query query = em.createQuery(hql, TeamMember.class);
        query.setParameter("firstName", name);
        return (TeamMember) query.getSingleResult();
    }

    @Override
    public void removeTeamMember(TeamMember member) {
        try {
            beginTransaction();
            em.remove(member);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void addTeamMember(TeamMember member) {
        try {
            beginTransaction();
            TeamMember merge = em.merge(member);
            em.persist(merge);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
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
        try {
            String hql = "FROM StockItem WHERE name = :itemName";
            Query query = em.createQuery(hql, StockItem.class);
            query.setParameter("itemName", name);
            return (StockItem) query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }

    }

    @Override
    public List<SoldItem> getSoldItemList() {
        return em.createQuery("from SoldItem", SoldItem.class).getResultList();
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        try {
            beginTransaction();
            StockItem merge = em.merge(stockItem);
            em.persist(merge);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                System.out.println("IF");
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStockItem(StockItem stockItem) {
        try {
            beginTransaction();
            em.remove(stockItem);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        try {
            beginTransaction();
            SoldItem merge = em.merge(item);
            em.persist(merge);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
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
        try {
            beginTransaction();

            for (SoldItem soldItem : purchase.getItems()) {
                em.persist(soldItem);
            }

            Purchase merge = em.merge(purchase);
            em.persist(merge);

            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }


    @Override
    public List<Purchase> getPurchaseList() {
        return em.createQuery("from Purchase", Purchase.class).getResultList();
    }
}
