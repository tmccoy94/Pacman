import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
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

        // for velocity
        char direction = 'U'; // U, D, L, R
        int velocityX = 0;
        int velocityY = 0;

        // Disred direction - implement for ghosts not getting stuck in a loop
        // when they cross the boundary of the map. See move() for more.
        char desiredDirection = '\0';        

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

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            // move one step ahead
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    // if it runs into something on this step, take a step back
                    // and change to original direction.
                    // Effectively causing no change on next paint.
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    // Panel size and images
    private int columnCount = 19;
    private int rowCount = 22;
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

    // Hash sets to store values for multi block objs and one for pac man
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    // Use to fire off an action event that triggers a repaint
    Timer gameLoop;

    // Use these for ghost movement
    char[] directions = {'U', 'D', 'L', 'R'}; // up, down, left, right (for ghosts)
    Random random = new Random();

    // Implement scoring mechanics
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    // Tile map for level
    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "OOOOOOOOOOOOOOOOOOO",
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
        addKeyListener(this); // this works because implements on this obj contains the keylistener
        setFocusable(true);

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

        // All below is in the game loop
        for (Block ghost: ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        // how does this connect to action performed?
        gameLoop = new Timer(50, this); // 50ms over 1000ms per second you get 20fps with this refresh rate
        gameLoop.start();
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

    // This only draws once, so we need a game loop attached to JPanel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over" + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {

        // Pac man movement ----
        pacman.x += pacman.velocityX; // changes the x position pacman is painted at
        pacman.y += pacman.velocityY; // changes the y position pacman is painted at
        // Check wall positions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }            
        }
        // Reset x upon crossing off screen for pacman
        // Right edge passed — wrap to left
        if (pacman.x >= boardWidth) {
            pacman.x = 0;
        }
        // Left edge passed — wrap to right (subtract pacman's width to keep full sprite on screen)
        else if (pacman.x + pacman.width <= 0) {
            pacman.x = boardWidth - pacman.width;
        }
        // Check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }            
        }
        foods.remove(foodEaten);
        // check ghost collision
        for (Block ghost : ghosts) {
            if(collision(pacman, ghost)) {
                lives -= 1;
                pacman.reset();
            }
        }


        // Ghost movement ----
        for (Block ghost : ghosts) {
            
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // change to desired direction
            if (ghost.desiredDirection != '\0') {
                ghost.updateDirection(ghost.desiredDirection);
            } 
            
            // Handle collisions for all ghosts
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    // step back
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    // Pick a new direction to go in
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            } 
            if (ghost.x + ghost.width >= boardWidth) {
                ghost.updateDirection('L');
                ghost.desiredDirection = directions[random.nextInt(2)]; // pick up or down
            } 
            else if (ghost.x <= 0) {
                // Turn around
                ghost.updateDirection('R');
                // Go up or down at next turn
                ghost.desiredDirection = directions[random.nextInt(2)]; // pick up or down
            }
            // Reset desired direction if moving in that direction
            if (ghost.desiredDirection == ghost.direction) {
                ghost.desiredDirection = '\0';
            } 
        }
        


    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width && // left side of a is left of right side of b
                a.x + a.width > b.x && // right side of a is right of left side of b
                a.y < b.y + b.height && // top of a is above bottom of b
                a.y + a.height > b.y;  // bottom of a is below top of b
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {        
    } // activates when you hold a key, we don't need that

    @Override
    public void keyPressed(KeyEvent e) {
    } // activates only when you press a key, we don't need that either

    @Override
    public void keyReleased(KeyEvent e) {
        // System.out.println("KeyEvent: " + e.getKeyCode());

        // If update direction called multiple times it increases velocity. I need that limited to 1 time per frame.
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }

        // Update image afterwards bc an attempt change in direction is not
        // always a change in direction.

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
    }
}
