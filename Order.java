public class Order {
    private final Customer customer;
    private final Food foodItem;
    private int preparationUnitsLeft;
    private boolean ready;
    private boolean served;

    public Order(Customer customer, Food foodItem) {
        if (customer == null || foodItem == null) {
            throw new IllegalArgumentException("An order requires a customer and food.");
        }
        this.customer = customer;
        this.foodItem = foodItem;
        this.preparationUnitsLeft = foodItem.getDefaultPreparationUnits();
    }

    public Customer getCustomer() { return customer; }
    public Food getItem() { return foodItem; }
    public int getPreparationUnitsLeft() { return preparationUnitsLeft; }
    public boolean isReady() { return ready; }
    public boolean isServed() { return served; }

    public String getStatus() {
        if (served) return "SERVED";
        if (ready) return "READY";
        if (preparationUnitsLeft < foodItem.getDefaultPreparationUnits()) return "COOKING";
        return "PENDING";
    }

    public void cookOneTurn() {
        if (ready || served) {
            return;
        }
        preparationUnitsLeft--;
        System.out.printf("Cooking %s for C%d. Prep turns left: %d%n",
                foodItem.getName(), customer.getId(), preparationUnitsLeft);
        if (preparationUnitsLeft == 0) {
            ready = true;
            System.out.printf("Order for C%d is READY!%n", customer.getId());
        }
    }

    public void markServed() {
        if (!ready || served) {
            throw new IllegalStateException("Only ready, unserved orders can be served.");
        }
        served = true;
    }
}
