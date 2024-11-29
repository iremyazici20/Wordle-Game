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

// Surukle-birak tipi oyun icin GUI sinifi
public class DragDropPlay extends JPanel {

    private static JFrame frame;

    // Tahminlerin olacagi paneldeki harfleri saklayan iki boyutlu array
    private final DTLetter[][] trials;
    // Klavye harflerini saklayan array
    private final DTLetter[] keyboard;
    // Veri transferini isleyen LetterTransferHandler nesnesi
    private final LetterTransferHandler letterHandler;

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

    public DragDropPlay(String name) {
        super(new BorderLayout());

        // Wordle oyunu baslatiliyor ve rastgele bir kelime aliniyor
        wordle = new Wordle();
        word = wordle.getWord();
        // Oyuncu adi ayarlaniyor
        this.name = name;

        // Klavyedeki harfler Q klavye siralamasina gore daha sonra ekrana eklenmek uzere bir arrayde tutuluyor
        Character[] keyboardLetters = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
                'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
                'Z', 'X', 'C', 'V', 'B', 'N', 'M'};

        // Arrayler ve handler baslatiliyor
        trials = new DTLetter[5][5];
        keyboard = new DTLetter[26];
        letterHandler = new LetterTransferHandler();

        // Zamanla ilgili ayarlamalar yapiliyor
        timeLabel.setText("Time: 0:00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timer.addActionListener(actionEvent -> {
            // Timer action'i delay parametresi 1 saniye olarak ayarlandigi icin her saniye calisiyor
            // Gecen zaman 1 artiriliyor ve ekrandaki yazi guncelleniyor
            timeCount++;
            timeLabel.setText("Time: " + WordleGui.countToTime(timeCount));
        });

        // Duzenli olabilmesi acisindan tahmin paneli 5x5'lik bir izgara (grid) duzeniyle olusturuluyor ve boyutlari ayarlaniyor
        JPanel trialPanel = new JPanel(new GridLayout(5, 5));
        trialPanel.setPreferredSize(new Dimension(450, 400));

        // Tahmin arrayine ici bos (null) DTLetter'lar ekleniyor ve tipi ayarlaniyor
        for (int i = 0; i < trials.length; i++) {
            for (int j = 0; j < trials[i].length; j++) {
                trials[i][j] = new DTLetter(null, LetterType.GUESS_LETTER);
                trialPanel.add(trials[i][j]);
            }
        }

        // Tahmin sayisiyla ilgili metin ayarlamalari yapiliyor
        guessLabel.setText("Guess Made: " + trialCount);
        guessLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Kalan hak sayisiyla ilgili metin ayarlamalari yapiliyor
        remainingLabel.setText("Remaining: " + lives);
        remainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Klavye oyunu olmadigi icin kullanicinin cevabi bir buton araciligiyla gondermesini sagliyoruz
        // Cevabi kontrol eden buton olusturuluyor ve tiklandiginda yapilacak islem ayarlaniyor
        JButton checkBtn = new JButton();
        checkBtn.setText("Check Answer");
        checkBtn.addActionListener(e -> checkAnswer());

        // Standart bir Q klavye duzeni icin gerekli gorunumler izgara (grid) duzeni yardimiyla olusturuluyor
        JPanel keyboardPanel = new JPanel(new GridLayout(3, 1));
        keyboardPanel.setPreferredSize(new Dimension(450, 210));
        JPanel keyboardRow1 = new JPanel(new GridLayout(1, 10));
        JPanel keyboardRow2 = new JPanel(new GridLayout(1, 9));
        JPanel keyboardRow3 = new JPanel(new GridLayout(1, 7));

        // Basta olusturulan arraydeki harfler okunuyor ve klavye panelinde ilgili satira ekleniyor
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new DTLetter(keyboardLetters[i], LetterType.KEYBOARD_LETTER);
            // Klavyenin her bir harfine veri transferi isleyicisi ataniyor
            keyboard[i].setTransferHandler(letterHandler);
            if (i < 10) keyboardRow1.add(keyboard[i]);
            else if (i < 19) keyboardRow2.add(keyboard[i]);
            else keyboardRow3.add(keyboard[i]);
        }

        // Klavye paneline klavyenin satirlari ekleniyor ve panel hazir hale geliyor
        keyboardPanel.add(keyboardRow1);
        keyboardPanel.add(keyboardRow2);
        keyboardPanel.add(keyboardRow3);

        // GUI'miz BorderLayout ile olusturulmustu
        // Ust (Kuzey), Orta (Center) ve Alt (South) bolgeleri icin uygun paneller olusturuluyor
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        JPanel southPanel = new JPanel(new BorderLayout());

        // Tum componentler ilgili bolgeye ekleniyor
        northPanel.add(timeLabel, BorderLayout.NORTH);
        northPanel.add(trialPanel, BorderLayout.SOUTH);
        centerPanel.add(guessLabel);
        centerPanel.add(checkBtn);
        centerPanel.add(remainingLabel);
        southPanel.add(keyboardPanel, BorderLayout.CENTER);

        /*
         * Kullanicinin ilk tahmini yapmadan baska satirlara harf suruklemesini engellemek icin
         * tahmin panelindeki hicbir satira LetterTransferHandler atanmamisti.
         *
         * Dolayisiyla uygulamanin bu halinde tahmin paneline harf suruklemek mumkun degil.
         *
         * Bu yuzden veri transferi isleyicisi atayan metot
         * yalnizca ilk satira (index=0) atama yapacak sekilde calistiriliyor
         */
        addHandler(0);

        // Elementler arasi bosluk olmasi acisindan bos cerceve ekleniyor
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Paneller ekrana ekleniyor
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Zamanlayici baslatiliyor
        timer.start();
    }

    // Parametre olarak verilen satirdaki harflere isleyici atayan metot
    private void addHandler(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setTransferHandler(letterHandler);
        }
    }

    // Parametre olarak verilen satirdaki harflerin isleyicilerini kaldiran metot
    private void removeHandler(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            trials[row][i].setTransferHandler(null);
        }
    }

    // Satirin tamamen dolup dolmadigini kontrol eden metot
    private boolean isRowFull(int row) {
        for (int i = 0; i < trials[row].length; i++) {
            if (trials[row][i].getChar() == null) return false;
        }
        return true;
    }

    // Parametre olarak verilen satirdaki harfleri birlestirip Stringe donusturen ve tahmini donduren metot
    private String getGuess(int row) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            answer.append(trials[row][i].getChar());
        }
        return answer.toString();
    }

    // Cevabin bulunup bulunmadigini kontrol eden metot
    private boolean isAnswerFound(String guess) {
        return guess.equalsIgnoreCase(word);
    }

    // Cevabi kontrol eden butona tiklandiginda calisan metot
    private void checkAnswer() {
        // Olasi bir hatada uygulamanin cokmemesi icin try-catch blogu icerisine aliyoruz
        try {
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
                } else { // Cevap bulunmadiysa
                    // Tahmin sayisi 4'u gecmediyse bir sonraki satira isleyici ataniyor
                    // Bu kontrolun sebebi tahmin sayisi 4'u gectigi zaman isleyicinin eklenebilecegi bir satir kalmiyor
                    // Bu yuzden isleyici yalnizca tahmin sayisi 4'ten kucuk ise ekleniyor
                    if (trialCount < 4) addHandler(trialCount + 1);
                    // Mevcut satirdaki isleyici kaldiriliyor
                    // Bu islemin sebebi de kullanicinin daha onceki tahminlerdeki harfleri degistirememesini saglamak
                    removeHandler(trialCount);
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
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    // Parametre olarak verilen satirdaki harfleri uygun renklere boyayan metot
    private void colorize(int row) {
        // Harf durumu wordle nesnesinden tahmin kontrol edilerek aliniyor
        LetterStatus[] statuses = wordle.getLetterStatuses(getGuess(row));
        for (int i = 0; i < statuses.length; i++) {
            // Tahmin harfinin durumu degistiriliyor
            trials[row][i].setStatus(statuses[i]);
            // Klavyedeki harfler icin de boyama islemi gerceklestiriliyor
            // Klavyedeki her bir harf icin
            for (DTLetter key : keyboard) {
                // Harfin tahmin harfiyle ayni olup olmadigi kontrol ediliyor
                if (key.getChar().equals(trials[row][i].getChar())) {
                    // Ayni ise tahmin panelindeki renkle ayni renge boyanmasi icin durumu degistiriliyor
                    key.setStatus(statuses[i]);
                }
            }
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
            if (wordle.gameOver(GameType.DRAG_DROP, timeCount, trialCount, word, name)) {
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
