import java.awt.Rectangle;

// Plain-Java model tests. No testing libraries.
// Compile: javac GameModel.java ModelTester.java
// Run:     java ModelTester
public class ModelTester {

    static int passed = 0;
    static int failed = 0;

    static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + description);
            passed++;
        } else {
            System.out.println("FAIL: " + description);
            failed++;
        }
    }

    public static void main(String[] args) {
        testPlayerBoundaryLeft();
        testPlayerBoundaryRight();
        testNoDoubleFire();
        testBulletRemovedAtTop();
        testScoreIncreasesOnKill();
        testGameOverOnNoLives();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
    }

    static void testPlayerBoundaryLeft() {
        GameModel m = new GameModel();
        for (int i = 0; i < 300; i++) m.movePlayerLeft();
        check("Player cannot move past left edge", m.getPlayerX() >= 0);
    }

    static void testPlayerBoundaryRight() {
        GameModel m = new GameModel();
        for (int i = 0; i < 300; i++) m.movePlayerRight();
        check("Player cannot move past right edge",
              m.getPlayerX() + GameModel.PLAYER_W <= GameModel.WIDTH);
    }

    static void testNoDoubleFire() {
        GameModel m = new GameModel();
        m.firePlayerBullet();
        Rectangle first = m.getPlayerBullet();
        m.firePlayerBullet(); // should be ignored
        check("Firing while bullet in flight does nothing",
              m.getPlayerBullet() == first);
    }

    static void testBulletRemovedAtTop() {
        GameModel m = new GameModel();
        m.firePlayerBullet();
        // Advance bullet until it should leave the top
        for (int i = 0; i < 200; i++) {
            if (m.getPlayerBullet() == null) break;
            m.getPlayerBullet().y -= GameModel.PLAYER_BULLET_SPEED;
            // simulate the removal check
            if (m.getPlayerBullet() != null &&
                m.getPlayerBullet().y + GameModel.BULLET_H < 0) {
                // call update to let the model clean it up
                break;
            }
        }
        // Force bullet off screen and call update
        GameModel m2 = new GameModel();
        m2.firePlayerBullet();
        m2.getPlayerBullet().y = -100; // place off screen
        m2.update(); // update should remove it
        check("Bullet off top of screen is removed", m2.getPlayerBullet() == null);
    }

    static void testScoreIncreasesOnKill() {
        GameModel m = new GameModel();
        int scoreBefore = m.getScore();
        m.firePlayerBullet();
        // update() moves the formation before checking collisions, so anticipate
        // the formation's new x position and place the bullet inside that alien cell
        int alienXAfterMove = m.getFormationX() + GameModel.ALIEN_STEP;
        int alienY          = m.getFormationY();
        m.getPlayerBullet().x = alienXAfterMove + GameModel.ALIEN_W / 2 - GameModel.BULLET_W / 2;
        // bullet moves up by PLAYER_BULLET_SPEED before collision check, so add that back
        m.getPlayerBullet().y = alienY + GameModel.ALIEN_H / 2 + GameModel.PLAYER_BULLET_SPEED;
        m.update();
        check("Score increases when alien is destroyed", m.getScore() > scoreBefore);
    }

    static void testGameOverOnNoLives() {
        GameModel m = new GameModel();
        // Drain lives directly
        while (m.getLives() > 0) {
            // place an alien bullet on the player
            m.getAlienBullets().add(new Rectangle(
                m.getPlayerX(), GameModel.HEIGHT - GameModel.PLAYER_H,
                GameModel.BULLET_W, GameModel.BULLET_H));
            m.update();
        }
        check("Game over triggers when all lives are lost", m.isGameOver());
    }
}
