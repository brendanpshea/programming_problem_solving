import java.util.*;

/**
 * MODEL: Contains all game state and business logic
 * - Pacman position and direction
 * - Ghost positions
 * - Maze layout
 * - Pellets and score
 * - Game logic (movement, collisions, win/lose conditions)
 */
public class PacmanModel {
    // Grid dimensions
    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 20;
    
    // Game entities
    private int pacmanX, pacmanY;
    private int directionX, directionY;
    private List<Ghost> ghosts;
    private boolean[][] walls;
    private boolean[][] pellets;
    private int score;
    private boolean gameOver;
    private boolean gameWon;
    
    // Inner class for Ghost
    public static class Ghost {
        public int x, y;
        public int dirX, dirY;
        
        public Ghost(int x, int y) {
            this.x = x;
            this.y = y;
            this.dirX = 1;
            this.dirY = 0;
        }
    }
    
    public PacmanModel() {
        initializeGame();
    }
    
    private void initializeGame() {
        // Initialize Pacman at center
        pacmanX = GRID_WIDTH / 2;
        pacmanY = GRID_HEIGHT / 2;
        directionX = 0;
        directionY = 0;
        
        // Initialize ghosts
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(3, 3));
        ghosts.add(new Ghost(GRID_WIDTH - 4, 3));
        ghosts.add(new Ghost(3, GRID_HEIGHT - 4));
        
        // Initialize maze (simple border + a few obstacles)
        walls = new boolean[GRID_HEIGHT][GRID_WIDTH];
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                // Border walls
                if (i == 0 || i == GRID_HEIGHT - 1 || j == 0 || j == GRID_WIDTH - 1) {
                    walls[i][j] = true;
                }
                // Some internal walls for variety
                if ((i == 5 || i == 15) && j > 5 && j < 15) {
                    walls[i][j] = true;
                }
                if ((j == 5 || j == 15) && i > 8 && i < 12) {
                    walls[i][j] = true;
                }
            }
        }
        
        // Initialize pellets (all non-wall spaces except Pacman's starting position)
        pellets = new boolean[GRID_HEIGHT][GRID_WIDTH];
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                pellets[i][j] = !walls[i][j] && !(i == pacmanY && j == pacmanX);
            }
        }
        
        score = 0;
        gameOver = false;
        gameWon = false;
    }
    
    // Update game state
    public void update() {
        if (gameOver || gameWon) return;
        
        // Move Pacman
        int newX = pacmanX + directionX;
        int newY = pacmanY + directionY;
        
        // Check wall collision
        if (!isWall(newX, newY)) {
            pacmanX = newX;
            pacmanY = newY;
            
            // Check pellet collection
            if (pellets[pacmanY][pacmanX]) {
                pellets[pacmanY][pacmanX] = false;
                score += 10;
                
                // Check win condition (all pellets collected)
                if (allPelletsCollected()) {
                    gameWon = true;
                }
            }
        }
        
        // Move ghosts
        for (Ghost ghost : ghosts) {
            moveGhost(ghost);
        }
        
        // Check ghost collision
        for (Ghost ghost : ghosts) {
            if (ghost.x == pacmanX && ghost.y == pacmanY) {
                gameOver = true;
            }
        }
    }
    
    private void moveGhost(Ghost ghost) {
        // Simple AI: try to move toward Pacman, but random on collision
        int newX = ghost.x + ghost.dirX;
        int newY = ghost.y + ghost.dirY;
        
        // If ghost hits a wall or 20% random chance, pick a new direction
        if (isWall(newX, newY) || Math.random() < 0.2) {
            // Try to move toward Pacman
            List<int[]> directions = new ArrayList<>();
            if (pacmanX > ghost.x) directions.add(new int[]{1, 0});
            if (pacmanX < ghost.x) directions.add(new int[]{-1, 0});
            if (pacmanY > ghost.y) directions.add(new int[]{0, 1});
            if (pacmanY < ghost.y) directions.add(new int[]{0, -1});
            
            // Add random directions for variety
            directions.add(new int[]{1, 0});
            directions.add(new int[]{-1, 0});
            directions.add(new int[]{0, 1});
            directions.add(new int[]{0, -1});
            
            // Try directions until we find a valid one
            Collections.shuffle(directions);
            for (int[] dir : directions) {
                if (!isWall(ghost.x + dir[0], ghost.y + dir[1])) {
                    ghost.dirX = dir[0];
                    ghost.dirY = dir[1];
                    break;
                }
            }
        }
        
        // Move ghost
        newX = ghost.x + ghost.dirX;
        newY = ghost.y + ghost.dirY;
        if (!isWall(newX, newY)) {
            ghost.x = newX;
            ghost.y = newY;
        }
    }
    
    private boolean allPelletsCollected() {
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                if (pellets[i][j]) return false;
            }
        }
        return true;
    }
    
    // Setters for user input
    public void setDirection(int dx, int dy) {
        this.directionX = dx;
        this.directionY = dy;
    }
    
    // Getters for View
    public int getPacmanX() { return pacmanX; }
    public int getPacmanY() { return pacmanY; }
    public List<Ghost> getGhosts() { return ghosts; }
    public boolean isWall(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) return true;
        return walls[y][x];
    }
    public boolean isPellet(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) return false;
        return pellets[y][x];
    }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isGameWon() { return gameWon; }
    
    public void reset() {
        initializeGame();
    }
}