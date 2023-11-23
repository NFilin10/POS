package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<Purchase> purchaseList;

    public InMemorySalesSystemDAO() {
        this.purchaseList = new ArrayList<>();
        List<StockItem> items = new ArrayList<StockItem>();

        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));

        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getBarcode() == id)
                return item;
        }
        return null;
    }

    @Override
    public StockItem findStockItem(String name) {
        System.out.println("ITEMS " + stockItemList);
        for (StockItem item : stockItemList) {
            if (Objects.equals(item.getName(), name))
                return item;
        }

        return null;
    }

    public List<SoldItem> getSoldItemList() {
        return soldItemList;
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void deleteStockItem(StockItem stockItem) {
        stockItemList.remove(stockItem);
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }

    @Override
    public List<TeamMember> getListOfTeamMembers() {
        return null;
    }

    @Override
    public TeamMember getTeamMember(String name) {
        return null;
    }

    @Override
    public void removeTeamMember(TeamMember member) {

    }

    @Override
    public void addTeamMember(TeamMember member) {

    }

    @Override
    public void savePurchase(Purchase purchase) {
        purchaseList.add(purchase);
        System.out.println("Purchase items: " + purchase.getItems());
    }

    @Override
    public boolean isTransactionActive(){
        return false;
    }

    @Override
    public List<Purchase> getPurchaseList(User user) {
        return null;
    }

    @Override
    public User getUserByUsername(String username){
        return null;
    }

    @Override
    public void addUser(User user){

    }

    @Override
    public List<User> getUsers(){
        return null;
    }

    @Override
    public void updateStockItem(StockItem item) {
        return;
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
