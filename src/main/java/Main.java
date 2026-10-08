// File: src/main/java/Main.java
import io.javalin.Javalin;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // An in-memory list to temporarily store our tasks
    private static final List<Task> taskList = new ArrayList<>();
    private static int idCounter = 1;

    public static void main(String[] args) {
        // Start Javalin on port 8080 inside the container
        var app = Javalin.create().start(8080);

        // 1. GET Endpoint: See all tasks
        app.get("/tasks", ctx -> {
            ctx.json(taskList); // Javalin automatically turns our Java objects into clean text/JSON!
        });

        // 2. POST Endpoint: Add a new task
        // You will send a query parameter like: /tasks?desc=Buy milk
        app.post("/tasks", ctx -> {
            String description = ctx.queryParam("desc");
            
            if (description == null || description.trim().isEmpty()) {
                ctx.status(400).result("Error: 'desc' parameter is missing!");
                return;
            }

            Task newTask = new Task(idCounter++, description);
            taskList.add(newTask);
            ctx.status(201).json(newTask); // Return the newly created task
        });
    }
}
