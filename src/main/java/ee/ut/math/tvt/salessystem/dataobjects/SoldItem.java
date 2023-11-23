package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */
@Entity
@Table
public class SoldItem {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private long barcode;
    @ManyToOne
    @Transient
    private StockItem stockItem;
    @Column
    private String name;
    @Column
    private Integer quantity;
    @Column
    private double price;
    @Column
    @Transient
    private double sum;

    public SoldItem() {
    }

    public SoldItem(StockItem stockItem, int quantity) {
        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
        this.barcode = stockItem.getBarcode();
        this.sum = getSum();
    }


    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public double getSum() {
        return price * ((double) quantity);
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public String toString() {
        return String.format("SoldItem{id=%d, name='%s'}", id, name);
    }
}
