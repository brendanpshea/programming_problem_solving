package ch07;

import java.util.Scanner;

public class EVEandNumber {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int userNumber;

        // Using do-while loop for input validation
        do {
            System.out.println("EVE asks: Please enter a number between 1 and 10.");
            userNumber = scanner.nextInt();
        } while (userNumber < 1 || userNumber > 10); // Validate the input

        // Using for loop to do something funny
        String message = "EVE says: I love Wall-E";
        for (int i = 1; i <= userNumber; i++) {
            message += "!";
            System.out.println(message);
        }
    }
}