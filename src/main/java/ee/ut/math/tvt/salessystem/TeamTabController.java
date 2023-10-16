package ee.ut.math.tvt.salessystem;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TeamTabController{
    private Label teamName;
    private Label teamLeader;
    private Label teamLeaderMail;
    private Label teamMembers;
    private ImageView teamLogo;

    public void initialize(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            teamName.setText(properties.getProperty("team.name"));
            teamLeader.setText(properties.getProperty("team.contactPerson"));
            teamLeaderMail.setText(properties.getProperty("team.contact"));
            teamMembers.setText(properties.getProperty("team.members"));

            String logoPath = properties.getProperty("team.logo");
            //Image logo = new Image(logoPath);
            //teamLogo.setImage(logo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}