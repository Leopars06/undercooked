public class Chef {
    public void cook(Order order) {
        if (order == null) {
            System.out.println("Chef has nothing to cook!");
            return;
        }
        if (order.isReady() || order.isServed()) {
            System.out.println("This order is already done!");
            return;
        }
        order.cookOneTurn();
    }
}
