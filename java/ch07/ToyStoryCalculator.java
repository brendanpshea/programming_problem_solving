import java.util.Scanner;

public class ToyStoryCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Woody discovers a calculator in Andy's room
        System.out.println("Howdy, partner! Woody here. We found a calculator in Andy's drawer. What should we do?");

        // Woody prompts for the first number
        System.out.println("Woody says: Gimme the first number, will ya?");
        float num1 = scanner.nextFloat();

        // Woody prompts for the second number
        System.out.println("Woody tips his hat: And what's the second number, partner?");
        float num2 = scanner.nextFloat();

        // Woody performs calculations
        float sum = num1 + num2;
        float difference = num1 - num2;
        float product = num1 * num2;
        float quotient = num1 / num2;
        float remainder = num1 % num2;

        // Woody shares the results
        System.out.println("Woody lassos the answers: Sum's " + sum + ", yeehaw!");
        System.out.println("Woody twirls his lasso: Difference is " + difference);
        System.out.println("Woody does a cowboy dance: Product's " + product);
        System.out.println("Woody strums his guitar: Quotient is " + quotient);
        System.out.println("Woody tips his hat again: And the remainder's " + remainder);

        // Woody says goodbye
        System.out.println("Woody waves: Thanks for calculatin' with us. See ya, partner!");
    }
}
