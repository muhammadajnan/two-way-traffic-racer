import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

 class CarGame extends JPanel implements ActionListener, KeyListener {
    // Window settings
    private static final int WIDTH = 380;
    private static final int HEIGHT = 520;
    private static final int ROAD_LEFT = 50;
    private static final int ROAD_WIDTH = 280;
    private static final int LANE_WIDTH = ROAD_WIDTH / 2; // 140px each
    private static final int CAR_WIDTH = 34;
    private static final int CAR_HEIGHT = 60;

    // Player car settings
    private int playerX = ROAD_LEFT + LANE_WIDTH + 50; // Starts on the right side
    private static final int PLAYER_Y = 410;
    private int score = 0;
    private int speed = 7;
    private int roadOffset = 0;

    // 1. Left Lane Car (Facing down, coming towards us)
    private int leftCarX = ROAD_LEFT + 50;
    private int leftCarY = -70;

    // 2. Right Lane Car (Facing up, going ahead of us)
    private int rightCarX = ROAD_LEFT + LANE_WIDTH + 50;
    private int rightCarY = -300;

    private boolean isGameOver = false;
    private boolean isPaused = false;

    private final Random rand = new Random();
    private final javax.swing.Timer timer;

    public CarGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 139, 34)); // Grass green
        setFocusable(true);
        addKeyListener(this);
        timer = new javax.swing.Timer(20, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Road surface
        g2.setColor(new Color(45, 45, 48));
        g2.fillRect(ROAD_LEFT, 0, ROAD_WIDTH, HEIGHT);

        // White side borders
        g2.setColor(Color.WHITE);
        g2.fillRect(ROAD_LEFT - 5, 0, 5, HEIGHT);
        g2.fillRect(ROAD_LEFT + ROAD_WIDTH, 0, 5, HEIGHT);

        // 2. Center Two-Way Road Divider (Dashed double yellow line)
        g2.setColor(new Color(255, 200, 0));
        int stripeHeight = 35;
        int gap = 25;
        int step = stripeHeight + gap;

        for (int y = -step + (roadOffset % step); y < HEIGHT + step; y += step) {
            g2.fillRect(WIDTH / 2 - 3, y, 6, stripeHeight);
        }

        // 3. Draw Opposite Traffic (Left Lane - Coming Down)
        drawCar(g2, leftCarX, leftCarY, new Color(220, 20, 60), true);

        // 4. Draw Forward Traffic (Right Lane - Going Up)
        drawCar(g2, rightCarX, rightCarY, new Color(138, 43, 226), false);

        // 5. Draw Player Car (Blue - Facing Up)
        drawCar(g2, playerX, PLAYER_Y, new Color(30, 144, 255), false);

        // 6. UI Bar
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(12, 12, 130, 36, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("SCORE: " + score, 22, 35);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("[P] Pause  |  [ENTER/R] Restart", 175, 26);

        // 7. Overlays
        if (isPaused && !isGameOver) {
            drawOverlay(g2, "PAUSED", "Press 'P' to Resume", new Color(0, 0, 0, 160));
        }

        if (isGameOver) {
            drawOverlay(g2, "CRASHED!", "Final Score: " + score + " | Press ENTER to Restart", new Color(150, 0, 0, 210));
        }
    }

    private void drawCar(Graphics2D g, int x, int y, Color bodyColor, boolean isComingDown) {
        // Wheels
        g.setColor(new Color(20, 20, 20));
        g.fillRoundRect(x - 3, y + 8, 5, 12, 3, 3);
        g.fillRoundRect(x + CAR_WIDTH - 2, y + 8, 5, 12, 3, 3);
        g.fillRoundRect(x - 3, y + CAR_HEIGHT - 20, 5, 12, 3, 3);
        g.fillRoundRect(x + CAR_WIDTH - 2, y + CAR_HEIGHT - 20, 5, 12, 3, 3);

        // Car Main Body
        g.setColor(bodyColor);
        g.fillRoundRect(x, y, CAR_WIDTH, CAR_HEIGHT, 10, 10);

        // Cabin
        g.setColor(bodyColor.darker());
        g.fillRoundRect(x + 4, y + 15, CAR_WIDTH - 8, CAR_HEIGHT - 28, 6, 6);

        if (isComingDown) {
            // Opposite car facing DOWN (Headlights at the bottom, Taillights at the top)
            g.setColor(new Color(120, 160, 185)); // Back glass
            g.fillRect(x + 6, y + 17, CAR_WIDTH - 12, 5);

            g.setColor(new Color(175, 225, 255)); // Front glass
            g.fillRect(x + 5, y + CAR_HEIGHT - 24, CAR_WIDTH - 10, 8);

            // Taillights (Top)
            g.setColor(new Color(255, 30, 30));
            g.fillRect(x + 4, y + 1, 6, 2);
            g.fillRect(x + CAR_WIDTH - 10, y + 1, 6, 2);

            // Headlights (Bottom)
            g.setColor(new Color(255, 255, 200));
            g.fillRoundRect(x + 3, y + CAR_HEIGHT - 4, 7, 5, 2, 2);
            g.fillRoundRect(x + CAR_WIDTH - 10, y + CAR_HEIGHT - 4, 7, 5, 2, 2);
        } else {
            // Car facing UP (Headlights at top, Taillights at bottom)
            g.setColor(new Color(175, 225, 255)); // Front glass
            g.fillRect(x + 5, y + 16, CAR_WIDTH - 10, 8);

            g.setColor(new Color(120, 160, 185)); // Back glass
            g.fillRect(x + 6, y + CAR_HEIGHT - 21, CAR_WIDTH - 12, 5);

            // Headlights (Top)
            g.setColor(new Color(255, 255, 200));
            g.fillRoundRect(x + 3, y + 1, 7, 5, 2, 2);
            g.fillRoundRect(x + CAR_WIDTH - 10, y + 1, 7, 5, 2, 2);

            // Taillights (Bottom)
            g.setColor(new Color(255, 30, 30));
            g.fillRect(x + 4, y + CAR_HEIGHT - 3, 6, 2);
            g.fillRect(x + CAR_WIDTH - 10, y + CAR_HEIGHT - 3, 6, 2);
        }
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle, Color bgColor) {
        g.setColor(bgColor);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        FontMetrics fm1 = g.getFontMetrics();
        g.drawString(title, (WIDTH - fm1.stringWidth(title)) / 2, 230);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(subtitle, (WIDTH - fm2.stringWidth(subtitle)) / 2, 265);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver && !isPaused) {
            roadOffset += speed;

            // 1. Move Left Lane Car (Coming straight at us: relative speed is higher)
            leftCarY += (speed + 2);
            if (leftCarY > HEIGHT) {
                leftCarY = -CAR_HEIGHT - rand.nextInt(100);
                leftCarX = ROAD_LEFT + 15 + rand.nextInt(LANE_WIDTH - CAR_WIDTH - 30);
                score++;
            }

            // 2. Move Right Lane Car (Going ahead in the same direction: relative speed is lower)
            rightCarY += (speed - 3);
            if (rightCarY > HEIGHT) {
                rightCarY = -CAR_HEIGHT - rand.nextInt(100);
                rightCarX = ROAD_LEFT + LANE_WIDTH + 15 + rand.nextInt(LANE_WIDTH - CAR_WIDTH - 30);
                score++;
            }

            // Hitbox checks
            Rectangle playerBox = new Rectangle(playerX + 2, PLAYER_Y + 3, CAR_WIDTH - 4, CAR_HEIGHT - 6);
            Rectangle leftBox = new Rectangle(leftCarX + 2, leftCarY + 3, CAR_WIDTH - 4, CAR_HEIGHT - 6);
            Rectangle rightBox = new Rectangle(rightCarX + 2, rightCarY + 3, CAR_WIDTH - 4, CAR_HEIGHT - 6);

            if (playerBox.intersects(leftBox) || playerBox.intersects(rightBox)) {
                isGameOver = true;
            }

            // Gradual speed boost with progress
            if (score > 0 && score % 10 == 0 && speed < 13) {
                speed = 7 + (score / 10);
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Steering: You can freely steer across BOTH left and right lanes
        if (!isGameOver && !isPaused) {
            if (key == KeyEvent.VK_LEFT && playerX > ROAD_LEFT + 6) {
                playerX -= 16;
            }
            if (key == KeyEvent.VK_RIGHT && playerX < (ROAD_LEFT + ROAD_WIDTH - CAR_WIDTH - 6)) {
                playerX += 16;
            }
        }

        // Pause
        if (key == KeyEvent.VK_P && !isGameOver) {
            isPaused = !isPaused;
        }

        // Restart
        if ((key == KeyEvent.VK_ENTER && isGameOver) || key == KeyEvent.VK_R) {
            restartGame();
        }
    }

    private void restartGame() {
        playerX = ROAD_LEFT + LANE_WIDTH + 50;
        leftCarY = -70;
        leftCarX = ROAD_LEFT + 50;
        rightCarY = -300;
        rightCarX = ROAD_LEFT + LANE_WIDTH + 50;
        score = 0;
        speed = 7;
        roadOffset = 0;
        isGameOver = false;
        isPaused = false;
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Two-Way Traffic Racer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CarGame());
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}