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


    @Override
    public String toString() {
        return "User{" +
                "name='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

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

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean verifyPassword(String enteredPassword) {
        // Compare the entered password with the stored password
        return this.password.equals(enteredPassword);
    }

}
