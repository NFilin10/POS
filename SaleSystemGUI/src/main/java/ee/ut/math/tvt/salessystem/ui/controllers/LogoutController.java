package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class LogoutController implements Initializable {
    @FXML
    private Button logout;

    private SalesSystemUI salesSystemUI;

    private SalesSystemDAO dao = new HibernateSalesSystemDAO();

    public LogoutController(SalesSystemUI salesSystemUI) {
        this.salesSystemUI = salesSystemUI;
    }

    @FXML
    private void handleLogout() {
        System.out.println("USERS " + dao.getUsers());

        SalesSystemUI.logout();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
