public class Waiter extends Staff {
    public Waiter(String name) {
        super(name);
    }

    public void seatCustomers(Table table, Customer customer) {
        table.seatCustomer(customer);
        assignTask(); // Staff task state updated
        System.out.printf("[Staff] %s seated C%d at Table %d.%n", getName(), customer.getId(), table.getTableId());
    }

    public Order takeOrder(Customer customer, Food food) {
        Order order = new Order(customer, food);
        customer.setOrder(order);
        customer.setOrdered();
        assignTask();
        System.out.printf("[Staff] %s took C%d's order: %s.%n", getName(), customer.getId(), food.getName());
        return order;
    }

    public void serveFood(Customer customer) {
        if (customer == null || customer.getOrder() == null || !customer.getOrder().isReady()) {
            throw new IllegalStateException("Cannot serve food before cooking finishes.");
        }
        customer.getOrder().markServed();
        customer.eating();
        assignTask();
        System.out.printf("[Staff] %s served C%d's %s.%n",
                getName(), customer.getId(), customer.getOrder().getItem().getName());
    }
}
