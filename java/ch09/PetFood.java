package ch09;

public class PetFood {
    private String brand;
    private String type; // e.g., Dry, Wet, Treats
    private double weight; // Weight in pounds

    // Constructor
    public PetFood(String brand, String type, double weight) {
        this.brand = brand;
        this.type = type;
        this.weight = weight;
    }

    // Overriding the toString() method
    @Override
    public String toString() {
        return "PetFood{" +
               "brand='" + brand + '\'' +
               ", type='" + type + '\'' +
               ", weight=" + weight +
               '}';
    }
}