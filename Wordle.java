package wordle;

import enums.GameType;
import enums.LetterStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

// Wordle oyununun sinifi
public class Wordle {

    private final String word;

    public Wordle() {
        // Kelimeler icin bir liste olusturuluyor
        ArrayList<String> words = new ArrayList<>();
        try {
            // Dosya okunuyor
            Scanner reader = new Scanner(new File("src/resources/words.txt"));
            while (reader.hasNext()) {
                // Dosyadaki her bir kelime listeye ekleniyor
                words.add(reader.nextLine());
            }
            // Hata almamak icin dosya kapatiliyor
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        // Rastgele bir sayi index olarak belirleniyor
        int index = new Random().nextInt(words.size());
        // Belirlenen indexte bulunan kelime cevap olarak ayarlaniyor
        word = words.get(index);
        System.out.println(word);
    }

    // Cevap kelimesini geri donduren Get metodu
    public String getWord() {
        return word;
    }

    // Parametre olarak oyuncunun tahminini alan ve harflerin durumunu donduren metot
    public LetterStatus[] getLetterStatuses(String guess) {
        // 5 harfli kelime oldugu icin 5 durumluk bir array olusturuluyor
        LetterStatus[] statuses = new LetterStatus[5];
        // Diger durumlar kontrol edilecegi icin array tamamiyle Yanlis durumuyla dolduruluyor
        Arrays.fill(statuses, LetterStatus.WRONG);

        // Cevap ve tahmin kelimelerinin karakterleri tek tek arraye donusturuluyor
        char[] wordChars = word.toUpperCase().toCharArray();
        char[] guessChars = guess.toUpperCase().toCharArray();

        // Her bir harf icin
        for (int i = 0; i < word.length(); i++) {
            // Tahmin harfinin yeri cevap harfinin yeriyle ayni ise (i=i)
            if (guessChars[i] == wordChars[i]) {
                // Durum Dogru olarak ayarlaniyor
                statuses[i] = LetterStatus.CORRECT;
                // Tahmin harfi karisikliga yol acmamasi icin siliniyor
                wordChars[i] = ' ';
            }
        }

        // Her bir ikili harf kombinasyonu (i, j) icin
        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < word.length(); j++) {
                // Tahmin harfi ile cevap harfi ayni ise ve harfin durumu Dogru degil ise
                // (Harfin durumunun dogru olmamasi yerlerinin farkli oldugunu gosterir)
                if (guessChars[i] == wordChars[j] && statuses[i] != LetterStatus.CORRECT) {
                    // Durum Dogru ama Yanlis Yerde olarak ayarlaniyor
                    statuses[i] = LetterStatus.MISPLACED;
                    // Tahmin harfi karisikliga yol acmamasi icin siliniyor
                    wordChars[j] = ' ';
                }
            }
        }
        // Durum arrayi geri donuyor
        // Yukaridaki kontrolleri saglamayan harflerin en basta tum arrayi Yanlis durumuyla doldurdugumuz icin
        // yeniden kontrol edilmeleri gerekmiyor
        return statuses;
    }

    // Yuksek skorlari okuyan metot
    // ShowHighScores ekrani uzerinden erisim saglanabilmesi icin static olarak ayarlandi
    public static ArrayList<HighScore> readHighScores() {
        // Yuksek skorlar icin bir liste olusturuluyor
        ArrayList<HighScore> scores = new ArrayList<>();
        try {
            // Dosya okunuyor
            Scanner reader = new Scanner(new File("src/resources/high_scores.txt"));
            while (reader.hasNext()) {
                // Satirdaki elemanlar parametrelere ayriliyor
                String[] score = reader.nextLine().split(", ");
                // Oyun tipi aliniyor
                GameType type;
                switch (score[0]) {
                    case "Drag&Drop" -> type = GameType.DRAG_DROP;
                    case "Keyboard" -> type = GameType.KEYBOARD;
                    case "Multiplayer" -> type = GameType.MULTIPLAYER;
                    default -> type = GameType.UNKNOWN;
                }
                // Sure aliniyor
                int timeCount = Integer.parseInt(score[1]);
                // Tahmin sayisi aliniyor
                int trialCount = Integer.parseInt(score[2]);
                // Cevap aliniyor
                String answer = score[3];
                // Oyuncu adi aliniyor
                String name = score[4];
                // Yuksek skorlar listesine yeni HighScore nesnesi olarak ekleniyor
                scores.add(new HighScore(type, timeCount, trialCount, answer, name));
            }
            // Hata almamak icin dosya kapatiliyor
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        // Liste geri donduruluyor
        return scores;
    }

    // Yuksek skorlari yazan metot
    private void writeHighScores(ArrayList<HighScore> scores) {
        try {
            // Dosya yazicisi olusturuluyor
            FileWriter writer = new FileWriter("src/resources/high_scores.txt", false);
            // String olusturucusu ayarlaniyor
            StringBuilder data = new StringBuilder();
            // Her bir yuksek skor icin
            for (HighScore score : scores) {
                // Yuksek skorun detaylari yazdiriliyor
                data.append(score.toString());
                // Yeni satira geciliyor
                data.append("\n");
            }
            // Veri dosyaya yazdiriliyor
            writer.write(data.toString());
            // Hata almamak icin dosya kapatiliyor
            writer.close();
        } catch (IOException e) {
            System.out.println("I/O exception");
        }
    }

    // Parametre olarak oyun sonucunu alan ve yeni yuksek skor olup olmadigini donduren metot
    public boolean gameOver(GameType type, int timeCount, int trialCount, String answer, String name) {
        // Oyun sonucuyla bir yuksek skor nesnesi olusturuluyor
        HighScore newScore = new HighScore(type, timeCount, trialCount, answer, name);
        // Guncel yuksek skorlar okunuyor
        ArrayList<HighScore> scores = readHighScores();
        // Her bir yuksek skor icin
        for (HighScore score : scores) {
            // Yeni sonuc var olan yuksek skor ile karsilastiriliyor
            // Eger yeni sonuc var olandan daha iyi ise
            if (newScore.compareTo(score) > 0) {
                // Var olanin indexi aliniyor
                int index = scores.indexOf(score);
                // Var olan skor siliniyor
                scores.remove(index);
                // Yerine yeni skor ekleniyor
                scores.add(index, newScore);
                // Dosyaya yaziliyor
                writeHighScores(scores);
                // Yeni yuksek skor oldugu icin true donuyor
                return true;
            }
        }
        // Kontroller basarisiz olursa yeni yuksek skor olmadigi icin false donuyor
        return false;
    }
}
