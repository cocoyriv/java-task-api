// File: src/main/java/Main.java
import io.javalin.Javalin;

//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    // An in-memory list to temporarily store our tasks
    //private static final List<Task> taskList = new ArrayList<>();
    //private static int idCounter = 1;

    // The database file will be saved inside a folder named 'data'
    private static final String DB_URL = "jdbc:sqlite:data/tasks.db";

    public static void main(String[] args) {
        // 1. Initialize the database table when the app starts
        createNewTable();

        // Start Javalin on port 8080 inside the container
        var app = Javalin.create().start(8080);

        // GET Endpoint: Fetch tasks directly from the SQLite database
        app.get("/tasks", ctx -> {
            //ctx.json(taskList); // Javalin automatically turns our Java objects into clean text/JSON!
            ctx.json(getAllTasks());
        });

        // 2. POST Endpoint: Add a new task
        // You will send a query parameter like: /tasks?desc=Buy milk
        app.post("/tasks", ctx -> {
            String description = ctx.queryParam("desc");
            
            if (description == null || description.trim().isEmpty()) {
                ctx.status(400).result("Error: 'desc' parameter is missing!");
                return;
            }

            //Task newTask = new Task(idCounter++, description);
            //taskList.add(newTask);
            //ctx.status(201).json(newTask); // Return the newly created task

            int generatedId = insertTask(description);
            ctx.status(201).json(new Task(generatedId, description));
        });
    }

     // Connects to SQLite and creates the table if it is missing
    private static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks ("
                   + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + " description TEXT NOT NULL,"
                   + " is_completed INTEGER DEFAULT 0"
                   + ");";
        
        // Ensure the 'data' directory exists so SQLite can write to it
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Database init error: " + e.getMessage());
        }
    }

    // Helper method to insert a task and get its unique database ID back
    private static int insertTask(String description) {
        String sql = "INSERT INTO tasks(description) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, description);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
        return -1;
    }

    // Helper method to pull all records from the database file
    private static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, description, is_completed FROM tasks";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task task = new Task(rs.getInt("id"), rs.getString("description"));
                task.setCompleted(rs.getInt("is_completed") == 1);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Fetch error: " + e.getMessage());
        }
        return tasks;
    }
}
