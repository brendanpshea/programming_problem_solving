package ch08;
public class Room {
    // Attributes
    private int roomNumber;
    private String type;
    private Guest guest;
    private double nightlyRate;

    // Constructor
    public Room(int roomNumber, String type, double nightlyRate) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.nightlyRate = nightlyRate;
        this.guest = null; // Initially, no guest is occupying the room
    }

    // Methods
    public void assignGuest(Guest guest) {
        this.guest = guest;
    }

    public void vacateRoom() {
        this.guest = null;
    }

    public boolean isOccupied() {
        return this.guest != null;
    }

    // Getter and setter methods
    // I've compressed these
    public int getRoomNumber(){return roomNumber;}
    public void setRoomNumber(int roomNumber){this.roomNumber = roomNumber;}
    public String getType(){return type;}
    public void setType(String type){this.type = type;}
    public double getRate(){return nightlyRate;}
    public void setRate(double rate){nightlyRate = rate;}

     public void displayRoomDetails() {
        System.out.println("Room Number: " + roomNumber);
        System.out.println("Type: " + type);
        if(isOccupied()){
          System.out.println("Occupied by: " + guest.getName());
        } else{
          System.out.println("Vacant.");
        }

        System.out.println("Nightly Rate: $" + nightlyRate);
    }
}