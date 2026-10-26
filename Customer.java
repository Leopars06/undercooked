public class Customer {

    // Enum to represent the customer's current state
    public enum State {
        WAITING,
        SEATED,
        ORDERED,
        EATING,
        READY_TO_PAY,
        PAID,
        LEFT
    }

    // Variables
    private State state = State.WAITING;
    private int satisfaction = 100;

    // Set customer state to waiting
    public void setWait() {
        state = State.WAITING;
    }

    // Set customer state to seated
    public void setSeat() {
        state = State.SEATED;
    }

    // Set customer state to eating after being served
    public void eating() {
        state = State.EATING;
    }

    // Set customer state to ready to pay
    public void readyToPay() {
        state = State.READY_TO_PAY;
    }

    // Set customer state to paid
    public void setPaid() {
        state = State.PAID;
    }

    // Reduce satisfaction while waiting or seated but not served
    public void loseSatisfaction(int amount) {
        if (state == State.WAITING || state == State.SEATED) {
            satisfaction = Math.max(0, satisfaction - amount);

            // Customer leaves if satisfaction reaches zero
            if (satisfaction == 0) {
                state = State.LEFT;
            }
        }
    }

    // Get the current state
    public State getState() {
        return state;
    }

    // Get the current satisfaction level
    public int getSatisfaction() {
        return satisfaction;
    }
}
