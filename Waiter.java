public class Waiter {
    private final String name;

    public Waiter(String name) { this.name = name; }

    public void seatCustomers(Table table, Customer customer) {
        table.seatCustomer(customer);
        System.out.printf("%s seated C%d at Table %d.%n", name, customer.getId(), table.getTableId());
    }

    public Order takeOrder(Customer customer, Food food) {
        Order order = new Order(customer, food);
        customer.setOrder(order);
        customer.setOrdered();
        System.out.printf("%s took C%d's order: %s.%n", name, customer.getId(), food.getName());
        return order;
    }

    public void serveFood(Customer customer) {
        if (customer == null || customer.getOrder() == null || !customer.getOrder().isReady()) {
            throw new IllegalStateException("Cannot serve food before cooking finishes.");
        }
        customer.getOrder().markServed();
        customer.readyToPay();
        System.out.printf("%s served C%d's %s.%n",
                name, customer.getId(), customer.getOrder().getItem().getName());
    }
}
