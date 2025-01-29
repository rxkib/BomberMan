package powerups;
import entity.Player;
import main.GamePanel;

/**
 * This class represents a BlastExpansion power-up in the game.
 * It extends the PowerUp class, inheriting its properties and methods.
 * The BlastExpansion power-up increases the player's bomb blast radius by one when picked up,
 * enhancing the bomb's effective area. The effect can be removed, reducing the player's bomb blast radius by one,
 * but not below a minimum threshold, ensuring a base level of enhanced capability remains.
 */
public class BlastExpansion extends PowerUp {
    /**
     * Initializes a BlastExpansion power-up at specific coordinates, setting its type to
     * BLAST_EXPANSION.
     * @param gp
     * @param x
     * @param y
     */
    public BlastExpansion(GamePanel gp, int x, int y) {
        super(gp, Type.BLAST_EXPANSION, x, y, 0);
    }

    /**
     * Increases the player's bomb blast radius by one, enhancing their bomb's effective
     * area.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        player.bombBlastRadius++;
    }

    /**
     * Reduces the player's bomb blast radius by one, but not below a minimum threshold,
     * ensuring a base level of enhanced capability remains.
     * @param player
     */
    @Override
    protected void removeEffect(Player player) {
        if (player.bombBlastRadius > 2) {
            player.bombBlastRadius--;
        }
    }
}
