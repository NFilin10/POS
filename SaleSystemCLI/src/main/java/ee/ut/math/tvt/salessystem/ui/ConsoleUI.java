package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    private final Warehouse warehouse;

    private final History history;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        this.warehouse = new Warehouse(dao);
        this.history = new History();
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new HibernateSalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        log.info("Session started");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
        }
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getBarcode() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (cart.getAll().size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\tShow this help");
        System.out.println("w\t\tShow warehouse contents");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("d IDX \tDelete item from cart");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("s IDX name amount price\t\tAdd new item in stock");
        System.out.println("ds IDX\t\tDelete item from stock");
        System.out.println("u IDX amount name price \t\tUpdate item in warehouse");
        System.out.println("f1 start_date end_date \t\tShow purchases between to dates. Format - year-month-day");
        System.out.println("f2 \t\tShow last 10 purchases");
        System.out.println("f3 \t\tShow all purchases");
        System.out.println("t\t\tShow team info");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        String[] c = command.split(" ");

        if (c[0].equals("h"))
            printUsage();
        else if (c[0].equals("q")) {
            log.info("Session ended");
            System.exit(0);
        }
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("p")) {
            cart.submitCurrentPurchase();
            System.out.println("Done. ");
            log.debug("Purchase submitted");
        }
        else if (c[0].equals("d")) {
            cart.deleteItemFromCart(dao.findStockItem(Long.parseLong(c[1])));
        } else if (c[0].equals("r")) {
            cart.cancelCurrentPurchase();
            System.out.println("Done. ");
            log.debug("Purchase cancelled");
        } else if (c[0].equals("t"))
            printTeamInfo();
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                    log.info("Item added to the cart");
                    System.out.println("Done. ");
                } else {
                    log.error("Invalid id");
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | ApplicationException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        } else if (c[0].equals("s") && c.length == 5) {
            try {
                warehouse.addNewProductToWarehouse(c[1], Integer.parseInt(c[3]), c[2], Double.parseDouble(c[4]));
                System.out.println("Done. ");
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            } catch (ApplicationException | NegativePriceException e) {
                System.out.println(e.getMessage());;
            }
        } else if (c[0].equals("ds")){
            warehouse.deleteItemFromWarehouse(Long.parseLong(c[1]));
            System.out.println("Done");
        } else if (c[0].equals("u")) {
            try {
                warehouse.updateItem(dao.findStockItem(Long.parseLong(c[1])), c[1], c[2], c[3], c[4]);
            } catch (NegativePriceException e) {
                System.out.println(e.getMessage());;
            }
        } else if (c[0].equals("f1") && c.length == 3) {
            try {
                filterBetweenDates(c);
            } catch (Exception e) {
                System.out.println("Wrong format");
            }

        } else if (c[0].equals("f2")) {
            getLast10Purchases();
        } else if (c[0].equals("f3")) {
            showAllPurchases();
        } else {
            log.error("Unidentifiable command");
            System.out.println("unknown command");
        }
    }

    private void printTeamInfo(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            System.out.println("Team name: " + properties.getProperty("name"));
            System.out.println("Team leader: " + properties.getProperty("contactPerson"));
            System.out.println("Team leader email: " + properties.getProperty("contact"));
            System.out.println("Team members: " + properties.getProperty("members"));
            log.info("Team info printed");
        } catch (IOException e) {
            log.error("Failed to print team info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterBetweenDates(String[] command) {
        LocalDate startDate = LocalDate.parse(command[1]);
        LocalDate endDate = LocalDate.parse(command[2]);

        List<Purchase> filteredPurchases = history.filterBetweenTwoDates(dao, startDate, endDate);

        printFiltered(filteredPurchases);

    }

    private void getLast10Purchases() {
        List<Purchase> filteredPurchases = history.getLast10(dao);
        printFiltered(filteredPurchases);

    }


    private void showAllPurchases(){
        List<Purchase> filteredPurchases = history.showAll(dao);
        printFiltered(filteredPurchases);
    }

    private void printFiltered(List<Purchase> filteredPurchases){
        if (filteredPurchases != null){
            for (Purchase filteredPurchase : filteredPurchases) {
                System.out.println(filteredPurchase.getDate() + " " + filteredPurchase.getTime() + " " + filteredPurchase.getPrice() + " Euro");
            }
        }
        else{
            System.out.println("No purchases in that interval");
        }
    }

}



