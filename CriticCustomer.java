package restraunt;

public class CriticCustomer extends Customer{
    public int amount = 12;
    @Override
    public void loseSatisfaction() {
        if (amount < 0) {
            throw new IllegalArgumentException("Satisfaction loss cannot be negative.");
        }
        // Seated, Waiting, and Ordered customers all decay over turns
        if (status == CustomerStatus.WAITING || status == CustomerStatus.SEATED || status == CustomerStatus.ORDERED) {
            satisfaction = Math.max(0, satisfaction - amount);
            if (satisfaction == 0) {
                status = CustomerStatus.LEFT_UNHAPPY;
            }
        }
    }
}