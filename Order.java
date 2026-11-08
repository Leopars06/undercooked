public class Order {
    private final Customer customer;
    private final Food foodItem;
    private int preparationUnitsLeft;
    private OrderStatus status = OrderStatus.PENDING;

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

    public OrderStatus getStatus() { return status; }
    public boolean isReady() { return status == OrderStatus.READY; }
    public boolean isServed() { return status == OrderStatus.SERVED; }

    public void cookOneTurn() {
        if (status == OrderStatus.READY || status == OrderStatus.SERVED || status == OrderStatus.CANCELLED) {
            return;
        }

        status = OrderStatus.COOKING;
        preparationUnitsLeft--;
        System.out.printf("\uD83C\uDF73 Cooking %s for C%d. Prep turns left: %d%n",
                foodItem.getName(), customer.getId(), preparationUnitsLeft);

        if (preparationUnitsLeft == 0) {
            status = OrderStatus.READY;
            System.out.printf("✅ Order for C%d is READY!%n", customer.getId());
        }
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Only ready, unserved orders can be served.");
        }
        status = OrderStatus.SERVED;
    }

    // cancel if customer leaves
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
