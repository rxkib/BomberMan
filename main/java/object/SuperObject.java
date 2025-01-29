package object;
import main.GamePanel;
import main.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class represents a SuperObject in the game.
 * SuperObject is a base class for other game objects.
 */
public class SuperObject {
    public BufferedImage image, image2;
    public String name;
    public boolean collision = false;
    public int x, y;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    UtilityTool uTool = new UtilityTool();

    /**
     * Draws the object.
     * @param g2 The Graphics2D object to draw on.
     * @param gp The game panel instance.
     */
    public void draw(Graphics2D g2, GamePanel gp){
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }

    /**
     * Updates the state of the object.
     */
    public void update() { }
}

