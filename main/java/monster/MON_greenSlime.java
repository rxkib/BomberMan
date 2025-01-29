package monster;
import entity.Entity;
import main.GamePanel;
import java.util.Random;

/**
 * Initializes a new instance of MON_greenSlime with a reference to the game panel
 * (GamePanel). Sets initial attributes such as name, speed, life, and the collision area of the slime. Calls the
 * getImage() method to load specific images for different movement directions.
 */
public class MON_greenSlime extends Entity {
    /**
     * nitializes a new instance of MON_greenSlime with a reference to the game panel
     * (GamePanel). Sets initial attributes such as name, speed, life, and the collision area of the slime. alls the
     * getImage() method to load specific images for different movement directions.
     * @param gp
     */
    public MON_greenSlime(GamePanel gp) {
        super(gp);

        String name = "Green Slime";
        speed = 2;
        maxLife = 1;
        currLife = maxLife;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage(){
        up1 = setup("/monster/greenslime1");
        up2 = setup("/monster/greenslime2");
        down1 = setup("/monster/greenslime1");
        down2 = setup("/monster/greenslime2");
        left1 = setup("/monster/greenslime1");
        left2 = setup("/monster/greenslime2");
        right1 = setup("/monster/greenslime1");
        right2 = setup("/monster/greenslime2");
    }

    public void setAction() {
        actionLockCounter++;
        Random random = new Random();

        if (actionLockCounter >= random.nextInt(60) + 60) {
            int i = random.nextInt(100) + 1;

            String proposedDirection = null;
            if (i <= 20) {
                proposedDirection = "up";
            } else if (i <= 40) {
                proposedDirection = "down";
            } else if (i <= 70) {
                proposedDirection = "left";
            } else if (i > 70) {
                proposedDirection = "right";
            }

            // Check if the proposed direction is feasible
            if (gp.cChecker.canMove(this, proposedDirection)) {
                direction = proposedDirection;
                //System.out.println("New Direction: " + direction);
            } else {
                //System.out.println("Proposed direction " + proposedDirection + " is blocked.");
            }

            speed = random.nextInt(3) + 1; // Speed varies from 1 to 3
            //System.out.println("New Speed: " + speed);

            actionLockCounter = 0;
        }
    }

}