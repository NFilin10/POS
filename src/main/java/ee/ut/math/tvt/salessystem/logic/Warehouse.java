package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;


public class Warehouse {

    private final SalesSystemDAO dao;

    public Warehouse(SalesSystemDAO dao) {
        this.dao = dao;
    }


    public void addNewProductToWarehouse(String barcode, int quantity, String name, double price)
            throws ApplicationException {
        dao.beginTransaction();
        if (barcode.isEmpty()) {
            dao.rollbackTransaction();
            throw new ApplicationException("Barcode cannot be empty");
        } else if (quantity <= 0) {
            dao.rollbackTransaction();
            throw new ApplicationException("Quantity cannot be zero or negative");
        } else if (dao.findStockItem(Long.parseLong(barcode)) == null) {
            StockItem addedItem = new StockItem(Long.parseLong(barcode), name, "", price, quantity);
            dao.saveStockItem(addedItem);
            dao.commitTransaction();
        } else {
            dao.rollbackTransaction();
            throw new ApplicationException("The barcode you entered already exists in the database. Please enter a new barcode.");
        }
    }

    public boolean deleteItemFromWarehouse(long barcode) {
        StockItem item = dao.findStockItem(barcode);
        if (item != null) {
            dao.deleteStockItem(item);
            return true;
        } else {
            return false;
        }
    }

    public String updateItem(StockItem item, String barcodeText, String quantityText, String name, String priceText) {
        try {
            long newBarcode = Long.parseLong(barcodeText);

            StockItem existingItem = dao.findStockItem(newBarcode);
            if (existingItem != null && existingItem != item) {
                return "The new barcode already exists in the database. Please enter a different barcode.";
            }

            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            item.setId(newBarcode);
            item.setQuantity(quantity);
            item.setName(name);
            item.setPrice(price);

            return null;
        } catch (NumberFormatException e) {
            return "Invalid input";
        }
    }
}
