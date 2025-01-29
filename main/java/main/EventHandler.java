package main;
import entity.Player;

import java.awt.*;

/**
 * The EventHandler class is used to detect and handle events in the game. It provides
 * methods to check for player interactions with specific areas on the game map and trigger
 * events based on those interactions.
 */
public class EventHandler {
    GamePanel gp;
    Rectangle eventRect;
    int eventRectDefaultX, eventRectDefaultY;

    /**
     * Initializes the EventHandler with a reference to the GamePanel. Sets up a default
     * rectangular area (eventRect) that will be used to detect events. This rectangle's
     * position and size can be adjusted to target specific areas for event detection.
     * @param gp
     */
    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new Rectangle();
        eventRect.x = 23;
        eventRect.y = 23;
        eventRect.width = 2;
        eventRect.height = 2;
        eventRectDefaultX = eventRect.x;
        eventRectDefaultY = eventRect.y;
    }

    /**
     * This method serves as a placeholder within the EventHandler class. It is intended to
     * be overridden or filled in with specific logic to check for and handle various game
     * events based on the game state and interactions detected by the hit method.
     */
    public void checkEvent() {}

    /**
     * Checks if any player has interacted with the specified event area based on their
     * position and direction.
     * @param eventCol  The column of the tile where the event is located.
     * @param eventRow The row of the tile where the event is located.
     * @param reqDirection The required direction the player needs to be facing to trigger the
     * event. This can also be set to "any" to allow the event to be triggered from any
     * direction.
     * @return
     */
    public boolean hit(int eventCol, int eventRow, String reqDirection) {
        boolean hit = false;

        for (Player player : gp.players) { // Iterate over each player
            player.solidArea.x = player.x + player.solidArea.x;
            player.solidArea.y = player.y + player.solidArea.y;
            eventRect.x = eventCol * gp.tileSize + eventRect.x;
            eventRect.y = eventRow * gp.tileSize + eventRect.y;

            if (player.solidArea.intersects(eventRect)) {
                if (player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
                    hit = true;
                    break; // Exit the loop early since we found a hit
                }
            }

            player.solidArea.x = player.solidAreaDefaultX;
            player.solidArea.y = player.solidAreaDefaultY;
            eventRect.x = eventRectDefaultX;
            eventRect.y = eventRectDefaultY;
        }

        return hit;
    }
}
