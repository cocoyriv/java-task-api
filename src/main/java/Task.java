// File: src/main/java/Task.java
public class Task {
    private int id;
    private String description;
    private boolean isCompleted;

    // Constructor to easily make a new task
    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.isCompleted = false; // Tasks start incomplete by default
    }

    // Getters so Javalin can read the values and turn them into text/JSON
    public int getId() { return id; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return isCompleted; }

    // Setter to change the status later
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
}
