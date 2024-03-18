public class Dog extends Animal {
    // attributes
    private boolean leashTrained;
  
    // constructor
    public Dog(String name, String breed, int age, boolean leashTrained) {
      super(name, "dog", breed, age);
      this.leashTrained = leashTrained;
    }
  
    // methods
    public boolean isLeashTrained() {
      return this.leashTrained;
    }
  
    public void setLeashTrained(boolean leashTrained) {
      this.leashTrained = leashTrained;
    }
  }