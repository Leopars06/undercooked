public class Chef {
    public void cook(Order order) {
        if (order == null) {
            System.out.println("Chef has nothing to cook!");
            return;
        }

        if (order.isReady) {
            System.out.println("This order is done!");
            return;
        }

        System.out.println("Chef is prepping the order for the seated customer...");
        order.cookOneTurn(); // Advances cooking!
    }

    public void idle() {
        System.out.println("Chef is waiting...");
    }
}