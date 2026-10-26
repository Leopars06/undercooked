public class Table {
    private final int tableId;
    private Customer customer;

    public Table(int tableId) {
        this.tableId = tableId;
    }

    public int getTableId() { return tableId; } 
    public Customer getCustomer() { return customer; }
    public boolean isOccupied() { return customer != null; }

    public void seatCustomer(Customer customer) {
        this.customer = customer;
        if (this.customer != null) {
            this.customer.setStatus(CustomerStatus.SEATED);
        }
    }

    public void clearTable() {
        this.customer = null;
    }
}