package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.ApplicationException;
import ee.ut.math.tvt.salessystem.logic.AuthenticationService;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;

    private static User loggedInUser;

    private SalesSystemDAO dao =  new HibernateSalesSystemDAO();

    private SalesSystemUI salesSystemUI;


    public LoginController(SalesSystemDAO dao, SalesSystemUI salesSystemUI) {
        this.salesSystemUI = salesSystemUI;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    @FXML
    public void loginButtonAction() throws IOException {
        String usernameInput = username.getText();
        String passwordInput = password.getText();

        User user = AuthenticationService.authenticateUser(usernameInput, passwordInput);



        if (user != null) {
            setLoggedInUser(user);
            salesSystemUI.successfulLogin(user);
        } else {
            ErrorManager.showError("Incorrect username or password");
        }
    }

    private void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    @FXML
    public void regButtonAction() throws IOException {
        salesSystemUI.showRegistrationForm();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}
