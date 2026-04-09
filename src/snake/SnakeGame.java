import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            frame.setResizable(false);

            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel {

    static final int CELL_SIZE = 30;
    static final int GRID_SIZE = 20;

    LinkedList<Point> snake = new LinkedList<>();
    int dirX = 1, dirY = 0; // moving right

    Point food;
    int score = 0;
    boolean gameOver = false;

    Random random = new Random();
    Timer timer;

    GamePanel() {
        // Start with 3 segments near center, moving right
        snake.add(new Point(11, 10));
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));

        spawnFood();

        timer = new Timer(150, e -> {
            move();
            repaint();
        });
        timer.start();
    }

    void spawnFood() {
        Point candidate;
        do {
            candidate = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (snake.contains(candidate));
        food = candidate;
    }

    void move() {
        Point head = snake.getFirst();
        int newX = head.x + dirX;
        int newY = head.y + dirY;

        // Wall collision
        if (newX < 0 || newX >= GRID_SIZE || newY < 0 || newY >= GRID_SIZE) {
            timer.stop();
            gameOver = true;
            return;
        }

        Point newHead = new Point(newX, newY);

        // Self collision (exclude tail, which will move away)
        if (snake.subList(0, snake.size() - 1).contains(newHead)) {
            timer.stop();
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score++;
            spawnFood();
            // Don't remove tail — snake grows by one
        } else {
            snake.removeLast();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw dark gray background grid
        g.setColor(new Color(50, 50, 50));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
            }
        }

        // Draw food in red
        g.setColor(Color.RED);
        g.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);

        // Draw snake in green
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
        }

        // Draw score in top-left
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 8, 20);

        // Draw game over message
        if (gameOver) {
            String msg = "Game Over";
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(x - 10, y - fm.getAscent() - 4, fm.stringWidth(msg) + 20, fm.getHeight() + 8);
            g.setColor(Color.WHITE);
            g.drawString(msg, x, y);
        }
    }
}
