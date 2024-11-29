package gui;

import enums.GameType;
import enums.LetterStatus;
import wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Klavye tipi oyun icin GUI sinifi
public class KeyboardPlay extends JPanel {

    private static JFrame frame;

    // Tahminlerin olacagi paneldeki harfleri saklayan iki boyutlu array
    private final JTextField[][] trials;

    // Yapilan tahmin ve kalan tahmin hakki tutuluyor
    private int trialCount = 0;
    private int lives = 5;
    // Ekrana yazdirilacagi icin birer JLabel olusturuluyor
    private final JLabel guessLabel = new JLabel();
    private final JLabel remainingLabel = new JLabel();

    // Kalan sureyi yazmak icin bir JLabel olusturuluyor
    private final JLabel timeLabel = new JLabel();
    // Gecen sureyi tutmak icin gerekli degisken ve Timer nesnesi ayarlaniyor
    private int timeCount = 0;
    // Timer'in delay parametresi milisaniye cinsinden 1000 olarak ayarlaniyor (1 saniye)
    private final Timer timer = new Timer(1000, null);

    // Wordle oyunu ve dogru cevap olan kelime icin degiskenler ayarlaniyor
    private final Wordle wordle;
    private final String word;
    // Oyuncu adi degiskeni hazirlaniyor
    private final String name;

    // Oyunun bitip bitmedigini kontrol ettikten sonra yapilacak islemleri ayirt etmek icin bir boolean ayarlaniyor
    private boolean isGameOver = false;

    public KeyboardPlay(String name) {
        super(new BorderLayout());

        // Wordle oyunu baslatiliyor ve rastgele bir kelime aliniyor
        wordle = new Wordle();
        word = wordle.getWord();
        // Oyuncu adi ayarlaniyor
        this.name = name;

        // Array olusturuluyor
        trials = new JTextField[5][5];

        // Zamanla ilgili ayarlamalar yapiliyor
        timeLabel.setText("Time: 0:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timer.addActionListener(actionEvent -> {
            // Timer action'i delay parametresi 1 saniye olarak ayarlandigi icin her saniye calisiyor
            // Gecen zaman 1 artiriliyor ve ekrandaki yazi guncelleniyor
            timeCount++;
            timeLabel.setText("Time: " + WordleGui.countToTime(timeCount));
        });

        // Tahmin sayisiyla ilgili metin ayarlamalari yapiliyor
        guessLabel.setText("Guesses Made: " + trialCount);
        guessLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Kalan hak sayisiyla ilgili metin ayarlamalari yapiliyor
        remainingLabel.setText("Remaining: " + lives);
        remainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Bilgi metinlerini saklayan izgara (grid) duzenine sahip bir panel olusturuluyor
        JPanel labels = new JPanel(new GridLayout(1, 3));
        // JLabellar bu panele ekleniyor
        labels.add(guessLabel);
        labels.add(timeLabel);
        labels.add(remainingLabel);

        // Duzenli olabilmesi acisindan tahmin paneli 5x5'lik bir izgara (grid) duzeniyle olusturuluyor
        JPanel trialPanel = new JPanel(new GridLayout(5, 5));
        // Hucreleri baslatan metot cagiriliyor
        initCells(trialPanel);
        /*
         * Kullanicinin ilk tahmini yapmadan baska satirlara harf yazmasini engellemek icin
         * tahmin panelindeki satirlarin degistirilebilme ozellikleri false olarak ayarlanmisti.
         *
         * Dolayisiyla uygulamanin bu halinde tahmin paneline harf yazmak mumkun degil.
         *
         * Bu yuzden hucrenin duzenlenebilme ozelligini ayarlayan metot
         * yalnizca ilk satira (index=0) uygulayacak sekilde (true) calistiriliyor
         */
        makeEditable(0, true);

        // Elementler arasi bosluk olmasi acisindan bos cerceve ekleniyor
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Paneller ekrana ekleniyor
        add(labels, BorderLayout.NORTH);
        add(trialPanel, BorderLayout.CENTER);

        // Zamanlayici baslatiliyor
        timer.start();
    }

    // Parametre olarak verilen tahmin panelindeki hucreleri olusturup baslatan metot
    private void initCells(JPanel trialPanel) {
        for (int i = 0; i < trials.length; i++) {
            for (int j = 0; j < trials[i].length; j++) {
                // Hucre olusturuluyor ve ilgili ozellikler ayarlaniyor
                JTextField cell = new JTextField();
                cell.setSize(new Dimension(20, 20));
                cell.setFont(new Font("Segue UI Black", Font.BOLD, 40));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                // Tahminlerin sirayla yapilabilmesi icin ilk asamada hucreler duzenlenemez duruma getiriliyor
                cell.setEditable(false);
                // Disaridan kopyala-yapistir yapilamamasi icin transfer isleyici devre disi birakiliyor
                cell.setTransferHandler(null);

                // Satir ve sutun degiskenleri hucrenin klavye dinleyicisinde kullanilacagi icin ayrica saklaniyor
                int row = i;
                int column = j;
                // Hucredeyken klavyede bir tusa basildiginda
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        // Basilan tusa ait karakter aliniyor
                        char c = e.getKeyChar();
                        try {
                            // Odak yoneticisi aliniyor
                            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                            // Basilan tus bir harf veya enter degilse
                            if (!Character.isAlphabetic(c) && c != KeyEvent.VK_ENTER) {
                                // Etkinlik iptal ediliyor
                                // Bu sayede hucreye noktalama isareti, sayi vs. yazilmasi onleniyor
                                e.consume();
                            } else if (c == KeyEvent.VK_ENTER) { // Basilan tus enter ise
                                // Satir doluysa odak bir sonraki satira aktariliyor
                                // Verilen parametre: mevcut satirdaki son sutun
                                // FocusNextComponent oldugu icin bir sonraki satirdaki ilk hucreye odak veriliyor
                                if (isRowFull(trialCount)) manager.focusNextComponent(trials[row][4]);
                                // Cevap kontrol ediliyor
                                checkAnswer();
                            } else if (Character.isAlphabetic(c)) { // Basilan tus harf ise
                                // Mevcut sutun son sutun ise
                                if (column == 4) {
                                    // Bos ve duzenlenebilir hucreye yalnizca harf yaziliyor
                                    if (cell.isEditable() && cell.getText().isBlank()) cell.setText(String.valueOf(c).toUpperCase());
                                    // Harfi 2 kez yazmamak icin etkinlik iptal ediliyor
                                    e.consume();
                                } else if (cell.isEditable()) { // Mevcut sutun son degil ama duzenlenebilir ise
                                    // Hucreye harf yaziliyor
                                    cell.setText(String.valueOf(c).toUpperCase());
                                    // Harfi 2 kez yazmamak icin etkinlik iptal ediliyor
                                    e.consume();
                                    // Ve odak bir sonraki hucreye veriliyor
                                    manager.focusNextComponent();
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                    }
                });
                // Hucre tahmin harflerinin arrayine ve tahmin paneline ekleniyor
                trials[i][j] = cell;
                trialPanel.add(trials[i][j]);
            }
        }
    }

    // Parametre olarak alinan satiri diger parametreye gore duzenlenebilir/duzenlenemez seklinde ayarlayan metot
    private void makeEditable(int row, boolean bool) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setEditable(bool);
        }
    }

    // Satirin tamamen dolup dolmadigini kontrol eden metot
    private boolean isRowFull(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            if (trials[row][i].getText().isBlank()) return false;
        }
        return true;
    }

    // Parametre olarak verilen satirdaki harfleri birlestirip Stringe donusturen ve tahmini donduren metot
    private String getGuess(int row) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            answer.append(trials[row][i].getText());
        }
        return answer.toString();
    }

    // Cevabin bulunup bulunmadigini kontrol eden metot
    private boolean isAnswerFound(String guess) {
        return guess.equalsIgnoreCase(word);
    }

    // Enter tusuna tiklandiginda calisan metot
    private void checkAnswer() {
        // Eger satir tamamen dolu ise
        if (isRowFull(trialCount)) {
            // Satirdaki harfler uygun renklerle boyaniyor
            colorize(trialCount);
            // Eger cevap bulunduysa
            if (isAnswerFound(getGuess(trialCount))) {
                // Bilgi metinleri guncelleniyor
                guessLabel.setText("Guesses Made: " + ++trialCount);
                remainingLabel.setText("Remaining: " + --lives);
                // Oyun bitis metodu cevap bulundugu icin true parametresiyle calistiriliyor
                gameOver(true);
            } else {// Cevap bulunmadiysa
                // Tahmin sayisi 4'u gecmediyse bir sonraki satir duzenlenebilir olarak ayarlaniyor
                // Bu kontrolun sebebi tahmin sayisi 4'u gectigi zaman duzenlenebilecek bir satir kalmiyor
                // Bu yuzden duzenlenebilme ozelligi yalnizca tahmin sayisi 4'ten kucuk ise ekleniyor
                if (trialCount < 4) makeEditable(trialCount + 1, true);
                // Mevcut satirdaki duzenlenebilme ozelligi kaldiriliyor
                // Bu islemin sebebi de kullanicinin daha onceki tahminlerdeki harfleri degistirememesini saglamak
                makeEditable(trialCount, false);
                // Bilgi metinleri guncelleniyor
                guessLabel.setText("Guesses Made: " + ++trialCount);
                remainingLabel.setText("Remaining: " + --lives);
                // Kalan tahmin hakki 0 ise cevap bulunamadigi icin oyun bitis metodu false parametresiyle calistiriliyor
                if (lives == 0) gameOver(false);
            }
        } else if (!isGameOver) { // Satir tamamen dolu degil ve oyun devam ediyor ise
            // Kullaniciya uyari gidiyor
            JOptionPane.showMessageDialog(this, "Not enough letters", "Oops!", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Parametre olarak verilen satirdaki harfleri uygun renklere boyayan metot
    private void colorize(int row) {
        // Harf durumu wordle nesnesinden tahmin kontrol edilerek aliniyor
        LetterStatus[] statuses = wordle.getLetterStatuses(getGuess(row));
        for (int i = 0; i < statuses.length; i++) {
            // Her bir harfin durumuna gore rengi degistiriliyor
            if (statuses[i] == LetterStatus.CORRECT) trials[row][i].setBackground(Color.GREEN);
            else if (statuses[i] == LetterStatus.MISPLACED) trials[row][i].setBackground(Color.YELLOW);
            else if (statuses[i] == LetterStatus.WRONG) trials[row][i].setBackground(Color.GRAY);
            else trials[row][i].setBackground(Color.WHITE);
        }
    }

    // Parametre olarak cevabin bulunup bulunmadigini alan oyun bitis metodu
    private void gameOver(boolean answerFound) {
        // Oncelikle sure durduruluyor
        timer.stop();
        // Oyun bitis degiskeni ayarlaniyor
        isGameOver = true;
        // Cevap bulunduysa uygun mesaj ayarlaniyor
        if (answerFound) {
            String title = "Congrats, " + name + "!";
            String message = "You have found the answer";
            // Eger yeni yuksek skor yapilmis ise mesaj guncelleniyor
            if (wordle.gameOver(GameType.KEYBOARD, timeCount, trialCount, word, name)) {
                message += " with a new high score!";
            } else message += "!";
            // Mesajla gozukecek icon seciliyor
            ImageIcon confetti = new ImageIcon("src/resources/confetti.gif");
            // Mesaj gosteriliyor
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.PLAIN_MESSAGE, confetti);
        } else { // Cevap bulunamadiysa uygun mesaj ayarlaniyor ve gosteriliyor
            String message = "The correct answer was \"" + word + "\"";
            JOptionPane.showMessageDialog(this, message, "Try Again", JOptionPane.PLAIN_MESSAGE);
        }
        // Oyun tipi secme ekranina donuluyor ve oyun ekrani kapatiliyor
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
