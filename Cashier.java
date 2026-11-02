public class Cashier {
    public boolean payment(Customer customer) {
        if (customer == null || customer.getState() != Customer.State.READY_TO_PAY) {
            System.out.println("Customer is not ready to pay.");
            return false;
        }
        customer.setPaid();
        System.out.printf("Payment completed for C%d.%n", customer.getId());
        return true;
    }
}
