package object;
import entity.Entity;
import main.GamePanel;
import entity.Player;
import org.w3c.dom.css.Rect;
import powerups.PowerUp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

/**
 * This class represents a Bomb object in the game.
 * It extends the SuperObject class, inheriting its properties and methods.
 * The Bomb object has a timer that counts down to an explosion, which affects entities within a certain radius.
 * The explosion can also trigger other bombs within a certain range.
 * The bomb can be forced to explode before the timer runs out.
 * The bomb's state is updated every frame, and it is drawn on the game panel.
 */
public class OBJ_bomb extends SuperObject {
    GamePanel gp;
    int timer = 3; // timer until explosion in seconds
    int timerCD = 0;
    int  delayBeforeExplode = 30;
    boolean active = true;
    boolean exploding = false;
    public boolean hasExploded = false;
    boolean delayed = false;
    boolean delayStarted = false;
    public Player owner;
    public boolean ignoreCollisionWithOwner = true;
    public BufferedImage explosionImage, fireballDown, fireballUp, fireballLeft, fireballRight;

    /**
     * Constructor for the bomb object
     * @param gp
     * @param owner
     * @param x
     * @param y
     */
    public OBJ_bomb(GamePanel gp, Player owner, int x, int y) {
        name = "Bomb";
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.owner = owner;
        //owner.bombActive = true;
        collision = true;
        solidArea = new Rectangle(x, y, gp.tileSize, gp.tileSize);

        getBufferedImage();
    }

    private void getBufferedImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/bomb.png"));
            explosionImage = ImageIO.read(getClass().getResourceAsStream("/objects/bomb_explosion.png"));
            fireballDown = ImageIO.read(getClass().getResourceAsStream("/objects/fireball_down.png"));
            fireballUp = ImageIO.read(getClass().getResourceAsStream("/objects/fireball_up.png"));
            fireballLeft = ImageIO.read(getClass().getResourceAsStream("/objects/fireball_left.png"));
            fireballRight = ImageIO.read(getClass().getResourceAsStream("/objects/fireball_right.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the bomb is active
     * @return
     */
    public boolean isActive() { return active; }

    /**
     * Explode the bomb
     */
    public void explode() {
        if (!hasExploded) {
            exploding = true;
            owner.bombExploded();
            hasExploded = true;
            this.image = explosionImage;
            affectEntities();
            triggerAdjacentBombs();
        }
    }

    /**
     * Affects entities within the bomb's blast radius.
     */
    public void affectEntities() {
        int range = owner.bombBlastRadius;
        for (int dir = 0; dir < 4; dir++) {
            for (int i = 0; i <= range; i++) {
                int dx = (dir == 2) ? -i : (dir == 3) ? i : 0;
                int dy = (dir == 0) ? -i : (dir == 1) ? i : 0;
                checkAndAffectEntity(x + dx * gp.tileSize, y + dy * gp.tileSize);
            }
        }
    }

    /**
     * Checks and affects an entity at a specific location.
     * @param targetX The x-coordinate of the target location.
     * @param targetY The y-coordinate of the target location.
     */
    private void checkAndAffectEntity(int targetX, int targetY) {
        Rectangle explosionArea = new Rectangle(targetX, targetY, gp.tileSize, gp.tileSize);
        for (int i = 0; i < gp.players.length; i++) {
            Player player = gp.players[i];

            if (player == null) continue;

            Rectangle playerArea = new Rectangle(player.x, player.y, player.solidArea.width, player.solidArea.height);

            if (playerArea.intersects(explosionArea) && player.currLife > 0 && !player.hitByCurrentExplosion && !player.isInvincible) {
                System.out.println("Player " + player.playerNum + " hit by bomb, before currLife: " + player.currLife);
                player.currLife--;
                System.out.println("Player " + player.playerNum + " hit by bomb, after currLife: " + player.currLife);
                player.hitByCurrentExplosion = true;
            }
        }



        for (int i = 0; i < gp.monsters.length; i++) {
            Entity monster = gp.monsters[i];

            if (monster == null) continue;

            Rectangle monsterArea = new Rectangle(monster.x, monster.y, monster.solidArea.width, monster.solidArea.height);

            if (monsterArea.intersects(explosionArea) && monster.currLife > 0 && !monster.hitByCurrentExplosion) {
                monster.currLife--;
                monster.hitByCurrentExplosion = true;
            }
        }
    }

    /**
     * Triggers adjacent bombs within a certain range.
     */
    private void triggerAdjacentBombs() {
        int range = 2; // Same as the fireball range
        for (int dir = 0; dir < 4; dir++) {
            for (int i = 1; i <= range; i++) {
                int dx = (dir == 2) ? -i : (dir == 3) ? i : 0;
                int dy = (dir == 0) ? -i : (dir == 1) ? i : 0;
                triggerBombAt(x + dx * gp.tileSize, y + dy * gp.tileSize);
            }
        }
    }

    /**
     * Triggers a bomb at a specific location.
     * @param checkX The x-coordinate of the location to check.
     * @param checkY The y-coordinate of the location to check.
     */
    private void triggerBombAt(int checkX, int checkY) {
        Rectangle checkArea = new Rectangle(checkX, checkY, gp.tileSize, gp.tileSize);
        for (SuperObject obj : gp.objs) {
            if (obj instanceof OBJ_bomb bomb && obj != this) {
                Rectangle bombArea = new Rectangle(bomb.x, bomb.y, gp.tileSize, gp.tileSize);
                if (bombArea.intersects(checkArea)) {
                    bomb.forceExplode();
                }
            }
        }
    }

    /**
     * Forces the bomb to explode.
     */
    public void forceExplode() {
        if (!exploding && !delayed) {
            delayed = true;
            delayStarted = true;
            timerCD = Math.max(timerCD, timer * gp.FPS - delayBeforeExplode);  // Ensure the explosion happens within the next second (if not already exploding
        }
    }

    /**
     * Draws fireballs in all directions from the bomb.
     * @param g2 The Graphics2D object to draw on.
     * @param gp The game panel instance.
     */
    private void drawFireballs(Graphics2D g2, GamePanel gp) {
        int range = owner.bombBlastRadius;  // Maximum range of fireballs

        // Handling each direction independently
        for (int dir = 0; dir < 4; dir++) {
            for (int i = 1; i <= range; i++) {
                int dx = 0, dy = 0;
                if      (dir == 0) { dy = -i; } // Up
                else if (dir == 1) { dy = i;  } // Down
                else if (dir == 2) { dx = -i; } // Left
                else               { dx = i;  } // Right

                int targetX = x + dx * gp.tileSize;
                int targetY = y + dy * gp.tileSize;
                int result = canPlaceFireball(targetX, targetY, gp);
                if (result == 0) {
                    break;  // Encountered a wall, stop drawing in this direction
                }

                // Select the correct fireball image
                BufferedImage img = switch (dir) {
                    case 0 -> fireballUp;
                    case 1 -> fireballDown;
                    case 2 -> fireballLeft;
                    case 3 -> fireballRight;
                    default -> null;
                };

                g2.drawImage(img, targetX, targetY, gp.tileSize, gp.tileSize, null);

                if (result == 2) break;  // Encountered a box, draw the fireball but stop further fireballs
            }
        }
    }

    /**
     * Checks if a fireball can be placed at a specific location.
     * @param x The x-coordinate of the location.
     * @param y The y-coordinate of the location.
     * @param gp The game panel instance.
     * @return 0 if a fireball cannot be placed, 1 if a fireball can be placed and continue, 2 if a fireball can be placed but stop further.
     */
    private int canPlaceFireball(int x, int y, GamePanel gp) {
        int col = x / gp.tileSize;
        int row = y / gp.tileSize;

        if (col <= 0 || row <= 0 || col >= gp.maxScreenCol - 1 || row >= gp.maxScreenRow - 1) {
            return 0;
        }

        int tileNum = gp.tileM.mapTileNum[col][row];

        if (tileNum == 1) {  // Wall
            return 0;  // Stop drawing fireballs completely
        } else if (tileNum == 2) {  // Box
            gp.tileM.mapTileNum[col][row] = 0;  // Change the box to grass (should stop further fireballs in this direction)

            Rectangle explosionArea = new Rectangle(x, y, gp.tileSize, gp.tileSize);
            for (PowerUp powerUp : gp.powerUps) {
                Rectangle powerUpArea = new Rectangle(powerUp.getX(), powerUp.getY(), gp.tileSize, gp.tileSize);
                if (powerUpArea.intersects(explosionArea)) {
                    powerUp.setVisible(true);
                }
            }

            return 2;  // Draw fireball but stop further
        }

        return 1;  // Normal tile, draw fireball and continue
    }

    /**
     * Updates the state of the bomb.
     */
    public void update() {
        if (!owner.hasDetonator) {
            if (active && !exploding && !delayed) {
                timerCD++;
                if (timerCD >= timer * gp.FPS) {
                    explode();
                }
            }
        }
        if (exploding) {
            timerCD++;
            if (timerCD >= (timer + 1) * gp.FPS) {
                active = false;
                exploding = false;
                delayed = false;
                timerCD = 0; // Reset timer for reuse
            }
        }
        if (delayed && timerCD >= (timer * gp.FPS - delayBeforeExplode)) {
            explode();
        }

    }

    /**
     * Draws the bomb and its explosion.
     * @param g2 The Graphics2D object to draw on.
     * @param gp The game panel instance.
     */
    public void draw(Graphics2D g2, GamePanel gp) {
        if (active || exploding) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        if (exploding) {
            drawFireballs(g2, gp);
        }
    }
}