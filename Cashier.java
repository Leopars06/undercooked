public class Cashier extends Staff {
    public Cashier(String name) {
        super(name);
    }

    public boolean payment(Customer customer) {
        if (customer == null || customer.getState() != Customer.State.READY_TO_PAY) {
            System.out.println("Customer is not ready to pay.");
            return false;
        }
        customer.setPaid();
        assignTask();
        System.out.printf("[Staff] Payment completed for C%d.%n", customer.getId());
        return true;
    }
}
