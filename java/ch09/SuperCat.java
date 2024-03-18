public class SuperCat extends SuperPet {

    public SuperCat(String name) {
        super(name, "Super Speed");
    }

    @Override
    public void usePower() {
        System.out.println(getName() + " runs faster than the speed of sound using his " + getSuperPower() + ".");
    }
}
