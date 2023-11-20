package ee.ut.math.tvt.salessystem.ui.controllers;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.User;
import ee.ut.math.tvt.salessystem.logic.AuthenticationService;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField name;

    @FXML
    private ChoiceBox<String> role;

    private SalesSystemDAO dao =  new HibernateSalesSystemDAO();
    private SalesSystemUI salesSystemUI;



    public RegistrationController(SalesSystemDAO dao, SalesSystemUI salesSystemUI) {
        this.salesSystemUI = salesSystemUI;
    }

    @FXML
    private void registerButtonAction() throws IOException {


        User user = AuthenticationService.registerUser(name.getText(), role.getValue(),  username.getText(), password.getText());
        salesSystemUI.removeRegistrationForm();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization logic, if needed
    }


}

