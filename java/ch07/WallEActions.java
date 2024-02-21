package ch07;

import java.util.Scanner;

public class WallEActions {
    public static void main(String[] args) {
        // Create a Scanner object to get user input
        Scanner scanner = new Scanner(System.in);

        // Ask the user to enter Wall-E's current battery level
        System.out.println("Enter Wall-E's current battery level (0 to 100): ");

        // Read the battery level entered by the user
        int batteryLevel = scanner.nextInt();

        // Check Wall-E's actions based on battery level
        if (batteryLevel > 80) {
            System.out.println("Explore the area.");
        } else if (batteryLevel >= 30) {
            System.out.println("Conserve energy and idle.");
        } else {
            System.out.println("Low battery! Find a recharge station.");
        }
    }
}