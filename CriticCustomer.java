package restraunt;

public class CriticCustomer extends Customer{
    public int amount = 15
    @Override
    public void loseSatisfaction() {
        if (state == State.WAITING || state == State.SEATED) {
            satisfaction = Math.max(0, satisfaction - amount);
            if (satisfaction == 0) {
                state = State.LEFT;
            }
        }
    }
}