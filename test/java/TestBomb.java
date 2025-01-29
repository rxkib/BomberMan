import entity.Player;
import main.GamePanel;
import main.KeyHandler;
import object.OBJ_bomb;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
/**
 * This class contains unit tests for the bomb-related functionalities in the game.
 * It tests various bomb interactions such as bomb planting, bomb ignoring its owner, and bomb interaction updates.
 */
class TestBomb {
    GamePanel gamePanel;
    Player player;

    /**
 * Sets up the game panel and player before each test.
 * @throws IOException If an error occurs while setting up the game panel.
 */
    @BeforeEach
    void setUp() throws IOException {
        gamePanel = new GamePanel();
        player = new Player(gamePanel, null, 1);
    }

    /**
 * Tests if the bomb stops ignoring its owner (the player) once the player moves away from the bomb.
 * @throws IOException If an error occurs while setting up the game panel.
 */
    @Test
    void testUpdateBombInteraction() throws IOException {
        GamePanel mockGamePanel = new GamePanel();
        KeyHandler mockKeyHandler = new KeyHandler(mockGamePanel);
        Player player = new Player(mockGamePanel, mockKeyHandler, 1);

        player.placeBomb();
        player.x += 100;
        player.updateBombInteraction();

        OBJ_bomb bomb = (OBJ_bomb) mockGamePanel.objs.getFirst();
        assertFalse(bomb.ignoreCollisionWithOwner, "Bomb should not ignore collision once player moves away");
    }

    /**
 * Tests if a bomb is added to the game objects when the player plants a bomb.
 */
    @Test
    void testBombPlanting() {
        player.placeBomb();
        assertEquals(1, gamePanel.objs.size(), "Bomb should be added to game objects.");
    }

    /**
 * Tests if a bomb initially ignores collision with its owner (the player).
 */
    @Test
    void testBombIgnoringOwner() {
        OBJ_bomb bomb = new OBJ_bomb(gamePanel, player, player.x, player.y);
        assertTrue(bomb.ignoreCollisionWithOwner, "Bomb should initially ignore collision with its owner.");
    }
}
