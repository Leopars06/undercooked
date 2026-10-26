
public class Order {
    public Customer customer;
    public Food foodItem;
    public int preparationUnitsLeft;
    public boolean isReady = false;

    public Order(Customer customer, Food foodItem) {
        this.customer = customer;
        this.foodItem = foodItem;
        this.preparationUnitsLeft = foodItem.getDefaultPreparationUnits();
    }

    // Decreases the countdown.
    public void cookOneTurn() {
        if (preparationUnitsLeft > 0) {
            preparationUnitsLeft--;
            System.out.println("Order for " + foodItem.getName() + " is cooking. Turns left: " + preparationUnitsLeft);
        }

        if (preparationUnitsLeft == 0 && !isReady) {
            isReady = true;
            System.out.println("Order for " + foodItem.getName() + " is now READY!");
        }
    }
}
