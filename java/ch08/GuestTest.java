public class GuestTest {
    public static void main(String[] args) {
        // Creating an object for Dracula
        Guest dracula = new Guest("Count Dracula", 537, "Vampire", "Blackout curtains for daytime sleep");
        // Suppose Dracula decides to update his special requirements
        dracula.setSpecialRequirements("Blackout curtains and soundproof walls");

        // Creating an object for a Werewolf
        Guest werewolf = new Guest("Larry Talbot", 150, "Werewolf", "Extra-large moonlit room");
        // Checking Larry's species
        System.out.println(werewolf.getName() + " is a " + werewolf.getSpecies());

        // Creating an object for a Frankenstein's Monster
        Guest frankenstein = new Guest("Frankie Stein", 200, "Monster", "Electric recharge station");
        // Frankie decides to change his name to "Frank"
        frankenstein.setName("Frank");

        // Display information for each guest
        dracula.displayGuestInfo();
        werewolf.displayGuestInfo();
        frankenstein.displayGuestInfo();
    }
}