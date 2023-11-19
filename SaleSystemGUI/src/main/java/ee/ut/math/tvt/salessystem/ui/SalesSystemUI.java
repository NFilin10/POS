package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Graphical user interface of the sales system.
 */
public class SalesSystemUI extends Application {

    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;

    private Tab loginTab;
    private TabPane tabPane;

    private User user;

    Tab registrationPane = new Tab();


    public SalesSystemUI() {
        dao = new HibernateSalesSystemDAO();
        shoppingCart = new ShoppingCart(dao);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("javafx version: " + System.getProperty("javafx.runtime.version"));

        Group root = new Group();
        Scene scene = new Scene(root, 700, 600, Color.WHITE);

        loginTab = new Tab();
        loginTab.setText("Login");
        loginTab.setClosable(false);
        loginTab.setContent(loadControls("LoginForm.fxml",  new LoginController(dao, this)));

        tabPane = new TabPane();
        tabPane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.getTabs().add(loginTab);

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);

        primaryStage.setTitle("Sales system");
        primaryStage.setScene(scene);
        primaryStage.show();

        log.info("Salesystem GUI started");
    }

    // Add this method to dynamically add tabs after successful login
    public void showMainTabs(User user) throws IOException {
        Tab purchaseTab = new Tab();
        purchaseTab.setText("Point-of-sale");
        purchaseTab.setClosable(false);
        purchaseTab.setContent(loadControls("PurchaseTab.fxml", new PurchaseController(dao, shoppingCart)));

        Tab stockTab = new Tab();
        stockTab.setText("Warehouse");
        stockTab.setClosable(false);
        stockTab.setContent(loadControls("StockTab.fxml", new StockController(dao, shoppingCart)));

        Tab historyTab = new Tab();
        historyTab.setText("History");
        historyTab.setClosable(false);
        historyTab.setContent(loadControls("HistoryTab.fxml", new HistoryController(shoppingCart, user)));

        Tab teamTab = new Tab();
        teamTab.setText("Team");
        teamTab.setClosable(false);
        teamTab.setContent(loadControls("TeamTab.fxml", new TeamTabController(dao)));

        tabPane.getTabs().addAll(purchaseTab, stockTab, historyTab, teamTab);
    }

    private Node loadControls(String fxml, Initializable controller) throws IOException {
        URL resource = getClass().getResource(fxml);
        if (resource == null) {
            log.error("FATAL: controller was not found for: " + fxml + " file");
            throw new IllegalArgumentException(fxml + " not found");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setController(controller);
        log.info("Controller successfully loaded");
        return fxmlLoader.load();
    }

    public void successfulLogin(User user) throws IOException {
        showMainTabs(user);
        tabPane.getTabs().remove(loginTab);
        tabPane.getSelectionModel().select(0);
    }


    public TabPane getTabPane() {
        return tabPane;
    }

    public void showRegistrationForm() throws IOException {
        registrationPane.setClosable(false);
        registrationPane.setText("Register");
        registrationPane.setContent(loadControls("RegistrationForm.fxml", new RegistrationController(dao, this)));
        tabPane.getTabs().add(registrationPane);

        tabPane.getSelectionModel().select(registrationPane);
        tabPane.getTabs().remove(loginTab);

    }

    public void removeRegistrationForm(){
        tabPane.getTabs().remove(registrationPane);

    }

}


