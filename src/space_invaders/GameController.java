import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Controller: owns the JFrame, game loop, and keyboard input.
public class GameController {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView  view  = new GameView(model);

            JFrame frame = new JFrame("Space Invaders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(view);
            frame.setSize(GameModel.WIDTH, GameModel.HEIGHT + 28); // +28 for title bar
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Timer[] timerRef = new Timer[1]; // array trick so lambda can reference timer

            Timer timer = new Timer(16, e -> {
                model.update();
                view.repaint();
                if (model.isGameOver()) timerRef[0].stop();
            });
            timerRef[0] = timer;

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            model.movePlayerLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            model.movePlayerRight();
                            break;
                        case KeyEvent.VK_SPACE:
                            model.firePlayerBullet();
                            break;
                        case KeyEvent.VK_R:
                            if (model.isGameOver()) {
                                model.reset();
                                timerRef[0].start();
                            }
                            break;
                    }
                }
            });

            timer.start();
        });
    }
}
