package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final ShoppingCart shoppingCart;




    private final SalesSystemDAO dao;

    @FXML
    private Button addItemButton;

    @FXML
    private TextField barCodeField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TableView<StockItem> warehouseTableView;

    @FXML
    private Button refresh;

    @FXML
    private Button delete;

    public StockController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addNewProduct();
        refreshButtonClicked();
        deleteButtonClicked();
        // TODO refresh view after adding new items
    }

    @FXML
    public void refreshButtonClicked() {
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                barCodeField.clear();
                quantityField.clear();
                nameField.clear();
                priceField.clear();
                refreshStockItems();
            }
        });
    }

    public void deleteButtonClicked() {
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long barcode = Integer.parseInt(barCodeField.getText());
                if (dao.findStockItem(barcode) != null) {
                    StockItem item = dao.findStockItem(barcode);
                    dao.deleteStockItem(item);
                } else {
                    log.error("Barcode field is empty or mentioned product doesn't exist");
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Barcode field is empty or mentioned product doesn't exist");
                    errorAlert.setContentText("The barcode you entered doesn't present in the database. Please enter a valid barcode.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }


    private void addNewProduct(){
        addItemButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long barcode = Integer.parseInt(barCodeField.getText());
                if (dao.findStockItem(barcode) == null){
                    int amount = Integer.parseInt(quantityField.getText());
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());

                    log.info(name);
                    StockItem addedItem = new StockItem(barcode, name, "", price, amount);
                    dao.saveStockItem(addedItem);
                }

                else{
                    log.error("barcode already exists");
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Barcode already exists");
                    errorAlert.setContentText("The barcode you entered already exists in the database. Please enter a new barcode.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

}
