package ch09;

public class Cat extends Animal {
    // attributes
    private boolean declawed;
  
    // constructor
    public Cat(String name, String breed, int age, boolean declawed) {
      super(name, "cat", breed, age);
      this.declawed = declawed;
    }
  
    // methods
    public boolean isDeclawed() {
      return this.declawed;
    }
  
    public void setDeclawed(boolean declawed) {
      this.declawed = declawed;
    }
  }