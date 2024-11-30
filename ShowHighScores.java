package gui;

import wordle.HighScore;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class ShowHighScores extends JPanel {

    private static JFrame frame;

    public ShowHighScores() {
        super(new GridLayout(6, 1));
      
        ArrayList<HighScore> highScores = Wordle.readHighScores();

    
        JLabel nameLabel, wordLabel, timeLabel, countLabel;
        nameLabel = new JLabel("Player Name");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordLabel = new JLabel("Word (Answer)");
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel = new JLabel("Time (secs)");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel = new JLabel("Trial Count");
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

     
        String[] types = {"-- Select Game Mode --", "Drag&Drop", "Keyboard", "Multiplayer"};
        JComboBox<String> gameTypeSelector = new JComboBox<>(types);

        gameTypeSelector.addActionListener(e -> {
        
            int i = gameTypeSelector.getSelectedIndex() - 1;
            if (i >= 0) {
                nameLabel.setText("Player: " + highScores.get(i).getName());
                wordLabel.setText("Word: " + highScores.get(i).getWord());
                timeLabel.setText(highScores.get(i).getTimeCount() + " secs");
                countLabel.setText(highScores.get(i).getTrialCount() + " trials");
            } else { 
                nameLabel.setText("Player Name");
                wordLabel.setText("Word (Answer)");
                timeLabel.setText("Time (secs)");
                countLabel.setText("Trial Count");
            }
        });
        JButton goBackBtn = new JButton("Back");
        goBackBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        goBackBtn.addActionListener(e -> {
            WordleGui.createAndShowGUI();
            frame.dispose();
        });
        goBackBtn.setVisible(true);

        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Elementler ekrana yerlestiriliyor
        add(gameTypeSelector);
        add(nameLabel);
        add(wordLabel);
        add(timeLabel);
        add(countLabel);
        add(goBackBtn);
    }

    public static void createAndShowGui() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Wordle | High Scores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ShowHighScores highScores = new ShowHighScores();
        highScores.setOpaque(true);
        frame.setContentPane(highScores);
        frame.setPreferredSize(new Dimension(300, 300));

        frame.pack();
        frame.setVisible(true);
    }
}
