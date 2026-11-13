public class Food {
    private final String name;
    private final double price;
    private final int defaultPreparationUnits;

    public Food(String name, double price, int defaultPreparationUnits) {
        if (name == null || name.trim().isEmpty() || price < 0 || defaultPreparationUnits < 1) {
            throw new IllegalArgumentException("Food must have a name, valid price, and cook time.");
        }
        this.name = name;
        this.price = price;
        this.defaultPreparationUnits = defaultPreparationUnits;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getDefaultPreparationUnits() { return defaultPreparationUnits; }
}
