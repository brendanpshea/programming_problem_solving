package ch09;

public class VolunteerTest {
    public static void main(String[] args) {
        Volunteer volunteer1 = new Volunteer("Anaxagoras");
        Volunteer volunteer2 = new Volunteer("Boethius", 25);
        Volunteer volunteer3 = new Volunteer("Chiron", 30, "Dog Walker");

        volunteer1.displayInfo();
        volunteer2.displayInfo();
        volunteer3.displayInfo();
    }
}