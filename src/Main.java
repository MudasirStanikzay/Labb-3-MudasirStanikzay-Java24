import java.sql.*;
import java.util.Scanner;

public class Main {

    // Databasanslutning
    public static class Database {
        private static final String URL = "jdbc:sqlite:recept.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL);
        }
    }

    // Lägga till ett recept
    public static void addRecipe(Scanner scanner) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Enter recipe name:");
            String name = scanner.nextLine();
            System.out.println("Enter recipe description:");
            String description = scanner.nextLine();

            String sql = "INSERT INTO recipe (name, description) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.executeUpdate();
                System.out.println("Recipe added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lägga till en ingrediens
    public static void addIngredient(Scanner scanner) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Enter recipe ID:");
            int recipeId = scanner.nextInt();
            scanner.nextLine(); // Ta bort ny rad
            System.out.println("Enter ingredient name:");
            String name = scanner.nextLine();
            System.out.println("Enter ingredient quantity:");
            String quantity = scanner.nextLine();

            String sql = "INSERT INTO ingredient (recipe_id, name, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, recipeId);
                pstmt.setString(2, name);
                pstmt.setString(3, quantity);
                pstmt.executeUpdate();
                System.out.println("Ingredient added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Visa recept och ingredienser
    public static void showRecipesWithIngredients() {
        try (Connection conn = Database.getConnection()) {
            String sql = """
                    SELECT r.name AS recipe_name, r.description AS recipe_description,
                           i.name AS ingredient_name, i.quantity AS ingredient_quantity
                    FROM recipe r
                    LEFT JOIN ingredient i ON r.id = i.recipe_id
                    """;

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String recipeName = rs.getString("recipe_name");
                    String recipeDescription = rs.getString("recipe_description");
                    String ingredientName = rs.getString("ingredient_name");
                    String ingredientQuantity = rs.getString("ingredient_quantity");

                    System.out.println("Recipe: " + recipeName + " - " + recipeDescription);
                    if (ingredientName != null) {
                        System.out.println("  Ingredient: " + ingredientName + " - " + ingredientQuantity);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Uppdatera ett recept
    public static void updateRecipe(Scanner scanner) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Enter recipe ID to update:");
            int id = scanner.nextInt();
            scanner.nextLine(); // Ta bort ny rad
            System.out.println("Enter new recipe name:");
            String name = scanner.nextLine();
            System.out.println("Enter new recipe description:");
            String description = scanner.nextLine();

            String sql = "UPDATE recipe SET name = ?, description = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setInt(3, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Recipe updated successfully!");
                } else {
                    System.out.println("Recipe not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Radera ett recept
    public static void deleteRecipe(Scanner scanner) {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Enter recipe ID to delete:");
            int id = scanner.nextInt();

            String sql = "DELETE FROM recipe WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Recipe deleted successfully!");
                } else {
                    System.out.println("Recipe not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Huvudprogram
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add Recipe");
            System.out.println("2. Add Ingredient");
            System.out.println("3. Show Recipes and Ingredients");
            System.out.println("4. Update Recipe");
            System.out.println("5. Delete Recipe");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Ta bort ny rad

            switch (choice) {
                case 1 -> addRecipe(scanner);
                case 2 -> addIngredient(scanner);
                case 3 -> showRecipesWithIngredients();
                case 4 -> updateRecipe(scanner);
                case 5 -> deleteRecipe(scanner);
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
