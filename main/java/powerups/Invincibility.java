package powerups;
import entity.Player;
import main.GamePanel;

/**
 * Initializes an Invincibility power-up at the specified coordinates on the game panel,
 * setting it to a type of INVINCIBILITY and a fixed duration of 5 units, likely translating to
 * seconds.
 * Confers temporary invincibility to the player if they are not already invincible. The duration
 * of the effect is calculated by converting seconds into frames (a common method in game
 * development for handling time-based effects).
 * Terminates the invincibility effect, making the player vulnerable again to damage and other
 * game mechanics that had been temporarily suspended.
 */
public class Invincibility extends PowerUp {
    /**
     * Initializes an Invincibility power-up at the specified coordinates on the game panel,
     * setting it to a type of INVINCIBILITY and a fixed duration of 5 units, likely translating to
     * seconds.
     * @param gp
     * @param x
     * @param y
     */
    public Invincibility(GamePanel gp, int x, int y) {
        super(gp, Type.INVINCIBILITY, x, y, 5);
    }

    /**
     * Confers temporary invincibility to the player if they are not already invincible. The duration
     * of the effect is calculated by converting seconds into frames (a common method in game
     * development for handling time-based effects).
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        if (player != null && !player.isInvincible) {
            player.makeInvincible(convertSecondsToFrames(duration));
        }
    }

    /**
     * Terminates the invincibility effect, making the player vulnerable again to damage and other
     * game mechanics that had been temporarily suspended.
     * @param player
     */
    @Override
    public void removeEffect(Player player) {
        if (player != null) {
            player.endInvincibility();
        }
    }
}
