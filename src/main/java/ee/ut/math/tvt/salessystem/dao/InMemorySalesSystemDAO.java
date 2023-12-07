package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<Purchase> purchaseList;
    private final List<TeamMember> teamMemberList;
    private final List<User> userList;

    public InMemorySalesSystemDAO() {
        this.stockItemList = new ArrayList<>();
        this.soldItemList = new ArrayList<>();
        this.purchaseList = new ArrayList<>();
        this.teamMemberList = new ArrayList<>();
        this.userList = new ArrayList<>();

        // Initialize with some dummy data
        // Add more initialization data as needed
    }

    @Override
    public List<StockItem> findStockItems() {
        return new ArrayList<>(stockItemList);
    }

    @Override
    public StockItem findStockItem(long barcode) {
        return stockItemList.stream()
                .filter(item -> item.getBarcode() == barcode)
                .findFirst()
                .orElse(null);
    }

    @Override
    public StockItem findStockItem(String name) {
        return stockItemList.stream()
                .filter(item -> Objects.equals(item.getName(), name))
                .findFirst()
                .orElse(null);
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
    public List<SoldItem> getSoldItemList() {
        return new ArrayList<>(soldItemList);
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

    @Override
    public void savePurchase(Purchase purchase) {
        purchaseList.add(purchase);
    }

    @Override
    public List<Purchase> getPurchaseList(User user) {
        List<Purchase> res = new ArrayList<>();
        for (Purchase purchase : purchaseList) {
            if (purchase.getUser() == user) {
                res.add(purchase);
            }
        }
        return res;
    }

    // Add other methods from the interface and implement them accordingly

    // Transaction-related methods can remain empty as they are not applicable
    @Override
    public void beginTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
    }

    // Implement methods for TeamMembers and Users similarly, using the teamMemberList and userList

    @Override
    public List<TeamMember> getListOfTeamMembers() {
        return new ArrayList<>(teamMemberList);
    }

    @Override
    public void addTeamMember(TeamMember member) {
        teamMemberList.add(member);
    }

    @Override
    public boolean isTransactionActive() {
        return false;
    }

    @Override
    public TeamMember getTeamMember(String name) {
        return teamMemberList.stream()
                .filter(member -> Objects.equals(member.getFirstname(), name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void removeTeamMember(TeamMember member) {
        teamMemberList.remove(member);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userList);
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userList.stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<Purchase> getPurchaseListBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        return purchaseList.stream()
                .filter(purchase -> Objects.equals(purchase.getUser(), user) &&
                        !purchase.getDate().isBefore(startDate) &&
                        !purchase.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Purchase> getLast10Purchases(User user) {
        return purchaseList.stream()
                .filter(purchase -> Objects.equals(purchase.getUser(), user))
                .sorted(Comparator.comparing(Purchase::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStockItem(StockItem item) {
        int index = -1;
        for (int i = 0; i < stockItemList.size(); i++) {
            if (stockItemList.get(i).getBarcode() == item.getBarcode()) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            stockItemList.set(index, item);
        } else {
            System.err.println("StockItem with barcode " + item.getBarcode() + " not found.");
        }
    }

    @Override
    public List<Purchase> findPurchase(SoldItem item, User user) {
        return purchaseList.stream()
                .filter(purchase -> Objects.equals(purchase.getUser(), user) &&
                        purchase.getItems().contains(item))
                .collect(Collectors.toList());
    }

    // ... Implement other methods as required
}
