package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import java.awt.*;

public class LoginController {

    private TextField username;
    private PasswordField password;
    private ChoiceBox<String> role;
    private Button login;

    public void loginButtonAction(){
        String usernameInput = username.getText();
        String passwordInput = password.getText();
        String selectedRole = role.getValue();

        if (usernameInput != null && passwordInput != null && selectedRole != null) {
            System.out.println("Login successful as " + selectedRole);
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

}
