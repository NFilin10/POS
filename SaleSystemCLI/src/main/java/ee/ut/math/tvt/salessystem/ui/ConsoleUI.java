package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.User;
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
    private static User loggedInUser;
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
    public void run() {
        printWelcomeMessage();
        authentication();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            processCommands(reader);
        } catch (IOException e) {
            log.error("Error reading user input: " + e.getMessage(), e);
        }
    }


    private void printWelcomeMessage() {
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        log.info("Session started");
    }


    private void authentication() {
        System.out.println("-------------------------");
        System.out.println("l\t\tLogin");
        System.out.println("su\t\tSign up");
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


    private void processCommands(BufferedReader reader) throws IOException {
        while (true) {
            System.out.print("> ");
            String command = reader.readLine().trim().toLowerCase();
            if (command.equals("q")) {
                log.info("Session ended");
                System.exit(0);
            }
            processCommand(command.split(" "));
        }
    }


    private void processCommand(String[] command) throws IOException {
        switch (command[0]) {
            case "l" -> login();
            case "su" -> signUp();
            case "h" -> printUsage();
            case "w" -> showStock();
            case "c" -> showCart();
            case "p" -> purchaseCart();
            case "d" -> deleteItemFromCart(command);
            case "r" -> cancelPurchase();
            case "t" -> printTeamInfo();
            case "a" -> addItemToCart(command);
            case "s" -> addNewProductToWarehouse(command);
            case "ds" -> deleteItemFromWarehouse(command);
            case "u" -> updateItemInWarehouse(command);
            case "f1" -> filterBetweenDates(command);
            case "f2" -> getLast10Purchases();
            case "f3" -> showAllPurchases();
            default -> {
                log.error("Unidentifiable command");
                System.out.println("unknown command");
            }
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


    private void updateItemInWarehouse(String[] command) {
        try {
            warehouse.updateItem(dao.findStockItem(Long.parseLong(command[1])), command[1], command[2], command[3], command[4]);
        } catch (NegativePriceException e) {
            System.out.println(e.getMessage());;
        }
    }


    private void deleteItemFromWarehouse(String[] command) {
        warehouse.deleteItemFromWarehouse(Long.parseLong(command[1]));
        System.out.println("Done");
    }


    private void addNewProductToWarehouse(String[] command) {
        try {
            warehouse.addNewProductToWarehouse(command[1], Integer.parseInt(command[3]), command[2], Double.parseDouble(command[4]));
            System.out.println("Done. ");
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        } catch (ApplicationException | NegativePriceException e) {
            System.out.println(e.getMessage());;
        }
    }


    private void addItemToCart(String[] command) {
        try {
            long idx = Long.parseLong(command[1]);
            int amount = Integer.parseInt(command[2]);
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
    }


    private void cancelPurchase() {
        cart.cancelCurrentPurchase();
        System.out.println("Done. ");
        log.debug("Purchase cancelled");
    }


    private void deleteItemFromCart(String[] command) {
        cart.deleteItemFromCart(dao.findStockItem(Long.parseLong(command[1])));
    }


    private void purchaseCart() {
        cart.submitCurrentPurchase(loggedInUser);
        System.out.println("Done. ");
        log.debug("Purchase submitted");
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

        List<Purchase> filteredPurchases = history.filterBetweenTwoDates(dao, startDate, endDate, loggedInUser);

        printFiltered(filteredPurchases);

    }


    private void getLast10Purchases() {
        List<Purchase> filteredPurchases = history.getLast10(dao, loggedInUser);
        printFiltered(filteredPurchases);

    }


    private void showAllPurchases(){
        List<Purchase> filteredPurchases = history.showAll(dao, loggedInUser);
        printFiltered(filteredPurchases);
    }


    private void printFiltered(List<Purchase> filteredPurchases){
        if (filteredPurchases.size() != 0){
            for (Purchase filteredPurchase : filteredPurchases) {
                System.out.println(filteredPurchase.getDate() + " " + filteredPurchase.getTime() + " " + filteredPurchase.getPrice() + " Euro");
            }
        }
        else{
            System.out.println("No purchases in that interval");
        }
    }


    private void login() throws IOException {
        BufferedReader auth = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Login: ");
        String login  = auth.readLine().trim().toLowerCase();
        System.out.print("\nPassword: ");
        String password  = auth.readLine().trim().toLowerCase();

        User user = AuthenticationService.authenticateUser(login, password);

        if (user != null) {
            setLoggedInUser(user);
            printUsage();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("> ");
                processCommand(in.readLine().trim().toLowerCase().split(" "));
            }

        } else {
            System.out.println("Incorrect username or password");
            authentication();
        }
    }


    private void signUp() throws IOException {
        BufferedReader auth = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Name: ");
        String name = auth.readLine().trim().toLowerCase();
        System.out.println("Choose role:");
        System.out.println("1. Warehouse manager\n2. Cashier\n3. Customer");
        String role =  auth.readLine().trim().toLowerCase();
        if (role.equals("1")){
            role = "Warehouse manager";
        } else if (role.equals("2")) {
            role = "Cashier";
        } else if (role.equals("3")) {
            role = "Customer";
        }

        System.out.print("Login: ");
        String login  = auth.readLine().trim().toLowerCase();
        System.out.print("\nPassword: ");
        String password  = auth.readLine().trim().toLowerCase();

        User user = AuthenticationService.registerUser(name, role, login, password);

        if (user != null) {
            setLoggedInUser(user);
            printUsage();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("> ");
                processCommand(in.readLine().trim().toLowerCase().split(" "));
            }
        } else {
            System.out.println("User already exists");
            authentication();
        }

    }


    private void setLoggedInUser(User user) {
        loggedInUser = user;
    }

}



