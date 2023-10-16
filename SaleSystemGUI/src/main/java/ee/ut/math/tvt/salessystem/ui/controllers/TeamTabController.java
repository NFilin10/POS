package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class TeamTabController implements Initializable {
    @FXML
    private Label teamName;

    @FXML
    private Label teamLeader;

    @FXML
    private Label teamLeaderMail;

    @FXML
    private Text teamMembers;

    @FXML
    private ImageView logo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTeamInfo();
    }

    public void setTeamInfo() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            teamName.setText(properties.getProperty("name"));
            teamLeader.setText(properties.getProperty("contactPerson"));
            teamLeaderMail.setText(properties.getProperty("contact"));
            teamMembers.setText(properties.getProperty("members"));

            String logoPath = properties.getProperty("logo");
            logo.setImage(new Image(logoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}