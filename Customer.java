public class Customer {
    private final int id;
    private CustomerStatus status = CustomerStatus.WAITING;
    private int satisfaction = 100;
    private Order order;

    public Customer(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        this.id = id;
    }

    public int getId() { return id; }
    public CustomerStatus getStatus() { return status; }
    public int getSatisfaction() { return satisfaction; }
    public Order getOrder() { return order; }

    public void setSeat() {
        if (status != CustomerStatus.WAITING) {
            throw new IllegalStateException("Only waiting customers can be seated.");
        }
        status = CustomerStatus.SEATED;
    }

    public void setOrder(Order order) {
        if (status != CustomerStatus.SEATED || this.order != null || order == null || order.getCustomer() != this) {
            throw new IllegalStateException("A seated customer may place one order.");
        }
        this.order = order;
    }

    public void setOrdered() {
        if (status != CustomerStatus.SEATED || order == null) {
            throw new IllegalStateException("The customer must be seated with an order.");
        }
        status = CustomerStatus.ORDERED;
    }

    public void eating() {
        if (status != CustomerStatus.ORDERED) {
            throw new IllegalStateException("Only customers that have ordered can start eating.");
        }
        status = CustomerStatus.EATING;
    }

    public void readyToPay() {
        if (status != CustomerStatus.ORDERED && status != CustomerStatus.EATING) {
            throw new IllegalStateException("Food must be served before payment.");
        }
        status = CustomerStatus.READY_TO_PAY;
    }

    public void setPaid() {
        if (status != CustomerStatus.READY_TO_PAY) {
            throw new IllegalStateException("Customer is not ready to pay.");
        }
        status = CustomerStatus.PAID;
    }
}
