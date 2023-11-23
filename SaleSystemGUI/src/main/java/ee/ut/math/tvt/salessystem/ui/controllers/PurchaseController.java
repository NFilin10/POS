package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.*;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;
    @FXML
    private ChoiceBox<String> chooseItemFromList;
    @FXML
    private Button plusButton;
    @FXML
    private Button minusButton;
    @FXML TextField searchTextField;
    User loggedInUser = LoginController.getLoggedInUser();


    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("PurchaseController initialises");
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        disablePriceAndBarcodeInput();
        purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
        disableProductField(true);


        List<StockItem> allItems = dao.findStockItems();
        ObservableList<String> itemNames = FXCollections.observableArrayList();
        for (StockItem item : allItems) {
            itemNames.add(item.getName());
        }
        chooseItemFromList.setItems(itemNames);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterItems(newValue);
        });

        chooseItemFromList.setOnAction(event -> {
            resetProductField();
            String selectedOption = chooseItemFromList.getValue();
            fillInputsBySelectedStockItem();
        });

        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                    log.debug("Registered changes in barcode field");
                }
            }
        });


        this.quantityField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                    log.debug("Registered changes in quantity field");
                }
            }
        });
    }

    private void disablePriceAndBarcodeInput() {
        barCodeField.setDisable(true);
        priceField.setDisable(true);
    }

    private void enablePriceAndBarcodeInput() {
        barCodeField.setDisable(false);
        priceField.setDisable(false);
    }

    private void filterItems(String keyword) {
        ObservableList<String> filteredItems = FXCollections.observableArrayList();
        List<StockItem> allItems = dao.findStockItems();
        for (StockItem item : allItems) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredItems.add(item.getName());
            }
        }
        chooseItemFromList.setItems(filteredItems);
    }

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        chooseItemFromList.setDisable(true);

        List<String> itemsToRemove = new ArrayList<>();

        itemsToRemove.addAll(chooseItemFromList.getItems());

        chooseItemFromList.getItems().removeAll(itemsToRemove);

        for (StockItem stockItem : dao.findStockItems()) {
            chooseItemFromList.getItems().add(stockItem.getName());
        }

        chooseItemFromList.setOnAction(event -> {
            resetProductField();
            fillInputsBySelectedStockItem();
        });


        this.chooseItemFromList.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                    log.debug("Registered changes in ItemList");
                }
            }
        });

        log.info("New sale process started");
        try {
            enableInputs();
        } catch (SalesSystemException e) {
            log.error("There is an error in new sale process beginning");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Cancelling purchase");
        try {
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete, have a nice day!");
        try {
            log.debug("Contents of the current basket:\n" + shoppingCart.getAll());

            shoppingCart.submitCurrentPurchase(loggedInUser);
            disableInputs();
            shoppingCart.cancelCurrentPurchase();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void enableInputs() {
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
        chooseItemFromList.setDisable(false);
        log.debug("Inputs are now enabled");
    }


    private void disableInputs() {
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        chooseItemFromList.setDisable(true);
        disableProductField(true);
        log.debug("Inputs are now disabled");
    }


    private void fillInputsBySelectedStockItem() {

        if (!Objects.equals(chooseItemFromList.getValue(), "")) {
            StockItem stockItem = getStockItemByName();
            if (stockItem != null) {
                barCodeField.setText(String.valueOf(stockItem.getBarcode()));
                priceField.setText(String.valueOf(stockItem.getPrice() * Double.parseDouble(quantityField.getText())));
                log.debug("Successfully input item using chooseItemFromList: " + chooseItemFromList.getValue());
            } else {
                resetProductField();
                log.debug("fillInputsBySelectedStockItem() method interrupted");
            }
        }
    }


    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            log.debug("Got stock item by barcode: " + code);
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            log.error("Getting stock item by barcode failed");
            return null;
        }
    }

    private StockItem getStockItemByName() {
        try {
            String name = chooseItemFromList.getValue();
            log.debug("Got stock item by name: " + name);
            return dao.findStockItem(name);
        } catch (EmptyStackException e) {
            log.error("Getting stock item by name failed");
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {

         if (!Objects.equals(chooseItemFromList.getValue(), "")) {
            StockItem stockItem = getStockItemByName();
            if (stockItem != null) {
                int quantity = Integer.parseInt(quantityField.getText());
                try {
                    shoppingCart.addItem(new SoldItem(stockItem, quantity));
                    dao.getSoldItemList().add(new SoldItem(stockItem, quantity));
                    purchaseTableView.refresh();
                    log.info("Item added");
                    log.debug("Added item name: " + chooseItemFromList.getValue() + " quantity: " + quantityField.getText() + " price: " + priceField.getText());
                } catch (ApplicationException e){
                    ErrorManager.showError(e.getMessage());
                }
            }
        }
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        this.addItemButton.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.chooseItemFromList.setDisable(disable);
        this.plusButton.setDisable(disable);
        this.minusButton.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("1");
        priceField.setText("");
    }

    @FXML
    private void deleteButtonClicked() {
        ObservableList<SoldItem> selectedProducts = purchaseTableView.getSelectionModel().getSelectedItems();
        purchaseTableView.getItems().removeAll(selectedProducts);
        purchaseTableView.refresh();
        purchaseTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void addOne() {
        quantityField.setText(String.valueOf(Integer.parseInt(quantityField.getText()) + 1));
        fillInputsBySelectedStockItem();
    }
    @FXML
    private void removeOne() {
        if (Integer.parseInt(quantityField.getText()) > 1) {
            quantityField.setText(String.valueOf(Integer.parseInt(quantityField.getText()) - 1));
            fillInputsBySelectedStockItem();
        }
    }
}
