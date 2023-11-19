package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table
public class Purchase {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Purchase() {

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Column
    private double price;
    @Column
    private LocalDate date;
    @Column
    private LocalTime time;
    @OneToMany
    private List<SoldItem> items;



    public Purchase(double price, LocalDate date, LocalTime time, List<SoldItem> items) {
        this.price = price;
        this.date = date;
        this.time = time;
        this.items = items;
    }

    public List<SoldItem> getItems() {
        return items;
    }

    public void setItems(List<SoldItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "price=" + price +
                ", date=" + date +
                ", time=" + time +
                '}'+ items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
