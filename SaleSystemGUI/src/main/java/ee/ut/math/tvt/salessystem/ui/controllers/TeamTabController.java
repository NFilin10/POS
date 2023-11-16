package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class TeamTabController implements Initializable {
    private static final Logger log = LogManager.getLogger(TeamTabController.class);

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

    private SalesSystemDAO dao;

    private List<String> membersList = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTeamInfo();
    }

    public TeamTabController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void setTeamInfo() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            //String members = properties.getProperty("members");
            String[] memberArray = new String[dao.getListOfTeamMembers().size()];
            for (int i = 0; i < dao.getListOfTeamMembers().size(); i++) {
                memberArray[i] = dao.getListOfTeamMembers().get(i).getFirstname() +  " " + dao.getListOfTeamMembers().get(i).getLastname();
            }
            membersList.addAll(Arrays.asList(memberArray));

            teamName.setText(properties.getProperty("name"));
            teamLeader.setText(properties.getProperty("contactPerson"));
            teamLeaderMail.setText(properties.getProperty("contact"));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < memberArray.length; i++) {
                builder.append(memberArray[i]);
                if (i + 1 == memberArray.length) {
                    continue;
                } else builder.append(", ");
            }
            teamMembers.setText(String.valueOf(builder));
            //teamMembers.setText(properties.getProperty("members"));


            String logoPath = properties.getProperty("logo");
            logo.setImage(new Image(logoPath));
            log.info("Team info is set");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Team info loading failed");
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
        log.debug("New member is added");
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
        log.debug("Member is removed");
    }

    private void updateTeamMembers() {
        String members = String.join(", ", membersList);
        teamMembers.setText(members);
        log.debug("Team member tab is updated");
    }


}

