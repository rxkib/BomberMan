package powerups;
import entity.Player;
import main.GamePanel;

/**
 * The RollerSkate class represents a power-up that increases the player's speed attribute by a
 * fixed amount, allowing them to move faster on the game board. This power-up has no inherent
 * duration, suggesting that the speed boost might be permanent until explicitly removed or the
 * game session ends.
 */
public class ExtraBomb extends PowerUp {
    /**
     * Initializes an ExtraBomb power-up at specified coordinates, specifically setting its type to
     * EXTRA_BOMB and indicating no inherent duration for the effect (denoted by 0).
     * @param gp
     * @param x
     * @param y
     */
    public ExtraBomb(GamePanel gp, int x, int y) {
        super(gp, Type.EXTRA_BOMB, x, y, 0);
    }

    /**
     * Increases the player's bomb capacity by one, but only if their current bomb limit is 1,
     * allowing them to carry multiple bombs.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        if (player.bombLimit == 1) player.bombLimit++;
    }

    /**
     * Decreases the player's bomb limit by one upon removal of the power-up, ensuring that the
     * limit does not fall below 1, thereby reverting to the initial bomb-carrying capacity.
     * @param player
     */
    @Override
    protected void removeEffect(Player player) {
        if (player.bombLimit > 1) {

            player.bombLimit--;
        }
    }
}
