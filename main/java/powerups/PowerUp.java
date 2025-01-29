package powerups;
import entity.Player;
import main.GamePanel;
import object.SuperObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PowerUp.java
 * Abstract class for power-ups in the game, which can be picked up by players to gain temporary
 * advantages or abilities. Power-ups have a type (such as EXTRA_BOMB, OBSTACLE, etc.), a duration
 * (in seconds), and an image representing the power-up on the game panel. Power-ups can be activated
 * by players to apply their effects and deactivated when their duration expires or under certain
 * game conditions.
 */
public abstract class PowerUp extends SuperObject {
    public enum Type {
        EXTRA_BOMB, OBSTACLE, INVINCIBILITY, BLAST_EXPANSION, DETONATOR, GHOST, ROLLER_SKATE
    }

    protected GamePanel gp;
    protected BufferedImage image;
    protected String name;
    protected boolean active;
    protected int duration;
    protected Type type;
    protected int durationInFrames;
    protected Player activatedBy;
    private int remainingFrames;
    private boolean isVisible = false;

    /**
     * Initializes a power-up object at a specified location with a defined duration (converted to
     * frames) and type (such as DETONATOR, OBSTACLE, etc.). It also initializes the power-up's
     * image based on its type.
     * @param gp
     * @param type
     * @param x
     * @param y
     * @param duration
     */
    public PowerUp(GamePanel gp, Type type, int x, int y, int duration) {
        this.gp = gp;
        this.type = type;
        this.x = x;
        this.y = y;
        this.active = true;
        this.duration = duration;

        getBufferedImage();
        this.remainingFrames = convertSecondsToFrames(this.duration);
    }

    /**
     * applyEffect(Player player): Applies the specific effect of the power-up to the player.
     * This must be implemented by subclasses to define what happens when a power-up is
     * activated.
     * @param player
     */
    protected abstract void applyEffect(Player player);

    /**
     * Reverses the effect of the power-up on the player. This is
     * typically called when the power-up's duration expires or under certain game conditions.
     * @param player
     */
    protected abstract void removeEffect(Player player);

    protected void getBufferedImage() {
        try {
            switch (type) {
                case DETONATOR -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/detonator.png"));
                case OBSTACLE -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/obstacle.png"));
                case ROLLER_SKATE -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/rollerSkate.png"));
                case GHOST -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/ghost.png"));
                case INVINCIBILITY -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/invincibility.png"));
                case BLAST_EXPANSION -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/blastExpansion.png"));
                case EXTRA_BOMB -> this.image = ImageIO.read(getClass().getResourceAsStream("/powerups/extraBomb.png"));
                default -> System.out.println("No image available for this type.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activates the power-up, applying its effect to the player and resetting its duration counter. It
     * sets the power-up as active.
     * @param player
     */
    public void activate(Player player) {
        this.active = true;
        this.activatedBy = player;
        applyEffect(player);
        this.remainingFrames = durationInFrames;
    }

    /**
     * Deactivates the power-up, removing its effect from the player who activated it and setting
     * the power-up as inactive.
     */
    public void deactivate() {
        this.active = false;
        removeEffect(this.activatedBy);
        this.activatedBy = null;
    }

    /**
     * Converts a time duration from seconds to frames, based on the game's frames per second
     * (FPS), facilitating time-based effects in terms of game update cycles.
     * @param seconds
     * @return
     */
    int convertSecondsToFrames(int seconds) {
        return seconds * gp.FPS;
    }

    /**
     * Draws the power-up's image at its position on the game panel if it is set to be visible.
     * @param g2
     */
    public void draw(Graphics2D g2) {
        if (isVisible && image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }

    /**
     * Updates the state of the power-up each game cycle; decrements the remaining active
     * frames and deactivates the power-up if its time runs out.
     */
    public void update() {
        if (active && remainingFrames > 0) {
            remainingFrames--;
            if (remainingFrames <= 0) {
                deactivate();
            }
        }
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * Returns whether the power-up is visible on the game panel.
     * @return
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Returns whether the power-up is currently active.
     * @return
     */
    public boolean isActive() { return active; }

    public Type getType() { return type; }

    public int getX() { return x; }

    public int getY() { return y; }
}
