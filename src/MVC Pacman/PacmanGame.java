import javax.swing.*;

/**
 * MAIN CLASS: Entry point for the Pacman game
 * Demonstrates the MVC pattern by:
 * 1. Creating the Model (game state and logic)
 * 2. Creating the View (visual representation)
 * 3. Creating the Controller (input handling and coordination)
 * 4. Wiring them together
 */
public class PacmanGame {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("Pacman - MVC Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        // Create MVC components
        // 1. MODEL: Contains game state and logic
        PacmanModel model = new PacmanModel();
        
        // 2. VIEW: Displays the game
        PacmanView view = new PacmanView(model);
        
        // 3. CONTROLLER: Handles input and coordinates updates
        PacmanController controller = new PacmanController(model, view);
        
        // Add view to frame
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
        
        // Start the game
        controller.startGame();
        
        // Display instructions
        showInstructions();
    }
    
    private static void showInstructions() {
        String instructions = 
            "Pacman Game Controls:\n\n" +
            "Arrow Keys or WASD - Move Pacman\n" +
            "R - Restart game\n" +
            "ESC - Pause/Unpause\n\n" +
            "Objective: Collect all pellets while avoiding ghosts!";
        
        JOptionPane.showMessageDialog(null, instructions, 
            "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
}