import javax.swing.*;
import java.awt.*;

public class BorderExample extends JFrame {
    public BorderExample() {
        setTitle("BorderLayout Demo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        add(new JButton("North"), BorderLayout.NORTH);
        add(new JButton("South"), BorderLayout.SOUTH);
        add(new JButton("East"), BorderLayout.EAST);
        add(new JButton("West"), BorderLayout.WEST);
        add(new JButton("Center"), BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BorderExample());
    }
}
