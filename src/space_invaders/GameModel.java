import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Model: all game state and rules. No Swing imports.
public class GameModel {

    // --- constants ---
    static final int WIDTH        = 600;
    static final int HEIGHT       = 650;
    static final int PLAYER_W     = 40;
    static final int PLAYER_H     = 20;
    static final int PLAYER_SPEED = 6;
    static final int ALIEN_COLS   = 11;
    static final int ALIEN_ROWS   = 5;
    static final int ALIEN_W      = 36;
    static final int ALIEN_H      = 24;
    static final int ALIEN_H_GAP  = 16;
    static final int ALIEN_V_GAP  = 12;
    static final int BULLET_W     = 4;
    static final int BULLET_H     = 12;
    static final int PLAYER_BULLET_SPEED = 8;
    static final int ALIEN_BULLET_SPEED  = 4;
    static final int ALIEN_STEP   = 8;   // pixels per formation move
    static final int ALIEN_DROP   = 20;

    // --- player ---
    int playerX;
    Rectangle playerBullet; // null when not in flight

    // --- aliens ---
    boolean[][] aliens = new boolean[ALIEN_ROWS][ALIEN_COLS];
    int formationX, formationY;
    int alienDirX = 1; // 1 = right, -1 = left

    // --- alien bullets ---
    List<Rectangle> alienBullets = new ArrayList<>();
    Random random = new Random();
    int alienFireCountdown = 60;

    // --- game state ---
    int score  = 0;
    int lives  = 3;
    boolean gameOver = false;

    public GameModel() {
        reset();
    }

    public void reset() {
        playerX = WIDTH / 2 - PLAYER_W / 2;
        playerBullet = null;
        for (int r = 0; r < ALIEN_ROWS; r++)
            for (int c = 0; c < ALIEN_COLS; c++)
                aliens[r][c] = true;
        formationX = 40;
        formationY = 60;
        alienDirX = 1;
        alienBullets.clear();
        score = 0;
        lives = 3;
        gameOver = false;
        alienFireCountdown = 60;
    }

    // --- player movement ---
    public void movePlayerLeft() {
        playerX = Math.max(0, playerX - PLAYER_SPEED);
    }

    public void movePlayerRight() {
        playerX = Math.min(WIDTH - PLAYER_W, playerX + PLAYER_SPEED);
    }

    // --- player bullet ---
    public void firePlayerBullet() {
        if (playerBullet != null) return; // one at a time
        playerBullet = new Rectangle(
            playerX + PLAYER_W / 2 - BULLET_W / 2,
            HEIGHT - PLAYER_H - BULLET_H - 2,
            BULLET_W, BULLET_H
        );
    }

    // --- update called each tick ---
    public void update() {
        if (gameOver) return;
        movePlayerBullet();
        moveFormation();
        maybeFireAlienBullet();
        moveAlienBullets();
        checkCollisions();
        checkGameOver();
    }

    private void movePlayerBullet() {
        if (playerBullet == null) return;
        playerBullet.y -= PLAYER_BULLET_SPEED;
        if (playerBullet.y + BULLET_H < 0) playerBullet = null;
    }

    private void moveFormation() {
        formationX += alienDirX * ALIEN_STEP;

        // find left/right edges of surviving aliens
        int leftCol  = leftmostAlienCol();
        int rightCol = rightmostAlienCol();
        if (leftCol < 0) return; // no aliens left

        int leftEdge  = formationX + leftCol  * (ALIEN_W + ALIEN_H_GAP);
        int rightEdge = formationX + rightCol * (ALIEN_W + ALIEN_H_GAP) + ALIEN_W;

        if (alienDirX == 1 && rightEdge >= WIDTH) {
            formationX -= alienDirX * ALIEN_STEP; // undo overshoot
            formationY += ALIEN_DROP;
            alienDirX = -1;
        } else if (alienDirX == -1 && leftEdge <= 0) {
            formationX -= alienDirX * ALIEN_STEP;
            formationY += ALIEN_DROP;
            alienDirX = 1;
        }
    }

    private void maybeFireAlienBullet() {
        alienFireCountdown--;
        if (alienFireCountdown > 0) return;
        alienFireCountdown = 40 + random.nextInt(40);

        // pick a random living alien in a random column bottom-most row
        List<int[]> candidates = new ArrayList<>();
        for (int c = 0; c < ALIEN_COLS; c++) {
            for (int r = ALIEN_ROWS - 1; r >= 0; r--) {
                if (aliens[r][c]) { candidates.add(new int[]{r, c}); break; }
            }
        }
        if (candidates.isEmpty()) return;
        int[] pick = candidates.get(random.nextInt(candidates.size()));
        int bx = formationX + pick[1] * (ALIEN_W + ALIEN_H_GAP) + ALIEN_W / 2 - BULLET_W / 2;
        int by = formationY + pick[0] * (ALIEN_H + ALIEN_V_GAP) + ALIEN_H;
        alienBullets.add(new Rectangle(bx, by, BULLET_W, BULLET_H));
    }

    private void moveAlienBullets() {
        alienBullets.removeIf(b -> {
            b.y += ALIEN_BULLET_SPEED;
            return b.y > HEIGHT;
        });
    }

    private void checkCollisions() {
        // player bullet vs aliens
        if (playerBullet != null) {
            outer:
            for (int r = 0; r < ALIEN_ROWS; r++) {
                for (int c = 0; c < ALIEN_COLS; c++) {
                    if (!aliens[r][c]) continue;
                    Rectangle alienRect = alienRect(r, c);
                    if (playerBullet.intersects(alienRect)) {
                        aliens[r][c] = false;
                        playerBullet = null;
                        score += 10;
                        break outer;
                    }
                }
            }
        }

        // alien bullets vs player
        Rectangle playerRect = new Rectangle(playerX, HEIGHT - PLAYER_H, PLAYER_W, PLAYER_H);
        alienBullets.removeIf(b -> {
            if (b.intersects(playerRect)) {
                lives--;
                return true;
            }
            return false;
        });
    }

    private void checkGameOver() {
        if (lives <= 0) { gameOver = true; return; }

        // aliens reach the player row
        int bottomRow = bottommostAlienRow();
        if (bottomRow >= 0) {
            int bottomEdge = formationY + bottomRow * (ALIEN_H + ALIEN_V_GAP) + ALIEN_H;
            if (bottomEdge >= HEIGHT - PLAYER_H) gameOver = true;
        }

        // all aliens destroyed — player wins (also game over for simplicity)
        if (aliensRemaining() == 0) gameOver = true;
    }

    // --- helpers ---
    public Rectangle alienRect(int row, int col) {
        int x = formationX + col * (ALIEN_W + ALIEN_H_GAP);
        int y = formationY + row * (ALIEN_H + ALIEN_V_GAP);
        return new Rectangle(x, y, ALIEN_W, ALIEN_H);
    }

    private int leftmostAlienCol() {
        for (int c = 0; c < ALIEN_COLS; c++)
            for (int r = 0; r < ALIEN_ROWS; r++)
                if (aliens[r][c]) return c;
        return -1;
    }

    private int rightmostAlienCol() {
        for (int c = ALIEN_COLS - 1; c >= 0; c--)
            for (int r = 0; r < ALIEN_ROWS; r++)
                if (aliens[r][c]) return c;
        return -1;
    }

    private int bottommostAlienRow() {
        for (int r = ALIEN_ROWS - 1; r >= 0; r--)
            for (int c = 0; c < ALIEN_COLS; c++)
                if (aliens[r][c]) return r;
        return -1;
    }

    public int aliensRemaining() {
        int count = 0;
        for (boolean[] row : aliens)
            for (boolean a : row)
                if (a) count++;
        return count;
    }

    // --- getters for View ---
    public int getPlayerX()            { return playerX; }
    public Rectangle getPlayerBullet() { return playerBullet; }
    public boolean[][] getAliens()     { return aliens; }
    public List<Rectangle> getAlienBullets() { return alienBullets; }
    public int getScore()              { return score; }
    public int getLives()              { return lives; }
    public boolean isGameOver()        { return gameOver; }
    public int getFormationX()         { return formationX; }
    public int getFormationY()         { return formationY; }
}
