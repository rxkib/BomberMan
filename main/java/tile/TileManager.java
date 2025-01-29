package tile;
import main.GamePanel;
import main.UtilityTool;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

/**
 * Purpose: Manages the tiles in the game, including their images and collision properties.
 * Details: The TileManager class sets up the tiles used in the game, loads the map layout from a file,
 * and renders the tiles onto the game panel. It also handles the destructible walls by storing their
 * locations for gameplay mechanics.
 */
public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    /**
     * Purpose: Initializes a new TileManager object, setting up tiles and loading a map.
     * Details: The constructor sets up the Tile array with basic tiles, loads specific tile images,
     * and initializes the game map from a text file representing tile indices.
     * @param gp
     */
    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap("/maps/map01.txt");
    }

    /**
     * Purpose: Loads and assigns images to the types of tiles handled by the TileManager.
     * Details: Sets up common tiles like grass, wall, and destructible wall, specifying which tiles
     * are passable (collision false) and which are not (collision true).
     */
    public void getTileImage() {
        setup(0, "grass", false);
        setup(1, "wall", true);
        setup(2, "destructiblewall", true);
    }

    /**
     * Purpose: Configures a tile at a specified index with an image and collision property.
     * Details: Uses UtilityTool to load and scale the tile image appropriately to the game's tile
     * size. It marks the tile as collidable or not based on the collision parameter.
     * @param index
     * @param imageName
     * @param collision
     */
    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Purpose: Loads a map layout from a specified file, populating the mapTileNum array which
     * dictates the tile types and locations on the game panel.
     * Details: Reads a text file where each number corresponds to a tile type, arranging them
     * according to the structure expected in the game's world. Special handling is noted for tiles
     * like destructible walls by storing their locations for gameplay mechanics.
     * @param filePath
     */
    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(col < gp.maxScreenCol && row < gp.maxScreenRow){
                String line = br.readLine();

                while(col < gp.maxScreenCol){
                    String[] numbers = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    if (num == 2) {
                        gp.boxes.add(new Point(col, row));
                    }

                    mapTileNum[col][row] = num;
                    col++;
                }

                if(col == gp.maxScreenCol){
                    col = 0;
                    row++;
                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Purpose: Renders the tiles onto the game panel.
     * Details: Iterates over the mapTileNum grid, drawing each tile at its appropriate screen
     * position, thereby constructing the visible game map for the player.
     * @param g2
     */
    public void draw(Graphics2D g2) {

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {

            int tileNum = mapTileNum[col][row]; // extract a main.java.tile number which is stored in mapTileNum[0][0]

            g2.drawImage(tile[tileNum].image, x, y, null);
            col++;
            x += gp.tileSize;

            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}


