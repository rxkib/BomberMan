package main;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class provides utility methods for the game.
 * Currently, it includes a method for scaling images.
 */
public class UtilityTool {
    /**
     * This method creates a new BufferedImage of the specified width and height, then
     * draws the original image onto the new image at the new dimensions. The
     * Graphics2D object used for drawing is then disposed to release system resources.
     * @param original
     * @param width
     * @param height
     * @return
     */
    public BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return scaledImage;
    }
}
