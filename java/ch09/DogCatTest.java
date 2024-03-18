public class DogCatTest {
    public static void main(String[] args) {
        // Creating instances of Dog, Cat, and Animal
        Dog krypto = new Dog("Krypto", "Kryptonian Canine", 3, true);
        Cat streaky = new Cat("Streaky", "Domestic Short Hair", 2, false);
        Animal hoppy = new Animal("Hoppy", "Bunny", "Super Leap Bunny", 1);

        // Demonstrating shared methods (getName and getAge)
        System.out.println(krypto.getName() + " is " + krypto.getAge() + " years old and leash trained: " + krypto.isLeashTrained());
        System.out.println(streaky.getName() + " is " + streaky.getAge() + " years old and declawed: " + streaky.isDeclawed());
        System.out.println(hoppy.getName() + " is a " + hoppy.getSpecies() + " and " + hoppy.getAge() + " years old.");

        // Demonstrating unique method calls
        // For Dog: isLeashTrained()
        System.out.println(krypto.getName() + " leash trained status: " + krypto.isLeashTrained());

        // For Cat: isDeclawed()
        System.out.println(streaky.getName() + " declawed status: " + streaky.isDeclawed());

        // Changing attributes using setters
        // Demonstrating shared method (setAge) and showing age update
        krypto.setAge(4); // Krypto's birthday passed
        System.out.println("After a birthday, " + krypto.getName() + " is now " + krypto.getAge() + " years old.");
    }
}
