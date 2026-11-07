import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*use the working food class*/
public class Menu {
    private final List<Food> foods = new ArrayList<>();

    public Menu() {
        // retain the three original dishes, prices, and preparation times.
        foods.add(new Food("Salad \uD83E\uDD57", 9.00, 1));
        foods.add(new Food("Burger \uD83C\uDF54", 15.00, 1));
        foods.add(new Food("Pasta \uD83C\uDF5D", 18.00, 2));
    }

    public List<Food> getFoods() { return Collections.unmodifiableList(foods); }
    public Food getFood(int index) { return foods.get(index); }

    public void displayMenu() {
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            System.out.printf("%d. %s - $%.2f (%d cook turn%s)%n",
                    i + 1, food.getName(), food.getPrice(),
                    food.getDefaultPreparationUnits(),
                    food.getDefaultPreparationUnits() == 1 ? "" : "s");
        }
    }
}
