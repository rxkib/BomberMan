package powerups;
import entity.Player;
import main.GamePanel;

/**
 * Initializes the Ghost power-up at the specified coordinates within the game panel and sets
 * a duration for the effect (5, which could be in seconds or game cycles depending on how
 * duration is used in the game logic).
 * Activates the ghost mode for the player, translating the duration from seconds to game
 * frames using convertSecondsToFrames(duration), allowing the player to pass through
 * obstacles.
 * Deactivates the ghost mode, returning the player to normal collision interactions with
 * obstacles.
 */
public class Ghost extends PowerUp {
    /**
     * Initializes the Ghost power-up at the specified coordinates within the game panel and sets
     * a duration for the effect (5, which could be in seconds or game cycles depending on how
     * duration is used in the game logic).
     * @param gp
     * @param x
     * @param y
     */
    public Ghost(GamePanel gp, int x, int y) {
        super(gp, Type.GHOST, x, y, 5);
    }


    /**
     * Activates the ghost mode for the player, translating the duration from seconds to game
     * frames using convertSecondsToFrames(duration), allowing the player to pass through
     * obstacles.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        if (player != null)
            player.enableGhost(convertSecondsToFrames(duration));
    }

    /**
     * Deactivates the ghost mode, returning the player to normal collision interactions with
     * obstacles.
     * @param player
     */
    @Override
    public void removeEffect(Player player) {
        if (player != null)
            player.disableGhost();
    }
}
