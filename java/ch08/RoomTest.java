package ch08;
public class RoomTest {

    public static void main(String[] args) {
        // Define some guests
        Guest dracula = new Guest("Count Dracula", 537, "Vampire", "Blackout curtains for daytime sleep");
        Guest werewolf = new Guest("Larry Talbot", 150, "Werewolf", "Extra-large moonlit room");

        // Creating Room objects
        Room room101 = new Room(101, "Single", 50.0);
        Room room102 = new Room(102, "Double", 75.0);

        // Assigning guests to rooms
        room101.assignGuest(dracula);
        room102.assignGuest(werewolf);

        // Displaying room details with guests
        System.out.println("Room details with guests:");
        room101.displayRoomDetails();
        room102.displayRoomDetails();

        // Vacating a room
        room101.vacateRoom();

        // Displaying updated room details after vacating
        System.out.println("\nUpdated room details after vacating room 101:");
        room101.displayRoomDetails();
    }
}
