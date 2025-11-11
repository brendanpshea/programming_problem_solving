import javax.swing.*;
import java.awt.*;

public class PanelExample extends JFrame {
    public PanelExample() {
        setTitle("Panel Demo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create a panel for input fields (vertical)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(new JTextField(20));
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(new JTextField(20));
        
        // Create a panel for buttons (horizontal)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Submit"));
        buttonPanel.add(new JButton("Clear"));
        buttonPanel.add(new JButton("Cancel"));
        
        // Add panels to frame
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PanelExample());
    }
}