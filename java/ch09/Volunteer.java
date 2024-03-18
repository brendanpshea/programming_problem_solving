public class Volunteer {
    private String name;
    private int age;
    private String role;

    // Constructor for name only
    public Volunteer(String name) {
        this.name = name;
    }

    // Constructor for name and age
    public Volunteer(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Constructor for name, age, and role
    public Volunteer(String name, int age, String role) {
        this(name, age); // Calls the constructor for name and age
        this.role = role;
    }

    // Method to display volunteer information
    public void displayInfo() {
        System.out.println("Name: " + name + ", Age: " + age + ", Role: " + role);
    }
}