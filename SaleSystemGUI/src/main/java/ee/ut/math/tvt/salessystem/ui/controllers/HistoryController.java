package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.logic.History;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);

    private final ShoppingCart shoppingCart;

    private final SalesSystemDAO dao;

    @FXML
    private TableView<SoldItem> cartTableView;

    @FXML
    private TableView<Purchase> purchaseTableView;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    private History history;


    public HistoryController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
        this.history = new History();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
        purchaseTableView.setItems(FXCollections.observableList(dao.getPurchaseList()));
        log.debug("HistoryController initialises");
    }

    @FXML
    private void refreshCart(){
        cartTableView.refresh();
    }

    @FXML
    private void filterBetweenDates() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<Purchase> filteredPurchases = history.filterBetweenTwoDates(dao, startDate, endDate);

        if (filteredPurchases != null){
            purchaseTableView.setItems(FXCollections.observableList(filteredPurchases));
        }

    }

    @FXML
    private void getLast10Purchases() {
        List<Purchase> filteredPurchases = history.getLast10Purchases(dao);
        purchaseTableView.setItems(FXCollections.observableList(filteredPurchases));
    }




}
