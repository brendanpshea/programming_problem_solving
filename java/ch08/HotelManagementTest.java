import java.util.Scanner;

public class HotelManagementTest{
  public static void main(String[] args) {
        HotelManagement hotelManagement = new HotelManagement();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an action: \n1. Add Guest\n2. Add Room\n3. Display Guests\n4. Display Rooms\n5. Exit");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1){hotelManagement.addGuest();}
            else if (choice == 2){hotelManagement.addRoom();}
            else if (choice == 3){hotelManagement.displayGuests();}
            else if (choice == 4){hotelManagement.displayRooms();}
            else if (choice == 5){
              System.out.println("Exiting...");
              return;
            }
            else{
              System.out.println("Invalid choice. Please try again.");
            }

        }
    }
}