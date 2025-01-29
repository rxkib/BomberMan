package powerups;
import entity.Player;
import main.GamePanel;

/**
 * The RollerSkate class extends the PowerUp class and represents a speed-boosting power-up in the game.
 * It includes methods for initializing a new instance of a RollerSkate power-up at specified coordinates,
 * applying the speed boost effect to the player, and reversing the speed boost effect.
 * The speed boost effect increases the player's speed attribute by a fixed amount, allowing the player to move faster on the game board.
 */
public class RollerSkate extends PowerUp {
    private final int speedIncrease = 2;

    /**
     * Initializes a RollerSkate power-up at specified coordinates, setting its type to
     * ROLLER_SKATE. This type of power-up has no inherent duration (0), suggesting that the
     * speed boost might be permanent until explicitly removed or the game session ends.
     * @param gp
     * @param x
     * @param y
     */
    public RollerSkate(GamePanel gp, int x, int y) {
        super(gp, Type.ROLLER_SKATE, x, y, 0);
    }

    /**
     * Enhances the player's speed attribute by a fixed amount (speedIncrease), which
     * is set to 2. This increase in speed allows the player to move faster on the game board,
     * facilitating quicker navigation and potentially evading opponents or dangers more
     * effectively.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        if (player != null) {
            player.speed += speedIncrease;
        }
    }

    /**
     *  Reverses the speed boost applied by the RollerSkate power-up, decreasing the
     * player's speed by the same amount it was increased. This method ensures that the player's
     * speed returns to normal once the power-up's effect is no longer needed or desired.
     * @param player
     */
    @Override
    protected void removeEffect(Player player) {
        if (player != null) {
            player.speed -= speedIncrease;
        }
    }
}
