import javax.swing.*;
import java.awt.event.*;

/**
 * CONTROLLER: Handles user input and coordinates Model-View interaction
 * - Listens for keyboard input
 * - Updates Model based on input
 * - Triggers View updates
 * - Manages game loop timing
 */
public class PacmanController {
    private PacmanModel model;
    private PacmanView view;
    private Timer gameTimer;
    
    private static final int GAME_SPEED = 200; // milliseconds between updates
    
    public PacmanController(PacmanModel model, PacmanView view) {
        this.model = model;
        this.view = view;
        
        setupKeyboardControls();
        setupGameLoop();
    }
    
    /**
     * Set up keyboard controls for Pacman movement
     */
    private void setupKeyboardControls() {
        view.setFocusable(true);
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }
    
    /**
     * Handle keyboard input and update model accordingly
     */
    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                model.setDirection(0, -1);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                model.setDirection(0, 1);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                model.setDirection(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                model.setDirection(1, 0);
                break;
            case KeyEvent.VK_R:
                // Restart game
                model.reset();
                break;
            case KeyEvent.VK_ESCAPE:
                // Pause/unpause game
                togglePause();
                break;
        }
    }
    
    /**
     * Set up the game loop that updates the model and refreshes the view
     */
    private void setupGameLoop() {
        gameTimer = new Timer(GAME_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update game state
                model.update();
                
                // Refresh the view
                view.repaint();
            }
        });
    }
    
    /**
     * Start the game loop
     */
    public void startGame() {
        gameTimer.start();
        view.requestFocusInWindow();
    }
    
    /**
     * Toggle pause state
     */
    private void togglePause() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }
    
    /**
     * Stop the game loop
     */
    public void stopGame() {
        gameTimer.stop();
    }
}