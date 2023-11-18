package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;

/**
 * Stock item.
 */
@Entity
@Table
public class StockItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private long barcode;
    @Column
    private String name;
    @Column
    private double price;
    @Transient
    private String description;
    @Column
    private int quantity;

    public StockItem() {
    }
    public StockItem(Long barcode, String name, String desc, double price, int quantity) {
        this.barcode = barcode;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("StockItem{id=%d, name='%s}", id, name);
    }
}
