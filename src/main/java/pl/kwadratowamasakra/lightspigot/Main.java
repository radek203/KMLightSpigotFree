package pl.kwadratowamasakra.lightspigot;

/**
 * The Main class is the entry point of the application.
 * It contains the main method which starts the LightSpigotServer.
 */
public class Main {

    /**
     * The main method is the entry point of the application.
     * It creates a new instance of the LightSpigotServer and starts it.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(final String[] args) {
        new LightSpigotServer().start();
    }

}