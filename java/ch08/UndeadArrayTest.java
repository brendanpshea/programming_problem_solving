import java.util.ArrayList;

public class UndeadArrayTest {

    public static void main(String[] args) {
        // Initializing and populating a fixed-size array of undead guests
        String[] undeadGuests = {"Vampire", "Zombie", "Ghost", "Mummy", "Lich"};
        System.out.println("Fixed-size Undead Guests Array:");
        for (String guest : undeadGuests) {
            System.out.println(guest);
        }

        // Demonstrating a dynamic ArrayList of undead guests
        ArrayList<String> dynamicUndeadGuests = new ArrayList<>();
        dynamicUndeadGuests.add("Vampire");
        dynamicUndeadGuests.add("Zombie");
        dynamicUndeadGuests.add("Ghost");
        dynamicUndeadGuests.add("Mummy");
        dynamicUndeadGuests.add("Lich");

        // Adding a new guest to the ArrayList
        dynamicUndeadGuests.add("Skeleton");
        System.out.println("\nDynamic Undead Guests ArrayList after addition:");
        for (String guest : dynamicUndeadGuests) {
            System.out.println(guest);
        }

        // Removing a guest from the ArrayList
        dynamicUndeadGuests.remove("Zombie");
        System.out.println("\nDynamic Undead Guests ArrayList after removal:");
        for (String guest : dynamicUndeadGuests) {
            System.out.println(guest);
        }

        // Accessing a specific guest in the ArrayList
        System.out.println("\nThe guest in room 3 is now a " + dynamicUndeadGuests.get(2));
    }
}
