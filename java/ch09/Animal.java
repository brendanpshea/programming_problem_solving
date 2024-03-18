public class Animal {
  private String name, species, breed;
  private int age;

  public Animal(String name, String species, String breed, int age) {
    this.name = name;
    this.species = species;
    this.breed = breed;
    this.age = age;
  }

  public String getName() { return this.name; }
  public void setName(String name) { this.name = name; }

  public String getSpecies() { return this.species; }
  public void setSpecies(String species) { this.species = species; }

  public String getBreed() { return this.breed; }
  public void setBreed(String breed) { this.breed = breed; }

  public int getAge() { return this.age; }
  public void setAge(int age) { this.age = age; }
}