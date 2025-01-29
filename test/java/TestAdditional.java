import entity.Player;
import main.GamePanel;
import main.KeyHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import powerups.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the game logic.
 * It tests various game functionalities such as player picking up power-ups, player life after damage,
 * player detonating bombs, game pausing and resuming, and player reset after round ends.
 */
class TestAdditional {
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
        player = new Player(gamePanel, keyHandler, 1);
        gamePanel.tileM.loadMap("/maps/map01.txt");
    }

    /**
     * Tests if the player's bomb limit increases after picking up an ExtraBomb power-up.
     */
    @Test
    void testPlayerPickUpPowerUp() {
        int initialBombLimit = player.bombLimit;
        ExtraBomb extraBomb = new ExtraBomb(gamePanel, player.x, player.y);
        gamePanel.powerUps.add(extraBomb);
        player.update();

        assertEquals(initialBombLimit + 1, player.bombLimit, "Player should pick up the ExtraBomb power-up and increase bomb limit.");
    }

    /**
     * Tests if the player's life decreases by 1 after taking damage.
     */
    @Test
    void testPlayerLifeAfterDamage() {
        int initialLife = player.currLife;
        player.currLife--;
        assertEquals(initialLife - 1, player.currLife, "Player life should decrease by 1 after taking damage.");
    }

    /**
     * Tests if the player's bomb count is 0 after detonating bombs.
     */
    @Test
    void testPlayerDetonateBombs() {
        player.placeBomb();
        assertEquals(1, player.bombCount, "Player should have 1 bomb placed.");
        player.detonateBombs();
        assertEquals(0, player.bombCount, "Bomb count should be 0 after detonating bombs.");
    }

    /**
     * Tests if the game state changes correctly when the game is paused and resumed.
     * @throws IOException If an error occurs while setting up the game panel.
     */
    @Test
    void testGamePausesAndResumes() throws IOException {
        gamePanel = new GamePanel();
        gamePanel.players = new Player[1];
        gamePanel.players[0] = new Player(gamePanel, gamePanel.keyH, 1);
        gamePanel.players[0].currLife = 3;
        gamePanel.gameState = gamePanel.playState;

        gamePanel.gameState = gamePanel.pauseState;
        gamePanel.update();

        assertEquals(gamePanel.pauseState, gamePanel.gameState, "Game should be in pause state.");

        gamePanel.gameState = gamePanel.playState;
        gamePanel.update();

        assertEquals(gamePanel.playState, gamePanel.gameState, "Game should be in play state after resuming.");
    }

    /**
     * Tests if the game advances to the next round and the players' lives are reset after a round ends.
     * @throws IOException If an error occurs while setting up the game panel.
     */
    @Test
    void testPlayerResetAfterRoundEnds() throws IOException {
        gamePanel = new GamePanel();
        gamePanel.players = new Player[2];
        gamePanel.players[0] = new Player(gamePanel, gamePanel.keyH, 1);
        gamePanel.players[1] = new Player(gamePanel, gamePanel.keyH, 2);
        gamePanel.players[0].currLife = 3;
        gamePanel.players[1].currLife = 3;
        gamePanel.gameState = gamePanel.playState;

        for (int i = 0; i < gamePanel.monsters.length; i++) {
            gamePanel.monsters[i] = null;
        }

        gamePanel.players[1].currLife = 0;

        gamePanel.update();

        assertEquals(2, gamePanel.currentRound, "Game should advance to the next round.");
        assertEquals(3, gamePanel.players[0].currLife, "Player 1's life should be reset.");
        assertEquals(3, gamePanel.players[1].currLife, "Player 2's life should be reset.");
    }
}
