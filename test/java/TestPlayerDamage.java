import entity.Player;
import main.GamePanel;
import object.OBJ_bomb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the player damage functionalities in the game.
 * It tests the player's life decrease after a bomb explosion.
 */
class TestPlayerDamage {
    GamePanel gamePanel;

    /**
 * Sets up the game panel and initializes the players before each test.
 * It also loads a map for the game panel.
 * @throws IOException If an error occurs while loading the map.
 */
    @BeforeEach
    void setUp() throws IOException {
        gamePanel = new GamePanel();
        gamePanel.tileM.loadMap("res/maps/map1.txt");
        gamePanel.initPlayers();
    }
/**
 * Tests if the player's life decreases after a bomb explosion.
 * The test plants a bomb at the player's location, triggers the bomb explosion, and checks if the player's life decreases.
 */
    @Test
    void testPlayerDamageByBomb() {
        Player player = gamePanel.players[0];
        int initialLife = player.currLife;
        OBJ_bomb bomb = new OBJ_bomb(gamePanel, player, player.x, player.y);
        bomb.explode();
        gamePanel.update();

        assertTrue(player.currLife < initialLife, "Player life should decrease after bomb explosion");
    }
}
