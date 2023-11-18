package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void addSoldItem(SoldItem item) {
        getAll().add(item);
    }

    /**
     * Add new SoldItem to table.
     */


    public void addItem(SoldItem item) throws ApplicationException {
        // TODO In case such stockItem already exists increase the quantity of the existing stock
        // TODO verify that warehouse items' quantity remains at least zero or throw an exception
        if (item.getQuantity() > dao.findStockItem(item.getBarcode()).getQuantity()) {
            throw new ApplicationException("You have exceeded product amount");
        }
        addSoldItem(item);
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
            StockItem itemInStock = dao.findStockItem(item.getStockItem().getBarcode());
            int itemInStockAmount = itemInStock.getQuantity();

            if (itemInStockAmount - item.getQuantity() == 0) {
                warehouse.deleteItemFromWarehouse(itemInStock.getBarcode());
            } else {
                itemInStock.setQuantity(itemInStockAmount - item.getQuantity());

            }


        }

        int seconds = time.getSecond();
        String formattedSeconds = String.format("%02d", seconds);
        LocalTime roundedTime = LocalTime.of(time.getHour(), time.getMinute(), Integer.parseInt(formattedSeconds));

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        Purchase purchase = new Purchase(cartCost, date, roundedTime, purchaseItems);
        dao.savePurchase(purchase);
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
