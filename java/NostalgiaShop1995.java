public class NostalgiaShop1995 {
    public static void main(String[] args) {
        // Print a welcome message
        System.out.println("Welcome to The Nostalgia Shop: Class of '95!");

        // Announce today's special item
        System.out.print("Today's special is ");
        System.out.print("'Jagged Little Pill' by Alanis Morissette.\n");

        // Greet a specific customer
        String customerName = "Brendan";
        System.out.println("Hi, " + customerName + "! Your order is ready.");

        // Show the price of a chosen item
        String itemChoice = "Toy Story DVD";
        float price = 19.95f;
        System.out.printf("The price of your %s is $%.2f.\n", itemChoice, price);

        // Sum up the experience
        System.out.print("Thanks for stopping by, " + customerName + ". ");
        System.out.printf("Your total is $%.2f. ", price);
        System.out.println("Enjoy your trip down memory lane!");
    }
}
