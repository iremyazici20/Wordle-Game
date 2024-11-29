package wordle;

import enums.GameType;

// Kendi nesnesiyle karsilastirilabilen yuksek skor sinifi
public class HighScore implements Comparable<HighScore> {

    // Oyunun sonucunun ozellikleri saklaniyor
    private final GameType gameType;
    private final String gameTypeStr;
    private final int timeCount;
    private final int trialCount;
    private final String word;
    private final String name;

    public HighScore(GameType gameType, int timeCount, int trialCount, String word, String name) {
        this.gameType = gameType;
        // GameType ozelligine gore uygun string atamasi yapiliyor
        switch (gameType) {
            case KEYBOARD -> gameTypeStr = "Keyboard";
            case DRAG_DROP -> gameTypeStr = "Drag&Drop";
            case MULTIPLAYER -> gameTypeStr = "Multiplayer";
            default -> gameTypeStr = "GameType";
        }
        this.timeCount = timeCount;
        this.trialCount = trialCount;
        this.word = word;
        this.name = name;
    }

    // Dosyaya kaydetmek icin oyun sonucu detaylari uygun formatta yazdiriliyor
    @Override
    public String toString() {
        return String.format("%s, %d, %d, %s, %s", gameTypeStr, timeCount, trialCount, word, name);
    }

    // 2 yuksek skoru karsilastirabilmek icin calistirilan metot
    @Override
    public int compareTo(HighScore score) {
        // Oyun tipleri ayni ise
        if (gameType == score.gameType) {
            // Deneme sayisi var olandan daha az ise yeni skor daha iyi (1)
            if (trialCount < score.trialCount) return 1;
            // Deneme sayisi var olandan daha cok ise var olan skor daha iyi (-1)
            else if (trialCount > score.trialCount) return -1;
            // Deneme sayilari esitse sureye bakilacak
            else {
                // Gecen sure var olandan daha kisa ise yeni skor daha iyi (1)
                if (timeCount < score.timeCount) return 1;
                // Gecen sure var olandan daha uzun ise var olan skor daha iyi (-1)
                else if (timeCount > score.timeCount) return -1;
                // Gecen sure esitse skorlar esit (0)
                else return 0;
            }
        } else return -2; // Oyun tipleri ayni degil ise skorlar karsilastirilamaz (-2)
    }

    // ShowHighScores ekraninda listeleyebilmek icin gereken standart Get metotlari

    public int getTimeCount() {
        return timeCount;
    }

    public int getTrialCount() {
        return trialCount;
    }

    public String getWord() {
        return word;
    }

    public String getName() {
        return name;
    }
}
