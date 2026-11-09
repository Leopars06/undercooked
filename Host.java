public class Host extends Staff { // host is part of staff
    // host seats at most one queued customer per turn, in addition to waiter's task
    // waiter can also still seat customers
    public Host(String name) {
        super(name); // call parent constructor
    }

    public void seatCustomers(Table table, Customer customer) {
        table.seatCustomer(customer); // seat customer
        assignTask(); // assign task
        System.out.printf("%s seated C%d at Table %d.%n", getName(), customer.getId(), table.getTableId());
    }
}
