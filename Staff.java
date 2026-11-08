
public abstract class Staff {
    private final String name;
    private boolean hasTask = false;

    public Staff(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public boolean hasTask() { return hasTask; }
    public void assignTask() { this.hasTask = true; }
    public void resetTask() { this.hasTask = false; }
}
