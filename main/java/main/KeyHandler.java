package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyHandler class is responsible for handling key inputs across different
 * parts of the game. It includes methods for processing key inputs specific to the title screen,
 * active gameplay, and when the game is paused. It also updates boolean flags for key states to false
 * when keys are released, indicating the end of the corresponding movement or action.
 */
public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, rightPressed, leftPressed;
    public boolean upPressed2, downPressed2, rightPressed2, leftPressed2;
    public boolean upPressed3, downPressed3, rightPressed3, leftPressed3;

    /**
     * Initializes the KeyHandler with a reference to GamePanel. Enables the handling of key inputs across different
     * parts of the game.
     * @param gp
     */
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    /**
     * Determines the game state and calls the corresponding method to handle key
     * presses for that state.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if      (gp.gameState == gp.titleState) { titleScreenInput(code); }
        else if (gp.gameState == gp.playState)  { playStateInput(code);   }
        else if (gp.gameState == gp.pauseState) { pauseStateInput(code);  }
    }

    /**
     * Updates boolean flags for key states to false when keys are released, indicating the
     * end of the corresponding movement or action.
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed    = false;
        if (code == KeyEvent.VK_A) leftPressed  = false;
        if (code == KeyEvent.VK_S) downPressed  = false;
        if (code == KeyEvent.VK_D) rightPressed = false;

        if (code == KeyEvent.VK_UP)    upPressed2    = false;
        if (code == KeyEvent.VK_DOWN)  downPressed2  = false;
        if (code == KeyEvent.VK_LEFT)  leftPressed2  = false;
        if (code == KeyEvent.VK_RIGHT) rightPressed2 = false;

        if (code == KeyEvent.VK_I) upPressed3    = false;
        if (code == KeyEvent.VK_K) downPressed3  = false;
        if (code == KeyEvent.VK_J) leftPressed3  = false;
        if (code == KeyEvent.VK_L) rightPressed3 = false;
    }

    /**
     * Processes key inputs specific to the title screen, such as navigating menus or
     * starting the game.
     * @param code
     */
    private void titleScreenInput(int code) {
        if(gp.ui.titleScreenState == 0){
            switch (code) {
                case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 0) gp.ui.commandNum = 2;
                }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 2) gp.ui.commandNum = 0;
                }
                case KeyEvent.VK_ENTER -> {
                    if (gp.ui.commandNum == 0) gp.gameState = gp.playState;
                    if (gp.ui.commandNum == 1) gp.ui.titleScreenState = 3;
                    if (gp.ui.commandNum == 2) System.exit(0);
                }
            }
        }
        else if (gp.ui.titleScreenState == 3) {
            switch (code) {
                case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 4) gp.ui.commandNum = 7;
                }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 7) gp.ui.commandNum = 4;
                }
                case KeyEvent.VK_ENTER -> {
                    if (gp.ui.commandNum == 4) gp.ui.titleScreenState = 4;
                    if (gp.ui.commandNum == 5) gp.ui.titleScreenState = 5;
                    //if (gp.ui.commandNum == 6) gp.ui.titleScreenState = 6;
                    if (gp.ui.commandNum == 7) gp.ui.titleScreenState = 0;
                }
            }
        }
        // num of Players
        else if (gp.ui.titleScreenState == 4) {
            switch (code) {
                case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 5) gp.ui.commandNum = 7;
                }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 7) gp.ui.commandNum = 5;
                }
                case KeyEvent.VK_ENTER -> {
                    if (gp.ui.commandNum == 5) {
                        gp.ui.playerNumber = 1;
                        gp.updatePlayerSettings();
                        gp.ui.titleScreenState = 0;}
                    if (gp.ui.commandNum == 6) {
                        gp.ui.playerNumber = 2;
                        gp.updatePlayerSettings();
                        gp.ui.titleScreenState = 0;}
                    if (gp.ui.commandNum == 7) {
                        gp.ui.playerNumber = 3;
                        gp.updatePlayerSettings();
                        gp.ui.titleScreenState = 0;}
                }
                default -> throw new IllegalStateException("Unexpected value: " + code);
            }
        }
        // map
        else if (gp.ui.titleScreenState == 5) {
            switch (code) {
                case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 6) gp.ui.commandNum = 8;
                }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 8) gp.ui.commandNum = 6;
                }
                case KeyEvent.VK_ENTER -> {
                    switch (gp.ui.commandNum) {
                        case 5 -> gp.tileM.loadMap("/maps/map01.txt");
                        case 6 -> gp.tileM.loadMap("/maps/map02.txt");
                        case 7 -> gp.tileM.loadMap("/maps/map03.txt");
                    }
                    gp.ui.commandNum = 0;
                    gp.ui.titleScreenState = 0;
                    gp.repaint();
                }
            }
        }
        else if (gp.ui.titleScreenState == 7) {
            switch (code) {
                case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 8) gp.ui.commandNum = 9;
                }
                case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 9) gp.ui.commandNum = 8;
                }
                case KeyEvent.VK_ENTER -> {
                    if (gp.ui.commandNum == 8) gp.ui.titleScreenState = 0;
                    if (gp.ui.commandNum == 9) System.exit(0);
                }
            }
        }
    }

    /**
     * Handles key inputs during active gameplay, including player movement, bomb
     * placement, and pausing the game.
     * @param code
     */
    private void playStateInput(int code) {
        switch (code) {
            case KeyEvent.VK_W -> upPressed    = true;
            case KeyEvent.VK_A -> leftPressed  = true;
            case KeyEvent.VK_S -> downPressed  = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_Q -> gp.players[0].placeBomb();
            case KeyEvent.VK_E -> {
                if (gp.players[0] != null) {
                    gp.players[0].detonateBombs();
                }
            }
            case KeyEvent.VK_R -> {
                if (gp.players[0] != null) {
                    gp.players[0].placeObstacle();
                }
            }

            case KeyEvent.VK_I -> upPressed3    = true;
            case KeyEvent.VK_K -> downPressed3  = true;
            case KeyEvent.VK_J -> leftPressed3  = true;
            case KeyEvent.VK_L -> rightPressed3 = true;
            case KeyEvent.VK_U -> gp.players[2].placeBomb();
            case KeyEvent.VK_O -> {
                if (gp.players[2] != null) {
                    gp.players[2].detonateBombs();
                }
            }
            case KeyEvent.VK_P -> {
                if (gp.players[2] != null) {
                    gp.players[2].placeObstacle();
                }
            }

            case KeyEvent.VK_UP    -> upPressed2    = true;
            case KeyEvent.VK_DOWN  -> downPressed2  = true;
            case KeyEvent.VK_LEFT  -> leftPressed2  = true;
            case KeyEvent.VK_RIGHT -> rightPressed2 = true;
            case KeyEvent.VK_SHIFT -> gp.players[1].placeBomb();
            case KeyEvent.VK_ENTER -> {
                if (gp.players[1] != null) {
                    gp.players[1].detonateBombs();
                }
            }
            case KeyEvent.VK_CONTROL -> {
                if (gp.players[1] != null) {
                    gp.players[1].placeObstacle();
                }
            }

            case KeyEvent.VK_SPACE -> {
                if (gp.gameState == gp.playState) gp.gameState = gp.pauseState;
                else if (gp.gameState == gp.pauseState) gp.gameState = gp.playState;
            }
        }
    }

    /**
     * Manages key inputs when the game is paused, allowing navigation through pause
     * menu options or resuming the game.
     * @param code
     */
    private void pauseStateInput(int code) {
        switch(code) {
            case KeyEvent.VK_W -> {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 2) gp.ui.commandNum = 3;
            }
            case KeyEvent.VK_S -> {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3) gp.ui.commandNum =2 ;
            }
            case KeyEvent.VK_ENTER -> {
                if (gp.ui.commandNum == 2) gp.gameState = gp.titleState;
                if (gp.ui.commandNum == 3) System.exit(0);
            }
        }
    }
}