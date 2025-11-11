import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TraditionalListener extends JFrame {
    private int clickCount = 0;
    
    public TraditionalListener() {
        setTitle("Click Counter");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        JLabel label = new JLabel("Clicks: 0");
        JButton button = new JButton("Click Me!");
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clickCount++;
                label.setText("Clicks: " + clickCount);
            }
        });
        
        add(label);
        add(button);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TraditionalListener());
    }
}