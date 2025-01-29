import entity.Player;
import main.GamePanel;
import main.KeyHandler;
import object.OBJ_bomb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import powerups.Obstacle;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the player-related functionalities in the game.
 * It tests various player interactions such as player initialization, player movement, and player collision with obstacles.
 */
class TestPlayer {
    GamePanel gamePanel;
    Player player;
    KeyHandler keyH;

    /**
 * Sets up the game panel, key handler, and player before each test.
 * @throws IOException If an error occurs while setting up the game panel.
 */
    @BeforeEach
    void setUp() throws IOException {
        gamePanel = new GamePanel();
        gamePanel.initPlayers();
        gamePanel.setupGame();
        keyH = new KeyHandler(gamePanel);
        player = new Player(gamePanel, keyH, 1);
    }

    /**
 * Tests if the player is initialized with correct properties.
 * The test checks the initial position, speed, direction, and life of the player.
 * @throws IOException If an error occurs while setting up the game panel.
 */
    @Test
    void testPlayerInit() throws IOException {
        GamePanel mockGamePanel = new GamePanel();
        KeyHandler mockKeyHandler = new KeyHandler(mockGamePanel);
        int playerNumber = 1;
        Player player = new Player(mockGamePanel, mockKeyHandler, playerNumber);

        assertEquals(50, player.x, "Initial x position should be 50 for player 1");
        assertEquals(50, player.y, "Initial y position should be 50 for player 1");
        assertEquals(4, player.speed, "Speed should be initialized to 4");
        assertEquals("down", player.direction, "Initial direction should be down");
        assertEquals(3, player.currLife, "Current life should be initialized to max life");
    }

    /**
 * Tests if the player moves correctly in response to key presses.
 * The test simulates a key press for moving up and checks if the player's y-coordinate decreases accordingly.
 * @throws IOException If an error occurs while setting up the game panel.
 */
    @Test
    void testPlayerMovement() throws IOException {
        GamePanel gamePanel = new GamePanel();
        KeyHandler keyHandler = new KeyHandler(gamePanel);

        Player player = new Player(gamePanel, keyHandler, 1);
        player.x = 100;
        player.y = 50;
        player.speed = 4;

        keyHandler.upPressed = true;
        player.update();
        keyHandler.upPressed = false;

        assertEquals(50, player.y, "Player should move up.");
    }

    /**
 * Tests if the player stops moving when colliding with an obstacle.
 * The test adds an obstacle in the player's path and checks if the player stops before the obstacle.
 */
    @Test
    void testPlayerCollision() {
        Obstacle obstacle = new Obstacle(gamePanel, player.x + player.speed, player.y);
        gamePanel.objs.add(obstacle);
        player.direction = "right";
        player.update();
        assertEquals(26, obstacle.x - player.solidArea.width, "Player should stop right before the obstacle.");
    }
}