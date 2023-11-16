package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.NegativePriceException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    public TextField barCodeField;

    @FXML
    public TextField quantityField;

    @FXML
    public TextField nameField;

    @FXML
    public TextField priceField;

    @FXML
    private TableView<StockItem> warehouseTableView;

    @FXML
    private Button refresh;

    @FXML
    private Button delete;

    private Warehouse warehouse;

    public StockController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
        this.dao = dao;
        this.warehouse = new Warehouse(dao);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButtonClicked();
        warehouseTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StockItem>() {
            public void changed(ObservableValue<? extends StockItem> observable, StockItem oldValue, StockItem newValue) {
                if (newValue != null) {
                    barCodeField.setText(String.valueOf(newValue.getBarcode()));
                    quantityField.setText(String.valueOf(newValue.getQuantity()));
                    nameField.setText(newValue.getName());
                    priceField.setText(String.valueOf(newValue.getPrice()));
                    log.debug("Registered change in the warehouseView");
                }
            }
        });
        // TODO refresh view after adding new items
        log.info("StockController initialised");
    }
    @FXML
    public void refreshButtonClicked() {
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshStockItems();
                log.debug("Refresh button clicked");
            }

        });
    }
    @FXML
    public void updateButtonClicked() throws NegativePriceException {
        int selectedIndex = warehouseTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            StockItem selectedItem = warehouseTableView.getItems().get(selectedIndex);

            String barcodeText = barCodeField.getText();
            String quantityText = quantityField.getText();
            String name = nameField.getText();
            String priceText = priceField.getText();

            String errorMessage = warehouse.updateItem(selectedItem, barcodeText, quantityText, name, priceText);

            if (errorMessage != null) {
                log.error("Error occurred while updating the item: " + errorMessage);
                ErrorManager.showError(errorMessage);
            } else {
                warehouseTableView.refresh();
                log.debug("Item successfully updated");
            }
        }
    }


    private boolean isBarcodeUnique(StockItem selectedItem, String newBarcodeText) {
        long newBarcode = Long.parseLong(newBarcodeText);
        if (selectedItem.getBarcode() == newBarcode) {
            return true; // The new barcode matches the selected item's barcode
        }

        // Check if the new barcode is already in use by another item
        StockItem existingItem = dao.findStockItem(newBarcode);
        return existingItem == null;
    }



    public void deleteButtonClicked() {
        String barcodeText = barCodeField.getText();
        if (barcodeText.isEmpty()) {
            log.error("Barcode field is empty");
            ErrorManager.showError("The barcode field cannot be empty.");
        } else {
            long barcode = Long.parseLong(barcodeText);

            if (warehouse.deleteItemFromWarehouse(barcode)) {
                log.debug("Item successfully deleted");
            } else {
                log.error("Mentioned product doesn't exist");
                ErrorManager.showError("The product with the given barcode doesn't exist in the database.");
            }
        }
    }


    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
        log.debug("Stock refreshed");
    }


    @FXML
    public void addNewProduct() {
        String barcode = barCodeField.getText();
        String name = nameField.getText();
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                ErrorManager.showNegativePriceError();
                return;
            }

            try {
                warehouse.addNewProductToWarehouse(barcode, quantity, name, price);
                barCodeField.clear();
                quantityField.clear();
                nameField.clear();
                priceField.clear();
                log.debug("Successfully added a new item to the stock");
            } catch (ApplicationException | NegativePriceException e) {
                ErrorManager.showError(e.getMessage());
            }
        } catch (NumberFormatException e){
           ErrorManager.showError("Invalid input");
        }



    }

}
