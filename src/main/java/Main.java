import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        // Starts the web server on port 8080
        var app = Javalin.create().start(8080);

        // Sets up a basic endpoint you can visit
        app.get("/", ctx -> ctx.result("Hello from your future Debian Task API!"));
    }
}
