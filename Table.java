public class Table {
    private final int tableId;
    private Customer customer;

    public Table(int tableId) {
        if (tableId < 1) throw new IllegalArgumentException("Invalid table ID.");
        this.tableId = tableId;
    }

    public int getTableId() { return tableId; }
    public Customer getCustomer() { return customer; }
    public boolean isOccupied() { return customer != null; }

    public void seatCustomer(Customer nextCustomer) {
        if (isOccupied() || nextCustomer == null || nextCustomer.getState() != Customer.State.WAITING) {
            throw new IllegalStateException("Table must be empty and customer must be waiting.");
        }
        customer = nextCustomer;
        customer.setSeat();
    }

    public void clearTable() { customer = null; }
}
