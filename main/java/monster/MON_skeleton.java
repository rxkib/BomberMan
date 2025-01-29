package monster;
import main.GamePanel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * The MON_skeleton class is a type of monster in the game, inheriting behaviors and properties from the MON_redSlime class.
 * It represents a skeleton monster with a unique identity and behavior. The skeleton monster occasionally makes incorrect decisions,
 * simulating error in its movement. This is achieved by overriding the behavior logic from the parent class.
 * The class also includes methods for initializing a new instance of a skeleton monster, setting its action, and getting a list of viable movement directions.
 */
public class MON_skeleton extends MON_redSlime {
    double errorProbability = 0.2;

    /**
     * Initializes a new instance of a skeleton monster, inheriting behaviors and
     * properties from the MON_redSlime class but changing the name to "Skeleton" to reflect its
     * unique identity.
     * @param gp
     */
    public MON_skeleton(GamePanel gp) {
        super(gp);
        this.name = "Skeleton";
    }

    /**
     * Purpose: Overrides the behavior logic to occasionally make incorrect decisions, simulating
     * error in the skeleton's movement.
     * Details: The method modifies the regular action cycle by introducing a probability
     * (errorProbability) where the skeleton might choose a random, potentially suboptimal
     */
    @Override
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= 120) {
            if (random.nextDouble() < errorProbability) {
                // wrong decision
                List<String> directions = getPossibleDirections();
                if (!directions.isEmpty()) {
                    Collections.shuffle(directions);
                    direction = directions.get(0);
                    //System.out.println("Made a wrong decision: " + direction);
                }
            } else { super.setAction(); }
            actionLockCounter = 0;
        }
        getImage();
    }

    /**
     * Purpose: Filters and returns a list of viable movement directions that are not currently
     * blocked by obstacles.
     * Details: Utilizes the CollisionChecker from GamePanel (gp.cChecker) to determine
     * which directions are currently feasible for movement.
     * @return
     */
    private List<String> getPossibleDirections() {
        List<String> directions = new ArrayList<>(Arrays.asList("up", "down", "left", "right"));
        directions.removeIf(dir -> !gp.cChecker.canMove(this, dir));
        return directions;
    }

    public void getImage(){
        up1 = setup("/monster/skeleton_up_1");
        up2 = setup("/monster/skeleton_up_2");
        down1 = setup("/monster/skeleton_down_1");
        down2 = setup("/monster/skeleton_down_2");
        left1 = setup("/monster/skeleton_left_2");
        left2 = setup("/monster/skeleton_left_2");
        right1 = setup("/monster/skeleton_right_1");
        right2 = setup("/monster/skeleton_right_2");
    }
}
