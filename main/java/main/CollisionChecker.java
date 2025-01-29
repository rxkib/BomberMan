package main;
import entity.Entity;
import entity.Player;
import monster.MON_orc;
import object.OBJ_bomb;
import object.SuperObject;
import powerups.PowerUp;
import java.awt.*;

/**
 * The CollisionChecker class is responsible for handling collision checks between entities
 */
public class CollisionChecker {
    GamePanel gp;
    public static final int NO_COLLISION = -1;

    /**
     * Initializes the CollisionChecker with a reference to the GamePanel to access game
     * environment details and entity positions.
     * @param gp
     */
    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Handles collision checks for a given entity against tiles in the game map.
     * Differentiates between general entities and specific types like MON_orc for custom
     * collision logic.
     * @param entity
     */
    public void checkTile(Entity entity) {
        if (entity instanceof MON_orc) {
            orcCollisionCheck((MON_orc) entity);
        } else {
            defaultCollisionCheck(entity);
        }
    }

    /**
     * Determines if a tile at the specified column and row is an edge tile of the game map.
     * @param col
     * @param row
     * @return
     */
    private boolean isEdgeTile(int col, int row) {
        return col == 0 || col == gp.maxScreenCol - 1 || row == 0 || row == gp.maxScreenRow - 1;
    }

    /**
     * Performs a collision check for the specified entity against the game's tiles based on
     * the entity's direction and speed. Includes logic to ignore collisions for ghost-mode
     * players unless at map edges.
     * @param entity
     */
    private void defaultCollisionCheck(Entity entity) {

        int entityLeftX = entity.x + entity.solidArea.x;
        int entityRightX = entity.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopY = entity.y + entity.solidArea.y;
        int entityBottomY = entity.y + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = Math.max(0, Math.min(gp.maxScreenCol - 1, entityLeftX / gp.tileSize));
        int entityRightCol = Math.max(0, Math.min(gp.maxScreenCol - 1, entityRightX / gp.tileSize));
        int entityTopRow = Math.max(0, Math.min(gp.maxScreenRow - 1, entityTopY / gp.tileSize));
        int entityBottomRow = Math.max(0, Math.min(gp.maxScreenRow - 1, entityBottomY / gp.tileSize));

        int adjustedTopRow = Math.max(0, Math.min(gp.maxScreenRow - 1, (entityTopY - entity.speed) / gp.tileSize));
        int adjustedBottomRow = Math.max(0, Math.min(gp.maxScreenRow - 1, (entityBottomY + entity.speed) / gp.tileSize));
        int adjustedLeftCol = Math.max(0, Math.min(gp.maxScreenCol - 1, (entityLeftX - entity.speed) / gp.tileSize));
        int adjustedRightCol = Math.max(0, Math.min(gp.maxScreenCol - 1, (entityRightX + entity.speed) / gp.tileSize));

        switch (entity.direction) {
            case "up":
                if (gp.tileM.tile[gp.tileM.mapTileNum[entityLeftCol][adjustedTopRow]].collision ||
                        gp.tileM.tile[gp.tileM.mapTileNum[entityRightCol][adjustedTopRow]].collision) {
                    if (entity instanceof Player player && player.isGhost &&
                    !isEdgeTile(entityLeftCol, adjustedTopRow) && !isEdgeTile(entityRightCol, adjustedTopRow))
                        break;
                    entity.collisionOn = true;
                }
                break;
            case "down":
                if (gp.tileM.tile[gp.tileM.mapTileNum[entityLeftCol][adjustedBottomRow]].collision ||
                        gp.tileM.tile[gp.tileM.mapTileNum[entityRightCol][adjustedBottomRow]].collision) {
                    if (entity instanceof Player player && player.isGhost &&
                    !isEdgeTile(entityLeftCol, adjustedBottomRow) && !isEdgeTile(entityRightCol, adjustedBottomRow))
                        break;
                    entity.collisionOn = true;
                }
                break;
            case "left":
                if (gp.tileM.tile[gp.tileM.mapTileNum[adjustedLeftCol][entityTopRow]].collision ||
                        gp.tileM.tile[gp.tileM.mapTileNum[adjustedLeftCol][entityBottomRow]].collision) {
                    if (entity instanceof Player player && player.isGhost &&
                            !isEdgeTile(adjustedLeftCol, entityTopRow) && !isEdgeTile(adjustedLeftCol, entityBottomRow))
                        break;
                    entity.collisionOn = true;
                }
                break;
            case "right":
                if (gp.tileM.tile[gp.tileM.mapTileNum[adjustedRightCol][entityTopRow]].collision ||
                        gp.tileM.tile[gp.tileM.mapTileNum[adjustedRightCol][entityBottomRow]].collision) {
                    if (entity instanceof Player player && player.isGhost &&
                            !isEdgeTile(adjustedRightCol, entityTopRow) && !isEdgeTile(adjustedRightCol, entityBottomRow))
                        break;
                    entity.collisionOn = true;
                }
                break;
        }
    }

    /**
     * Specialized collision check for MON_orc type entities that involves checking
     * intersections with game objects like bombs.
     * @param orc
     */
    private void orcCollisionCheck(MON_orc orc) {
        for (SuperObject obj : gp.objs) {
            if (obj instanceof OBJ_bomb) {
                Rectangle orcRect = new Rectangle(orc.x + orc.solidArea.x, orc.y + orc.solidArea.y, orc.solidArea.width, orc.solidArea.height);
                Rectangle bombRect = new Rectangle(obj.x + obj.solidArea.x, obj.y + obj.solidArea.y, obj.solidArea.width, obj.solidArea.height);

                if (orcRect.intersects(bombRect)) {
                    orc.collisionOn = true;
                    return;
                }
            }
        }
        orc.collisionOn = false;
    }

    /**
     * Checks for collisions between the specified entity and bombs in the game, adjusting
     * collision status based on entity properties like ghost mode.
     * @param entity
     * @param player
     * @return
     */
    public int checkBombCollision(Entity entity, boolean player) {
        int index = 999;

        for (SuperObject obj : gp.objs) {
            if (obj != null) {
                // get main.java.entity's solid area position
                entity.solidArea.x = entity.x + entity.solidAreaDefaultX;
                entity.solidArea.y = entity.y + entity.solidAreaDefaultY;

                // get objects solid area position
                obj.solidArea.x = obj.x + obj.solidAreaDefaultX;
                obj.solidArea.y = obj.y + obj.solidAreaDefaultY;

                index = switch (entity.direction) {
                    case "up" -> {
                        entity.solidArea.y -= entity.speed;
                        yield collisionIndex(entity, player, index, obj);
                    }
                    case "down" -> {
                        entity.solidArea.y += entity.speed;
                        yield collisionIndex(entity, player, index, obj);
                    }
                    case "left" -> {
                        entity.solidArea.x -= entity.speed;
                        yield collisionIndex(entity, player, index, obj);
                    }
                    case "right" -> {
                        entity.solidArea.x += entity.speed;
                        yield collisionIndex(entity, player, index, obj);
                    }
                    default -> index;
                };

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }
        return index;
    }

    /**
     * Helper method used during bomb collision checks to determine if an entity collides
     * with an object and returns the index of the colliding object.
     * @param entity
     * @param player
     * @param index
     * @param obj
     * @return
     */
    private int collisionIndex(Entity entity, boolean player, int index, SuperObject obj) {
        if (entity.solidArea.intersects(obj.solidArea)) {
            if (obj.collision) {
                OBJ_bomb bomb = (OBJ_bomb) obj;
                if (!bomb.ignoreCollisionWithOwner  && bomb.owner == entity) {
                    entity.collisionOn = !bomb.owner.isGhost;
                    if (player) {
                        index = gp.objs.indexOf(obj);
                    }
                }
            }
        }
        return index;
    }

    /**
     * Checks for collisions between the entity and non-bomb objects in the game, setting
     * collision flags and possibly returning the index of the colliding object.
     * @param entity
     * @param player
     * @return
     */
    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (SuperObject obj : gp.objs) {
            if (obj != null && !(obj instanceof OBJ_bomb)) {

                // get main.java.entity's solid area position
                entity.solidArea.x += entity.x;
                entity.solidArea.y += entity.y;

                // get the main.java.object's solid area position
                obj.solidArea.x += obj.x;
                obj.solidArea.y += obj.y;

                switch (entity.direction) {
                    case "up" -> {
                        entity.solidArea.y -= entity.speed;
                        index = getObjectIndex(entity, player, obj, index);
                    }
                    case "down" -> {
                        entity.solidArea.y += entity.speed;
                        index = getObjectIndex(entity, player, obj, index);
                    }
                    case "left" -> {
                        entity.solidArea.x -= entity.speed;
                        index = getObjectIndex(entity, player, obj, index);
                    }
                    case "right" -> {
                        entity.solidArea.x += entity.speed;
                        index = getObjectIndex(entity, player, obj, index);
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }

        return index;
    }

    /**
     * Checks for intersections between the player and power-up objects, returning the
     * index of the colliding power-up for potential collection.
     * @param player
     * @return
     */
    public int checkPowerUpCollision(Player player) {
        Rectangle playerRect = new Rectangle(player.x + player.solidArea.x, player.y + player.solidArea.y, player.solidArea.width, player.solidArea.height);

        for (int i = 0; i < gp.powerUps.size(); i++) {
            PowerUp powerUp = gp.powerUps.get(i);
            Rectangle powerUpRect = new Rectangle(powerUp.getX() + powerUp.solidArea.x, powerUp.getY() + powerUp.solidArea.y, powerUp.solidArea.width, powerUp.solidArea.height);
                if (playerRect.intersects(powerUpRect)) {
                    return i;
                }
        }
        return -1;
    }

    /**
     * Helper method used during object collision checks to determine if an entity collides
     * @param entity
     * @param player
     * @param obj
     * @param index
     * @return
     */
    private int getObjectIndex(Entity entity, boolean player, SuperObject obj, int index) {
        if (entity.solidArea.intersects(obj.solidArea)) {
            if (obj.collision) {
                entity.collisionOn = true;
                if (player) {
                    index = gp.objs.indexOf(obj);
                }
            }
        }
        return index;
    }

    /**
     * Checks for collisions between the specified entity and an array of target entities,
     * such as monsters, updating collision flags as necessary.
     * @param entity
     * @param target
     * @return
     */
    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999;

        for (int i = 0; i < target.length; i++) {

            if (target[i] != null) {
                // get main.java.entity's solid area position
                entity.solidArea.x = entity.x + entity.solidArea.x;
                entity.solidArea.y = entity.y + entity.solidArea.y;

                // get objects solid area position
                target[i].solidArea.x = target[i].x + target[i].solidArea.x;
                target[i].solidArea.y = target[i].y + target[i].solidArea.y;

                directionChange(entity);

                if (entity.solidArea.intersects(target[i].solidArea)) {
                    if (target[i] != entity) {
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    /**
     * Checks for collisions between a non-player entity and all player entities in the game,
     * updating collision flags as necessary.
     * @param entity
     */
    public void checkPlayer(Entity entity) {
        // get main.java.entity's solid area position
        entity.solidArea.x = entity.x + entity.solidArea.x;
        entity.solidArea.y = entity.y + entity.solidArea.y;

        for (Player player : gp.players) {
            if (player == null) continue; // Skip if player is not initialized

            // get player's solid area position
            player.solidArea.x = player.x + player.solidArea.x;
            player.solidArea.y = player.y + player.solidArea.y;

            // Adjust the solid area based on the main.java.entity's direction
            directionChange(entity);

            // Check for intersection with the main.java.entity's solid area
            if (entity.solidArea.intersects(player.solidArea)) {
                entity.collisionOn = true;
                break; // If a collision is detected, no need to check further
            }

            // Reset main.java.entity's solid area position
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;

            // Reset player's solid area position
            player.solidArea.x = player.solidAreaDefaultX;
            player.solidArea.y = player.solidAreaDefaultY;
        }
    }

    /**
     * Adjusts the position of the entity's collision area based on its movement direction
     * and speed, preparing for collision checks.
     * @param entity
     */
    private void directionChange(Entity entity) {
        switch (entity.direction) {
            case "up" -> entity.solidArea.y -= entity.speed;
            case "down" -> entity.solidArea.y += entity.speed;
            case "left" -> entity.solidArea.x -= entity.speed;
            case "right" -> entity.solidArea.x += entity.speed;
        }
    }

    /**
     * Determines if an entity can move in a specified direction without colliding with tiles,
     * using the game's tile management system to check collidable tiles.
     * @param entity
     * @param direction
     * @return
     */
    public boolean canMove(Entity entity, String direction) {
        int newX = entity.x;
        int newY = entity.y;

        switch (direction) {
            case "up" -> newY -= entity.speed;
            case "down" -> newY += entity.speed;
            case "left" -> newX -= entity.speed;
            case "right" -> newX += entity.speed;
        }

        int entityLeftCol = (newX + entity.solidArea.x) / gp.tileSize;
        int entityRightCol = (newX + entity.solidArea.x + entity.solidArea.width) / gp.tileSize;
        int entityTopRow = (newY + entity.solidArea.y) / gp.tileSize;
        int entityBottomRow = (newY + entity.solidArea.y + entity.solidArea.height) / gp.tileSize;

        // Check if coordinates are within map bounds
        if (entityLeftCol < 0 || entityRightCol >= gp.maxScreenCol || entityTopRow < 0 || entityBottomRow >= gp.maxScreenRow) {
            //System.out.println("Movement out of bounds at: " + direction);
            return false;
        }

        // Check each corner's main.java.tile for collisions
        if (isCollidable(entityLeftCol, entityTopRow) || isCollidable(entityRightCol, entityTopRow) ||
                isCollidable(entityLeftCol, entityBottomRow) || isCollidable(entityRightCol, entityBottomRow)) {
            //System.out.println("Collision detected when moving " + direction);
            return false;
        }
        //System.out.println("Movement approved in direction: " + direction);
        return true;
    }

    /**
     * Checks if a tile at a specific column and row is collidable, aiding movement checks
     * and collision handling.
     * @param col
     * @param row
     * @return
     */
    private boolean isCollidable(int col, int row) {
        return gp.tileM.mapTileNum[col][row] != 0;
    }
}