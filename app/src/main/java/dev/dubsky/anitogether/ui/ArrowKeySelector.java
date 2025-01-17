package dev.dubsky.anitogether.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ArrowKeySelector {

    public static <T> T select(List<T> items, String prompt) throws IOException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("The items list cannot be null or empty.");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println(prompt);
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, items.get(i).toString());
            }

            System.out.print("Enter your selection (1-" + items.size() + "): ");
            String input = reader.readLine();

            try {
                int selection = Integer.parseInt(input) - 1;

                if (selection >= 0 && selection < items.size()) {
                    return items.get(selection);
                } else {
                    System.out.println("Invalid selection. Please select a number between 1 and " + items.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
