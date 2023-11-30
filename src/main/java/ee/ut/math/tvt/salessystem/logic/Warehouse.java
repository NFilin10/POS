package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;


public class Warehouse {

    private final SalesSystemDAO dao;

    public Warehouse(SalesSystemDAO dao) {
        this.dao = dao;
    }


    public void addNewProductToWarehouse(String barcode, int quantity, String name, double price)
            throws ApplicationException, NegativePriceException {
        dao.beginTransaction();
        try {
            long barcodeValue = Long.parseLong(barcode);
            if (barcodeValue < 0){
                throw new ApplicationException("Barcode cannot be negative");
            }
            if (barcode.isEmpty()) {
                throw new ApplicationException("Barcode cannot be empty");
            } else if (quantity <= 0) {
                throw new ApplicationException("Quantity cannot be zero or negative");
            } else if (quantity >= 999) {
                throw new ApplicationException("Max quantity reached");
            } else if (price < 0) {
                throw new NegativePriceException();
            } else if (dao.findStockItem(Long.parseLong(barcode)) == null) {
                StockItem addedItem = new StockItem(Long.parseLong(barcode), name, "", price, quantity);
                dao.saveStockItem(addedItem);
                System.out.println(dao.findStockItems());

                dao.commitTransaction();
            } else {
                throw new ApplicationException("The barcode you entered already exists in the database. Please enter a new barcode.");
            }
        } catch (NumberFormatException e){
            dao.rollbackTransaction();
            throw new ApplicationException("Invalid barcode format");
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

    public String updateItem(StockItem item, String barcodeText, String quantityText, String name, String priceText) throws NegativePriceException {
        try {
            long newBarcode = Long.parseLong(barcodeText);

            StockItem existingItem = dao.findStockItem(newBarcode);
            if (existingItem != null && existingItem != item) {
                return "The new barcode already exists in the database. Please enter a different barcode.";
            }

            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);
            if (price < 0){
                throw new NegativePriceException();
            }

            item.setBarcode(newBarcode);
            item.setQuantity(quantity);
            item.setName(name);
            item.setPrice(price);
            dao.updateStockItem(item);

            return null;
        } catch (NumberFormatException e) {
            return "Invalid input";
        }
    }
}
