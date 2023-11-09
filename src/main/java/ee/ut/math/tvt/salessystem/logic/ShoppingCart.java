package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) throws ApplicationException {
        // TODO In case such stockItem already exists increase the quantity of the existing stock
        // TODO verify that warehouse items' quantity remains at least zero or throw an exception
        if (item.getQuantity() > dao.findStockItem(item.getId()).getQuantity()){
            throw new  ApplicationException("You have exceeded product amount");
        }
        items.add(item);
        log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
        log.debug("Purchase cancelled");
    }

    public void submitCurrentPurchase() {
        Warehouse warehouse = new Warehouse(dao);
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        List<SoldItem> purchaseItems = new ArrayList<>();

        double cartCost = 0;
        for (SoldItem item : items) {
            purchaseItems.add(item);
            cartCost += item.getSum();
            StockItem itemInStock = dao.findStockItem(item.getStockItem().getId());
            int itemInStockAmount = itemInStock.getQuantity();

            if (itemInStockAmount - item.getQuantity() == 0){
                warehouse.deleteItemFromWarehouse(itemInStock.getId());
            } else{
                itemInStock.setQuantity(itemInStockAmount - item.getQuantity());
            }


        }

        int seconds = time.getSecond();
        String formattedSeconds = String.format("%02d", seconds);
        LocalTime roundedTime = LocalTime.of(time.getHour(), time.getMinute(), Integer.parseInt(formattedSeconds));

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        dao.beginTransaction();
        try {
            Purchase purchase = new Purchase(cartCost, date, roundedTime, purchaseItems);
            dao.savePurchase(purchase);

            dao.commitTransaction();
            items.clear();
            log.debug("Current purchase submitted");
        } catch (Exception e) {
            dao.rollbackTransaction();
            log.error("Current purchase submitting gone wrong: " + e.getMessage());
            throw e;
        }
    }

    public void deleteItemFromCart(StockItem item){
        Iterator<SoldItem> it = items.iterator();
        while (it.hasNext()){
            SoldItem product = it.next();
            if (product.getName().equals(item.getName())){
                it.remove();
                break;
            }
        }
    }

}
