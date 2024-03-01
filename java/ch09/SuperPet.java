package ch09;

public abstract class SuperPet {
    protected String name;
    protected String superPower;

    public SuperPet(String name, String superPower) {
        this.name = name;
        this.superPower = superPower;
    }

    public String getName() {
        return name;
    }

    public String getSuperPower() {
        return superPower;
    }

    // Abstract method that subclasses must implement
    public abstract void usePower();
}
