package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class History {


    public List<Purchase> filterBetweenTwoDates(SalesSystemDAO dao, LocalDate startDate, LocalDate endDate, User user){

        List<Purchase> filteredPurchases = dao.getPurchaseListBetweenDates(user, startDate, endDate);
        return filteredPurchases;
    }


    public List<Purchase> getLast10(SalesSystemDAO dao, User user) {

        List<Purchase> filteredPurchases = dao.getLast10Purchases(user);

        return filteredPurchases;
    }


    public List<Purchase> showAll(SalesSystemDAO dao, User user) {
        return dao.getPurchaseList(user);
    }


}
