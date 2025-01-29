package tile;
import java.awt.image.BufferedImage;

/**
 * Purpose: Represents a single tile in the game map.
 * Details: Each tile has an image and a boolean flag indicating whether it is a collision tile.
 */
public class Tile {
    public BufferedImage image;
    public boolean collision = false;
}
