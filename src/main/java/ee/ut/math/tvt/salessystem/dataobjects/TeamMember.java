package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;

@Entity
@Table
public class TeamMember {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String firstname;
    @Column
    private String lastname;
    @Column
    private String email;

    public TeamMember(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public TeamMember() {

    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
