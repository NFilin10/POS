package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column()
    private String name;

    @Column()
    private String username;

    private String role;


    @Column(nullable = false)
    private String password;

    @Transient
    private List<Purchase> purchases;


    public User(String name, String role, String username, String password) {
        this.name = name;
        this.role = role;
        this.password = password;
        this.purchases = new ArrayList<>();
        this.username = username;

    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean verifyPassword(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", purchases=" + purchases +
                '}';
    }
}
