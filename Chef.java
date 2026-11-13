public class Chef extends Staff {
    public Chef(String name) {
        super(name);
    }

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
        assignTask();
    }
}
