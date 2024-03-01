package ch09;

public class SuperPetTest {

    public static void main(String[] args) {
        SuperDog krypto = new SuperDog("Krypto");
        SuperCat streaky = new SuperCat("Streaky");

        // Demonstrating how each super pet uses their power
        krypto.usePower();
        streaky.usePower();
    }
}