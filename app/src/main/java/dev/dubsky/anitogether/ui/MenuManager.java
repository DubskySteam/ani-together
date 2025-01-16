package dev.dubsky.anitogether.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class MenuManager {

    private static final Scanner scanner = new Scanner(System.in);

    private final Map<String, MenuOption> options = new HashMap<>();
    private final String title;

    public MenuManager(String title) {
        this.title = title;
    }

    public void addOption(String key, String description, Consumer<Void> action) {
        options.put(key, new MenuOption(description, action));
    }

    public void display() {
        System.out.println(Color.CYAN + "\n=== " + title + " ===" + Color.RESET);
        for (Map.Entry<String, MenuOption> entry : options.entrySet()) {
            System.out.println(Color.GREEN + entry.getKey() + ". " + entry.getValue().getDescription() + Color.RESET);
        }
        System.out.print(Color.YELLOW + "Choose an option: " + Color.RESET);
    }

    public void executeChoice(String choice) {
        MenuOption option = options.get(choice);
        if (option != null) {
            try {
                option.getAction().accept(null);
            } catch (Exception e) {
                System.out.println(Color.RED + "Error executing option: " + e.getMessage() + Color.RESET);
            }
        } else {
            System.out.println(Color.RED + "Invalid option. Try again." + Color.RESET);
        }
    }

    public String getUserInput() {
        return scanner.nextLine().trim();
    }

    private static class MenuOption {
        private final String description;
        private final Consumer<Void> action;

        public MenuOption(String description, Consumer<Void> action) {
            this.description = description;
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public Consumer<Void> getAction() {
            return action;
        }
    }
}
