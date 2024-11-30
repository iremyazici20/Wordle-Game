package gui;

import javax.swing.*;
import java.awt.*;
public class WordleGui extends JPanel {

    private static final JFrame frame = new JFrame("Wordle");

    public WordleGui() {
        super(new GridLayout(6, 1));
        JLabel welcomeLabel = new JLabel("Welcome to the Wordle Game!");
        welcomeLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        welcomeLabel.setVisible(true);


        JLabel selectLabel = new JLabel("Please select game mode.");
        selectLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectLabel.setVerticalAlignment(SwingConstants.TOP);
        selectLabel.setVisible(true);


        JButton dragDropPlayBtn = new JButton("Drag & Drop");
        dragDropPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        dragDropPlayBtn.addActionListener(e -> dragDropPlay());
        dragDropPlayBtn.setVisible(true);


        JButton keyboardPlayBtn = new JButton("Keyboard");
        keyboardPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        keyboardPlayBtn.addActionListener(e -> keyboardPlay());
        keyboardPlayBtn.setVisible(true);


        JButton multiplayerPlayBtn = new JButton("Multiplayer");
        multiplayerPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        multiplayerPlayBtn.addActionListener(e -> multiplayerPlay());
        multiplayerPlayBtn.setVisible(true);


        JButton highScoresBtn = new JButton("Show High Scores");
        highScoresBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        highScoresBtn.addActionListener(e -> showHighScores());
        highScoresBtn.setVisible(true);


        add(welcomeLabel);
        add(selectLabel);
        add(dragDropPlayBtn);
        add(keyboardPlayBtn);
        add(multiplayerPlayBtn);
        add(highScoresBtn);
    }


    private void dragDropPlay() {

        String name = JOptionPane.showInputDialog("Player Name");
        if (name == null || name.isBlank()) name = "Player";
        DragDropPlay.createAndShowGUI(name);
        frame.dispose();
    }


    private void keyboardPlay() {
        String name = JOptionPane.showInputDialog("Player Name");
        if (name == null || name.isBlank()) name = "Player";
        KeyboardPlay.createAndShowGUI(name);

        frame.dispose();
    }


    private void multiplayerPlay() {
   
        String name1 = JOptionPane.showInputDialog("Enter a name for player 1");
        String name2 = JOptionPane.showInputDialog("Enter a name for player 2");
   
        if (name1 == null || name1.isBlank()) name1 = "Player 1";
        if (name2 == null || name2.isBlank()) name2 = "Player 2";

        MultiplayerPlay.createAndShowGUI(name1, name2);

        frame.dispose();
    }

 
    private void showHighScores() {
        ShowHighScores.createAndShowGui();
        frame.dispose();
    }

    protected static String countToTime(int count) { 
        String min = Integer.toString(count / 60);    
        String sec = Integer.toString(count % 60);
        if (Integer.parseInt(sec) / 10 == 0) sec = "0" + sec;
        return min + ":" + sec;
    }

    public static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WordleGui game = new WordleGui();
        game.setOpaque(true);
        frame.setContentPane(game);
        frame.setPreferredSize(new Dimension(400, 400));

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(WordleGui::createAndShowGUI);
    }
}
