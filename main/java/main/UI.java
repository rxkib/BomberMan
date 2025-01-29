package main;
import entity.Entity;
import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
/**
 * This class provides utility methods for the game.
 * Currently, it includes a method for scaling images.
 */
public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_20;
    BufferedImage heart_full, heart_blank;
    boolean messageOn = false;
    String message = "";
    int commandNum = 0;
    public int titleScreenState = 0;
    int playerNumber = 1;  // Default to 1 player
    String[] playerNames = new String[3];
    int playerSettingState = 0;
    private int okButtonX;
    private int okButtonY;
    JTextField[] nameFields = new JTextField[3];

    /**
     * Initializes the UI with a reference to the GamePanel and sets up initial UI-related
     * configurations, such as fonts and player heart icons.
     * @param gp
     */
    public UI(GamePanel gp){
        this.gp = gp;
        arial_20 = new Font("Arial", Font.PLAIN, 20);
        Arrays.fill(playerNames, "");

        Entity heart = new Entity(gp);
        heart_full = heart.image;
        heart_blank = heart.image2;
    }

    /**
     * Updates UI components based on the current game state, delegating to specific
     * drawing methods like drawTitleScreen or drawPlayerLife.
     */
    public void updateUI() {
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        } else if (gp.gameState == gp.playState) {
            drawPlayerLife();
        } else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        } else if (gp.gameState == gp.gameOverState) {
            showEndGameScores(gp.playerScores);
        }
    }

    /**
     * Sets the x and y coordinates for the OK button used in various UI screens.
     * @param x
     * @param y
     */
    public void setOkButtonCoordinates(int x, int y) {
        this.okButtonX = x;
        this.okButtonY = y;
    }

    /**
     * Returns the x-coordinate of the OK button.
     * @return
     */
    public int getOkButtonX() {
        return okButtonX;
    }

    /**
     * Returns the y-coordinate of the OK button.
     * @return
     */
    public int getOkButtonY() {
        return okButtonY;
    }

    /**
     * Sets the Graphics2D object for the UI, which is used for drawing on the GamePanel.
     * @param g2
     */
    public void setGraphics(Graphics2D g2){
        this.g2 = g2;
    }

    /**
     * Displays a message on the screen temporarily. This uses a separate thread to
     * manage visibility duration.
     * @param text
     */
    public void showMessage(String text){
        message = text;
        messageOn = true;

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageOn = false;
                gp.repaint();
            }
        }).start();
    }

    /**
     * Central drawing method that updates the Graphics2D object and delegates drawing
     * based on the game state.
     * @param g2
     */
    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(arial_20);
        g2.setColor(Color.WHITE);

        // title
        if (gp.gameState == gp.titleState) drawTitleScreen();

        // displaying GameState
        if (gp.gameState == gp.playState) drawPlayerLife();
        if (gp.gameState == gp.pauseState) drawPauseScreen();
        if (gp.gameState == gp.gameOverState) showEndGameScores(gp.playerScores);

        // displaying messages
        // Now, draw the message if messageOn is true
        if (messageOn) {
            g2.drawString(message, getXForCentreText(message), gp.tileSize * 5);
        }
    }

    /**
     * Draws the life indicators (hearts) for players, showing current and maximum life.
     */
    public void drawPlayerLife() {
        int playerXOffset = gp.tileSize;

        // Loop through each player
        for (int i = 0; i < gp.players.length; i++) {
            Player player = gp.players[i];
            if (player == null) continue;

            int x = playerXOffset + (gp.tileSize * 5 * i);
            int y = gp.tileSize / 2;

            // Draw Player Number and Name
            String playerInfo = "Player " + (i + 1) + ": " + player.currLife + "/" + player.maxLife;
            g2.drawString(playerInfo, x, y);

            // Display hearts right next to the player info
            int heartX = x + g2.getFontMetrics().stringWidth(playerInfo) + 10;

            // Draw full hearts for current life
            for (int j = 0; j < player.currLife; j++) {
                g2.drawImage(heart_full, heartX, y - 10, null);
                heartX += gp.tileSize;
            }

            // Draw blank hearts for lost life
            for (int j = player.currLife; j < player.maxLife; j++) {
                g2.drawImage(heart_blank, heartX, y - 10, null);
                heartX += gp.tileSize;
            }
        }
    }

    /**
     * Draws the player number selection screen, allowing the user to choose how many
     * players will participate.
     * @param g2
     */
    public void drawPlayerNumberSelection(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
        String text = "Select Number of Players";
        int x = getXForCentreText(text);
        int y = gp.tileSize*3;

        // shadow
        g2.setColor(Color.black);
        g2.drawString(text, x + 3, y + 3);

        // main colour
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        text = "1 Player";
        x = getXForCentreText(text);
        y = gp.tileSize * 6;
        g2.drawString(text, x, y);

        if (commandNum == 5) g2.drawString(">", x - gp.tileSize, y);

        text = "2 Players";
        x = getXForCentreText(text);
        y = gp.tileSize * 7;
        g2.drawString(text, x, y);

        if (commandNum == 6) g2.drawString(">", x - gp.tileSize, y);

        text = "3 Players";
        x = getXForCentreText(text);
        y = gp.tileSize * 8;
        g2.drawString(text, x, y);

        if (commandNum == 7) g2.drawString(">", x - gp.tileSize, y);
    }

    /**
     * Provides input fields for players to enter their names, typically used in the game
     * setup phase.
     * @param g2
     */
    public void drawPlayerNameInputs(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
        String text = "Player Names";
        int x = getXForCentreText(text);
        int y = gp.tileSize*3;

        // shadow
        g2.setColor(Color.black);
        g2.drawString(text, x + 3, y + 3);

        // main colour
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);


        for (int i = 0; i < gp.players.length; i++) {
            if (nameFields[i] != null) {
                nameFields[i].setVisible(true);
            }
        }
    }

    /**
     * Allows players to select a game map from available options.
     * @param g2
     */
    public void drawMapSelection(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
        String text = "Select the Map";
        int x = getXForCentreText(text);
        int y = gp.tileSize*3;

        // shadow
        g2.setColor(Color.black);
        g2.drawString(text, x + 3, y + 3);

        // main colour
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        text = "Map 1";
        x = getXForCentreText(text);
        y = gp.tileSize * 6;
        g2.drawString(text, x, y);

        if (commandNum == 6) g2.drawString(">", x - gp.tileSize, y);

        text = "Map 2";
        x = getXForCentreText(text);
        y = gp.tileSize * 7;
        g2.drawString(text, x, y);

        if (commandNum == 7) g2.drawString(">", x - gp.tileSize, y);

        text = "Map 3";
        x = getXForCentreText(text);
        y = gp.tileSize * 8;
        g2.drawString(text, x, y);

        if (commandNum == 8) g2.drawString(">", x - gp.tileSize, y);
    }

    /**
     * Draws the main title screen of the game, which includes menu options like start
     * game, settings, and exit.
     */
    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
            String text = "Bomberman" + playerNumber;
            int x = getXForCentreText(text);
            int y = gp.tileSize*3;

            // shadow
            g2.setColor(Color.black);
            g2.drawString(text, x + 3, y + 3);

            // main colour
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);

            // menu
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
            text = "Start Game";
            x = getXForCentreText(text);
            y = gp.tileSize * 6;
            g2.drawString(text, x, y);

            if (commandNum == 0) g2.drawString(">", x - gp.tileSize, y);

            text = "Settings";
            x = getXForCentreText(text);
            y = gp.tileSize * 7;
            g2.drawString(text, x, y);

            if (commandNum == 1) g2.drawString(">", x - gp.tileSize, y);

            text = "Exit";
            x = getXForCentreText(text);
            y = gp.tileSize * 8;
            g2.drawString(text, x, y);

            if (commandNum == 2) g2.drawString(">", x - gp.tileSize, y);
        }
        else if (titleScreenState == 1) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
            String text = "Start Game";
            int x = getXForCentreText(text);
            int y = gp.tileSize*3;

            // shadow
            g2.setColor(Color.black);
            g2.drawString(text, x + 3, y + 3);

            // main colour
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);

            // menu
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));

            text = "New Game";
            x = getXForCentreText(text);
            y = gp.tileSize * 6;
            g2.drawString(text, x, y);

            if (commandNum == 1) g2.drawString(">", x - gp.tileSize, y);

            text = "Resume";
            x = getXForCentreText(text);
            y = gp.tileSize * 7;
            g2.drawString(text, x, y);

            if (commandNum == 2) g2.drawString(">", x - gp.tileSize, y);

            text = "Go Back";
            x = getXForCentreText(text);
            y = gp.tileSize * 8;
            g2.drawString(text, x, y);

            if (commandNum == 3) g2.drawString(">", x - gp.tileSize, y);
        }

        else if (titleScreenState == 2) {} // PLayer Names
        else if (titleScreenState == 3) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
            String text = "Settings";
            int x = getXForCentreText(text);
            int y = gp.tileSize*3;

            // shadow
            g2.setColor(Color.black);
            g2.drawString(text, x + 3, y + 3);

            // main colour
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);

            // menu
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
            text = "Select Player Number";
            x = getXForCentreText(text);
            y = gp.tileSize * 6;
            g2.drawString(text, x, y);

            if (commandNum == 4) g2.drawString(">", x - gp.tileSize, y);

            text = "Select Map";
            x = getXForCentreText(text);
            y = gp.tileSize * 7;
            g2.drawString(text, x, y);

            if (commandNum == 5) g2.drawString(">", x - gp.tileSize, y);

            text = "Go Back";
            x = getXForCentreText(text);
            y = gp.tileSize * 8;
            g2.drawString(text, x, y);

            if (commandNum == 6) g2.drawString(">", x - gp.tileSize, y);
        }
        else if (titleScreenState == 4) drawPlayerNumberSelection(g2);
        else if (titleScreenState == 5) drawMapSelection(g2);
        else if (titleScreenState == 6) drawPlayerNameInputs(g2);
        else if (titleScreenState == 7) drawFinishScreen(g2);
    }

    /**
     * Draws the pause screen, offering options like resuming the game or going to the
     * settings.
     */
    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
        g2.setColor(Color.WHITE);
        String text = "Paused";
        int x = getXForCentreText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        text = "Settings";
        x = getXForCentreText(text);
        y += 50;

        if (commandNum == 2) g2.drawString(">", x - gp.tileSize, y);

        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        text = "Exit";
        x = getXForCentreText(text);
        y += 40;
        g2.drawString(text, x, y);

        if (commandNum == 3) g2.drawString(">", x - gp.tileSize, y);
    }

    /**
     * Displays the game over screen with the final scores and a message indicating the
     * game's completion.
     * @param g2
     */
    public void drawFinishScreen(Graphics2D g2){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44F));
        g2.setColor(Color.WHITE);
        String text = "Game Over";
        int x = getXForCentreText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        text = "Winner is:";
        x = getXForCentreText(text);
        y += 50;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        text = "Settings";
        x = getXForCentreText(text);
        y += 40;

        if (commandNum == 8) g2.drawString(">", x - gp.tileSize, y);

        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.WHITE);
        text = "Exit";
        x = getXForCentreText(text);
        y += 30;
        g2.drawString(text, x, y);

        if (commandNum == 9) g2.drawString(">", x - gp.tileSize, y);
    }

    /**
     * Shows the end game scores on a custom-designed game over screen with an OK
     * button to acknowledge the end of the game.
     * @param scores
     */
    public void showEndGameScores(int[] scores) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.drawImage(gp.mainMenuBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 44));
        g2.setColor(Color.WHITE);
        String text = "Game Over";
        int x = getXForCentreText(text);
        int y = gp.tileSize * 2;
        //g2.drawString(text, x, y);
        drawTextWithBorder(g2, text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22));
        y += gp.tileSize * 2;
        for (int i = 0; i < scores.length; i++) {
            text = "Player " + (i + 1) + ": " + scores[i];
            x = getXForCentreText(text);
            //g2.drawString(text, x, y);
            drawTextWithBorder(g2, text, x, y);
            y += gp.tileSize;
        }

        String okText = "OK";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36));
        int okX = getXForCentreText(okText);
        int okY = y + gp.tileSize * 2;
        //g2.drawString(okText, okX, okY);

        int buttonWidth = gp.tileSize * 2;
        int buttonHeight = gp.tileSize;
        g2.setColor(Color.GRAY);
        g2.fillRect(okX - 20, okY - buttonHeight + 10, buttonWidth, buttonHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(okX - 20, okY - buttonHeight + 10, buttonWidth, buttonHeight);
        drawTextWithBorder(g2, okText, okX, okY);

        g2.drawRect(okX - 20, okY - 40, gp.tileSize * 2, gp.tileSize);
        setOkButtonCoordinates(okX, okY);

        gp.repaint();
    }

    /**
     * Utility method to draw text with a border for better visibility against varied
     * backgrounds.
     * @param g2
     * @param text
     * @param x
     * @param y
     */
    private void drawTextWithBorder(Graphics2D g2, String text, int x, int y) {
        g2.setColor(Color.BLACK);
        g2.drawString(text, x - 1, y - 1);
        g2.drawString(text, x + 1, y + 1);
        g2.drawString(text, x - 1, y + 1);
        g2.drawString(text, x + 1, y - 1);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
    }

    /**
     * Computes and returns the y-coordinate for the OK button on the game over screen.
     * @return
     */
    public int getGameOverOkButtonY() {
        return (gp.tileSize * 2) * gp.playerScores.length + gp.tileSize * 3;
    }

    /**
     * Calculates the x-coordinate for centering text on the screen based on its length.
     * @param text
     * @return
     */
    public int getXForCentreText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return (gp.screenWidth / 2 - length / 2);
    }
}