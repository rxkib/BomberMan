package main;
import main.GamePanel;
import javax.swing.JFrame;
import java.io.IOException;

/**
 * This class is responsible for managing the user interface of the game.
 * It includes methods for showing end game scores, drawing text with a border for better visibility,
 * computing the y-coordinate for the OK button on the game over screen, and calculating the x-coordinate
 * for centering text on the screen based on its length.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        JFrame window = new JFrame();
        GamePanel gamePanel = new GamePanel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Bomberman");
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);

        window.setVisible(true);
        gamePanel.setupGame();

        gamePanel.startGameThread();
    }
}
