package object;
import entity.Entity;
import main.GamePanel;

/**
 * This class represents a Ghost object in the game.
 * It extends the Entity class, inheriting its properties and methods.
 */
public class OBJ_ghost extends Entity {
    /**
     * Constructor for the Ghost object.
     * @param gp The game panel instance.
     */
    public  OBJ_ghost(GamePanel gp) {
        super(gp);
        name = "Ghost";
        down1 = setup("/objects/ghost");
    }
}
