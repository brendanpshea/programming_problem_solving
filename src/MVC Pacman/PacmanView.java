import javax.swing.*;
import java.awt.*;

/**
 * VIEW: Handles all rendering and display
 * - Draws the game board
 * - Renders Pacman, ghosts, walls, and pellets
 * - Displays score and game status
 * Does NOT contain game logic or handle input directly
 */
public class PacmanView extends JPanel {
    private PacmanModel model;
    private static final int CELL_SIZE = 30;
    
    // Colors for game elements
    private static final Color WALL_COLOR = new Color(33, 33, 222);
    private static final Color PACMAN_COLOR = Color.YELLOW;
    private static final Color GHOST_COLOR = Color.RED;
    private static final Color PELLET_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    
    public PacmanView(PacmanModel model) {
        this.model = model;
        setPreferredSize(new Dimension(
            PacmanModel.GRID_WIDTH * CELL_SIZE,
            PacmanModel.GRID_HEIGHT * CELL_SIZE + 50 // Extra space for score
        ));
        setBackground(BACKGROUND_COLOR);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the maze
        drawMaze(g);
        
        // Draw pellets
        drawPellets(g);
        
        // Draw Pacman
        drawPacman(g);
        
        // Draw ghosts
        drawGhosts(g);
        
        // Draw score
        drawScore(g);
        
        // Draw game over / win message
        drawGameStatus(g);
    }
    
    private void drawMaze(Graphics g) {
        g.setColor(WALL_COLOR);
        for (int y = 0; y < PacmanModel.GRID_HEIGHT; y++) {
            for (int x = 0; x < PacmanModel.GRID_WIDTH; x++) {
                if (model.isWall(x, y)) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
    
    private void drawPellets(Graphics g) {
        g.setColor(PELLET_COLOR);
        for (int y = 0; y < PacmanModel.GRID_HEIGHT; y++) {
            for (int x = 0; x < PacmanModel.GRID_WIDTH; x++) {
                if (model.isPellet(x, y)) {
                    int centerX = x * CELL_SIZE + CELL_SIZE / 2;
                    int centerY = y * CELL_SIZE + CELL_SIZE / 2;
                    g.fillOval(centerX - 3, centerY - 3, 6, 6);
                }
            }
        }
    }
    
    private void drawPacman(Graphics g) {
        g.setColor(PACMAN_COLOR);
        int x = model.getPacmanX() * CELL_SIZE;
        int y = model.getPacmanY() * CELL_SIZE;
        
        // Draw Pacman as a filled circle with a mouth
        g.fillArc(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 45, 270);
    }
    
    private void drawGhosts(Graphics g) {
        g.setColor(GHOST_COLOR);
        for (PacmanModel.Ghost ghost : model.getGhosts()) {
            int x = ghost.x * CELL_SIZE;
            int y = ghost.y * CELL_SIZE;
            
            // Draw ghost as a rounded rectangle with wavy bottom
            g.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 10, 10);
            
            // Add eyes
            g.setColor(Color.WHITE);
            g.fillOval(x + 8, y + 8, 6, 6);
            g.fillOval(x + CELL_SIZE - 14, y + 8, 6, 6);
            g.setColor(Color.BLUE);
            g.fillOval(x + 10, y + 10, 3, 3);
            g.fillOval(x + CELL_SIZE - 12, y + 10, 3, 3);
            
            g.setColor(GHOST_COLOR);
        }
    }
    
    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int scoreY = PacmanModel.GRID_HEIGHT * CELL_SIZE + 30;
        g.drawString("Score: " + model.getScore(), 10, scoreY);
    }
    
    private void drawGameStatus(Graphics g) {
        if (model.isGameOver()) {
            drawCenteredMessage(g, "GAME OVER! Press R to restart", Color.RED);
        } else if (model.isGameWon()) {
            drawCenteredMessage(g, "YOU WIN! Press R to restart", Color.GREEN);
        }
    }
    
    private void drawCenteredMessage(Graphics g, String message, Color color) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), PacmanModel.GRID_HEIGHT * CELL_SIZE);
        
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = (PacmanModel.GRID_HEIGHT * CELL_SIZE) / 2;
        g.drawString(message, x, y);
    }
}