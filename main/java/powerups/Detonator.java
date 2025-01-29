package powerups;
import entity.Player;
import main.GamePanel;

import javax.imageio.ImageIO;

/**
 * Initializes a Detonator power-up at specified coordinates, setting its type to DETONATOR
 * and a duration of 5 (presumably 5 seconds or game cycles).
 * Grants the player the ability to detonate bombs at will, enabling strategic gameplay
 * enhancements.
 * Reverses the detonator ability, removing the player's capacity to manually detonate bombs.
 */
public class Detonator extends PowerUp {
    /**
     * Initializes a Detonator power-up at specified coordinates, setting its type to DETONATOR
     * and a duration of 5 (presumably 5 seconds or game cycles).
     * @param gp
     * @param x
     * @param y
     */
    public Detonator(GamePanel gp, int x, int y) {
        super(gp, Type.DETONATOR, x, y,5);
    }

    /**
     * Grants the player the ability to detonate bombs at will, enabling strategic gameplay
     * enhancements.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        if (player != null)
            player.hasDetonator = true;
    }

    /**
     * Reverses the detonator ability, removing the player's capacity to manually detonate bombs.
     * @param player
     */
    @Override
    protected void removeEffect(Player player) {
        if (player != null)
            player.hasDetonator = false;
    }
}
