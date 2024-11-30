package gui;

import dragDrop.DTLetter;
import dragDrop.LetterTransferHandler;
import enums.GameType;
import enums.LetterStatus;
import enums.LetterType;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;

/*
 * Kaynak:
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */

public class DragDropPlay extends JPanel {

    private static JFrame frame;

    private final DTLetter[][] trials;
    private final DTLetter[] keyboard;
    private final LetterTransferHandler letterHandler;

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

    public DragDropPlay(String name) {
        super(new BorderLayout());

        wordle = new Wordle();
        word = wordle.getWord();
        this.name = name;

        Character[] keyboardLetters = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
                'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
                'Z', 'X', 'C', 'V', 'B', 'N', 'M'};

        trials = new DTLetter[5][5];
        keyboard = new DTLetter[26];
        letterHandler = new LetterTransferHandler();

        timeLabel.setText("Time: 0:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timer.addActionListener(actionEvent -> {
            timeCount++;
            timeLabel.setText("Time: " + WordleGui.countToTime(timeCount));
        });

        JPanel trialPanel = new JPanel(new GridLayout(5, 5));
        trialPanel.setPreferredSize(new Dimension(450, 400));

        for (int i = 0; i < trials.length; i++) {
            for (int j = 0; j < trials[i].length; j++) {
                trials[i][j] = new DTLetter(null, LetterType.GUESS_LETTER);
                trialPanel.add(trials[i][j]);
            }
        }

        guessLabel.setText("Guess Made: " + trialCount);
        guessLabel.setHorizontalAlignment(SwingConstants.CENTER);

        remainingLabel.setText("Remaining: " + lives);
        remainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton checkBtn = new JButton();
        checkBtn.setText("Check Answer");
        checkBtn.addActionListener(e -> checkAnswer());

        JPanel keyboardPanel = new JPanel(new GridLayout(3, 1));
        keyboardPanel.setPreferredSize(new Dimension(450, 210));
        JPanel keyboardRow1 = new JPanel(new GridLayout(1, 10));
        JPanel keyboardRow2 = new JPanel(new GridLayout(1, 9));
        JPanel keyboardRow3 = new JPanel(new GridLayout(1, 7));

        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new DTLetter(keyboardLetters[i], LetterType.KEYBOARD_LETTER);
            keyboard[i].setTransferHandler(letterHandler);
            if (i < 10) keyboardRow1.add(keyboard[i]);
            else if (i < 19) keyboardRow2.add(keyboard[i]);
            else keyboardRow3.add(keyboard[i]);
        }

        keyboardPanel.add(keyboardRow1);
        keyboardPanel.add(keyboardRow2);
        keyboardPanel.add(keyboardRow3);

        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        JPanel southPanel = new JPanel(new BorderLayout());

        northPanel.add(timeLabel, BorderLayout.NORTH);
        northPanel.add(trialPanel, BorderLayout.SOUTH);
        centerPanel.add(guessLabel);
        centerPanel.add(checkBtn);
        centerPanel.add(remainingLabel);
        southPanel.add(keyboardPanel, BorderLayout.CENTER);

    
        addHandler(0);

        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        timer.start();
    }

    private void addHandler(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setTransferHandler(letterHandler);
        }
    }

    private void removeHandler(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setTransferHandler(null);
        }
    }

    private boolean isRowFull(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            if (trials[row][i].getChar() == null) return false;
        }
        return true;
    }

    private String getGuess(int row) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            answer.append(trials[row][i].getChar());
        }
        return answer.toString();
    }

    private boolean isAnswerFound(String guess) {
        return guess.equalsIgnoreCase(word);
    }

    private void checkAnswer() {
        try {
            if (isRowFull(trialCount)) {
                colorize(trialCount);
                if (isAnswerFound(getGuess(trialCount))) {
                    guessLabel.setText("Guesses Made: " + ++trialCount);
                    remainingLabel.setText("Remaining: " + --lives);
                    gameOver(true);
                } else {
                    if (trialCount < 4) addHandler(trialCount + 1);
                    removeHandler(trialCount);
                    guessLabel.setText("Guesses Made: " + ++trialCount);
                    remainingLabel.setText("Remaining: " + --lives);
                    if (lives == 0) gameOver(false);
                }
            } else if (!isGameOver) { 
                JOptionPane.showMessageDialog(this, "Not enough letters", "Oops!", JOptionPane.WARNING_MESSAGE);
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    private void colorize(int row) {
        LetterStatus[] statuses = wordle.getLetterStatuses(getGuess(row));
        for (int i = 0; i < statuses.length; i++) {
            trials[row][i].setStatus(statuses[i]);
            for (DTLetter key : keyboard) {
                if (key.getChar().equals(trials[row][i].getChar())) {
                    key.setStatus(statuses[i]);
                }
            }
        }
    }

    private void gameOver(boolean answerFound) {
        timer.stop();
        isGameOver = true;
        if (answerFound) {
            String title = "Congrats, " + name + "!";
            String message = "You have found the answer";
            if (wordle.gameOver(GameType.DRAG_DROP, timeCount, trialCount, word, name)) {
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

        frame = new JFrame("Wordle | Drag&Drop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DragDropPlay dragDropPlay = new DragDropPlay(name);
        dragDropPlay.setOpaque(true);
        frame.setContentPane(dragDropPlay);
        frame.setPreferredSize(new Dimension(500, 700));

        frame.pack();
        frame.setVisible(true);
    }
}
