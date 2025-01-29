import entity.Player;
import main.GamePanel;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import javax.swing.*;

/**
 * This class contains unit tests for the UI of the game.
 * It tests various UI functionalities such as the drawing of the title screen and the player's life.
 */
public class TestUI extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    GamePanel gamePanel;
/**
 * Sets up the game panel and frame before each test.
 * It also initializes the window fixture for AssertJ Swing testing.
 */
    @Override
    protected void onSetUp() {
        GamePanel gamePanel = GuiActionRunner.execute(() -> new GamePanel());
        JFrame frame = new JFrame();
        frame.add(gamePanel);
        window = new FrameFixture(robot(), frame);
        window.show();
    }
/**
 * Tests if the title screen is drawn correctly.
 * The test sets the UI state to the title screen and repaints the game panel.
 * It then checks if the title screen panel is visible.
 */
    @Test
    public void testTitleScreenDrawing() {
        GuiActionRunner.execute(() -> {
            gamePanel.ui.titleScreenState = 0;
            gamePanel.repaint();
        });
        window.panel("TitleScreenPanel").requireVisible();
    }
/**
 * Tests if the player's life is drawn correctly.
 * The test initializes a player with a certain life, sets the game state to the play state, and repaints the game panel.
 * It then checks if the player life panel is visible.
 */
    @Test
    public void testPlayerLifeDrawing() {
        GuiActionRunner.execute(() -> {
            gamePanel.players[0] = new Player(gamePanel, null, 1);
            gamePanel.players[0].currLife = 3;
            gamePanel.players[0].maxLife = 5;
            gamePanel.gameState = gamePanel.playState;
            gamePanel.repaint();
        });
        window.panel("PlayerLifePanel").requireVisible();
    }
/**
 * Cleans up the window fixture after each test.
 */
    @Override
    protected void onTearDown() {
        window.cleanUp();
    }
}
