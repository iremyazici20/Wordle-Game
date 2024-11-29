package gui;

import wordle.HighScore;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Yuksek skorlari listelemek icin GUI sinifi
public class ShowHighScores extends JPanel {

    private static JFrame frame;

    public ShowHighScores() {
        super(new GridLayout(6, 1));
        // Wordle sinifindan yuksek skorlari dosyadan okuyan metot cagiriliyor
        ArrayList<HighScore> highScores = Wordle.readHighScores();

        // Yuksek skorun yapildigi oyun bilgileri icin labellar hazirlaniyor
        JLabel nameLabel, wordLabel, timeLabel, countLabel;
        nameLabel = new JLabel("Player Name");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordLabel = new JLabel("Word (Answer)");
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel = new JLabel("Time (secs)");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel = new JLabel("Trial Count");
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Kullanicinin, hangi oyun modunun yuksek skorunu goruntulemek istiyorsa onu secebilecegi bir secim kutusu ekleniyor
        // Secim kutusunun itemlari bir arrayde ayarlaniyor
        String[] types = {"-- Select Game Mode --", "Drag&Drop", "Keyboard", "Multiplayer"};
        JComboBox<String> gameTypeSelector = new JComboBox<>(types);
        // Secimde bir degisiklik oldugu zaman
        gameTypeSelector.addActionListener(e -> {
            // Secilen indexin 1 eksigi aliniyor
            // Bunun sebebi soyle aciklanabilir:
            // Yukarida yuksek skorlarin tutuldugu listeyi Wordle sinifindan almistik
            // O listede 3 oyun modu oldugu icin 3 eleman var
            // Bizim secim kutumuzda ise 4 eleman var ve bunlardan ilki "Mod Seciniz" uyarisi
            // Indexlerin uyusmasi icin secim kutusunda secilen indexten 1 cikariyoruz
            int i = gameTypeSelector.getSelectedIndex() - 1;
            // Secilen indexin 1 eksigi 0'dan buyuk esit ise
            if (i >= 0) {
                // Yuksek skorlar listesinde secilen indexin uygun degerleri ilgili labellara yaziliyor
                nameLabel.setText("Player: " + highScores.get(i).getName());
                wordLabel.setText("Word: " + highScores.get(i).getWord());
                timeLabel.setText(highScores.get(i).getTimeCount() + " secs");
                countLabel.setText(highScores.get(i).getTrialCount() + " trials");
            } else { // Secilen indexin 1 eksigi negatif ise
                // "-- Select Game Mode --" secenegi secilmis anlamina geliyor
                // Kafa karisikligi olmamasi icin labellar ilk hallerine donduruluyor
                nameLabel.setText("Player Name");
                wordLabel.setText("Word (Answer)");
                timeLabel.setText("Time (secs)");
                countLabel.setText("Trial Count");
            }
        });

        // Geri gitme butonu
        JButton goBackBtn = new JButton("Back");
        goBackBtn.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        goBackBtn.addActionListener(e -> {
            // Oyun modu secme GUI'si olusturuluyor ve gosteriliyor
            WordleGui.createAndShowGUI();
            // Yuksek skorlar GUI'si kapatiliyor
            frame.dispose();
        });
        goBackBtn.setVisible(true);

        // Elementler arasi bosluk icin bos cerceve ekleniyor
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
