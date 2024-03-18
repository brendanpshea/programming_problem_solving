import java.util.ArrayList;
import java.util.Scanner;

public class HotelManagement {
    private ArrayList<Guest> guests = new ArrayList<>();
    private ArrayList<Room> rooms = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addGuest() {
        System.out.println("Enter guest name:");
        String name = scanner.nextLine();
        System.out.println("Enter guest age:");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter guest species:");
        String species = scanner.nextLine();
        System.out.println("Enter any special requirements:");
        String specialRequirements = scanner.nextLine();

        Guest guest = new Guest(name, age, species, specialRequirements);
        guests.add(guest);
        System.out.println("Guest added successfully.");
    }

    public void addRoom() {
        System.out.println("Enter room number:");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter room type:");
        String type = scanner.nextLine();
        System.out.println("Enter nightly rate:");
        double nightlyRate = Double.parseDouble(scanner.nextLine());

        Room room = new Room(roomNumber, type, nightlyRate);
        rooms.add(room);
        System.out.println("Room added successfully.");
    }

    public void displayGuests() {
        if (guests.isEmpty()) {
            System.out.println("No guests found.");
            return;
        }
        for (Guest guest : guests) {
            guest.displayGuestInfo();
        }
    }

    public void displayRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
            return;
        }
        for (Room room : rooms) {
            room.displayRoomDetails();
        }
    }
}