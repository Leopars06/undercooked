public class ComboMeal extends Food {
    private static final double COMBO_DISCOUNT = 0.9;
    // discount of a combo meal is 90% of the components’ combined price, rounded to
    // the nearest cent

    private final Food firstItem;
    private final Food secondItem;

    public ComboMeal(Food firstItem, Food secondItem) {
        // 1. Combine names: e.g. "Salad & Pasta Combo"
        // 2. Compute price: (price1 + price2) * 0.9, rounded to nearest cent
        // 3. Compute prep units: units1 + units2
        super( // food's constructor using super to construct meal
                buildComboName(firstItem, secondItem),
                calculateComboPrice(firstItem, secondItem),
                calculateComboPrepUnits(firstItem, secondItem));
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    // helper method to check if the two food entries are valid
    private static void validateItems(Food firstItem, Food secondItem) {
        if (firstItem == null || secondItem == null) {
            throw new IllegalArgumentException("ComboMeal requires two valid Food items.");
        }
    }

    // public Food(String name, double price, int defaultPreparationUnits)

    // name
    private static String buildComboName(Food firstItem, Food secondItem) {
        validateItems(firstItem, secondItem); // check if valid
        return firstItem.getName() + " & " + secondItem.getName() + " Combo";
    }

    // price
    private static double calculateComboPrice(Food firstItem, Food secondItem) {
        validateItems(firstItem, secondItem); // check if valid
        // calculate discounted price
        double discounted = (firstItem.getPrice() + secondItem.getPrice()) * COMBO_DISCOUNT;
        return Math.round(discounted * 100.0) / 100.0; // round off result
    }

    // defaultPreparationUnits
    private static int calculateComboPrepUnits(Food firstItem, Food secondItem) {
        validateItems(firstItem, secondItem); // check if valid
        return firstItem.getDefaultPreparationUnits() + secondItem.getDefaultPreparationUnits();
    }

    public Food getFirstItem() {
        return firstItem;
    }

    public Food getSecondItem() {
        return secondItem;
    }
}
