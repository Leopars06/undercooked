import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Controller {
    // constants
    private static final int MAX_TURNS = 18;
    private static final int TABLE_COUNT = 2;
    private static final int ARRIVAL_INTERVAL = 3;          // arrive on 1, 4, 7, 10, 13, 16
    private static final int SATISFACTION_LOSS_REGULAR = 8;

    // winning targets
    private static final int TARGET_PAID_CUSTOMERS = 4;
    private static final double TARGET_REVENUE = 45.0;
    private static final double TARGET_AVG_SATISFACTION = 60.0;

    // infrastructure
    private final UI ui = new UI();
    private final Menu menu = new Menu();
    private final Waiter waiter = new Waiter("Waiter");
    private final Chef chef = new Chef("Gordon Ramsey");
    private final Cashier cashier = new Cashier("Cashier");

    // game state lists
    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> waitingQueue = new ArrayList<>();
    private final List<Customer> paidCustomers = new ArrayList<>();
    private final List<Order> allOrders = new ArrayList<>();

    // Tracks customer eating turns internally since Customer.java does not have a tick method
    private final List<Customer> eatingCustomers = new ArrayList<>();

    private int nextCustomerId = 1;
    private double revenue = 0.0;

    // constructor
    public Controller() {
        for (int i = 1; i <= TABLE_COUNT; i++) {
            tables.add(new Table(i));
        }
    }

    // main game loop!!
    public void runGame() {
        ui.showMessage("=== Welcome to UNDERCOOKED ===");
        ui.showMessage(String.format("Goal: Serve >= %d customers, Earn >= $%.2f, Avg Satisfaction >= %.1f%%.",
                TARGET_PAID_CUSTOMERS, TARGET_REVENUE, TARGET_AVG_SATISFACTION));

        for (int turn = 1; turn <= MAX_TURNS; turn++) {
            // Step 1: Customer Arrivals
            arriveCustomers(turn);

            // Turn Header State Print
            ui.displayState(turn, revenue, paidCustomers, waitingQueue, tables, allOrders);

            // Step 2: Manager choices & Action execution
            takeManagerChoices();

            // Step 4: Customers eat and automated Cashier handles transactions
            processEatingAndPayments();

            // Step 5: Queue & seated customer patience updates
            updateWaitingAndPatience();
        }

        // Step 6: Final results calculation
        evaluateEndGame();
    }

    // spawn customers
    private void arriveCustomers(int turn) {
        if (turn == 1 || (turn - 1) % ARRIVAL_INTERVAL == 0) {
            if (nextCustomerId <= 6) {
                Customer customer = new Customer(nextCustomerId++);
                waitingQueue.add(customer);
                ui.showMessage("\n[Event] C" + customer.getId() + " has joined the waiting queue!");
            }
        }
    }

    // Prompts the manager to delegate tasks to the Waiter and the Chef sequentially.
    private void takeManagerChoices() {
        boolean waiterActionUsed = false;
        boolean chefActionUsed = false;

        ui.showMessage("\n--- MANAGER ACTION DESK ---");

        // 1. WAITER ACTION
        while (!waiterActionUsed) {
            ui.showMessage("Waiter Tasks:");
            ui.showMessage(" 1. Seat a waiting customer");
            ui.showMessage(" 2. Take a seated customer's order");
            ui.showMessage(" 3. Serve a READY order");
            ui.showMessage(" 0. Wait (Do nothing)");
            int choice = ui.getManagerChoice("Select Waiter Action: ", 3);

            if (choice == 0) {
                ui.showMessage("Waiter is standing by.");
                waiterActionUsed = true;
            } else if (choice == 1) {
                waiterActionUsed = executeWaiterSeat();
            } else if (choice == 2) {
                waiterActionUsed = executeWaiterTakeOrder();
            } else if (choice == 3) {
                waiterActionUsed = executeWaiterServe();
            }
        }

        // 2. CHEF ACTION
        while (!chefActionUsed) {
            ui.showMessage("\nChef Tasks:");
            ui.showMessage(" 1. Advance an order by 1 prep turn");
            ui.showMessage(" 0. Wait (Do nothing)");
            int choice = ui.getManagerChoice("Select Chef Action: ", 1);

            if (choice == 0) {
                ui.showMessage("Chef is standing by.");
                chefActionUsed = true;
            } else if (choice == 1) {
                chefActionUsed = executeChefCook();
            }
        }
    }

    private boolean executeWaiterSeat() {
        if (waitingQueue.isEmpty()) {
            ui.showMessage("Error: No customers waiting in queue.");
            return false;
        }
        for (Table table : tables) {
            if (!table.isOccupied()) {
                Customer next = waitingQueue.remove(0);
                waiter.seatCustomers(table, next);
                return true;
            }
        }
        ui.showMessage("Error: No empty tables available.");
        return false;
    }

    private boolean executeWaiterTakeOrder() {
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null && c.getState() == Customer.State.SEATED && c.getOrder() == null) {
                int foodIndex = (c.getId() - 1) % 3;
                Food chosenFood = menu.getFood(foodIndex);
                Order order = waiter.takeOrder(c, chosenFood);
                allOrders.add(order);
                return true;
            }
        }
        ui.showMessage("Error: No seated customers require an order to be taken.");
        return false;
    }

    private boolean executeWaiterServe() {
        for (Order order : allOrders) {
            if ("READY".equals(order.getStatus())) {
                waiter.serveFood(order.getCustomer());
                eatingCustomers.add(order.getCustomer()); // Track eating turn in controller
                return true;
            }
        }
        ui.showMessage("Error: No orders are currently READY to be served.");
        return false;
    }

    private boolean executeChefCook() {
        if (allOrders.isEmpty()) {
            ui.showMessage("Error: No orders placed in the kitchen.");
            return false;
        }
        List<Order> cookable = new ArrayList<>();
        for (Order order : allOrders) {
            String status = order.getStatus();
            if ("PENDING".equals(status) || "COOKING".equals(status)) {
                cookable.add(order);
            }
        }
        if (cookable.isEmpty()) {
            ui.showMessage("Error: No orders are currently pending cooking.");
            return false;
        }

        ui.showMessage("Select order to cook:");
        for (int i = 0; i < cookable.size(); i++) {
            Order o = cookable.get(i);
            System.out.printf(" %d. C%d's %s (%d unit(s) remaining)%n",
                    i + 1, o.getCustomer().getId(), o.getItem().getName(), o.getPreparationUnitsLeft());
        }
        int selection = ui.getManagerChoice("Order selection (0 to cancel): ", cookable.size());
        if (selection == 0) return false;

        chef.cook(cookable.get(selection - 1));
        return true;
    }

    // Customers eat for 1 turn. Oldest ready-to-pay customer is automatically processed by Cashier.
    private void processEatingAndPayments() {
        // Transition eating customers to READY_TO_PAY
        Iterator<Customer> eatingIterator = eatingCustomers.iterator();
        while (eatingIterator.hasNext()) {
            Customer c = eatingIterator.next();
            if (c.getState() == Customer.State.EATING) {
                c.readyToPay();
                eatingIterator.remove();
            }
        }

        // Automatic checkout processing
        Customer oldestToPay = null;
        Table occupiedTable = null;
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null && c.getState() == Customer.State.READY_TO_PAY) {
                if (oldestToPay == null || c.getId() < oldestToPay.getId()) {
                    oldestToPay = c;
                    occupiedTable = table;
                }
            }
        }

        if (oldestToPay != null) {
            double price = oldestToPay.getOrder().getItem().getPrice();
            if (cashier.payment(oldestToPay)) {
                revenue += price;
                paidCustomers.add(oldestToPay);
                occupiedTable.clearTable();
            }
        }
    }

    // patience logic
    private void updateWaitingAndPatience() {
        // 1. Waiting Queue Decay
        Iterator<Customer> waitingIterator = waitingQueue.iterator();
        while (waitingIterator.hasNext()) {
            Customer c = waitingIterator.next();
            c.loseSatisfaction(SATISFACTION_LOSS_REGULAR);
            if (c.getState() == Customer.State.LEFT) {
                waitingIterator.remove();
                ui.showMessage("! C" + c.getId() + " got tired of waiting and left! :(");
            }
        }

        // 2. Seated Table Decay & Order Cancellation logic
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null) {
                if (c.getState() == Customer.State.SEATED || c.getState() == Customer.State.ORDERED) {
                    c.loseSatisfaction(SATISFACTION_LOSS_REGULAR);
                    if (c.getState() == Customer.State.LEFT) {
                        ui.showMessage("! C" + c.getId() + " got tired of waiting and left Table " + table.getTableId() + "! :(");
                        table.clearTable();
                    }
                }
            }
        }
    }

    // Evaluates end game success targets.
    private void evaluateEndGame() {
        double finalAvgSatisfaction = paidCustomers.stream()
                .mapToInt(Customer::getSatisfaction)
                .average()
                .orElse(0.0);

        boolean victory = paidCustomers.size() >= TARGET_PAID_CUSTOMERS
                && revenue >= TARGET_REVENUE
                && finalAvgSatisfaction >= TARGET_AVG_SATISFACTION;

        ui.showFinalResult(victory, revenue, paidCustomers.size(), finalAvgSatisfaction);
    }
}
