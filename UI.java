import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UI {
    private final Scanner scanner = new Scanner(System.in);

    public void displayState(int turn, double revenue, List<Customer> paidCustomers,
                             List<Customer> waitingQueue, List<Table> tables, List<Order> allOrders) {
        double average = paidCustomers.stream()
                .mapToInt(Customer::getSatisfaction).average().orElse(0.0);
        System.out.printf("%n==================== TURN %d/18 ====================%n", turn);
        System.out.printf(" \uD83D\uDCB0 Revenue: $%.2f | \uD83D\uDCB2 Paid: %d | \uD83D\uDE0A Avg satisfaction: %.1f%n",
                revenue, paidCustomers.size(), average);

        String queue = waitingQueue.stream()
                .map(c -> String.format("C%d(sat:%d)", c.getId(), c.getSatisfaction()))
                .collect(Collectors.joining(", "));
        System.out.println("⏳ Waiting queue: " + (queue.isEmpty() ? "[Empty]" : queue));

        System.out.println("\uD83C\uDF7D\uFE0F Tables:");
        for (Table table : tables) {
            if (!table.isOccupied()) {
                System.out.printf("  Table %d: [EMPTY]%n", table.getTableId());
            } else {
                Customer c = table.getCustomer();
                Order order = c.getOrder();
                String description = order == null ? "No order" :
                        order.getItem().getName() + " (" + order.getStatus() + ")";
                System.out.printf("  Table %d: C%d | %s | sat:%d | %s%n",
                        table.getTableId(), c.getId(), c.getStatus(),
                        c.getSatisfaction(), description);
            }
        }

        String kitchen = allOrders.stream().filter(o -> !o.isServed())
                .map(o -> String.format("C%d %s [%s, %d left]", o.getCustomer().getId(),
                        o.getItem().getName(), o.getStatus(), o.getPreparationUnitsLeft()))
                .collect(Collectors.joining(", "));
        System.out.println("\uD83E\uDDC2 Kitchen: " + (kitchen.isEmpty() ? "[No open orders]" : kitchen));
        System.out.println("-------------------------------------------------");
    }

    public int getManagerChoice(String prompt, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                int choice = Integer.parseInt(line.trim());
                if (choice >= 0 && choice <= max) return choice;
                System.out.println("❌ Invalid choice. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }
    }

    public void showMessage(String message) { System.out.println(message); }

    public void showFinalResult(boolean victory, double revenue, int paidCount, double average) {
        System.out.println("\n==================== GAME OVER ====================");
        System.out.printf("\uD83D\uDCB0 Final revenue: $%.2f%n", revenue);
        System.out.printf("\uD83D\uDC64 Paid customers: %d%n", paidCount);
        System.out.printf("☺\uFE0F Average satisfaction: %.1f%n", average);
        System.out.println("Result: " + (victory ? "\uD83C\uDF89 VICTORY \uD83C\uDF89" : "☠\uFE0F DEFEAT ☠\uFE0F"));
    }
}
