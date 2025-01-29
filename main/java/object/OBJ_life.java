package object;
import entity.Entity;
import main.GamePanel;

/**
 * This class represents a Life object in the game.
 * It extends the Entity class, inheriting its properties and methods.
 */
public class OBJ_life extends Entity {
    /**
     * Constructor for the Life object.
     * @param gp The game panel instance.
     */
    public  OBJ_life(GamePanel gp) {
        super(gp);
        name = "Life";
        image = setup("/main/resources/objects/life");
        image2 = setup("/main/resources/objects/blankLife");
    }
}
