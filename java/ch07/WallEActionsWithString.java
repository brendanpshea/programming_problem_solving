import java.util.Scanner;

public class WallEActionsWithString {
    public static void main(String[] args) {
        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Ask the user to enter a command for Wall-E
        System.out.println("Enter a command for Wall-E (explore, idle, recharge): ");

        // Read the command entered by the user
        String actionCommand = scanner.nextLine();

        // Decide Wall-E's actions based on the command
        if (actionCommand.equals("explore")) {
            System.out.println("Wall-E is exploring the area.");
        } else if (actionCommand.equals("idle")) {
            System.out.println("Wall-E is conserving energy and idling.");
        } else if (actionCommand.equals("recharge")) {
            System.out.println("Wall-E is finding a recharge station.");
        } else {
            System.out.println("Invalid command. Wall-E is confused.");
        }
    }
}
