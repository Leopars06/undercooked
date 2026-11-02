import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Controller {
    private static final int MAX_TURNS = 18;
    private static final int TABLE_COUNT = 3;
    private static final int ARRIVAL_INTERVAL = 2;
    private static final int SATISFACTION_LOSS_PER_TURN = 10;

    private static final double TARGET_REVENUE = 45.0;

    private final UI ui = new UI();
    private final Menu menu = new Menu();
    private final Waiter waiter = new Waiter("Alex");
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();

    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> waitingQueue = new ArrayList<>();
    private final List<Customer> paidCustomers = new ArrayList<>();
    private final List<Order> allOrders = new ArrayList<>();

    private int nextCustomerId = 1;
    private double revenue = 0.0;

    public Controller() {
        for (int i = 1; i <= TABLE_COUNT; i++) {
            tables.add(new Table(i));
        }
    }

    public static void main(String[] args) {
        new Controller().runGame();
    }

    public void runGame() {
        ui.showMessage("Undercooked! (Intermediate build)");
        ui.showMessage("Goal: Earn at least $45 by the end of turn 18.");
        ui.showMessage("One action per turn. Enter 0 to pass or cancel an order.");
        for (int turn = 1; turn <= MAX_TURNS; turn++) {

            updateWaitingCustomers();
            arriveCustomers(turn);
            ui.displayState(turn, revenue, paidCustomers, waitingQueue, tables, allOrders);
            takeTurn();
        }

        double average = paidCustomers.stream()
                .mapToInt(Customer::getSatisfaction).average().orElse(0.0);
        ui.showFinalResult(revenue >= TARGET_REVENUE, revenue, paidCustomers.size(), average);
    }

    private void arriveCustomers(int turn) {
        if (turn == 1 || turn % ARRIVAL_INTERVAL == 0) {
            Customer customer = new Customer(nextCustomerId++);
            waitingQueue.add(customer);
            ui.showMessage("Customer C" + customer.getId() + " arrived.");
        }
    }

    private void takeTurn() {
        ui.showMessage("1. Seat next customer");
        ui.showMessage("2. Take order");
        ui.showMessage("3. Cook order");
        ui.showMessage("4. Serve food");
        ui.showMessage("5. Collect payment");
        ui.showMessage("0. Do nothing");

        int choice = ui.getManagerChoice("Action: ", 5);

        switch (choice) {
            case 1: seatNextCustomer(); break;
            case 2: takeOrder(); break;
            case 3: cookOrder(); break;
            case 4: serveFood(); break;
            case 5: collectPayment(); break;
            case 0: ui.showMessage("Turn passed."); break;
            default: throw new IllegalStateException("Unexpected action.");
        }
    }

    private void seatNextCustomer() {
        if (waitingQueue.isEmpty()) {
            ui.showMessage("No customers are waiting.");
            return;
        }
        for (Table table : tables) {
            if (!table.isOccupied()) {
                Customer next = waitingQueue.remove(0);
                waiter.seatCustomers(table, next);
                return;
            }
        }
        ui.showMessage("No empty tables available.");
    }


    private void takeOrder() {
        for (Table table : tables) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.getState() == Customer.State.SEATED
                    && customer.getOrder() == null) {
                ui.showMessage("Ordering for C" + customer.getId() + ":");
                menu.displayMenu();
                int selection = ui.getManagerChoice("Dish (0 to cancel): ", menu.getFoods().size());
                if (selection == 0) {
                    ui.showMessage("Order canceled; turn passed.");
                    return;
                }
                Order order = waiter.takeOrder(customer, menu.getFood(selection - 1));
                allOrders.add(order);
                return;
            }
        }
        ui.showMessage("No seated customers need an order.");
    }

    private void cookOrder() {
        for (Order order : allOrders) {
            if (!order.isReady() && !order.isServed()) {
                chef.cook(order);
                return;
            }
        }
        ui.showMessage("There are no pending orders to cook.");
    }


    private void serveFood() {
        for (Order order : allOrders) {
            if (order.isReady() && !order.isServed()) {
                waiter.serveFood(order.getCustomer());
                return;
            }
        }
        ui.showMessage("No cooked orders are ready to serve.");
    }


    private void collectPayment() {
        for (Table table : tables) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.getState() == Customer.State.READY_TO_PAY) {
                if (cashier.payment(customer)) {
                    double price = customer.getOrder().getItem().getPrice();
                    revenue += price;
                    paidCustomers.add(customer);
                    int tableId = table.getTableId();
                    table.clearTable();
                    ui.showMessage(String.format("Collected $%.2f; Table %d is free.", price, tableId));
                }
                return;
            }
        }
        ui.showMessage("No customers are ready to pay.");
    }


    private void updateWaitingCustomers() {
        Iterator<Customer> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            customer.loseSatisfaction(SATISFACTION_LOSS_PER_TURN);
            if (customer.getState() == Customer.State.LEFT) {
                iterator.remove();
                ui.showMessage("C" + customer.getId() + " left after waiting too long.");
            }
        }
        for (Table table : tables) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.getState() == Customer.State.SEATED
                    && customer.getOrder() == null) {
                customer.loseSatisfaction(SATISFACTION_LOSS_PER_TURN);
                if (customer.getState() == Customer.State.LEFT) {
                    table.clearTable();
                    ui.showMessage("C" + customer.getId() + " left Table "
                            + table.getTableId() + " before ordering.");
                }
            }
        }
    }
}
