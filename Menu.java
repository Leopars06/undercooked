import java.util.ArrayList;

public class Menu {
    private ArrayList<Food> foods;

    public Menu() {
        foods = new ArrayList<>();

        foods.add(new Food("Salad", 9.0, 1));
        foods.add(new Food("Burger", 15.0, 1));
        foods.add(new Food("Pasta", 18.0, 2));
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public Food getFood(int index) {
        return foods.get(index);
    }

    public void displayMenu() {
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);

            System.out.println(
                (i + 1) + ". " +
                food.getName() +
                " - $" + food.getPrice()
            );
        }
    }
}
