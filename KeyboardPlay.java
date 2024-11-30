package gui;

import enums.GameType;
import enums.LetterStatus;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardPlay extends JPanel {

    private static JFrame frame;

    private final JTextField[][] trials;

    private int trialCount = 0;
    private int lives = 5;
    private final JLabel guessLabel = new JLabel();
    private final JLabel remainingLabel = new JLabel();

    private final JLabel timeLabel = new JLabel();
    private int timeCount = 0;
    private final Timer timer = new Timer(1000, null);

    private final Wordle wordle;
    private final String word;
    private final String name;

    private boolean isGameOver = false;

    public KeyboardPlay(String name) {
        super(new BorderLayout());

        wordle = new Wordle();
        word = wordle.getWord();
        this.name = name;

        trials = new JTextField[5][5];

        timeLabel.setText("Time: 0:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timer.addActionListener(actionEvent -> {
            timeCount++;
            timeLabel.setText("Time: " + WordleGui.countToTime(timeCount));
        });

        guessLabel.setText("Guesses Made: " + trialCount);
        guessLabel.setHorizontalAlignment(SwingConstants.CENTER);

        remainingLabel.setText("Remaining: " + lives);
        remainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel labels = new JPanel(new GridLayout(1, 3));
        labels.add(guessLabel);
        labels.add(timeLabel);
        labels.add(remainingLabel);

        JPanel trialPanel = new JPanel(new GridLayout(5, 5));
        initCells(trialPanel);
    
        makeEditable(0, true);

        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        add(labels, BorderLayout.NORTH);
        add(trialPanel, BorderLayout.CENTER);

        timer.start();
    }

    private void initCells(JPanel trialPanel) {
        for (int i = 0; i < trials.length; i++) {
            for (int j = 0; j < trials[i].length; j++) {
                JTextField cell = new JTextField();
                cell.setSize(new Dimension(20, 20));
                cell.setFont(new Font("Segue UI Black", Font.BOLD, 40));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setEditable(false);
                cell.setTransferHandler(null);

                int row = i;
                int column = j;
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        try {
                            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                            if (!Character.isAlphabetic(c) && c != KeyEvent.VK_ENTER) {                       
                                e.consume();
                            } else if (c == KeyEvent.VK_ENTER) { // Basilan tus enter ise
                                if (isRowFull(trialCount)) manager.focusNextComponent(trials[row][4]);
                                checkAnswer();
                            } else if (Character.isAlphabetic(c)) { // Basilan tus harf ise
                                if (column == 4) {
                                    if (cell.isEditable() && cell.getText().isBlank()) cell.setText(String.valueOf(c).toUpperCase());
                                    e.consume();
                                } else if (cell.isEditable()) { // Mevcut sutun son degil ama duzenlenebilir ise
                                    cell.setText(String.valueOf(c).toUpperCase());
                                    e.consume();
                                    manager.focusNextComponent();
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                    }
                });
                trials[i][j] = cell;
                trialPanel.add(trials[i][j]);
            }
        }
    }

    private void makeEditable(int row, boolean bool) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setEditable(bool);
        }
    }

    private boolean isRowFull(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            if (trials[row][i].getText().isBlank()) return false;
        }
        return true;
    }

    private String getGuess(int row) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            answer.append(trials[row][i].getText());
        }
        return answer.toString();
    }

    private boolean isAnswerFound(String guess) {
        return guess.equalsIgnoreCase(word);
    }

    private void checkAnswer() {
        if (isRowFull(trialCount)) {
            colorize(trialCount);
            if (isAnswerFound(getGuess(trialCount))) {
                guessLabel.setText("Guesses Made: " + ++trialCount);
                remainingLabel.setText("Remaining: " + --lives);
                gameOver(true);
            } else {
                if (trialCount < 4) makeEditable(trialCount + 1, true);
                makeEditable(trialCount, false);
                guessLabel.setText("Guesses Made: " + ++trialCount);
                remainingLabel.setText("Remaining: " + --lives);
                if (lives == 0) gameOver(false);
            }
        } else if (!isGameOver) { // Satir tamamen dolu degil ve oyun devam ediyor ise
            JOptionPane.showMessageDialog(this, "Not enough letters", "Oops!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void colorize(int row) {
        LetterStatus[] statuses = wordle.getLetterStatuses(getGuess(row));
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i] == LetterStatus.CORRECT) trials[row][i].setBackground(Color.GREEN);
            else if (statuses[i] == LetterStatus.MISPLACED) trials[row][i].setBackground(Color.YELLOW);
            else if (statuses[i] == LetterStatus.WRONG) trials[row][i].setBackground(Color.GRAY);
            else trials[row][i].setBackground(Color.WHITE);
        }
    }

    private void gameOver(boolean answerFound) {
        timer.stop();
        isGameOver = true;
        if (answerFound) {
            String title = "Congrats, " + name + "!";
            String message = "You have found the answer";
            if (wordle.gameOver(GameType.KEYBOARD, timeCount, trialCount, word, name)) {
                message += " with a new high score!";
            } else message += "!";
            ImageIcon confetti = new ImageIcon("src/resources/confetti.gif");
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.PLAIN_MESSAGE, confetti);
        } else { 
            String message = "The correct answer was \"" + word + "\"";
            JOptionPane.showMessageDialog(this, message, "Try Again", JOptionPane.PLAIN_MESSAGE);
        }
        WordleGui.createAndShowGUI();
        frame.dispose();
    }

    public static void createAndShowGUI(String name) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Wordle | Keyboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KeyboardPlay keyboardPlay = new KeyboardPlay(name);
        keyboardPlay.setOpaque(true);
        frame.setContentPane(keyboardPlay);
        frame.setPreferredSize(new Dimension(500, 500));

        frame.pack();
        frame.setVisible(true);
    }
}
