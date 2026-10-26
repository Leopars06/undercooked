public class Cashier {

    public void payment(Customer a) {
        if (a == null) {
            System.out.println("Invalid customer.");
            return;
        }

        if (a.getState() == Customer.State.READY_TO_PAY) {
            a.setPaid();
            System.out.println("Payment completed successfully.");
        } else {
            System.out.println("Customer is not ready to pay.");
        }
    }
}
