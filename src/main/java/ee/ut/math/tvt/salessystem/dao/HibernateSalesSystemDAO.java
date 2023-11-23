package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO () {
        // if you get ConnectException / JDBCConnectionException then you
        // probably forgot to start the database before starting the application
        emf = Persistence.createEntityManagerFactory ("pos");
        em = emf.createEntityManager ();

//        saveStockItem(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
//        saveStockItem(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
//        saveStockItem(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
//        saveStockItem(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
//
//        addTeamMember(new TeamMember("Artjom", "Shishkov", "artjom.siskov@ut.ee"));
//        addTeamMember(new TeamMember("Nikita", "Filin", "nikita.filin@ut.ee"));
//        addTeamMember(new TeamMember("Kaisa", "Kumpas", "kaisa.kumpas@ut.ee"));
//        addTeamMember(new TeamMember("Alina", "Gudkova", "alina.gudkova@ut.ee"));



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
    public StockItem findStockItem(long barcode) {
        try {
            String hql = "FROM StockItem WHERE barcode = :barcode";
            Query query = em.createQuery(hql, StockItem.class);
            query.setParameter("barcode", barcode);
            return (StockItem) query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }

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
            StockItem merge = em.merge(stockItem);
            em.persist(merge);
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
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

    public boolean isTransactionActive() {
        return em.getTransaction().isActive();
    }



    @Override
    public List<Purchase> getPurchaseList(User user) {
        try {
            String hql = "FROM Purchase WHERE user = :user";
            Query query = em.createQuery(hql, Purchase.class);
            query.setParameter("user", user);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList(); // Return an empty list if no purchases are found
        }
    }

    @Override
    public List<Purchase> getPurchaseListBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        try {
            String hql = "FROM Purchase WHERE user = :user AND date BETWEEN :startDate AND :endDate";
            Query query = em.createQuery(hql, Purchase.class);
            query.setParameter("user", user);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList(); // Return an empty list if no purchases are found
        }
    }


    @Override
    public List<Purchase> getLast10Purchases(User user) {
        try {
            String hql = "FROM Purchase WHERE user = :user ORDER BY date DESC";
            Query query = em.createQuery(hql, Purchase.class);
            query.setParameter("user", user);
            query.setMaxResults(10);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }



    @Override
    public User getUserByUsername(String username) {
        try {
            String hql = "FROM User WHERE username = :username";
            Query query = em.createQuery(hql, User.class);
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null; // User with the specified username not found
        }
    }


    @Override
    public void addUser(User user) {
        try {
            beginTransaction();
            em.persist(user);
            commitTransaction();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getUsers(){
        return em.createQuery("from User", User.class).getResultList();
    }


    public void updateStockItem(StockItem stockItem) {
        try {
            beginTransaction();

            // Check if the stock item already exists in the database
            StockItem existingItem = em.find(StockItem.class, stockItem.getId());

            if (existingItem != null) {
                // Update the fields of the existing item with the new values
                existingItem.setBarcode(stockItem.getBarcode());
                existingItem.setName(stockItem.getName());
                existingItem.setDescription(stockItem.getDescription());
                existingItem.setPrice(stockItem.getPrice());
                existingItem.setQuantity(stockItem.getQuantity());

                // Merge and persist the updated item
                em.merge(existingItem);
                commitTransaction();
            } else {
                // Handle the case where the stock item is not found
                System.err.println("StockItem with ID " + stockItem.getId() + " not found.");
            }
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                rollbackTransaction();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<Purchase> findPurchase(SoldItem item, User user) {
        List<Purchase> list = new ArrayList<>();
        for (Purchase purchase : getPurchaseList(user)) {
            if (purchase.getItems().contains(item)) {
                list.add(purchase);
            }
        }
        return list;
    }
}


