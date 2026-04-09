import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

// View: reads from the Model and draws everything. Never changes game state.
public class GameView extends JPanel {

    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawAliens(g);
        drawPlayer(g);
        drawPlayerBullet(g);
        drawAlienBullets(g);
        drawHud(g);

        if (model.isGameOver()) drawGameOver(g);
    }

    private void drawAliens(Graphics g) {
        boolean[][] aliens = model.getAliens();
        for (int r = 0; r < GameModel.ALIEN_ROWS; r++) {
            for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                if (!aliens[r][c]) continue;
                Rectangle rect = model.alienRect(r, c);
                // shade rows slightly differently so the formation has depth
                g.setColor(r < 2 ? new Color(80, 220, 80) : new Color(50, 180, 50));
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(model.getPlayerX(),
                   GameModel.HEIGHT - GameModel.PLAYER_H,
                   GameModel.PLAYER_W, GameModel.PLAYER_H);
    }

    private void drawPlayerBullet(Graphics g) {
        Rectangle b = model.getPlayerBullet();
        if (b == null) return;
        g.setColor(Color.YELLOW);
        g.fillRect(b.x, b.y, b.width, b.height);
    }

    private void drawAlienBullets(Graphics g) {
        g.setColor(Color.RED);
        for (Rectangle b : model.getAlienBullets()) {
            g.fillRect(b.x, b.y, b.width, b.height);
        }
    }

    private void drawHud(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + model.getScore(), 8, 20);
        String livesStr = "Lives: " + model.getLives();
        FontMetrics fm = g.getFontMetrics();
        g.drawString(livesStr, GameModel.WIDTH - fm.stringWidth(livesStr) - 8, 20);
    }

    private void drawGameOver(Graphics g) {
        String line1 = model.aliensRemaining() == 0 ? "You Win!" : "Game Over";
        String line2 = "Score: " + model.getScore();
        String line3 = "Press R to play again";

        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        int x1 = (GameModel.WIDTH - fm.stringWidth(line1)) / 2;

        g.setFont(new Font("Arial", Font.BOLD, 24));
        fm = g.getFontMetrics();
        int x2 = (GameModel.WIDTH - fm.stringWidth(line2)) / 2;

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        int x3 = (GameModel.WIDTH - fm.stringWidth(line3)) / 2;

        int cy = GameModel.HEIGHT / 2;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, cy - 80, GameModel.WIDTH, 130);

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString(line1, x1, cy - 20);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(line2, x2, cy + 20);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(200, 200, 200));
        g.drawString(line3, x3, cy + 50);
    }
}
