package main;
import monster.MON_greenSlime;
import monster.MON_orc;
import monster.MON_redSlime;
import monster.MON_skeleton;
import object.OBJ_ghost;
import powerups.*;

import java.awt.*;
import java.util.Random;

/**
 * AssetSetter class is responsible for setting up game assets such as monsters, ghosts, and
 * power-ups at predefined locations on the game map. It initializes ghost objects, spawns
 * different types of monsters, and randomly distributes various types of power-ups in available
 */
public class AssetSetter {
    GamePanel gp;

    /**
     * Initializes an instance of AssetSetter with a reference to the GamePanel object to
     * access and manipulate game elements.
     * @param gp
     */
    public AssetSetter(GamePanel gp) { this.gp = gp; }

    /**
     * Places ghost objects at predefined positions on the game map. This method
     * initializes two ghost entities and positions them using the game panel's tile size for
     * precise placement.
     */
    public void setObject() {
        gp.entities.add(new OBJ_ghost(gp));
        gp.entities.get(0).x = 2 * gp.tileSize;
        gp.entities.get(0).y = 9 * gp.tileSize;

        gp.entities.add(new OBJ_ghost(gp));
        gp.entities.get(1).x = 6 * gp.tileSize;
        gp.entities.get(1).y = 3 * gp.tileSize;
    }

    /**
     * Spawns different types of monsters at specific locations on the game map. This
     * method sets up an array of monsters, including red slime, green slime, orc, and
     * skeleton, by positioning them using the game panel's tile size.
     */
    public void setMonster() {
        gp.monsters[0] = new MON_redSlime(gp);
        gp.monsters[0].x = gp.tileSize;
        gp.monsters[0].y = 10 * gp.tileSize;

        gp.monsters[1] = new MON_greenSlime(gp);
        gp.monsters[1].x = 14 * gp.tileSize;
        gp.monsters[1].y = 2 * gp.tileSize;

        gp.monsters[2] = new MON_orc(gp);
        gp.monsters[2].x = 3 * gp.tileSize;
        gp.monsters[2].y = 2 * gp.tileSize;

        gp.monsters[3] = new MON_skeleton(gp);
        gp.monsters[3].x = 3 * gp.tileSize;
        gp.monsters[3].y = 5 * gp.tileSize;
    }

    /**
     * Randomly distributes various types of power-ups in available box locations on the
     * game map. It uses the Random class to select random box locations for placing
     * power-ups such as extra bombs, blast expansion, detonators, roller skates,
     * invincibility, ghost abilities, and obstacles. If there are no more boxes available, the
     * loop breaks early.
     */
    public void setPowerUp() {
        Random rand = new Random();
        
        for (PowerUp.Type type : PowerUp.Type.values()) {
            if (gp.boxes.isEmpty()) break;
            
            int index = rand.nextInt(gp.boxes.size());
            Point box = gp.boxes.get(index);
            
            int x = box.x * gp.tileSize;
            int y = box.y * gp.tileSize;

            switch (type) {
                case EXTRA_BOMB      -> gp.powerUps.add(new ExtraBomb(gp, x, y));
                case BLAST_EXPANSION -> gp.powerUps.add(new BlastExpansion(gp, x, y));
                case DETONATOR       -> gp.powerUps.add(new Detonator(gp, x, y));
                case ROLLER_SKATE    -> gp.powerUps.add(new RollerSkate(gp, x, y));
                case INVINCIBILITY   -> gp.powerUps.add(new Invincibility(gp, x, y));
                case GHOST           -> gp.powerUps.add(new Ghost(gp, x, y));
                case OBSTACLE        -> gp.powerUps.add(new Obstacle(gp, x, y));
                default              -> throw new IllegalStateException("Unexpected value: " + type);
            }

            gp.boxes.remove(index);
        }
    }
}