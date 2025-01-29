package monster;
import entity.Entity;
import main.GamePanel;
import object.OBJ_bomb;
import object.SuperObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * The MON_orc class extends the Entity class and represents an orc monster in the game.
 * It includes methods for initializing a new instance of an orc, updating the state of the orc each frame,
 * checking if the orc's path is blocked or if the next step would bring the orc too close to the map's edge,
 * changing the orc's movement direction randomly, checking if moving in the specified direction leads to a non-walkable tile,
 * testing if moving in a certain direction would result in a collision with the map edge, and managing sprite animation by cycling through sprite images.
 */
public class MON_orc extends Entity {
    Random random = new Random();

    /**
     * Purpose: Initializes a new instance of an orc with base settings for movement
     * speed, life, and collision areas.
     * Details: Sets specific attributes like name, movement speed, life statistics, and
     * initializes the sprite images for various movement directions.
     * @param gp
     */
    public MON_orc(GamePanel gp) {
        super(gp);

        name = "Orc";
        speed = 1;
        maxLife = 1;
        currLife = maxLife;

        solidArea = new Rectangle(3, 18, 42, 30);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage(){
        up1 = setup("/monster/orc_up_1");
        up2 = setup("/monster/orc_up_2");
        down1 = setup("/monster/orc_down_1");
        down2 = setup("/monster/orc_down_2");
        left1 = setup("/monster/orc_left_1");
        left2 = setup("/monster/orc_left_2");
        right1 = setup("/monster/orc_right_1");
        right2 = setup("/monster/orc_right_2");
    }

    public void setAction() {
        if(noGrassTileAhead(direction) || isPathBlocked() || isNextStepAtEdge()) {
            changeDirectionRandomly();
        }
    }

    /**
     * Checks if the orc's next move leads to a collision with bombs or
     * map boundaries.
     * @return
     */
    private boolean isPathBlocked() {
        int nextX = x + (direction.equals("right") ? speed : direction.equals("left") ? -speed : 0);
        int nextY = y + (direction.equals("down") ? speed : direction.equals("up") ? -speed : 0);
        Rectangle expectedPosition = new Rectangle(nextX + solidArea.x, nextY + solidArea.y, solidArea.width, solidArea.height);

        // Check for bombs in the path
        for (SuperObject obj : gp.objs) {
            if (obj instanceof OBJ_bomb) {
                Rectangle objArea = new Rectangle(obj.x, obj.y, gp.tileSize, gp.tileSize);
                if (expectedPosition.intersects(objArea)) {
                    return true; // Bomb detected in the path
                }
            }
        }

        // Check for map edges (similar to previous implementations)
        return (nextX < 0 || nextY < 0 || nextX > gp.screenWidth - solidArea.width || nextY > gp.screenHeight - solidArea.height);
    }

    /**
     * Determines if the next move would bring the orc too close to
     * the map's edge, preventing movement in that direction.
     * @return
     */
    private boolean isNextStepAtEdge() {
        int nextX = x;
        int nextY = y;

        switch (direction) {
            case "up"    -> nextY -= speed;
            case "down"  -> nextY += speed;
            case "left"  -> nextX -= speed;
            case "right" -> nextX += speed;
        }

        // Calculate edges of the solid area based on direction
        int nextLeftX   = nextX + solidArea.x;
        int nextRightX  = nextX + solidArea.x + solidArea.width;
        int nextTopY    = nextY + solidArea.y;
        int nextBottomY = nextY + solidArea.y + solidArea.height;

        // Convert these edge positions to main.java.tile indices
        int leftTile   = nextLeftX / gp.tileSize;
        int rightTile  = (nextRightX - 1) / gp.tileSize; // Subtract 1 to stay within bounds
        int topTile    = nextTopY / gp.tileSize;
        int bottomTile = (nextBottomY - 1) / gp.tileSize; // Subtract 1 to stay within bounds

        // Check if any of these indices are out of the acceptable range
        return leftTile < 1 || rightTile >= gp.maxScreenCol - 1 || topTile < 1 || bottomTile >= gp.maxScreenRow - 1;  // The main.java.monster is about to step over an edge wall
    }

    /**
     * Changes the orc's movement direction randomly,
     * ensuring it does not move into blocked paths.
     */
    private void changeDirectionRandomly() {
        ArrayList<String> directions = new ArrayList<>(Arrays.asList("up", "down", "left", "right"));
        Collections.shuffle(directions);

        ArrayList<String> safeDirections = new ArrayList<>(directions);
        safeDirections.removeIf(this::wouldCollideWithEdge);
        safeDirections.removeIf(this::noGrassTileAhead);

        // Use safe directions if available, otherwise, use potentially colliding ones as a last resort
        if (!safeDirections.isEmpty()) {
            Collections.shuffle(safeDirections);  // Shuffle to choose randomly among safe directions
            direction = safeDirections.get(random.nextInt(safeDirections.size()));
        } else {
            // If no "safe" directions, pick any remaining one to avoid being stuck
            direction = directions.get(random.nextInt(directions.size()));
        }
    }

    /**
     * Checks if moving in the specified direction leads to a non-walkable tile
     * (e.g., a tile without grass).
     * Details: This method is part of the decision-making process for changing movement
     * direction.
     * @param direction
     * @return
     */
    private boolean noGrassTileAhead(String direction) {
        int dx = 0, dy = 0;

        switch (direction) {
            case "up"    -> dy = -1;
            case "down"  -> dy =  1;
            case "left"  -> dx = -1;
            case "right" -> dx =  1;
        }

        // Start checking from the next main.java.tile in the specified direction
        int checkX = (x / gp.tileSize) + (direction.equals("up") || direction.equals("left") ? 0 : dx);;
        int checkY = (y / gp.tileSize) + (direction.equals("up") || direction.equals("left") ? 0 : dy);

        // Continue checking in the same direction until the edge of the map
        while (checkX >= 0 && checkX < gp.maxScreenCol && checkY >= 0 && checkY < gp.maxScreenRow) {

            if (gp.tileM.mapTileNum[checkX][checkY] == 0) {
                return false;
            }
            checkX += dx;
            checkY += dy;

        }

        return true;
    }

    /**
     * Purpose: Tests if moving in a certain direction would result in a collision with the
     * map edge.
     * Details: Useful in the decision-making process to avoid paths leading directly into
     * boundaries.
     * @param testDirection
     * @return
     */
    private boolean wouldCollideWithEdge(String testDirection) {
        int testX = x, testY = y;
        switch (testDirection) {
            case "up"    -> testY -= speed;
            case "down"  -> testY += speed;
            case "left"  -> testX -= speed;
            case "right" -> testX += speed;
        }
        return isNextStepAtEdge(testX, testY);
    }

    /**
     * Purpose: Overloaded version of isNextStepAtEdge() that checks for boundary
     * collisions based on hypothetical next positions.
     * Details: Supports the wouldCollideWithEdge method by providing a way to predict
     * collisions before making a move.
     * @param testX
     * @param testY
     * @return
     */
    private boolean isNextStepAtEdge(int testX, int testY) {
        int leftTile   = (testX + solidArea.x) / gp.tileSize;
        int rightTile  = (testX + solidArea.x + solidArea.width - 1) / gp.tileSize;
        int topTile    = (testY + solidArea.y) / gp.tileSize;
        int bottomTile = (testY + solidArea.y + solidArea.height - 1) / gp.tileSize;

        return leftTile < 1 || rightTile >= gp.maxScreenCol - 1 || topTile < 1 || bottomTile >= gp.maxScreenRow - 1;
    }

    /**
     * Purpose: Regularly updates the orc's state, including its position based on the
     * current speed and direction.
     * Details: Also manages the animation cycle by updating sprite frames.
     */
    @Override
    public void update() {
        setAction();
        switch (direction) {
            case "up"    -> y -= speed;
            case "down"  -> y += speed;
            case "left"  -> x -= speed;
            case "right" -> x += speed;
        }
        updateSprite();
    }

    /**
     * Purpose: Manages sprite animation by cycling through sprite images.
     * • Details: Adjusts spriteNum based on spriteCounter to switch between animation
     * frames.
     */
    private void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = 1 + spriteNum % 2;  // Cycle between two sprites for animation
            spriteCounter = 0;
        }
    }
}
