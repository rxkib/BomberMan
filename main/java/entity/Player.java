package entity;
import main.GamePanel;
import main.KeyHandler;
import object.OBJ_bomb;
import object.SuperObject;
import powerups.PowerUp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import java.awt.*;

/**
 * This class represents a Player in the game.
 * It extends the Entity class, inheriting its properties and methods.
 * The Player class includes properties for managing player-specific behaviors such as movement,
 * collision checks, power-up interactions, and bomb placement.
 */
public class Player extends Entity {
    KeyHandler keyH;
    public int playerNum;
    public int bombLimit;
    public int bombCount;
    public int obstacleLimit = 0;
    public int invincibilityDuration = 0;
    public int ghostDuration = 0;
    public boolean hasDetonator = false;
    public boolean isInvincible = false;
    public boolean isGhost = false;
    public BufferedImage invincibilitySprite;
    public BufferedImage ghostSprite;
    public boolean isBlinking = false;
    public int bombBlastRadius = 2;
    public List<Point> obstaclesPlaced = new ArrayList<>();
    List<OBJ_bomb> bombsPlaced = new ArrayList<>();

    public Point lastPosition;

    /**
     * Initializes a player with a specific number within a game, linking to the game's
     * control handling system. Sets up player-specific images and default properties.
     * @param gp
     * @param keyH
     * @param playerNum
     */
    public Player(GamePanel gp, KeyHandler keyH, int playerNum) {
        super(gp);
        this.keyH = keyH;
        this.playerNum = playerNum;

        solidArea = new Rectangle(10, 18, 28, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            invincibilitySprite = ImageIO.read(getClass().getResourceAsStream("/powerups/invincibility.png"));
            ghostSprite = ImageIO.read(getClass().getResourceAsStream("/powerups/ghost.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setDefaultValues();
        getPlayerImage();
    }

    /**
     * Sets the default values for player properties such as position, speed, life, and bomb
     * limits based on the player number.
     */
    public void setDefaultValues() {
        switch (playerNum) {
            case 1 -> {
                x = 50;
                y = 50;
            }
            case 2 -> {
                x = 200;
                y = 400;
            }
            case 3 -> {
                x = 485;
                y = 470;
            }
        }

        speed = 4;
        direction = "down";
        maxLife = 3;
        currLife = maxLife;
        bombLimit = 1;
        bombCount = 0;
    }

    /**
     * Loads and sets up the sprite images for the player based on their specific color,
     * which is determined by the player number.
     */
    public void getPlayerImage() {
        String color = switch (playerNum) {
            case 1 -> "blue";
            case 2 -> "orange";
            case 3 -> "pink";
            default -> "red";
        };

        up1 = setup("/player/" + color + "_up1");
        up2 = setup("/player/" + color + "_up2");
        down1 = setup("/player/" + color + "_down1");
        down2 = setup("/player/" + color + "_down2");
        left1 = setup("/player/" + color + "_left1");
        left2 = setup("/player/" + color + "_left2");
        right1 = setup("/player/" + color + "_right1");
        right2 = setup("/player/" + color + "_right2");
    }

    /**
     * Allows the player to place a bomb if they haven't reached their bomb limit, adds it to
     * the game, and updates the bomb count.
     */
    public void placeBomb() {
        if (bombCount < bombLimit) {
            OBJ_bomb newBomb = new OBJ_bomb(gp, this, x, y);
            bombsPlaced.add(newBomb);
            gp.objs.add(newBomb);
            bombCount++;
        }
    }

    /**
     * Triggers the detonation of all placed bombs by the player, clears the bombs list, and
     * resets the detonator status.
     */
    public void detonateBombs() {
        for (OBJ_bomb bomb : bombsPlaced) {
            bomb.explode();
        }
        hasDetonator = false;
        bombsPlaced.clear();
    }

    /**
     * Places an obstacle on the game map at the player's last position if the obstacle limit
     * hasn't been reached and the tile is not already occupied.
     */
    public void placeObstacle() {
        if (obstaclesPlaced.size() < obstacleLimit && lastPosition != null) {
            if (gp.tileM.mapTileNum[lastPosition.x][lastPosition.y] == 0 && !gp.isTileOccupied(lastPosition.x, lastPosition.y, this)) {
                obstaclesPlaced.add(lastPosition);
                gp.tileM.mapTileNum[lastPosition.x][lastPosition.y] = 2; // Marking the tile as an obstacle
                System.out.println("Obstacle placed at: " + lastPosition);
            } else {
                System.out.println("Failed to place obstacle at: " + lastPosition + " | Tile occupied or not empty.");
            }
        } else {
            System.out.println("Obstacle limit reached: " + obstacleLimit);
        }
    }

    /**
     * Grants the player temporary invincibility for a set duration, preventing damage from
     * collisions or attacks.
     * @param duration
     */
    public void makeInvincible(int duration) {
        isInvincible = true;
        invincibilityDuration = duration;
    }

    /**
     * Ends the player's invincibility and stops any blinking effects that indicate
     * invincibility.
     */
    public void endInvincibility() {
        isInvincible = false;
        isBlinking = false;
    }

    /**
     * Makes the player a ghost, allowing them to move without collisions for a set
     * duration.
     * @param duration
     */
    public void enableGhost(int duration) {
        isGhost = true;
        collisionOn = false;
        ghostDuration = duration;
    }

    /**
     * Disables the ghost state, restoring normal collision behavior.
     */
    public void disableGhost() {
        isGhost = false;
        collisionOn = true;
    }

    /**
     * Decreases the count of active bombs when one explodes, managing the player's
     * capacity to place more bombs.
     */
    public void bombExploded() {
        bombCount--;
    }

    /**
     * Checks and updates interactions between the player and their bombs, primarily
     * managing bomb collision avoidance rules.
     */
    public void updateBombInteraction() {
        for (SuperObject obj : gp.objs) {
            if (obj instanceof OBJ_bomb bomb) {
                if (bomb.owner == this) {
                    Rectangle playerRect = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
                    Rectangle bombRect = new Rectangle(bomb.x + bomb.solidArea.x, bomb.y + bomb.solidArea.y, bomb.solidArea.width, bomb.solidArea.height);
                    if (!playerRect.intersects(bombRect)) {
                        bomb.ignoreCollisionWithOwner = false;
                    }
                }
            }
        }
    }

    /**
     * Overrides the Entity.update() method to incorporate player-specific controls and
     * behaviors such as movement handling based on key inputs, collision checks, and
     * power-up interactions.
     */
    public void update() {
        boolean up = false, down = false, left = false, right = false;

        switch (playerNum) {
            case 1 -> {
                up    = keyH.upPressed;
                down  = keyH.downPressed;
                left  = keyH.leftPressed;
                right = keyH.rightPressed;
            }
            case 2 -> {
                up    = keyH.upPressed2;
                down  = keyH.downPressed2;
                left  = keyH.leftPressed2;
                right = keyH.rightPressed2;
            }
            case 3 -> {
                up    = keyH.upPressed3;
                down  = keyH.downPressed3;
                left  = keyH.leftPressed3;
                right = keyH.rightPressed3;
            }
        }

        if (up || down || left || right) {
            int newX = x, newY = y;

            if (up) {
                newY -= speed;
                direction = "up";
            } else if (down) {
                newY += speed;
                direction = "down";
            } else if (left) {
                newX -= speed;
                direction = "left";
            } else { // right
                newX += speed;
                direction = "right";
            }

            collisionOn = false;
            gp.cChecker.checkTile(this);
            int bombCollisionIndex = gp.cChecker.checkBombCollision(this, true);
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            if (!collisionOn) {
                x = newX;
                y = newY;
            }

            spriteCounter++;

            if (spriteCounter > 12) {
                spriteNum = spriteNum == 1 ? 2 : 1;
                spriteCounter = 0;
            }

            if (currLife <= 0) return;

            updateBombInteraction();
            updateGhost();
        }

        int powerUpIndex = gp.cChecker.checkPowerUpCollision(this);
        if (powerUpIndex != -1) {
            pickUpPowerUp(powerUpIndex);
        }

        for (int i = 0; i < gp.monsters.length; i++) {
            Entity monster = gp.monsters[i];
            if (monster == null) continue;

            Rectangle monsterArea = new Rectangle(monster.x, monster.y, monster.solidArea.width, monster.solidArea.height);
            Rectangle playerArea = new Rectangle(x, y, solidArea.width, solidArea.height);
            if (playerArea.intersects(monsterArea)) {
                contactMonster(i);
            }
        }

        updateInvincibility();

        lastPosition = new Point(x / gp.tileSize, y / gp.tileSize);
    }

    /**
     * Activates and removes a power-up from the game when the player collides with it.
     * @param index
     */
    public void pickUpPowerUp(int index) {
        PowerUp powerUp = gp.powerUps.get(index);
        powerUp.activate(this);  // Activate the power-up effects
        gp.powerUps.remove(index);  // Remove the power-up from the game
    }

    /**
     * Picks up an object if the player collides with it, removing it from the game.
     * @param i
     */
    public void pickUpObject(int i) {
        if (i != 999) gp.objs.set(i, null);
    }

    /**
     * Handles interactions with monsters, setting the player's life to zero if they collide
     * with a monster while not invincible.
     * @param i
     */
    public void contactMonster(int i) {
        if (i != 999 && !isInvincible) {
            currLife = 0;
            invincible = true;
        }
    }

    /**
     * Manages the countdown of the player's invincibility duration and blinking effect as it
     * nears expiration.
     */
    public void updateInvincibility() {
        if (isInvincible) {
            invincibilityDuration--;
            if (invincibilityDuration <= 0) {
                endInvincibility();
            } else if (invincibilityDuration <= 2 * gp.FPS) {
                if (invincibilityDuration % (gp.FPS / 3) == 0) {
                    isBlinking = !isBlinking;
                }
            }
        }
    }

    /**
     * Manages the countdown and potential deactivation of the ghost mode, including the
     * blinking effect as it nears expiration.
     */
    public void updateGhost() {
        if (isGhost) {
            ghostDuration--;
            if (ghostDuration <= 0) {
                disableGhost();
            } else if (ghostDuration <= 2 * gp.FPS) {
                if (ghostDuration % (gp.FPS / 3) == 0) {
                    isBlinking = !isBlinking;
                }
            }
        }
    }

    /**
     * Extends the drawing capabilities of Entity.draw() to incorporate visual effects related
     * to invincibility and ghost states, including semi-transparency and positional
     * adjustments based on overlapping effects.
     * @param g2 the Graphics2D object to draw the entity's sprite
     */
    @Override
    public void draw(Graphics2D g2) {
        BufferedImage currentSprite = image;

        int offset = 0;

        if (isInvincible) {
            currentSprite = invincibilitySprite;

            if (isGhost) {
                offset = 8;
            }

            if (isBlinking) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }

            g2.drawImage(currentSprite, x - 10 + offset, y, null);
        }

        if (isGhost) {
            currentSprite = ghostSprite;

            if (isInvincible) {
                offset = -8;
            }

            if (isBlinking) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }

            g2.drawImage(currentSprite, x - 10 + offset, y, null);
        }

        if ((isInvincible && isBlinking) || (isGhost && isBlinking)) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            super.draw(g2);
        }
    }
}