package gui;

import enums.GameType;
import enums.LetterStatus;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MultiplayerPlay extends JPanel {

    private static JFrame frame;

    private final JTextField[][] trials;

    private int trialCount1 = 0;
    private int trialCount2 = 0;
    private int lives1 = 3;
    private int lives2 = 3;

    private final JLabel guessLabel1 = new JLabel();
    private final JLabel guessLabel2 = new JLabel();
    private final JLabel remainingLabel1 = new JLabel();
    private final JLabel remainingLabel2 = new JLabel();

    private final JLabel timeLabel1 = new JLabel();
    private final JLabel timeLabel2 = new JLabel();
    private int timeCount1 = 0;
    private int timeCount2 = 0;
    private final Timer timer1 = new Timer(1000, null);
    private final Timer timer2 = new Timer(1000, null);

    private final Wordle wordle;
    private final String word;

    private final String name1;
    private final String name2;

    private boolean isGameOver = false;

    public MultiplayerPlay(String name1, String name2) {
        super(new BorderLayout());
        this.name1 = name1;
        this.name2 = name2;

        wordle = new Wordle();
        word = wordle.getWord();

        trials = new JTextField[6][5];

        timeLabel1.setText("Time: 0:00");
        timeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        timer1.addActionListener(actionEvent -> {
            timeCount1++;
            timeLabel1.setText(String.format("Time (%s): %s", name1, WordleGui.countToTime(timeCount1)));
        });

        timeLabel2.setText("Time: 0:00");
        timeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        timer2.addActionListener(actionEvent -> {
            timeCount2++;
            timeLabel2.setText(String.format("Time (%s): %s", name2, WordleGui.countToTime(timeCount2)));
        });

        guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, timeCount1));
        guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, timeCount2));
        guessLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        guessLabel2.setHorizontalAlignment(SwingConstants.CENTER);

        remainingLabel1.setText(String.format("Remaining for %s: %d", name1, lives1));
        remainingLabel2.setText(String.format("Remaining for %s: %d", name2, lives2));
        remainingLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        remainingLabel2.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel labels = new JPanel(new GridLayout(2, 3));
        labels.add(guessLabel1);
        labels.add(timeLabel1);
        labels.add(remainingLabel1);
        labels.add(guessLabel2);
        labels.add(timeLabel2);
        labels.add(remainingLabel2);

        JPanel trialPanel = new JPanel(new GridLayout(6, 5));
        initCells(trialPanel);
        makeEditable(0, true);

        setPreferredSize(new Dimension(500, 500));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        add(labels, BorderLayout.NORTH);
        add(trialPanel, BorderLayout.CENTER);

        timer1.start();
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
                        int trialCount = trialCount1 + trialCount2;
                        try {
                            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                            if (!Character.isAlphabetic(c) && c != KeyEvent.VK_ENTER) {
                                e.consume();
                            } else if (c == KeyEvent.VK_ENTER) { // Basilan tus enter ise
                                if (isRowFull(trialCount)) manager.focusNextComponent(trials[row][4]);
                                if (timer1.isRunning()) checkAnswer1();
                                else if (timer2.isRunning()) checkAnswer2();
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
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
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

    private void checkAnswer1() {
        int trialCount = trialCount1 + trialCount2;
        if (isRowFull(trialCount)) {
            timer1.stop();
            colorize(trialCount);
            if (isAnswerFound(getGuess(trialCount))) {
                guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, ++trialCount1));
                remainingLabel1.setText(String.format("Remaining for %s: %d", name1, --lives1));
                gameOver(true, name1, timeCount1, trialCount1);
            } else { // Cevap bulunmadiysa
                if (trialCount < 5) makeEditable(trialCount + 1, true);
                makeEditable(trialCount, false);
                guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, ++trialCount1));
                remainingLabel1.setText(String.format("Remaining for %s: %d", name1, --lives1));
                timer2.start();
            }
        } else if (!isGameOver) { 
            JOptionPane.showMessageDialog(this, "Not enough letters", "Oops!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkAnswer2() {
        int trialCount = trialCount1 + trialCount2;
        if (isRowFull(trialCount)) {
            timer2.stop();
            colorize(trialCount);
            if (isAnswerFound(getGuess(trialCount))) {
                guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, ++trialCount2));
                remainingLabel2.setText(String.format("Remaining for %s: %d", name2, --lives2));
                gameOver(true, name2, timeCount2, trialCount2);
            } else { 
                if (trialCount < 5) makeEditable(trialCount + 1, true);
                makeEditable(trialCount, false);
                guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, ++trialCount2));
                remainingLabel2.setText(String.format("Remaining for %s: %d", name2, --lives2));
                if (lives2 == 0) gameOver(false, "", timeCount2, trialCount2);
                else timer1.start();
            }
        } else if (!isGameOver) { 
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

    private void gameOver(boolean answerFound, String name, int timeCount, int trialCount) {
        timer1.stop();
        timer2.stop();
        isGameOver = true;
        if (answerFound) {
            String message = name + " has found the answer";
            if (wordle.gameOver(GameType.MULTIPLAYER, timeCount, trialCount, word, name)) {
                message += " with a new high score!";
            } else message += "!";
            ImageIcon confetti = new ImageIcon("src/resources/confetti.gif");
            JOptionPane.showMessageDialog(this, message, "Congrats!", JOptionPane.PLAIN_MESSAGE, confetti);
        } else { // Cevap bulunamadiysa uygun mesaj ayarlaniyor ve gosteriliyor
            String message = "The correct answer was \"" + word + "\"";
            JOptionPane.showMessageDialog(this, message, "Try Again", JOptionPane.PLAIN_MESSAGE);
        }
        WordleGui.createAndShowGUI();
        frame.dispose();
    }

    public static void createAndShowGUI(String name1, String name2) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Wordle | Multiplayer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MultiplayerPlay multiplayerPlay = new MultiplayerPlay(name1, name2);
        multiplayerPlay.setOpaque(true);
        frame.setContentPane(multiplayerPlay);
        frame.setPreferredSize(new Dimension(600, 800));

        frame.pack();
        frame.setVisible(true);
    }
}
