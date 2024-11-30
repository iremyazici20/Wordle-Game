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

public class Wordle {

    private final String word;

    public Wordle() {

        ArrayList<String> words = new ArrayList<>();
        try {
        
            Scanner reader = new Scanner(new File("src/resources/words.txt"));
            while (reader.hasNext()) {
                words.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        int index = new Random().nextInt(words.size());
        word = words.get(index);
        System.out.println(word);
    }

    public String getWord() {
        return word;
    }
    public LetterStatus[] getLetterStatuses(String guess) {
 
        LetterStatus[] statuses = new LetterStatus[5];
        Arrays.fill(statuses, LetterStatus.WRONG);

        char[] wordChars = word.toUpperCase().toCharArray();
        char[] guessChars = guess.toUpperCase().toCharArray();

        for (int i = 0; i < word.length(); i++) {
            if (guessChars[i] == wordChars[i]) {
                statuses[i] = LetterStatus.CORRECT;
                wordChars[i] = ' ';
            }
        }


        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < word.length(); j++) {
                if (guessChars[i] == wordChars[j] && statuses[i] != LetterStatus.CORRECT) {
                    statuses[i] = LetterStatus.MISPLACED;
                    wordChars[j] = ' ';
                }
            }
        }

        return statuses;
    }


    public static ArrayList<HighScore> readHighScores() {
        ArrayList<HighScore> scores = new ArrayList<>();
        try {
            Scanner reader = new Scanner(new File("src/resources/high_scores.txt"));
            while (reader.hasNext()) {
                String[] score = reader.nextLine().split(", ");

                GameType type;
                switch (score[0]) {
                    case "Drag&Drop" -> type = GameType.DRAG_DROP;
                    case "Keyboard" -> type = GameType.KEYBOARD;
                    case "Multiplayer" -> type = GameType.MULTIPLAYER;
                    default -> type = GameType.UNKNOWN;
                }

                int timeCount = Integer.parseInt(score[1]);
     
                int trialCount = Integer.parseInt(score[2]);

                String answer = score[3];

                String name = score[4];
                scores.add(new HighScore(type, timeCount, trialCount, answer, name));
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        return scores;
    }


    private void writeHighScores(ArrayList<HighScore> scores) {
        try {

            FileWriter writer = new FileWriter("src/resources/high_scores.txt", false);
            StringBuilder data = new StringBuilder();
            for (HighScore score : scores) {
                data.append(score.toString());
     
                data.append("\n");
            }
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("I/O exception");
        }
    }


    public boolean gameOver(GameType type, int timeCount, int trialCount, String answer, String name) {
        HighScore newScore = new HighScore(type, timeCount, trialCount, answer, name);
        ArrayList<HighScore> scores = readHighScores();
        for (HighScore score : scores) {
            if (newScore.compareTo(score) > 0) {
                int index = scores.indexOf(score);
                scores.remove(index);
                scores.add(index, newScore);
                writeHighScores(scores);
                return true;
            }
        }
        return false;
    }
}
