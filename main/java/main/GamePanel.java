package main;
import entity.Entity;
import entity.Player;
import object.OBJ_bomb;
import object.SuperObject;
import powerups.ExtraBomb;
import powerups.PowerUp;
import tile.TileManager;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * The GamePanel class is the main class for the game. It extends JPanel and implements Runnable and MouseListener.
 * It contains the game loop, handles game state updates and rendering, and manages game elements such as players,
 * monsters, power-ups, and game objects. It also handles user input through key handlers and mouse listeners.
 * The class is responsible for initializing and resetting the game, advancing rounds, and ending the game.
 * It also provides utility methods for displaying the game winner or indicating a draw, and for checking if a specific
 * tile is occupied by any player other than the one specified.
 */
public class GamePanel extends JPanel implements Runnable, MouseListener {
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public BufferedImage mainMenuBackground;
    public int FPS = 60;

    public int currentRound = 1; //new
    public final int maxRounds = 4; //new
    public int[] playerScores; //new

    public List<Point> boxes = new ArrayList<>();
    public TileManager tileM = new TileManager(this);

    public KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;

    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);

    // main.java.entity and objects
    public Player[] players = new Player[ui.playerNumber]; // TODO: NEED TO MAKE ARRAY LIST
    public List<SuperObject> objs = new ArrayList<>();
    public List<Entity> entities = new ArrayList<>();
    public Entity[] monsters = new Entity[20];
    public List<PowerUp> powerUps = new ArrayList<>();

    // game state
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;

    // handling the endgame
    public long gracePeriodEndTime = 0;
    public final long GRACE_PERIOD_DURATION = 2000;


    /**
     * Initializes the game panel, setting up dimensions based on tile size and screen grid.
     * It also loads essential game resources and initializes player settings, UI
     * components, and game state management.
     * @throws IOException
     */
    public GamePanel() throws IOException {
        playerScores = new int[ui.playerNumber];
        Arrays.fill(playerScores, 0); //new

        initPlayers();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        mainMenuBackground = ImageIO.read(getClass().getResourceAsStream("/maps/bg2a.png"));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();
        gameState = titleState;
        ui = new UI(this);
    }

    /**
     * Initializes or re-initializes the array of player objects based on the number of players
     * configured in the game UI.
     */
    public void initPlayers() {
        if (players != null) {
            Arrays.fill(players, null);
        }
        players = new Player[ui.playerNumber];
        for (int i = 0; i < ui.playerNumber; i++) {
            players[i] = new Player(this, keyH, i + 1);
        }
        playerScores = new int[ui.playerNumber];
        Arrays.fill(playerScores, 0); //new
    }

    /**
     * Resets player settings, typically called when game settings are changed, like the
     * number of players.
     */
    public void updatePlayerSettings() {
        initPlayers();
        //resetGame(); // Call resetGame to reapply the new settings throughout the game
    }

    /**
     * Calls methods from the AssetSetter to populate the game environment with
     * objects, monsters, and power-ups.
     */
    public void setupGame() {
        aSetter.setObject();
        aSetter.setMonster();
        aSetter.setPowerUp();
    }

    /**
     * Starts the game loop running in a new thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Contains the game loop, which handles game state updates and rendering at a fixed
     * time interval determined by the FPS setting.
     */
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /**
     * Updates game logic, including player and monster updates, checking for power-up
     * interactions, and handling game state transitions based on gameplay events such
     * as all players dying or all monsters being cleared.
     */
    public void update() {
        int alivePlayers = 0;
        Player lastStandingPlayer = null;
        boolean allMonstersDead = true;

        if (gameState == playState) {
            for (int i = 0; i < players.length; i++) {
                if (players[i] != null) {
                    if (players[i].currLife <= 0 && gracePeriodEndTime == 0) {
                        gracePeriodEndTime = System.currentTimeMillis() + GRACE_PERIOD_DURATION;
                        players[i] = null;
                        System.out.println("Player " + (i + 1) + " died.");
                    } else if (players[i].currLife > 0) {
                        players[i].update();
                        players[i].hitByCurrentExplosion = false;

                        int powerUpIndex = cChecker.checkPowerUpCollision(players[i]);
                        if (powerUpIndex != -1) {
                            players[i].pickUpPowerUp(powerUpIndex);
                        }

                        alivePlayers++;
                        lastStandingPlayer = players[i];
                    }
                }
            }
            for (int i = 0; i < monsters.length; i++) {
                if (monsters[i] != null) {
                    if (monsters[i].currLife <= 0) {
                        monsters[i] = null;
                    } else {
                        monsters[i].update();
                        allMonstersDead = false;
                    }
                }
            }
            for (PowerUp powerUp : powerUps) {
                if (powerUp != null) {
                    powerUp.update();
                }
            }
            for (int i = this.objs.size() - 1; i >= 0; i--) {
                SuperObject objs = this.objs.get(i);
                if (objs != null) objs.update();
                if (objs instanceof OBJ_bomb && !((OBJ_bomb) objs).isActive()) {
                    this.objs.remove(i);
                }
            }

            System.out.println("Alive players: " + alivePlayers);
            System.out.println("All monsters dead: " + allMonstersDead);

            if (alivePlayers == 1 && allMonstersDead) {
                System.out.println("Player " + lastStandingPlayer.playerNum + " cleared all monsters.");
                //playerScores[lastStandingPlayer.playerNum - 1]++;
                nextRound(lastStandingPlayer);
            } else if (gracePeriodEndTime > 0 && System.currentTimeMillis() > gracePeriodEndTime) {
                if (alivePlayers == 1) {
                    System.out.println("Player " + lastStandingPlayer.playerNum + " is the last standing.");
                    nextRound(lastStandingPlayer);
                } else if (alivePlayers == 0 || (alivePlayers > 0 && allMonstersDead)) {
                    System.out.println("Advancing to next round.");
                    nextRound(lastStandingPlayer);
                }
                gracePeriodEndTime = 0;
                repaint();
            }
        }
    }

    /**
     * Advances the game to the next round, updating player scores and resetting the
     * game environment as necessary.
     * @param winner
     */
    private void nextRound(Player winner) {
        if (winner != null) {
            int winnerIndex = winner.playerNum - 1;
            if (winnerIndex >= 0 && winnerIndex < playerScores.length) {
                playerScores[winnerIndex]++;
            } else {
                System.err.println("Invalid winner index: " + winnerIndex);
            }
        }

        currentRound++;
        if (currentRound > maxRounds){
            endGame();
        } else {
            System.out.println("Advancing to round " + currentRound);
            loadRandomMap();
            resetRound();
        }
    }

    /**
     * Resets the game to its initial state for a new round, reinitializing game assets and
     * settings.
     */
    private void resetRound() {
        System.out.println("Resetting round to default values.");
        // Reset game elements and state for a new round
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                System.out.println("Resetting player " + (i + 1) + " to default values.");
                players[i].setDefaultValues();
            } else {
                players[i] = new Player(this, keyH, i + 1);
                players[i].setDefaultValues();
            }
        }

        // Reset the game world
        tileM = new TileManager(this);
        cChecker = new CollisionChecker(this);
        aSetter = new AssetSetter(this);
        eHandler = new EventHandler(this);

        // Reset other game-related elements
        objs.clear();
        entities.clear();
        monsters = new Entity[20];
        powerUps.clear();
        boxes.clear();

        setupGame();
        gameState = playState;
        repaint();
    }

    /**
     * Handles the end of the game, displaying final scores and transitioning to the game
     * over state.
     */
    private void endGame() {
        System.out.println("End of game. Displaying scores:");
        for (int i = 0; i < playerScores.length; i++) {
            System.out.println("Player " + (i + 1) + ": " + playerScores[i]);
        }
        gameState = gameOverState;
        ui.showEndGameScores(playerScores);
    }

    /**
     * Loads a random map from available resources, adding variability to game rounds.
     */
    private void loadRandomMap() {
        Random rand = new Random();
        int mapNumber = rand.nextInt(3) + 1; // Assuming 3 maps available
        tileM.loadMap("/maps/map0" + mapNumber + ".txt");
    }

    /**
     * Checks if a specific tile is occupied by any player other than the one specified, used
     * to validate placements of objects or movement.
     * @param x
     * @param y
     * @param placingPlayer
     * @return
     */
    public boolean isTileOccupied(int x, int y, Player placingPlayer) {
        Rectangle tileArea = new Rectangle(x * tileSize, y * tileSize, tileSize, tileSize);

        // Check for other players or entities
        for (Player player : players) {
            if (player != null && player != placingPlayer) {
                Rectangle playerArea = new Rectangle(player.x, player.y, player.solidArea.width, player.solidArea.height);
                if (playerArea.intersects(tileArea)) {
                    return true;
                }
            }
        } return false;
    }

    /**
     * Utility methods to display the game winner or indicate a draw, respectively.
     * @param player
     */
    public void displayWinner(Player player) {
        System.out.println("Winner is Player " + player.playerNum);
        ui.showMessage("Winner is Player " + player.playerNum);
        resetGame();
        gameState = titleState;
        ui.titleScreenState = 0;
        repaint();
    }

    /**
     * Utility method to display a draw when no players or monsters remain.
     */
    public void displayNoWinner() {
        System.out.println("Game ends in a draw!");
        ui.showMessage("Game ends in a draw!");
        resetGame();
        gameState = titleState;
        ui.titleScreenState = 7;
        repaint();
    }

    /**
     * Resets the entire game to default values, typically called after a game over or when
     * restarting the game from the menu.
     */
    public void resetGame() {
        System.out.println("Resetting game to default values.");
        currentRound = 1;
        playerScores = new int[players.length];
        gameState = titleState;
        // Reset game elements and state
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                players[i].setDefaultValues();
            } else {
                players[i] = new Player(this, keyH, i + 1);
                players[i].setDefaultValues();
            }
        }

        // Reset the game world
        tileM = new TileManager(this);
        cChecker = new CollisionChecker(this);
        aSetter = new AssetSetter(this);
        eHandler = new EventHandler(this);

        gracePeriodEndTime = 0; //

        objs.clear();
        entities.clear();
        monsters = new Entity[20];
        powerUps.clear();
        boxes.clear();

        this.requestFocus();
        setupGame();
        repaint();
    }

    /**
     * Overrides JPanel's paintComponent to render the game elements on the panel,
     * handling both UI and game object rendering.
     * @param g the Graphics object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);
        ui.setGraphics(g2);
        ui.updateUI();

        if (gameState == titleState) {
            ui.drawTitleScreen();
        } else if (gameState == playState) {
            tileM.draw(g2);
            for (SuperObject obj : objs)
                if (obj != null) obj.draw(g2, this);

            for (PowerUp powerUp : powerUps)
                if (powerUp != null && powerUp.isVisible()) powerUp.draw(g2);

            for (Entity entity : monsters) {
                if (entity != null) entities.add(entity);
            }

            entities.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    return Integer.compare(e1.x, e2.y);
                }
            });

            // draw entities
            for (Entity entity : entities) {
                if (entity != null) entity.draw(g2);
            }

            // empty main.java.entity list
            entities.clear();
            for (Player player : players) {
                if (player != null) player.draw(g2);
            }
            ui.draw(g2);
        }
        g2.dispose();
    }

    /**
     * Implements methods from MouseListener to handle mouse interactions, primarily
     * used to detect clicks on UI elements like buttons in the game over screen.
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameState == gameOverState) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            String okText = "OK";
            int okX = ui.getOkButtonX();
            int okY = ui.getOkButtonY();

            System.out.println("Mouse clicked at (" + mouseX + ", " + mouseY + ")");
            System.out.println("OK button at (" + okX + ", " + okY + ")");

            // Adjust the boundaries for the OK button click detection
            int buttonWidth = tileSize * 2;
            int buttonHeight = tileSize;

            if (mouseX >= okX - 20 && mouseX <= okX + buttonWidth - 20 &&
                    mouseY >= okY - buttonHeight && mouseY <= okY) {
                System.out.println("OK button clicked.");
                gameState = titleState;
                resetGame();
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {}

    /**
     * Implements methods from MouseListener to handle mouse interactions, primarily
     * used to detect clicks on UI elements like buttons in the game over screen.
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * Implements methods from MouseListener to handle mouse interactions, primarily
     * used to detect clicks on UI elements like buttons in the game over screen.
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Implements methods from MouseListener to handle mouse interactions, primarily
     * used to detect clicks on UI elements like buttons in the game over screen.
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {}
}