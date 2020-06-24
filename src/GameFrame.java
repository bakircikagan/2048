import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public final class GameFrame extends JFrame implements KeyListener, Observer{
    // CONSTANTS
    private static final Font FONT_1 = new Font("Roman", Font.BOLD, 30);
    private static final Font FONT_2 = new Font("Roman", Font.BOLD, 20);

    // PROPERTIES
    private JPanel mainPanel;
    private JLabel label;
    private JPanel northPanel;
    private JPanel centerPanel;
    private JButton[][] buttons;
    private Game game;
    private final int size;

    private HashMap<Integer, ColorHSV> colorMap;
    private ColorHSV darkest;

    // Static high score variable
    private static int highScore = 0;

    public GameFrame() {
        this(4);
    }

    public GameFrame(int size) {
        super("2048");
        this.size = size;
        buttons = new JButton[size][size];
        mainPanel = new JPanel();
        northPanel = new JPanel();
        centerPanel = new JPanel();

        game = new Game(size, this);

        highScore = Math.max(game.calculateScore(), highScore);
        label = new JLabel("High Score: " + highScore);

        northPanel.add(label);
        centerPanel.setLayout(new GridLayout(size, size));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);

        colorMap = new HashMap<>();
        darkest = new ColorHSV();
        colorMap.put(0, ColorHSV.WHITE);
        colorMap.put(Game.BASE, darkest);

        darkest = darkest.darker();
        colorMap.put(Game.NEXT_BASE, darkest);

        //Creating the buttons
        for (int i = 0; i < buttons.length; ++i) {
            for (int j = 0; j < buttons[0].length; ++j) {
                int num = game.getNum(i, j);
                String text = num == 0 ? "" : num + "";
                buttons[i][j] = new JButton(text);
                buttons[i][j].setFont(FONT_1);
                buttons[i][j].setFocusable(false);
                buttons[i][j].setBackground(colorMap.get(num).toAwtColor());
                centerPanel.add(buttons[i][j]);
            }
        }

        label.setFont(FONT_2);
        northPanel.setBackground(Color.CYAN);

        addKeyListener(this);

        add(mainPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // METHODS
    @Override
    public void update() {
        int score = game.calculateScore();
        if (!colorMap.containsKey(score)) {
            darkest = darkest.darker();
            colorMap.put(score, darkest);
        }
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int num = game.getNum(i, j);
                String text = num == 0 ? "" : num + "";
                buttons[i][j].setText(text);
                ColorHSV hsv = colorMap.get(num);
                Useful.require(hsv != null);
                buttons[i][j].setBackground(hsv.toAwtColor());
            }
        }
        if (game.isOver()) {
            label.setText("Game Over. Press R to restart");
        }
        else {
            highScore = Math.max(score, highScore);
            label.setText("High Score: " + highScore);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();
        if (code == KeyEvent.VK_R) {
            dispose();
            new GameFrame(size);
        }
        else if( !game.isOver()) {
            if (code == KeyEvent.VK_UP) {
                game.moveUp(true);
            } else if (code == KeyEvent.VK_DOWN) {
                game.moveDown(true);
            } else if (code == KeyEvent.VK_RIGHT) {
                game.moveRight(true);
            } else if (code == KeyEvent.VK_LEFT) {
                game.moveLeft(true);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
}

