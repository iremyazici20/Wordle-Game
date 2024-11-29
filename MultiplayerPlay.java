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

    // Tahminlerin olacagi paneldeki harfleri saklayan iki boyutlu array
    private final JTextField[][] trials;

    // Her oyuncu icin ayri ayri yapilan tahmin ve kalan tahmin hakki tutuluyor
    private int trialCount1 = 0;
    private int trialCount2 = 0;
    private int lives1 = 3;
    private int lives2 = 3;

    // Ekrana yazdirilacagi icin birer JLabel olusturuluyor
    private final JLabel guessLabel1 = new JLabel();
    private final JLabel guessLabel2 = new JLabel();
    private final JLabel remainingLabel1 = new JLabel();
    private final JLabel remainingLabel2 = new JLabel();

    // Kalan sureyi yazmak icin bir JLabel olusturuluyor
    private final JLabel timeLabel1 = new JLabel();
    private final JLabel timeLabel2 = new JLabel();
    // Gecen sureyi tutmak icin gerekli degisken ve Timer nesnesi ayarlaniyor
    private int timeCount1 = 0;
    private int timeCount2 = 0;
    // Timer'in delay parametresi milisaniye cinsinden 1000 olarak ayarlaniyor (1 saniye)
    private final Timer timer1 = new Timer(1000, null);
    private final Timer timer2 = new Timer(1000, null);

    // Wordle oyunu ve dogru cevap olan kelime icin degiskenler ayarlaniyor
    private final Wordle wordle;
    private final String word;

    // Oyuncularin isimleri icin degiskenler ayarlaniyor
    private final String name1;
    private final String name2;

    // Oyunun bitip bitmedigini kontrol ettikten sonra yapilacak islemleri ayirt etmek icin bir boolean ayarlaniyor
    private boolean isGameOver = false;

    // Parametre olarak oyuncu isimlerini alan constructor metodumuz
    public MultiplayerPlay(String name1, String name2) {
        super(new BorderLayout());
        this.name1 = name1;
        this.name2 = name2;

        // Wordle oyunu baslatiliyor ve rastgele bir kelime aliniyor
        wordle = new Wordle();
        word = wordle.getWord();

        // Array olusturuluyor
        trials = new JTextField[6][5];

        // 1. oyuncu icin zamanla ilgili ayarlamalar yapiliyor
        timeLabel1.setText("Time: 0:00");
        timeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        timer1.addActionListener(actionEvent -> {
            // Timer action'i delay parametresi 1 saniye olarak ayarlandigi icin her saniye calisiyor
            // Gecen zaman 1 artiriliyor ve ekrandaki yazi guncelleniyor
            timeCount1++;
            timeLabel1.setText(String.format("Time (%s): %s", name1, WordleGui.countToTime(timeCount1)));
        });

        // 2. oyuncu icin zamanla ilgili ayarlamalar yapiliyor
        timeLabel2.setText("Time: 0:00");
        timeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        timer2.addActionListener(actionEvent -> {
            // Timer action'i delay parametresi 1 saniye olarak ayarlandigi icin her saniye calisiyor
            // Gecen zaman 1 artiriliyor ve ekrandaki yazi guncelleniyor
            timeCount2++;
            timeLabel2.setText(String.format("Time (%s): %s", name2, WordleGui.countToTime(timeCount2)));
        });

        // Tahmin sayisiyla ilgili metin ayarlamalari yapiliyor
        guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, timeCount1));
        guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, timeCount2));
        guessLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        guessLabel2.setHorizontalAlignment(SwingConstants.CENTER);

        // Kalan hak sayisiyla ilgili metin ayarlamalari yapiliyor
        remainingLabel1.setText(String.format("Remaining for %s: %d", name1, lives1));
        remainingLabel2.setText(String.format("Remaining for %s: %d", name2, lives2));
        remainingLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        remainingLabel2.setHorizontalAlignment(SwingConstants.CENTER);

        // Bilgi metinlerini saklayan izgara (grid) duzenine sahip bir panel olusturuluyor
        JPanel labels = new JPanel(new GridLayout(2, 3));
        // JLabellar bu panele ekleniyor
        labels.add(guessLabel1);
        labels.add(timeLabel1);
        labels.add(remainingLabel1);
        labels.add(guessLabel2);
        labels.add(timeLabel2);
        labels.add(remainingLabel2);

        // Duzenli olabilmesi acisindan tahmin paneli 6x5'lik bir izgara (grid) duzeniyle olusturuluyor
        JPanel trialPanel = new JPanel(new GridLayout(6, 5));
        // Hucreleri baslatan metot cagiriliyor
        initCells(trialPanel);
        /*
         * 1. oyuncunun ilk tahmini yapmadan baska satirlara harf yazmasini engellemek icin
         * tahmin panelindeki satirlarin degistirilebilme ozellikleri false olarak ayarlanmisti.
         *
         * Dolayisiyla uygulamanin bu halinde tahmin paneline harf yazmak mumkun degil.
         *
         * Bu yuzden hucrenin duzenlenebilme ozelligini ayarlayan metot
         * yalnizca ilk satira (index=0) uygulayacak sekilde (true) calistiriliyor
         */
        makeEditable(0, true);

        // Boyut ayarlamalari yapiliyor
        setPreferredSize(new Dimension(500, 500));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Paneller ekrana ekleniyor
        add(labels, BorderLayout.NORTH);
        add(trialPanel, BorderLayout.CENTER);

        // Zamanlayici baslatiliyor
        timer1.start();
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
                        // Toplam tahmin sayisi 2 oyuncunun tahminlerinin toplami olarak belirleniyor
                        int trialCount = trialCount1 + trialCount2;
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
                                // Eger 1. oyuncunun sayaci isliyorsa 1. oyuncu icin tahmin kontrol metodu calistiriliyor
                                if (timer1.isRunning()) checkAnswer1();
                                // Eger 2. oyuncunun sayaci isliyorsa 2. oyuncu icin tahmin kontrol metodu calistiriliyor
                                else if (timer2.isRunning()) checkAnswer2();
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
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
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

    // 1. oyuncunun tahminini kontrol eden metot
    private void checkAnswer1() {
        // Toplam tahmin sayisi 2 oyuncunun tahminlerinin toplami olarak belirleniyor
        int trialCount = trialCount1 + trialCount2;
        // Eger satir tamamen dolu ise
        if (isRowFull(trialCount)) {
            // 1. oyuncunun zaman sayaci durduruluyor
            timer1.stop();
            // Satirdaki harfler uygun renklerle boyaniyor
            colorize(trialCount);
            // Eger cevap bulunduysa
            if (isAnswerFound(getGuess(trialCount))) {
                // Bilgi metinleri guncelleniyor
                guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, ++trialCount1));
                remainingLabel1.setText(String.format("Remaining for %s: %d", name1, --lives1));
                // Oyun bitis metodu cevap bulundugu icin true parametresiyle calistiriliyor ve 1. oyuncunun ismi aktariliyor
                gameOver(true, name1, timeCount1, trialCount1);
            } else { // Cevap bulunmadiysa
                // Toplam tahmin sayisi 5'i gecmediyse bir sonraki satir duzenlenebilir olarak ayarlaniyor
                // Bu kontrolun sebebi toplam tahmin sayisi 5'i gectigi zaman duzenlenebilecek bir satir kalmiyor
                // Bu yuzden duzenlenebilme ozelligi yalnizca tahmin sayisi 5'ten kucuk ise ekleniyor
                if (trialCount < 5) makeEditable(trialCount + 1, true);
                // Mevcut satirdaki duzenlenebilme ozelligi kaldiriliyor
                // Bu islemin sebebi de oyuncularin daha onceki tahminlerdeki harfleri degistirememesini saglamak
                makeEditable(trialCount, false);
                // Bilgi metinleri guncelleniyor
                guessLabel1.setText(String.format("Guesses Made by %s: %d", name1, ++trialCount1));
                remainingLabel1.setText(String.format("Remaining for %s: %d", name1, --lives1));
                // 2. oyuncunun zaman sayaci baslatiliyor/devam ettiriliyor
                timer2.start();
            }
        } else if (!isGameOver) { // Satir tamamen dolu degil ve oyun devam ediyor ise
            // Kullaniciya uyari gidiyor
            JOptionPane.showMessageDialog(this, "Not enough letters", "Oops!", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 2. oyuncunun tahminini kontrol eden metot
    private void checkAnswer2() {
        // Toplam tahmin sayisi 2 oyuncunun tahminlerinin toplami olarak belirleniyor
        int trialCount = trialCount1 + trialCount2;
        // Eger satir tamamen dolu ise
        if (isRowFull(trialCount)) {
            // 2. oyuncunun zaman sayaci durduruluyor
            timer2.stop();
            // Satirdaki harfler uygun renklerle boyaniyor
            colorize(trialCount);
            // Eger cevap bulunduysa
            if (isAnswerFound(getGuess(trialCount))) {
                // Bilgi metinleri guncelleniyor
                guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, ++trialCount2));
                remainingLabel2.setText(String.format("Remaining for %s: %d", name2, --lives2));
                // Oyun bitis metodu cevap bulundugu icin true parametresiyle calistiriliyor ve 2. oyuncunun ismi aktariliyor
                gameOver(true, name2, timeCount2, trialCount2);
            } else { // Cevap bulunmadiysa
                // Tahmin sayisi 5'i gecmediyse bir sonraki satir duzenlenebilir olarak ayarlaniyor
                // Bu kontrolun sebebi toplam tahmin sayisi 5'i gectigi zaman duzenlenebilecek bir satir kalmiyor
                // Bu yuzden duzenlenebilme ozelligi yalnizca tahmin sayisi 5'ten kucuk ise ekleniyor
                if (trialCount < 5) makeEditable(trialCount + 1, true);
                // Mevcut satirdaki duzenlenebilme ozelligi kaldiriliyor
                // Bu islemin sebebi de oyuncularin daha onceki tahminlerdeki harfleri degistirememesini saglamak
                makeEditable(trialCount, false);
                // Bilgi metinleri guncelleniyor
                guessLabel2.setText(String.format("Guesses Made by %s: %d", name2, ++trialCount2));
                remainingLabel2.setText(String.format("Remaining for %s: %d", name2, --lives2));
                // 2. oyuncunun kalan tahmin hakkinin 0 olmasi, 1. oyuncunun da tahmin haklarinin bittigi anlamina geliyor
                // Cevap bulunamadigi icin de oyun bitis metodu false parametresiyle calistiriliyor ve herhangi bir isim aktarilmiyor
                if (lives2 == 0) gameOver(false, "", timeCount2, trialCount2);
                // Oyun bitmediyse 2. oyuncunun zaman sayaci devam ettiriliyor
                else timer1.start();
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

    // Parametre olarak cevabin bulunup bulunmadigini ve kazanan oyuncu ismini alan oyun bitis metodu
    private void gameOver(boolean answerFound, String name, int timeCount, int trialCount) {
        // Oncelikle sureler durduruluyor
        timer1.stop();
        timer2.stop();
        // Oyun bitis degiskeni ayarlaniyor
        isGameOver = true;
        // Cevap bulunduysa uygun mesaj ayarlaniyor
        if (answerFound) {
            String message = name + " has found the answer";
            if (wordle.gameOver(GameType.MULTIPLAYER, timeCount, trialCount, word, name)) {
                message += " with a new high score!";
            } else message += "!";
            // Mesajla gozukecek icon seciliyor
            ImageIcon confetti = new ImageIcon("src/resources/confetti.gif");
            // Mesaj gosteriliyor
            JOptionPane.showMessageDialog(this, message, "Congrats!", JOptionPane.PLAIN_MESSAGE, confetti);
        } else { // Cevap bulunamadiysa uygun mesaj ayarlaniyor ve gosteriliyor
            String message = "The correct answer was \"" + word + "\"";
            JOptionPane.showMessageDialog(this, message, "Try Again", JOptionPane.PLAIN_MESSAGE);
        }
        // Oyun tipi secme ekranina donuluyor ve oyun ekrani kapatiliyor
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
