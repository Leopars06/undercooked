public class Customer {
    public enum State {
        WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT
    }

    private final int id;
    private State state = State.WAITING;
    private int satisfaction = 100;
    private Order order;

    public Customer(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        this.id = id;
    }

    public int getId() { return id; }
    public State getState() { return state; }
    public int getSatisfaction() { return satisfaction; }
    public Order getOrder() { return order; }

    public void setSeat() {
        if (state != State.WAITING) {
            throw new IllegalStateException("Only waiting customers can be seated.");
        }
        state = State.SEATED;
    }

    public void setOrder(Order order) {
        if (state != State.SEATED || this.order != null || order == null || order.getCustomer() != this) {
            throw new IllegalStateException("A seated customer may place one order.");
        }
        this.order = order;
    }

    public void setOrdered() {
        if (state != State.SEATED || order == null) {
            throw new IllegalStateException("The customer must be seated with an order.");
        }
        state = State.ORDERED;
    }


    public void eating() {
        if (state != State.ORDERED) {
            throw new IllegalStateException("Only ordered customers can start eating.");
        }
        state = State.EATING;
    }

    public void readyToPay() {
        if (state != State.ORDERED && state != State.EATING) {
            throw new IllegalStateException("Food must be served before payment.");
        }
        state = State.READY_TO_PAY;
    }

    public void setPaid() {
        if (state != State.READY_TO_PAY) {
            throw new IllegalStateException("Customer is not ready to pay.");
        }
        state = State.PAID;
    }
    public abstract void loseSatisfaction();
}
