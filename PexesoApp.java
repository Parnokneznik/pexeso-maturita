import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class PexesoApp {

    private static final int BOARD_SIZE = 4;
    private static final String[] ANIMALS = {
        "Woodpecker", "Peacock", "Hare", "Squirrel",
        "Fawn", "Bear", "Otter", "Jay",
        "Wolf", "Boar", "Lynx", "Lizard",
        "Hedgehog", "Deer", "Badger", "Fox"
    };
    private static String[][] board;
    private static boolean[][] revealed;
    private static JButton[][] buttons;
    private static JFrame frame;
    private static int pairsFound = 0;
    private static int mistakes = 0;
    private static JButton firstButton = null;
    private static JButton secondButton = null;
    private static int[] firstCoords = null;
    private static int[] secondCoords = null;
    private static JLabel statusLabel;

    public static void main(String[] args) {
        board = new String[BOARD_SIZE][BOARD_SIZE];
        revealed = new boolean[BOARD_SIZE][BOARD_SIZE];
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

        initializeBoard();
        shuffleBoard();
        createAndShowGUI();
    }

    private static void initializeBoard() {
        List<String> cards = new ArrayList<>();
        for (String animal : ANIMALS) {
            cards.add(animal);
            cards.add(animal);
        }

        Collections.shuffle(cards);
        
        int index = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = cards.get(index++);
                revealed[i][j] = false;
            }
        }
    }

    private static void shuffleBoard() {
        Random random = new Random();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int x = random.nextInt(BOARD_SIZE);
                int y = random.nextInt(BOARD_SIZE);

                String temp = board[i][j];
                board[i][j] = board[x][y];
                board[x][y] = temp;
            }
        }
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Pexeso Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new JButton("*");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                int row = i;
                int col = j;
                buttons[i][j].addActionListener(e -> handleButtonClick(row, col));
                boardPanel.add(buttons[i][j]);
            }
        }

        statusLabel = new JLabel("Pairs found: 0 | Mistakes: 0", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private static void handleButtonClick(int row, int col) {
        if (revealed[row][col] || (firstButton != null && secondButton != null)) {
            return;
        }

        buttons[row][col].setText(board[row][col]);

        if (firstButton == null) {
            firstButton = buttons[row][col];
            firstCoords = new int[]{row, col};
        } else {
            secondButton = buttons[row][col];
            secondCoords = new int[]{row, col};
            checkMatch();
        }
    }

    private static void checkMatch() {
        if (board[firstCoords[0]][firstCoords[1]].equals(board[secondCoords[0]][secondCoords[1]])) {
            revealed[firstCoords[0]][firstCoords[1]] = true;
            revealed[secondCoords[0]][secondCoords[1]] = true;
            pairsFound++;
            firstButton = null;
            secondButton = null;

            if (pairsFound == (BOARD_SIZE * BOARD_SIZE) / 2) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You found all pairs!\nMistakes made: " + mistakes);
                frame.dispose();
            }
        } else {
            mistakes++;
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                firstButton.setText("*");
                secondButton.setText("*");
                firstButton = null;
                secondButton = null;
                updateStatusLabel();
            });
            timer.setRepeats(false);
            timer.start();
        }
        updateStatusLabel();
    }

    private static void updateStatusLabel() {
        statusLabel.setText("Pairs found: " + pairsFound + " | Mistakes: " + mistakes);
    }
} 