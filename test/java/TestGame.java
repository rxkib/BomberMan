import entity.Player;
import main.GamePanel;
import main.KeyHandler;
import org.junit.jupiter.api.BeforeEach;
import powerups.*;
import object.OBJ_bomb;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the game logic.
 * It tests various game functionalities such as bomb explosion, power-ups effects, and player invincibility.
 */
class TestGame {
    GamePanel gamePanel;
    Player player;

    /**
 * Sets up the game panel, key handler, and player before each test.
 * It also loads a map for the game panel.
 * @throws IOException If an error occurs while loading the map.
 */
    @BeforeEach
    void setUp() throws IOException {
        gamePanel = new GamePanel();
        KeyHandler keyHandler = new KeyHandler(gamePanel);
        player = new Player(gamePanel, null, 1);
        gamePanel.tileM.loadMap("/maps/map01.txt");
    }

    /**
 * Tests if a tile changes to grass after a bomb explosion.
 */
    @Test
    void testBombExplosionChangesTile() {
        int bombX = 3;
        int bombY = 3;
        Player player = new Player(gamePanel, null, 1);

        OBJ_bomb bomb = new OBJ_bomb(gamePanel, player, bombX * gamePanel.tileSize, bombY * gamePanel.tileSize);
        gamePanel.objs.add(bomb);
        bomb.explode();

        int expectedTileNum = 0;
        int actualTileNum = gamePanel.tileM.mapTileNum[bombX][bombY];
        assertEquals(expectedTileNum, actualTileNum, "Tile should be grass after explosion");
    }

    /**
 * Tests if the player's bomb limit increases after picking up an ExtraBomb power-up.
 */
    @Test
    void testPowerUpExtraBomb() {
        player.bombLimit = 1;

        ExtraBomb extraBomb = new ExtraBomb(gamePanel, 100, 100);
        extraBomb.applyEffect(player);
        assertEquals(2, player.bombLimit, "Bomb limit should increase by 1 after picking up an Extra Bomb power-up.");
    }

    /**
 * Tests if the player's bomb blast radius increases after picking up a BlastExpansion power-up.
 */
    @Test
    void testPowerUpBlastExpansion() {
        int initialRadius = player.bombBlastRadius;
        BlastExpansion blastExpansion = new BlastExpansion(gamePanel, 0, 0);
        blastExpansion.applyEffect(player);
        assertEquals(initialRadius + 1, player.bombBlastRadius, "Bomb blast radius should increase by 1.");
    }

    /**
 * Tests if the player can trigger bombs at will after picking up a Detonator power-up.
 */
    @Test
    void testPowerUpDetonator() {
        Detonator detonator = new Detonator(gamePanel, 0, 0);
        detonator.applyEffect(player);
        assertTrue(player.hasDetonator, "Player should be able to trigger bombs at will after picking up Detonator.");
    }

    /**
 * Tests if the player's speed increases after picking up a RollerSkate power-up.
 */
    @Test
    void testPowerUpRoller() {
        int initialSpeed = player.speed;
        RollerSkate rollerSkate = new RollerSkate(gamePanel, 0, 0);
        rollerSkate.applyEffect(player);
        assertEquals(initialSpeed + 2, player.speed, "Player speed should increase with Roller Skates.");
    }

    /**
 * Tests if the player can pass through obstacles (Ghost mode) after picking up a Ghost power-up.
 * Also tests if the Ghost mode is deactivated after the effect is removed.
 */
    @Test
    void testPowerUpGhost() {
        Ghost ghost = new Ghost(gamePanel, 0, 0);
        ghost.applyEffect(player);
        assertTrue(player.isGhost, "Ghost mode should be activated.");

        ghost.removeEffect(player);
        assertFalse(player.isGhost, "Ghost mode should be deactivated.");
    }

    /**
 * Tests if the player becomes invincible after picking up an Invincibility power-up.
 * Also tests if the invincibility is deactivated after the effect is removed.
 */
    @Test
    void testInvincibilityActivationAndDeactivation() {
        Invincibility invincibility = new Invincibility(gamePanel, 0, 0);
        invincibility.applyEffect(player);
        assertTrue(player.isInvincible, "Player should be invincible after picking up Invincibility.");

        invincibility.removeEffect(player);
        assertFalse(player.isInvincible, "Invincibility should be deactivated.");
    }

    /**
 * Tests if the player's obstacle limit increases after picking up an Obstacle power-up.
 */
    @Test
    void estPowerUpObstacle() {
        int initialLimit = player.obstacleLimit;
        Obstacle obstacle = new Obstacle(gamePanel, 0, 0);
        obstacle.applyEffect(player);
        assertEquals(initialLimit + 3, player.obstacleLimit, "Obstacle limit should increase by 3.");
    }
}
