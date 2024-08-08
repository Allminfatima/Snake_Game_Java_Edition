import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 20;
    private static final int WIDTH = GRID_SIZE * CELL_SIZE;
    private static final int HEIGHT = GRID_SIZE * CELL_SIZE;
    private static final int SCOREBOARD_HEIGHT = 30;

    private ArrayList<Point> snake;
    private Point food;
    private int direction; // 0 = UP, 1 = RIGHT, 2 = DOWN, 3 = LEFT
    private boolean running;
    private int score;
    private boolean grow; // Flag to indicate if the snake should grow

    private Timer timer;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT + SCOREBOARD_HEIGHT));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);

        snake = new ArrayList<>();
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));
        direction = 1;
        running = true;
        score = 0;
        grow = false;

        placeFood();

        timer = new Timer(100, this);
        timer.start();
    }

    private void placeFood() {
        Random rand = new Random();
        int x = rand.nextInt(GRID_SIZE);
        int y = rand.nextInt(GRID_SIZE);
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw scoreboard
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, SCOREBOARD_HEIGHT - 5);

        if (running) {
            // Draw snake
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE + SCOREBOARD_HEIGHT, CELL_SIZE, CELL_SIZE);
            }

            // Draw food
            g.setColor(Color.RED);
            g.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE + SCOREBOARD_HEIGHT, CELL_SIZE, CELL_SIZE);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Game Over! Score: " + score, WIDTH / 4, HEIGHT / 2 + SCOREBOARD_HEIGHT);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
            checkFood();
            repaint();
        }
    }

    private void move() {
        Point head = new Point(snake.get(0));

        switch (direction) {
            case 0: head.y--; break; // UP
            case 1: head.x++; break; // RIGHT
            case 2: head.y++; break; // DOWN
            case 3: head.x--; break; // LEFT
        }

        // Wrap around the boundaries
        if (head.x < 0) head.x = GRID_SIZE - 1;
        if (head.x >= GRID_SIZE) head.x = 0;
        if (head.y < 0) head.y = GRID_SIZE - 1;
        if (head.y >= GRID_SIZE) head.y = 0;

        snake.add(0, head);

        if (grow) {
            grow = false; // Reset the grow flag
        } else {
            snake.remove(snake.size() - 1); // Remove the tail if not growing
        }
    }

    private void checkCollisions() {
        Point head = snake.get(0);

        // Check if the snake's head collides with its own body
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }
    }

    private void checkFood() {
        Point head = snake.get(0);

        if (head.equals(food)) {
            grow = true; // Set the flag to grow the snake
            placeFood();
            score += 10; // Increase score
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int newDirection = direction;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    if (direction != 2) newDirection = 0; break;
            case KeyEvent.VK_RIGHT: if (direction != 3) newDirection = 1; break;
            case KeyEvent.VK_DOWN:  if (direction != 0) newDirection = 2; break;
            case KeyEvent.VK_LEFT:  if (direction != 1) newDirection = 3; break;
        }

        direction = newDirection;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
