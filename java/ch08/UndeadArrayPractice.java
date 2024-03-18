import java.util.ArrayList;
public class UndeadArrayPractice {

    public static void main(String[] args) {

        // TODO: Construct an array of Strings containing 3 undead creatures
        String[] undeadCreatures = ?

        // TODO: Construct an array of integers containing the ages of the undead creatures
        int[] undeadAges = ?

        // TODO: Construct an ArrayList of Strings containing 3 different undead creatures
        ArrayList<String> undeadCreaturesList = ?

        // TODO: Construct an ArrayList of booleans indicating if the undead creatures are friendly or not
        ArrayList<Boolean> undeadFriendlyList = ?

        // Print the elements of the arrays and ArrayLists
        System.out.println("Undead Creatures Array: ");
        for (String creature : undeadCreatures) {
            System.out.println(creature);
        }
        System.out.println("Undead Ages Array: ");
        for (int age : undeadAges) {
            System.out.println(age);
        }
        System.out.println("Undead Creatures ArrayList: ");
        for (String creature : undeadCreaturesList) {
            System.out.println(creature);
        }

        System.out.println("Undead Friendly ArrayList: ");
        for (boolean friendly : undeadFriendlyList) {
            System.out.println(friendly);
        }
    }
}