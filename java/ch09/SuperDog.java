public class SuperDog extends SuperPet {

    public SuperDog(String name) {
        super(name, "Super Strength");
    }

    @Override
    public void usePower() {
        System.out.println(getName() + " lifts heavy objects with ease using his " + getSuperPower() + ".");
    }
}