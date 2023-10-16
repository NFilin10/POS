package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

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

    private List<String> membersList = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTeamInfo();
    }

    public void setTeamInfo() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            String members = properties.getProperty("members");
            String[] memberArray = members.split(", ");
            membersList.addAll(Arrays.asList(memberArray));

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


    @FXML
    private void addMember() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Member");
        dialog.setHeaderText("Enter the member's name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            membersList.add(name);
            updateTeamMembers();
        });
    }

    @FXML
    private void removeMember() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(membersList.get(0), membersList);
        dialog.setTitle("Remove Member");
        dialog.setHeaderText("Select a member to remove:");
        dialog.setContentText("Member:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(member -> {
            membersList.remove(member);
            updateTeamMembers();
        });
    }

    private void updateTeamMembers() {
        String members = String.join(", ", membersList);
        teamMembers.setText(members);
    }


}

