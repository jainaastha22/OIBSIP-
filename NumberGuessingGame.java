import java.util.Random;
import javax.swing.JOptionPane;

public class NumberGuessingGame {

    public static void main(String[] args) {
        playGame();
    }

    private static void playGame() {
        int minRange = 1;
        int maxRange = 100;

        int difficultyLevel = chooseDifficulty();
        adjustDifficulty(difficultyLevel, minRange, maxRange);

        int maxAttempts = 5;
        int userScore = 0;

        Random random = new Random();
        int correctNumber = random.nextInt(maxRange - minRange + 1) + minRange;

        String rules = "Welcome to the Enhanced Number Guessing Game!\n\n" +
                "Rules:\n" +
                "1. The system generates a random number within the chosen difficulty level range.\n" +
                "2. You have a limited number of attempts to guess the correct number.\n" +
                "3. After each guess, you will be informed if your guess is higher or lower than the correct number.\n" +
                "4. Your score is based on the number of attempts it takes to guess the correct number.\n" +
                "5. Good luck, and have fun!\n";
        JOptionPane.showMessageDialog(null, rules);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String userInput = JOptionPane.showInputDialog("Round " + attempt + ": Guess the number between " + minRange + " and " + maxRange + ":");

            if (userInput == null) {
                JOptionPane.showMessageDialog(null, "Game aborted. The correct number was: " + correctNumber);
                System.exit(0);
            }

            int userGuess;
            try {
                userGuess = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                attempt--;
                continue;
            }

            if (userGuess == correctNumber) {
                JOptionPane.showMessageDialog(null, "Congratulations!!!! You've guessed the right number: " + userGuess + " in " + attempt + " attempts.");
                userScore += calculateScore(maxAttempts, attempt);
                break;
            } else if (userGuess < correctNumber) {
                JOptionPane.showMessageDialog(null, "Your guess is lower than the correct number. Try again. " + (maxAttempts - attempt) + " Attempts Left.");
            } else {
                JOptionPane.showMessageDialog(null, "Your guess is higher than the correct number. Try again." + (maxAttempts - attempt) + " Attempts Left.");
            }

            if (attempt == maxAttempts) {
                JOptionPane.showMessageDialog(null, "Sorry, you've run out of attempts. The correct number was: " + correctNumber);
            }
        }

        JOptionPane.showMessageDialog(null, "Your final score is: " + userScore);

        int playAgain = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (playAgain == JOptionPane.YES_OPTION) {
            playGame();
        } else {
            JOptionPane.showMessageDialog(null, "Thank you for playing! Goodbye.");
        }
    }

    private static int chooseDifficulty() {
        String[] options = {"Easy (1-50)", "Medium (1-100)", "Hard (1-200)"};
        int choice = JOptionPane.showOptionDialog(null, "Choose a difficulty level:", "Difficulty Level", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return choice;
    }

    private static void adjustDifficulty(int difficultyLevel, int minRange, int maxRange) {
        switch (difficultyLevel) {
            case 0:
                maxRange = 50;
                break;
            case 1:
                maxRange = 100;
                break;
            case 2:
                maxRange = 200;
                break;
            default:
                break;
        }
        JOptionPane.showMessageDialog(null, "Difficulty set: " + (minRange) + " to " + maxRange);
    }

    private static int calculateScore(int maxAttempts, int attempts) {
        int baseScore = 100;
        int deduction = (maxAttempts - attempts) * 10;
        return Math.max(0, baseScore - deduction);
    }
}
