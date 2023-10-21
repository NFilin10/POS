package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {

    private final ShoppingCart shoppingCart;

    private final SalesSystemDAO dao;

    @FXML
    private TableView<SoldItem> cartTableView;


    public HistoryController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
        cartTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));
    }


}
