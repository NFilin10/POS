package ee.ut.math.tvt.salessystem.dataobjects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Purchase {
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

    private double price;
    private LocalDate date;
    private LocalTime time;

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

    @Override
    public String toString() {
        return "Purchase{" +
                "price=" + price +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
