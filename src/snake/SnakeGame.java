import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

            gamePanel.requestFocusInWindow();
        });
    }
}

class GamePanel extends JPanel {

    static final int CELL_SIZE = 30;
    static final int GRID_SIZE = 20;

    LinkedList<Point> snake = new LinkedList<>();
    int dirX = 1, dirY = 0;   // current direction
    int nextDirX = 1, nextDirY = 0; // buffered direction from key press

    Point food;
    int score = 0;
    boolean gameOver = false;

    Random random = new Random();
    Timer timer;

    GamePanel() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (dirY != 1) { nextDirX = 0; nextDirY = -1; }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (dirY != -1) { nextDirX = 0; nextDirY = 1; }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (dirX != 1) { nextDirX = -1; nextDirY = 0; }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (dirX != -1) { nextDirX = 1; nextDirY = 0; }
                        break;
                    case KeyEvent.VK_R:
                        if (gameOver) restart();
                        break;
                }
            }
        });

        initGame();

        timer = new Timer(150, e -> {
            move();
            repaint();
        });
        timer.start();
    }

    void initGame() {
        snake.clear();
        snake.add(new Point(11, 10));
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));
        dirX = 1; dirY = 0;
        nextDirX = 1; nextDirY = 0;
        score = 0;
        gameOver = false;
        spawnFood();
    }

    void restart() {
        initGame();
        timer.restart();
    }

    void spawnFood() {
        Point candidate;
        do {
            candidate = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (snake.contains(candidate));
        food = candidate;
    }

    void move() {
        // Apply buffered direction
        dirX = nextDirX;
        dirY = nextDirY;

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

        // Self collision (exclude tail, which moves away this tick)
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

        // Draw game over overlay
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            String line1 = "Game Over";
            String line2 = "Score: " + score;
            String line3 = "Press R to restart";

            int x1 = (getWidth() - fm.stringWidth(line1)) / 2;
            int x2 = (getWidth() - fm.stringWidth(line2)) / 2;

            g.setFont(new Font("Arial", Font.BOLD, 18));
            fm = g.getFontMetrics();
            int x3 = (getWidth() - fm.stringWidth(line3)) / 2;

            int centerY = getHeight() / 2;

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, centerY - 80, getWidth(), 130);

            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.WHITE);
            g.drawString(line1, x1, centerY - 20);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(line2, x2, centerY + 20);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(new Color(200, 200, 200));
            g.drawString(line3, x3, centerY + 50);
        }
    }
}
