public class Guest {
    // Attributes of the Guest class
    private String name;
    private int age;
    private String species;
    private String specialRequirements;

    // Constructor to initialize the Guest object
    public Guest(String name, int age, String species, String specialRequirements) {
        this.name = name;
        this.age = age;
        this.species = species;
        this.specialRequirements = specialRequirements;
    }

    // Method to return the guest's name
    public String getName() {
        return name;
    }

    // Method to set the guest's name
    public void setName(String name) {
        this.name = name;
    }

    // Method to return the guest's age
    public int getAge() {
        return age;
    }

    // Method to set the guest's age
    public void setAge(int age) {
        this.age = age;
    }

    // Method to return the guest's species
    public String getSpecies() {
        return species;
    }

    // Method to set the guest's species
    public void setSpecies(String species) {
        this.species = species;
    }

    // Method to return the guest's special requirements
    public String getSpecialRequirements() {
        return specialRequirements;
    }

    // Method to set the guest's special requirements
    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    // Method to display guest information
    public void displayGuestInfo() {
        System.out.println("Guest Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Species: " + species);
        System.out.println("Special Requirements: " + specialRequirements);
    }
}