package wordle;

import enums.GameType;

public class HighScore implements Comparable<HighScore> {

    private final GameType gameType;
    private final String gameTypeStr;
    private final int timeCount;
    private final int trialCount;
    private final String word;
    private final String name;

    public HighScore(GameType gameType, int timeCount, int trialCount, String word, String name) {
        this.gameType = gameType;
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

    @Override
    public String toString() {
        return String.format("%s, %d, %d, %s, %s", gameTypeStr, timeCount, trialCount, word, name);
    }

    @Override
    public int compareTo(HighScore score) {
        if (gameType == score.gameType) {
            if (trialCount < score.trialCount) return 1;
            else if (trialCount > score.trialCount) return -1;
            else {
                if (timeCount < score.timeCount) return 1;
                else if (timeCount > score.timeCount) return -1;
                else return 0;
            }
        } else return -2; 
    }


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
