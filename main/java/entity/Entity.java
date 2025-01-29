package entity;
import main.GamePanel;
import main.UtilityTool;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This class represents an Entity in the game.
 * An Entity is a base class for other game objects like players, monsters, etc.
 * It contains common properties like position, speed, images for different directions, and collision status.
 * It also contains methods for updating the entity's state and drawing it on the game panel.
 */
public class Entity {
    protected GamePanel gp;
    
    public int x,y;
    public int speed;
    
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // it describes an Image with an accesible buffer if image data.(we use this to store our image files)
    public String direction = "down";
    
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(0, 0, 48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int actionLockCounter = 0;

    public BufferedImage image, image2;
    public String name;
    public boolean collision = false;

    public boolean hitByCurrentExplosion = false;

    // FOR THE GHOST POWER-UP
    public boolean invincible = false;


    // character Status
    public int maxLife;
    public int currLife;

    /**
     * Constructor Entity(GamePanel gp)
     * Initializes a new entity with a reference to the game panel for accessing game-wide
     * properties and methods.
     * @param gp the gamepanel parameter
     */
    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * update() method
     * Intended for overriding; should specify entity-specific behaviors like movement or
     * interactions.
     */
    public void setAction() { }

    /**
     * update() method
     * Processes movement based on direction, handles collision checks with tiles, monsters,
     * and players, and manages animation cycles.
     */
    public void update() {
        setAction();

        collisionOn = false;

        //check main.java.tile collision
        gp.cChecker.checkTile(this);

        //check main.java.monster collision
        int checkEntityIndex = gp.cChecker.checkEntity(this, gp.monsters);

        //check player collision
        gp.cChecker.checkPlayer(this);

        // if collision is false, player can move
        if (!collisionOn) {
            switch (direction) {
                case "up"    -> y -= speed;
                case "down"  -> y += speed;
                case "left"  -> x -= speed;
                case "right" -> x += speed;
            }
        }

        spriteCounter++;

        if (spriteCounter > 12) {
            if      (spriteNum == 1) { spriteNum = 2; }
            else if (spriteNum == 2) { spriteNum = 1; }

            spriteCounter = 0;
        }
    }

    /**
     * Determines and draws the current sprite image based on the entity's direction and
     * animation state.
     * @param g2 the Graphics2D object to draw the entity's sprite
     */
    public void draw(Graphics2D g2){

        BufferedImage image = null;

        switch (direction) {
            case "up" -> {
                if (spriteNum == 1) { image = up1; }
                if (spriteNum == 2) { image = up2; }
            }
            case "down" -> {
                if (spriteNum == 1) { image = down1; }
                if (spriteNum == 2) { image = down2; }
            }
            case "left" -> {
                if (spriteNum == 1) { image = left1; }
                if (spriteNum == 2) { image = left2; }
            }
            case "right" -> {
                if (spriteNum == 1) { image = right1; }
                if (spriteNum == 2) { image = right2; }
            }
        }
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }

    /**
     * Loads, scales, and returns an image from a given path to use as the entity's sprite.
     * @param imagePath
     * @return the image to be used as the entity's sprite
     */
    public BufferedImage setup(String imagePath) {

        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}