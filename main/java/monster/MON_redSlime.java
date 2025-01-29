package monster;
import entity.Entity;
import main.GamePanel;
import java.awt.*;
import java.util.*;
import java.util.List;
import entity.Player;

/**
 * The MON_redSlime class extends the Entity class and represents a red slime monster in the game.
 * It includes methods for initializing a new instance of a red slime, updating the state of the red slime each frame,
 * checking if the red slime should stop moving based on the proximity of the nearest player, stopping the movement of the red slime,
 * moving the slime in its current direction if there are no obstacles, getting the image for the red slime, setting the action of the red slime,
 * attempting to move directly towards the nearest player by evaluating which direction will decrease the distance to the player the most,
 * moving the slime in a specified direction, calculating the relevance of a direction based on how directly it leads towards a player,
 * choosing a random direction to move when no clear path towards the player is available, searching for the closest player,
 * and getting the next step towards the player.
 */
public class MON_redSlime extends Entity {
    GamePanel gp;
    Random random = new Random();
    public boolean isMoving = true;

    /**
     * Initializes the Red Slime with specific attributes such as name, speed, life, and
     * collision area.
     * Calls getImage() to load images for different movement animations.
     * @param gp
     */
    public MON_redSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;

        String name = "Red Slime";
        speed = 3;
        maxLife = 1;
        currLife = maxLife;

        solidArea = new Rectangle(3, 18, 42, 30);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Updates the state of the Red Slime each frame, deciding on stopping or moving
     * based on the proximity of the nearest player.
     */
    @Override
    public void update() {
        setAction();

        Player nearestPlayer = findNearestPlayer();
        if (nearestPlayer != null && shouldStop(nearestPlayer)) {
            stopMovement();
        } else { moveInCurrentDirection(); }
    }

    /**
     * Checks if the Red Slime should stop moving based on the proximity of the nearest player.
     * @param player
     * @return
     */
    private boolean shouldStop(Player player) {
        int dx = Math.abs(player.x - this.x);
        int dy = Math.abs(player.y - this.y);

        if (dx < gp.tileSize && dy > gp.tileSize && dy < 3 * gp.tileSize) {
            return true;
        }
        return false;
    }

    /**
     * Stops the movement of the Red Slime.
     */
    private void stopMovement() {
        isMoving = false;
    }

    /**
     * Moves the slime in its current direction if there are no obstacles, using collision
     * checking from the GamePanel.
     */
    private void moveInCurrentDirection() {
        switch (direction) {
            case "up":
                if (gp.cChecker.canMove(this, "up")) {
                    y -= speed;
                }
                break;
            case "down":
                if (gp.cChecker.canMove(this, "down")) {
                    y += speed;
                    //System.out.println("Moved down to: " + y);
                }
                break;
            case "left":
                if (gp.cChecker.canMove(this, "left")) {
                    x -= speed;
                    //System.out.println("Moved left to: " + x);
                }
                break;
            case "right":
                if (gp.cChecker.canMove(this, "right")) {
                    x += speed;
                    //System.out.println("Moved right to: " + x);
                }
                break;
            default:
                //System.out.println("No valid direction to move.");
                break;
        }
    }

    public void getImage(){
        up1 = setup("/monster/redslime1");
        up2 = setup("/monster/redslime2");
        down1 = setup("/monster/redslime1");
        down2 = setup("/monster/redslime2");
        left1 = setup("/monster/redslime1");
        left2 = setup("/monster/redslime2");
        right1 = setup("/monster/redslime1");
        right2 = setup("/monster/redslime2");
    }

    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= 120) {
            Player nearestPlayer = findNearestPlayer();
//            if (nearestPlayer != null) {
//                System.out.println("Nearest Player at: (" + nearestPlayer.x + ", " + nearestPlayer.y + ")");
//            } else {
//                System.out.println("No player found.");
//            }

            boolean moved = attemptMoveTowardsPlayer(nearestPlayer);
            if (!moved) {
                //System.out.println("Obstacle detected, wandering randomly.");
                wanderRandomly();
            }
            actionLockCounter = 0;
        }
    }

    /**
     * Attempts to move directly towards the nearest player by evaluating which direction
     * will decrease the distance to the player the most, considering obstacles.
     * @param player
     * @return
     */
    private boolean attemptMoveTowardsPlayer(Player player) {
        if (player == null) return false;

        int dx = player.x - this.x;
        int dy = player.y - this.y;
        //System.out.println("Distance to player: dx = " + dx + ", dy = " + dy);
        List<String> directions = Arrays.asList("up", "down", "left", "right");
        directions.sort((a, b) -> Double.compare(relevance(b, dx, dy), relevance(a, dx, dy)));

        for (String dir : directions) {
            //System.out.println("Trying direction: " + dir + " with relevance: " + relevance(dir, dx, dy));
            if (gp.cChecker.canMove(this, dir)) {
                direction = dir;
                moveInDirection(dir);
                //System.out.println("Successful move towards player in direction: " + dir);
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the slime in a specified direction, adjusting its coordinates based on its
     * current speed.
     * @param dir
     */
    private void moveInDirection(String dir) {
        switch (dir) {
            case "up": y -= speed; break;
            case "down": y += speed; break;
            case "left": x -= speed; break;
            case "right": x += speed; break;
        }
    }

    /**
     * Calculates the relevance of a direction based on how directly it leads towards a
     * player, used to sort potential movement directions.
     * @param direction
     * @param dx
     * @param dy
     * @return
     */
    private double relevance(String direction, int dx, int dy) {
        switch (direction) {
            case "up":
                return dy < 0 ? Math.abs(dy) : -Math.abs(dy);
            case "down":
                return dy > 0 ? Math.abs(dy) : -Math.abs(dy);
            case "left":
                return dx < 0 ? Math.abs(dx) : -Math.abs(dx);
            case "right":
                return dx > 0 ? Math.abs(dx) : -Math.abs(dx);
            default:
                return 0;
        }
    }

    /**
     * Chooses a random direction to move when no clear path towards the player is
     * available, ensuring the slime remains active.
     */
    private void wanderRandomly() {
        List<String> directions = Arrays.asList("up", "down", "left", "right");
        Collections.shuffle(directions);
        for (String dir : directions) {
            if (gp.cChecker.canMove(this, dir)) {
                direction = dir;
                break;
            }
        }
        System.out.println("Wandering direction: " + direction);
    }

    /**
     * Searches for the closest player by calculating distances from all available players,
     * returning the nearest one.
     * @return
     */
    private Player findNearestPlayer() {
        Player nearestPlayer = null;
        double minDist = Double.MAX_VALUE;
        for (Player player : gp.players) {
            if (player != null) {
                double dist = Math.hypot(player.x - x, player.y - y);
                if (dist < minDist) {
                    minDist = dist;
                    nearestPlayer = player;
                }
            }
        } return nearestPlayer;
    }

    /**
     * Updates the state of the Red Slime each frame, deciding on stopping or moving
     * based on the proximity of the nearest player.
     */
    private Point getNextStepTowardsPlayer(Player player) {
        if (player == null) return null;
        int dx = player.x - this.x;
        int dy = player.y - this.y;
        List<String> directions = Arrays.asList("up", "down", "left", "right");
        directions.sort((d1, d2) -> rankDirection(d1, dx, dy) - rankDirection(d2, dx, dy));

        for (String dir : directions) {
            if (gp.cChecker.canMove(this, dir)) {
                switch (dir) {
                    case "right": return new Point(x + speed, y);
                    case "left": return new Point(x - speed, y);
                    case "down": return new Point(x, y + speed);
                    case "up": return new Point(x, y - speed);
                }
            }
        } return null;
    }

    /**
     * Ranks the direction based on how directly it leads towards the player.
     * @param dir
     * @param dx
     * @param dy
     * @return
     */
    private int rankDirection(String dir, int dx, int dy) {
        switch (dir) {
            case "up": return dy > 0 ? Integer.MAX_VALUE : -dy;
            case "down": return dy < 0 ? Integer.MAX_VALUE : dy;
            case "left": return dx > 0 ? Integer.MAX_VALUE : -dx;
            case "right": return dx < 0 ? Integer.MAX_VALUE : dx;
            default: return Integer.MAX_VALUE;
        }
    }
}
