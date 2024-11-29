package gui;

import javax.swing.*;
import java.awt.*;

// Oyun tipi secimi yapmak icin GUI sinifi
public class WordleGui extends JPanel {

    private static final JFrame frame = new JFrame("Wordle");

    public WordleGui() {
        super(new GridLayout(6, 1));

        // Hos geldiniz yazisi
        JLabel welcomeLabel = new JLabel("Welcome to the Wordle Game!");
        welcomeLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        welcomeLabel.setVisible(true);

        // Mod seciniz yazisi
        JLabel selectLabel = new JLabel("Please select game mode.");
        selectLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectLabel.setVerticalAlignment(SwingConstants.TOP);
        selectLabel.setVisible(true);

        // Surukle-birak modu butonu
        JButton dragDropPlayBtn = new JButton("Drag & Drop");
        dragDropPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        dragDropPlayBtn.addActionListener(e -> dragDropPlay());
        dragDropPlayBtn.setVisible(true);

        // Klavye modu butonu
        JButton keyboardPlayBtn = new JButton("Keyboard");
        keyboardPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        keyboardPlayBtn.addActionListener(e -> keyboardPlay());
        keyboardPlayBtn.setVisible(true);

        // Multiplayer modu butonu
        JButton multiplayerPlayBtn = new JButton("Multiplayer");
        multiplayerPlayBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        multiplayerPlayBtn.addActionListener(e -> multiplayerPlay());
        multiplayerPlayBtn.setVisible(true);

        // Yuksek puanlari listeleme butonu
        JButton highScoresBtn = new JButton("Show High Scores");
        highScoresBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        highScoresBtn.addActionListener(e -> showHighScores());
        highScoresBtn.setVisible(true);

        // Tum elementler ekrana ekleniyor
        add(welcomeLabel);
        add(selectLabel);
        add(dragDropPlayBtn);
        add(keyboardPlayBtn);
        add(multiplayerPlayBtn);
        add(highScoresBtn);
    }

    // Surukle-birak modu butonu tiklandiginda calisan metot
    private void dragDropPlay() {
        // Oyuncu ismi aliniyor
        String name = JOptionPane.showInputDialog("Player Name");
        // Isim girilmedigi takdirde isim ayarlamasi otomatik yapiliyor
        if (name == null || name.isBlank()) name = "Player";
        // Surukle-birak GUI'si olusturuluyor ve gosteriliyor
        DragDropPlay.createAndShowGUI(name);
        // Mod secme GUI'si kapatiliyor
        frame.dispose();
    }

    // Klavye modu butonu tiklandiginda calisan metot
    private void keyboardPlay() {
        // Oyuncu ismi aliniyor
        String name = JOptionPane.showInputDialog("Player Name");
        // Isim girilmedigi takdirde isim ayarlamasi otomatik yapiliyor
        if (name == null || name.isBlank()) name = "Player";
        // Klavye GUI'si olusturuluyor ve gosteriliyor
        KeyboardPlay.createAndShowGUI(name);
        // Mod secme GUI'si kapatiliyor
        frame.dispose();
    }

    // Multiplayer butonu tiklandiginda calisan metot
    private void multiplayerPlay() {
        // Oyuncularin isimleri aliniyor
        String name1 = JOptionPane.showInputDialog("Enter a name for player 1");
        String name2 = JOptionPane.showInputDialog("Enter a name for player 2");
        // Isim girilmedigi takdirde isim ayarlamalari otomatik yapiliyor
        if (name1 == null || name1.isBlank()) name1 = "Player 1";
        if (name2 == null || name2.isBlank()) name2 = "Player 2";
        // Multiplayer GUI'si olusturuluyor ve gosteriliyor
        MultiplayerPlay.createAndShowGUI(name1, name2);
        // Mod secme GUI'si kapatiliyor
        frame.dispose();
    }

    // Yuksek skorlari listeleyen ekrani acan metot
    private void showHighScores() {
        // Yuksek skorlar ekrani olusturuluyor ve gosteriliyor
        ShowHighScores.createAndShowGui();
        // Mod secme GUI'si kapatiliyor
        frame.dispose();
    }

    // Parametre olarak toplam saniyeyi alan ve gecen sureyi dakika:saniye olarak yazan metot
    protected static String countToTime(int count) {
        // Toplam saniyenin 60'a bolumu aliniyor
        String min = Integer.toString(count / 60);
        // Toplam saniyenin 60 modundaki degeri aliniyor
        String sec = Integer.toString(count % 60);
        // Saniyeyi iki basamakli (00, 01...) gosterebilmek icin gerekli ayarlama yapiliyor
        if (Integer.parseInt(sec) / 10 == 0) sec = "0" + sec;
        // Gecen sure donduruluyor
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
