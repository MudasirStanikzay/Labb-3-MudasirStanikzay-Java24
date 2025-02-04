import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Connect to the SQLite database
        String url = "jdbc:sqlite:recipes.db";
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                System.out.println("Connected to the database.");
                createTables(connection); // Ensure the tables are created
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    System.out.println("1. Add Recipe");
                    System.out.println("2. Add Ingredient");
                    System.out.println("3. Show Recipes and Ingredients");
                    System.out.println("4. Update Recipe");
                    System.out.println("5. Delete Recipe");
                    System.out.println("6. Update Ingredient");
                    System.out.println("7. Delete Ingredient");
                    System.out.println("8. Exit");
                    System.out.print("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character

                    switch (choice) {
                        case 1:
                            addRecipe(connection);
                            break;
                        case 2:
                            addIngredient(connection);
                            break;
                        case 3:
                            showRecipesAndIngredients(connection);
                            break;
                        case 4:
                            updateRecipe(connection);
                            break;
                        case 5:
                            deleteRecipe(connection);
                            break;
                        case 6:
                            updateIngredient(connection);
                            break;
                        case 7:
                            deleteIngredient(connection);
                            break;
                        case 8:
                            System.out.println("Exiting...");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Method to create tables if they don't exist
    public static void createTables(Connection connection) {
        try {
            // Drop tables if they exist
            String dropIngredientsTable = "DROP TABLE IF EXISTS ingredients";
            String dropRecipeTable = "DROP TABLE IF EXISTS recipe";

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(dropIngredientsTable);
                stmt.execute(dropRecipeTable);
            }

            // Create tables
            String createRecipeTable = "CREATE TABLE recipe (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT)";
            String createIngredientsTable = "CREATE TABLE ingredients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "recipe_id INTEGER, " +
                    "ingredient TEXT, " +
                    "quantity TEXT, " +
                    "FOREIGN KEY(recipe_id) REFERENCES recipe(id))";

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createRecipeTable);
                stmt.execute(createIngredientsTable);
                System.out.println("Database tables created successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Add a new recipe
    public static void addRecipe(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter recipe name: ");
            String name = scanner.nextLine();
            System.out.print("Enter recipe description: ");
            String description = scanner.nextLine();

            String sql = "INSERT INTO recipe (name, description) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.executeUpdate();
                System.out.println("Recipe added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Add a new ingredient
    public static void addIngredient(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter recipe ID: ");
            int recipeId = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            System.out.print("Enter ingredient name: ");
            String ingredient = scanner.nextLine();
            System.out.print("Enter ingredient quantity: ");
            String quantity = scanner.nextLine();

            String sql = "INSERT INTO ingredients (recipe_id, ingredient, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, recipeId);
                pstmt.setString(2, ingredient);
                pstmt.setString(3, quantity);
                pstmt.executeUpdate();
                System.out.println("Ingredient added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Show recipes and ingredients
    public static void showRecipesAndIngredients(Connection connection) {
        try {
            String sqlRecipes = "SELECT * FROM recipe";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlRecipes)) {
                while (rs.next()) {
                    int recipeId = rs.getInt("id");
                    String recipeName = rs.getString("name");
                    String recipeDescription = rs.getString("description");

                    System.out.println("Recipe: " + recipeName + " - " + recipeDescription);

                    // Show ingredients for this recipe
                    String sqlIngredients = "SELECT * FROM ingredients WHERE recipe_id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlIngredients)) {
                        pstmt.setInt(1, recipeId);
                        try (ResultSet rsIngredients = pstmt.executeQuery()) {
                            while (rsIngredients.next()) {
                                String ingredient = rsIngredients.getString("ingredient");
                                String quantity = rsIngredients.getString("quantity");
                                System.out.println("\tIngredient: " + ingredient + " - " + quantity);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Update a recipe
    public static void updateRecipe(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter recipe ID to update: ");
            int recipeId = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            System.out.print("Enter new recipe name: ");
            String newName = scanner.nextLine();
            System.out.print("Enter new recipe description: ");
            String newDescription = scanner.nextLine();

            String sql = "UPDATE recipe SET name = ?, description = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setString(2, newDescription);
                pstmt.setInt(3, recipeId);
                pstmt.executeUpdate();
                System.out.println("Recipe updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Delete a recipe
    public static void deleteRecipe(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter recipe ID to delete: ");
            int recipeId = scanner.nextInt();

            // First, delete the ingredients for this recipe
            String deleteIngredients = "DELETE FROM ingredients WHERE recipe_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteIngredients)) {
                pstmt.setInt(1, recipeId);
                pstmt.executeUpdate();
            }

            // Now, delete the recipe itself
            String deleteRecipe = "DELETE FROM recipe WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteRecipe)) {
                pstmt.setInt(1, recipeId);
                pstmt.executeUpdate();
                System.out.println("Recipe deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Update an ingredient
    public static void updateIngredient(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter ingredient ID to update: ");
            int ingredientId = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            System.out.print("Enter new ingredient name: ");
            String newIngredient = scanner.nextLine();
            System.out.print("Enter new ingredient quantity: ");
            String newQuantity = scanner.nextLine();

            String sql = "UPDATE ingredients SET ingredient = ?, quantity = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, newIngredient);
                pstmt.setString(2, newQuantity);
                pstmt.setInt(3, ingredientId);
                pstmt.executeUpdate();
                System.out.println("Ingredient updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Delete an ingredient
    public static void deleteIngredient(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter ingredient ID to delete: ");
            int ingredientId = scanner.nextInt();

            String sql = "DELETE FROM ingredients WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, ingredientId);
                pstmt.executeUpdate();
                System.out.println("Ingredient deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}