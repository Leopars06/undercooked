import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.*;

public class Controller {
    // constants
    private static final int MAX_TURNS = 18;
    private static final int TABLE_COUNT = 2;
    private static final int ARRIVAL_INTERVAL = 3;          // arrive on 1, 4, 7, 10, 13, 16
    private final Random random = new Random();
    private boolean criticArrived = false;
    private boolean vipArrived = false;

    // winning targets
    private static final int TARGET_PAID_CUSTOMERS = 4;
    private static final double TARGET_REVENUE = 45.0;
    private static final double TARGET_AVG_SATISFACTION = 60.0;

    // infrastructure
    private final UI ui = new UI();
    private final Menu menu = new Menu();
    private final Host host = new Host("Host");
    private final Waiter waiter = new Waiter("Waiter");
    private final Chef chef = new Chef("Gordon Ramsay");
    private final Cashier cashier = new Cashier("Cashier");
    private final List<Staff> staffMembers = List.of(host, waiter, chef, cashier);

    // game state lists
    private final List<Table> tables = new ArrayList<>();
    private final List<Customer> waitingQueue = new ArrayList<>();
    private final List<Customer> paidCustomers = new ArrayList<>();
    private final List<Order> allOrders = new ArrayList<>();

    // Tracks customer eating turns internally
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
        ui.showMessage("=== Welcome to UNDERCOOKED \uD83C\uDF54\uD83C\uDF2D\uD83C\uDF5D===");
        ui.showMessage(String.format("Goal: Serve >= %d customers, Earn >= $%.2f, Avg Satisfaction >= %.1f%%.",
                TARGET_PAID_CUSTOMERS, TARGET_REVENUE, TARGET_AVG_SATISFACTION));

        for (int turn = 1; turn <= MAX_TURNS; turn++) {
            // Reset staff task flags at the start of each turn
            for (Staff staff : staffMembers) {
                staff.resetTask();
            }

            // Step 1: Customer Arrivals & Automated Host Seating
            arriveCustomers(turn);
            processHostSeating();

            // Turn Header State Print
            ui.displayState(turn, revenue, paidCustomers, waitingQueue, tables, allOrders);

            // Step 2 & 3: Manager choices & Action execution
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
                // Determine Customer Type Polymorphically (20% Critic, 25% VIP, 55% Regular)
                double rng = random.nextDouble();
                Customer customer;

                // Calculate how many customer slots are left (assuming nextCustomerId starts at 1)
                int remainingSlots = 7 - nextCustomerId;

                // Force Critic if we are on the last slot and still haven't had one
                if (!criticArrived && remainingSlots == 1 && vipArrived) {
                    customer = new CriticCustomer(nextCustomerId++);
                    criticArrived = true;
                    ui.showMessage("\n✍️ A food CRITIC customer has arrived in line!");
                }
                // Force VIP if we are on the last slot and still haven't had one
                else if (!vipArrived && remainingSlots == 1 && criticArrived) {
                    customer = new VIPCustomer(nextCustomerId++);
                    vipArrived = true;
                    ui.showMessage("\n💎 A VIP customer has arrived in line!");
                }
                // normal spawning
                else if (rng < 0.20) {
                    customer = new CriticCustomer(nextCustomerId++);
                    criticArrived = true;
                    ui.showMessage("\n✍️ A food CRITIC customer has arrived in line!");
                } else if (rng < 0.45) {
                    customer = new VIPCustomer(nextCustomerId++);
                    vipArrived = true;
                    ui.showMessage("\n💎 A VIP customer has arrived in line!");
                } else {
                    customer = new RegularCustomer(nextCustomerId++);
                    ui.showMessage("\n👤 A regular customer has arrived in line.");
                }

                waitingQueue.add(customer);
                ui.showMessage("\n[\uD83D\uDD14] C" + customer.getId() + " has joined the waiting queue!");
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
            ui.showMessage("\uD83D\uDCCB Waiter Tasks:");
            ui.showMessage(" 1. Seat a waiting customer \uD83E\uDE91");
            ui.showMessage(" 2. Take a seated customer's order \uD83D\uDECE\uFE0F");
            ui.showMessage(" 3. Serve a READY order \uD83C\uDF7D\uFE0F");
            ui.showMessage(" 0. Wait (Do nothing) ⏳");
            int choice = ui.getManagerChoice("▶\uFE0F Select Waiter Action: ", 3);

            if (choice == 0) {
                ui.showMessage("Waiter is standing by.");
                waiter.assignTask();
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
            ui.showMessage("\n\uD83E\uDDD1\u200D\uD83C\uDF73 Chef Tasks:");
            ui.showMessage(" 1. Advance an order by 1 prep turn \uD83C\uDF73");
            ui.showMessage(" 0. Wait (Do nothing) ⏳");
            int choice = ui.getManagerChoice("▶\uFE0F Select Chef Action: ", 1);

            if (choice == 0) {
                ui.showMessage("Chef is standing by.");
                chef.assignTask();
                chefActionUsed = true;
            } else if (choice == 1) {
                chefActionUsed = executeChefCook();
            }
        }
    }

    // Automated Host seats at most one waiting customer per turn if an empty table is available
    private void processHostSeating() {
        if (waitingQueue.isEmpty() || host.hasTask()) {
            return;
        }
        for (Table table : tables) {
            if (!table.isOccupied()) {
                Customer next = waitingQueue.remove(0);
                host.seatCustomers(table, next);
                return;
            }
        }
    }

    private boolean executeWaiterSeat() {
        if (waitingQueue.isEmpty()) {
            ui.showMessage("❌ Error: No customers waiting in queue.");
            return false;
        }
        for (Table table : tables) {
            if (!table.isOccupied()) {
                Customer next = waitingQueue.remove(0);
                waiter.seatCustomers(table, next);
                return true;
            }
        }
        ui.showMessage("❌ Error: No empty tables available.");
        return false;
    }

    private boolean executeWaiterTakeOrder() {
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null && c.getStatus() == CustomerStatus.SEATED && c.getOrder() == null) {
                Food chosenFood;
                // VIPs/Critics (or a 30% chance for Regular customers) order a ComboMeal
                if (c instanceof VIPCustomer || c instanceof CriticCustomer || random.nextDouble() < 0.30) {
                    int comboIndex = 3 + ((c.getId() - 1) % 3);
                    chosenFood = menu.getFood(comboIndex);
                } else {
                    int foodIndex = (c.getId() - 1) % 3;
                    chosenFood = menu.getFood(foodIndex);
                }
                Order order = waiter.takeOrder(c, chosenFood);
                allOrders.add(order);
                return true;
            }
        }
        ui.showMessage("❌ Error: No seated customers require an order to be taken.");
        return false;
    }

    private boolean executeWaiterServe() {
        for (Order order : allOrders) {
            if (order.getStatus() == OrderStatus.READY) {
                waiter.serveFood(order.getCustomer());
                eatingCustomers.add(order.getCustomer()); // Track eating turn in controller
                return true;
            }
        }
        ui.showMessage("❌ Error: No orders are currently READY to be served.");
        return false;
    }

    private boolean executeChefCook() {
        if (allOrders.isEmpty()) {
            ui.showMessage("❌ Error: No orders placed in the kitchen.");
            return false;
        }
        List<Order> cookable = new ArrayList<>();
        for (Order order : allOrders) {
            OrderStatus status = order.getStatus();
            if (status == OrderStatus.PENDING || status == OrderStatus.COOKING) {
                cookable.add(order);
            }
        }
        if (cookable.isEmpty()) {
            ui.showMessage("❌ Error: No orders are currently pending cooking.");
            return false;
        }

        ui.showMessage("Select order to cook \uD83D\uDD25:");
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
        // 1. Automatic checkout processing (runs first so newly finished eaters pay next turn)
        Customer oldestToPay = null;
        Table occupiedTable = null;
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null && c.getStatus() == CustomerStatus.READY_TO_PAY) {
                if (oldestToPay == null || c.getId() < oldestToPay.getId()) {
                    oldestToPay = c;
                    occupiedTable = table;
                }
            }
        }

        if (oldestToPay != null) {
            double price = oldestToPay.getOrder().getItem().getPrice();
            if (cashier.payment(oldestToPay)) {
                //implement Critic special effects
                if (oldestToPay instanceof CriticCustomer) {
                    ui.showMessage("✨ CRITIC REVIEW BONUS! Critic C" + oldestToPay.getId() + " left a glowing review! +15 Satisfaction to everyone waiting!");
                    for (Customer waiting : waitingQueue) {
                        waiting.increaseSatisfaction(15);
                    }
                    for (Table t : tables) {
                        if (t.isOccupied() && t.getCustomer().getStatus() == CustomerStatus.SEATED) {
                            t.getCustomer().increaseSatisfaction(15);
                        }
                    }
                }
                //10% off for VIP customer
                if (oldestToPay instanceof VIPCustomer) {
                    price = price * 0.9;
                }
                revenue += price;
                paidCustomers.add(oldestToPay);
                occupiedTable.clearTable();
            }
        }

        // 2. Transition eating customers to READY_TO_PAY for the next turn
        Iterator<Customer> eatingIterator = eatingCustomers.iterator();
        while (eatingIterator.hasNext()) {
            Customer c = eatingIterator.next();
            if (c.getStatus() == CustomerStatus.EATING) {
                c.readyToPay();
                eatingIterator.remove();
            }
        }
    }

    // patience logic
    private void updateWaitingAndPatience() {
        // 1. Waiting Queue Decay
        Iterator<Customer> waitingIterator = waitingQueue.iterator();
        while (waitingIterator.hasNext()) {
            Customer c = waitingIterator.next();
            c.loseSatisfaction();
            if (c.getStatus() == CustomerStatus.LEFT_UNHAPPY) {
                waitingIterator.remove();
                ui.showMessage("\uD83D\uDE21 C" + c.getId() + " got tired of waiting and left! \uD83D\uDE21 ");
            }
        }

        // 2. Seated Table Decay & Order Cancellation logic
        for (Table table : tables) {
            Customer c = table.getCustomer();
            if (c != null) {
                if (c.getStatus() == CustomerStatus.SEATED || c.getStatus() == CustomerStatus.ORDERED) {
                    c.loseSatisfaction();
                    if (c.getStatus() == CustomerStatus.LEFT_UNHAPPY) {
                        ui.showMessage("\uD83D\uDE21 C" + c.getId() + " got tired of waiting and left Table " + table.getTableId() + "! \uD83D\uDE21");
                        table.clearTable();
                        if (c.getOrder() != null) {
                            c.getOrder().setStatus(OrderStatus.CANCELLED);
                        }
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
