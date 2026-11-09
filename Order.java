public class Order {
    private static final double VIP_DISCOUNT = 0.9;

    private final Customer customer;
    private final Food foodItem;
    private final double finalPrice;
    private int preparationUnitsLeft;
    private OrderStatus status = OrderStatus.PENDING;

    public Order(Customer customer, Food foodItem) {
        this(customer, foodItem, false);
    }

    public Order(Customer customer, Food foodItem, boolean happyHourActive) {
        if (customer == null || foodItem == null) {
            throw new IllegalArgumentException("An order requires a customer and food.");
        }
        this.customer = customer;
        this.foodItem = foodItem;
        this.preparationUnitsLeft = foodItem.getDefaultPreparationUnits();
        this.finalPrice = calculateLockedPrice(customer, foodItem, happyHourActive);
    }

    // Apply VIP discount first (0.9), then Happy Hour discount (0.8), locked when
    // order is taken
    private static double calculateLockedPrice(Customer customer, Food foodItem, boolean happyHourActive) {
        double price = foodItem.getPrice();
        if (customer instanceof VIPCustomer) {
            price = price * VIP_DISCOUNT;
        }
        if (happyHourActive) {
            price = price * HappyHour.getDiscountMultiplier();
        }
        return Math.round(price * 100.0) / 100.0;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Food getItem() {
        return foodItem;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public int getPreparationUnitsLeft() {
        return preparationUnitsLeft;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    public boolean isServed() {
        return status == OrderStatus.SERVED;
    }

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
