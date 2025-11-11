

import javax.swing.*;
import java.awt.*;

public class GreetingMachine extends JFrame {
    public GreetingMachine() {
        setTitle("Greeting Machine");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        JLabel prompt = new JLabel("Enter your name:");
        JTextField nameField = new JTextField(15);
        JButton greetButton = new JButton("Greet Me!");
        
        add(prompt);
        add(nameField);
        add(greetButton);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GreetingMachine());
    }
}