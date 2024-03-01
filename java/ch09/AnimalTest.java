package ch09;

public class AnimalTest {
    public static void main(String[] args) {
      // create an object of the Animal class
      Animal myAnimal = new Animal("Fluffy", "cat", "Persian", 3);
  
      // call the getName() method on the object
      String name = myAnimal.getName();
      System.out.println("The animal's name is: " + name);
    }
  }