import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
Handles user inputs and outputs
 */
public class UI {
    private final Scanner scanner = new Scanner(System.in);

    public void displayState(int turn, double revenue, List<Customer> paidCustomers, List<Customer> waitingQueue, List<Table> tables, List<Order> allOrders) {
        double avgSatisfaction = paidCustomers.stream().mapToInt(Customer::getSatisfaction).average().orElse(0.0);

        System.out.println("\n==================== TURN " + turn + "/18 ====================");
        System.out.printf("Revenue: $%.2f | Paid Customers: %d | Avg Satisfaction: %.2f%n", revenue, paidCustomers.size(), avgSatisfaction);
        System.out.println("-------------------------------------------------");

        String queue = waitingQueue.stream().map(c -> String.format("C%d(Sat:%d)", c.getId(), c.getSatisfaction())).collect(Collectors.joining(", "));
        System.out.println("Waiting Queue: " + (queue.isEmpty() ? "[Empty]" : queue));

        System.out.println("Tables:");
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer c = table.getCustomer();
                Order o = c.getOrder();
                String orderInfo = (o != null) ? String.format("%s (%s)", o.getItem().getName(), o.getStatus()) : "No Order";
                System.out.printf("  - Table %d: [OCCUPIED by C%d] Status: %s, Sat: %d, Order: %s%n",
                        table.getTableId(), c.getId(), c.getStatus(), c.getSatisfaction(), orderInfo);
            } else {
                System.out.printf("  - Table %d: [EMPTY]%n", table.getTableId());
            }
        }
        System.out.println("-------------------------------------------------");
    }

    public int getManagerChoice(String prompt, int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print(prompt);
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice > max) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return choice;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showFinalResult(boolean victory, double revenue, int paidCount, double avgSatisfaction) {
        System.out.println("\n==================== GAME OVER ====================");
        System.out.printf("Final Revenue: $%.2f%n", revenue);
        System.out.printf("Total Paid Customers: %d%n", paidCount);
        System.out.printf("Average Satisfaction: %.2f%n", avgSatisfaction);
        if (victory) {
            System.out.println("Result: VICTORY! Congrats!");
        } else {
            System.out.println("Result: DEFEAT :(");
        }
    }
}
