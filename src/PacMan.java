import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel {
    // Classes for images
    class Block {
        // are these things I must define when I create the object?
        int x;
        int y;
        int width;
        int height;
        Image image;

        // save the starting X and Y positions since the pos will change.
        int startX;
        int startY;

        // if so then what are these?
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    }

    // Panel size and images
    private int columnCount = 19;
    private int rowCount = 21;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;

    // Hash sets to store values for multi block objs and one for packman
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

        // load images
        wallImage = new ImageIcon(getClass().getResource("./pixel_art/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./pixel_art/blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./pixel_art/redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pixel_art/pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./pixel_art/orangeGhost.png")).getImage();

        pacmanDownImage = new ImageIcon(getClass().getResource("./pixel_art/pacmanDown.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pixel_art/pacmanUp.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pixel_art/pacmanRight.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pixel_art/pacmanLeft.png")).getImage();
    } 
}
