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
    private Image foodImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;

    // Hash sets to store values for multi block objs and one for packman
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    // Tile map for level
    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

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

        loadMap();
    } 

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) { // start at row 1 in the tilemap
            for (int c = 0; c < columnCount; c++) { // go through each column
                String row = tileMap[r]; // grab the whole row
                char tileMapChar = row.charAt(c); // check which char exists at the proper column in the row
                
                int x = c*tileSize; // use c var to calc pix horiz
                int y = r*tileSize; // use r var to calc pix vert

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
                
            }
        }
    }

    // part of panel obj somehow - look that up
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.fillRect(pacman.x, pacman.y, pacman.width, pacman.height);
    }
}
