package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class History {


    public List<Purchase> filterBetweenTwoDates(SalesSystemDAO dao, LocalDate startDate, LocalDate endDate){

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            // Handle invalid date range or missing dates, display a message, etc.
            return null;
        }

        List<Purchase> filteredPurchases = new ArrayList<>();
        List<Purchase> allPurchases = dao.getPurchaseList();

        for (Purchase purchase : allPurchases) {
            LocalDate purchaseDate = purchase.getDate();

            if (purchaseDate.isEqual(startDate) || purchaseDate.isEqual(endDate) ||
                    (purchaseDate.isAfter(startDate) && purchaseDate.isBefore(endDate))) {
                filteredPurchases.add(purchase);
            }
        }
        return filteredPurchases;
    }


    public List<Purchase> getLast10(SalesSystemDAO dao) {

        List<Purchase> allPurchases = dao.getPurchaseList();
        int numberOfPurchases = Math.min(10, allPurchases.size());

        List<Purchase> last10Purchases = new ArrayList<>();
        for (int i = allPurchases.size() - 1; i >= allPurchases.size() - numberOfPurchases; i--) {
            last10Purchases.add(allPurchases.get(i));
        }

        return last10Purchases;
    }


    public List<Purchase> showAll(SalesSystemDAO dao) {
        return dao.getPurchaseList();
    }


}
