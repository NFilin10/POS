package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.History;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
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
    @FXML
    private TableView<Purchase> purchaseTableView;
    @FXML
    private TableView<SoldItem> purchaseInfo;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    private User user;
    private History history;
    private static SalesSystemDAO dao;


    public HistoryController(ShoppingCart shoppingCart, User user, SalesSystemDAO dao) {
        this.shoppingCart = shoppingCart;
        this.history = new History();
        this.user = user;
        this.dao = dao;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("HistoryController initializes");
    }

    private void updatePurchaseInfo(Purchase selectedPurchase) {
        List<SoldItem> purchaseItems = selectedPurchase.getItems();
        purchaseInfo.setItems(FXCollections.observableList(purchaseItems));
    }


    @FXML
    private void filterBetweenDates() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<Purchase> filteredPurchases = history.filterBetweenTwoDates(dao, startDate, endDate, user);

        if (filteredPurchases != null){
            purchaseTableView.setItems(FXCollections.observableList(filteredPurchases));
            setItems();
        }
    }


    @FXML
    private void getLast10Purchases() {
        List<Purchase> filteredPurchases = history.getLast10(dao, user);
        if (filteredPurchases != null){
            purchaseTableView.setItems(FXCollections.observableList(filteredPurchases));
            setItems();
        }
    }


    @FXML
    private void showAllPurchases(){
        purchaseTableView.setItems(FXCollections.observableList(history.showAll(dao, user)));
        setItems();
    }


    private void setItems(){
        purchaseTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updatePurchaseInfo(newSelection);
            }
        });
    }
}
