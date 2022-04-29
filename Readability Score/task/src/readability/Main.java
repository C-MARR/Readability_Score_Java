package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        try (Scanner scanner = new Scanner(file)) {
            String essay = scanner.nextLine();
            int characterTotal = getCharacterTotal(essay);
            int wordTotal = getWordTotal(essay);
            int sentenceTotal = getSentenceTotal(essay);
            int syllableTotal = getSyllableTotal(essay);
            int polysyllableTotal = getPolysyllableTotal(essay);
            double automatedScore = calculateAutomatedScore(characterTotal, wordTotal, sentenceTotal);
            double fleschKincaidScore = calculateFleschKincaidScore(wordTotal, sentenceTotal, syllableTotal);
            double gobbledygookScore = calculateGobbledygookScore(sentenceTotal, polysyllableTotal);
            double colemanLiauScore = calculateColemanLiauScore(characterTotal, wordTotal, sentenceTotal);
            printResult(essay, characterTotal, wordTotal, sentenceTotal, syllableTotal, polysyllableTotal,
                    automatedScore, fleschKincaidScore, gobbledygookScore, colemanLiauScore);
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + args[0]);
        }

    }

    protected static int getCharacterTotal(String essay) {
        int total = 0;
        var ignoreRegex = "\\S";
        for (int i = 0; i < essay.length(); i++) {
            if (essay.substring(i,i + 1).matches(ignoreRegex)) {
                total++;
            }
        }
        return total;
    }

    protected static int getWordTotal(String essay) {
        return essay.split(" ").length;
    }

    protected static int getSentenceTotal(String essay) {
        return essay.split("[.!?]").length;
    }

    protected static int getSyllableTotal(String essay) {
        String[] words = essay.split(" ");
        boolean lastCharVowel = false;
        int total = 0;
        var regex = "[aeiouy]";
        for (String word : words) {
            if (word.matches(".*[?.!,]$")) {
                word = word.substring(0, word.length() - 1);
            }
            int eligibleVowels = 0;
            for (int i = 0; i < word.length(); i++) {
                boolean matches = word.substring(i, i + 1).toLowerCase().matches(regex);
                if (lastCharVowel) {
                    if (!matches) lastCharVowel = false;
                } else if (i == word.length() - 1 && word.substring(i).toLowerCase().matches("[aiouy]")) {
                    eligibleVowels++;
                } else if (i != word.length() - 1 && matches) {
                    eligibleVowels++;
                    lastCharVowel = true;
                }
            }
            if (eligibleVowels > 1) {
                total += eligibleVowels;
            } else {
                total++;
            }
            lastCharVowel = false;
        }
        return total;
    }

    protected static int getPolysyllableTotal(String essay) {
        String[] words = essay.split(" ");
        boolean lastCharVowel = false;
        int total = 0;
        var regex = "[aeiouy]";
        for (String word : words) {
            if (word.matches(".*[?.!,]$")) {
                word = word.substring(0, word.length() - 1);
            }
            int eligibleVowels = 0;
            for (int i = 0; i < word.length(); i++) {
                boolean matches = word.substring(i, i + 1).toLowerCase().matches(regex);
                if (lastCharVowel) {
                    if (!matches) lastCharVowel = false;
                } else if (i == word.length() - 1 && word.substring(i).toLowerCase().matches("[aiouy]")) {
                    eligibleVowels++;
                } else if (i != word.length() - 1 && matches) {
                    eligibleVowels++;
                    lastCharVowel = true;
                }
            }
            if (eligibleVowels > 2) {
                total ++;
            }
            lastCharVowel = false;
        }
        return total;
    }

    protected static double calculateAutomatedScore(int characters, int words, int sentences) {
        final double A = 4.71;
        final double B = 0.5;
        final double C = 21.43;
        return A * (double) characters / (double) words + B *
                (double) words / (double) sentences - C;
    }

    protected static double calculateFleschKincaidScore(int words, int sentences, int syllables) {
        final double A = 0.39;
        final double B = 11.8;
        final double C = 15.59;
        return A * (double) words / (double) sentences + B *
                (double) syllables / (double) words - C;
    }

    protected static double calculateGobbledygookScore(int sentences, int polysyllables) {
        final double A = 1.043;
        final double B = 30;
        final double C = 3.1291;
        return A * Math.sqrt(polysyllables * (B / sentences)) + C;
    }

    protected static double calculateColemanLiauScore(int characters, int words, int sentences) {
        final double A = 0.0588;
        final double B = 0.296;
        final double C = 15.8;
        final double L = (double) characters / (double) words * 100;
        final double S = (double) sentences / (double) words * 100;
        return A * L - B * S - C;
    }
    protected static int getAge(double score) {
        for (ReadabilityIndex group : ReadabilityIndex.values()) {
                if (group.score == Math.round(score)) {
                    return group.ageGroup[group.ageGroup.length - 1];
                }
        }
        return 5;
    }


    protected static void printResult(String essay, int characters, int words, int sentences, int syllables,
                                      int polysyllables, double automatedScore, double fleishKincaidScore,
                                      double gobbledygookScore, double colemanLiauScore) {

        Scanner scanner = new Scanner(System.in);
        String automatedPrint = "\nAutomated Readability Index: " + String.format("%.2f", automatedScore) +
                String.format(" (about %d-year-olds).", getAge(automatedScore));
        String fleishKincaidPrint = "\nFlesch–Kincaid readability tests: " + String.format("%.2f", fleishKincaidScore) +
                String.format(" (about %d-year-olds).", getAge(fleishKincaidScore));
        String gobbledygookPrint = "\nSimple Measure of Gobbledygook: " + String.format("%.2f", gobbledygookScore) +
                String.format(" (about %d-year-olds).", getAge(gobbledygookScore));
        String colemanLiauPrint = "\nColeman–Liau index: " + String.format("%.2f", colemanLiauScore) +
                String.format(" (about %d-year-olds).", getAge(colemanLiauScore));

        System.out.print("The text is:\n" +
                        essay +
                        "\n\nWords: " + words +
                        "\nSentences: " + sentences +
                        "\nCharacters: " + characters +
                        "\nSyllables: " + syllables +
                        "\nPolysyllables: " + polysyllables +
                        "\nEnter the automatedScore you want to calculate (ARI, FK, SMOG, CL, all): "
        );
        String input = scanner.nextLine();
        if (input.matches("ARI|ari")) {
            System.out.print(automatedPrint);
        } else if (input.matches("FK|fk")) {
            System.out.print(fleishKincaidPrint);
        } else if (input.matches("SMOG|smog")) {
            System.out.print(gobbledygookPrint);
        } else if (input.matches("CL|cl")) {
            System.out.print(colemanLiauPrint);
        } else {
            System.out.print(automatedPrint);
            System.out.print(fleishKincaidPrint);
            System.out.print(gobbledygookPrint);
            System.out.print(colemanLiauPrint);
        }
        System.out.printf("\n\nThis text should be understood in average by %s-year-olds.",
                String.format("%.2f", (getAge(automatedScore) + getAge(fleishKincaidScore) +
                        getAge(gobbledygookScore) + getAge(colemanLiauScore)) / 4.0));
    }

}

enum ReadabilityIndex {
    ONE(1, new int[] {5, 6}),
    TWO(2, new int[] {6, 7}),
    THREE(3, new int[] {7, 8, 9}),
    FOUR(4, new int[] {9, 10}),
    FIVE(5, new int[] {10, 11}),
    SIX(6, new int[] {11, 12}),
    SEVEN(7, new int[] {12, 13}),
    EIGHT(8, new int[] {13, 14}),
    NINE(9, new int[] {14, 15}),
    TEN(10, new int[] {15, 16}),
    ELEVEN(11, new int[] {16, 17}),
    TWELVE(12, new int[] {17, 18}),
    THIRTEEN(13, new int[] {18, 19, 20, 21, 22, 23, 24}),
    FOURTEEN(14, new int[] {24, 25, 26});

    final int score;
    final int[] ageGroup;

    ReadabilityIndex(int score, int[] ageGroup) {
        this.score = score;
        this.ageGroup = ageGroup;
    }


    @Override
    public String toString() {
        int age = ageGroup[ageGroup.length - 1];
        return String.format(" (about %d-year-olds).", age);
    }

}