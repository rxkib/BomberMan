package powerups;
import entity.Player;
import main.GamePanel;

/**
 * The Obstacle class represents a power-up that increases the player's limit for placing obstacles
 * in the game environment. This power-up is intended to be permanent or not time-based, as it does
 * not implement a method to remove the effect.
 */
public class Obstacle extends PowerUp {
    /**
     * Initializes an Obstacle power-up at the specified coordinates within the game panel. It sets
     * this power-up type to OBSTACLE and assigns no duration (0), indicating that the effect may
     * be permanent or not time-based.
     * @param gp
     * @param x
     * @param y
     */
    public Obstacle(GamePanel gp, int x, int y) {
        super(gp, Type.OBSTACLE, x, y, 0);
    }

    /**
     * Increases the player's limit for placing obstacles by 3, allowing them to strategically alter
     * the game environment by adding more barriers that can block or reroute other players and
     * enemies.
     * @param player
     */
    @Override
    public void applyEffect(Player player) {
        player.obstacleLimit += 3;
    }

    /**
     * This method is overridden but not implemented, suggesting that the effects of this power
     * up are intended to be permanent or that the game does not require the removal of this type
     * of effect under normal gameplay circumstances.
     * @param player
     */
    @Override
    protected void removeEffect(Player player) {

    }
}
