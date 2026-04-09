import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

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

    Timer timer;

    GamePanel() {
        // Start with 3 segments near center, moving right
        snake.add(new Point(11, 10));
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));

        timer = new Timer(150, e -> {
            move();
            repaint();
        });
        timer.start();
    }

    void move() {
        Point head = snake.getFirst();
        int newX = (head.x + dirX + GRID_SIZE) % GRID_SIZE;
        int newY = (head.y + dirY + GRID_SIZE) % GRID_SIZE;
        snake.addFirst(new Point(newX, newY));
        snake.removeLast();
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

        // Draw snake in green
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
        }
    }
}
