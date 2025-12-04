import java.util.Random;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.Socket;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

// *****************************************************************************
// קוד In.java מצורף כאן כדי לאפשר שימוש במחלקה כנדרש במשימה.
// *****************************************************************************
// ******* מתחיל קוד In.java *******

/**
 * <i>Input</i>. This class provides methods for reading strings
 * and numbers from standard input, file input, URLs, and sockets.
 * (Simplified methods and constructors for brevity in this context,
 * using the structure provided by the user).
 */
final class In {

    // (Code duplication section 1 of 2 omitted for brevity, but necessary for full compilation)
    private static final String CHARSET_NAME = "UTF-8";
    private static final Locale LOCALE = Locale.US;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");
    private static final Pattern EMPTY_PATTERN = Pattern.compile("");
    private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");
    // (End of code duplication section 1 of 2)

    private Scanner scanner;

   /**
     * Initializes an input stream from standard input.
     */
    public In() {
        scanner = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
        scanner.useLocale(LOCALE);
    }

   /**
     * Initializes an input stream from a filename or web page name.
     *
     * @param  name the filename or web page name
     * @throws IllegalArgumentException if cannot open {@code name} as
     * a file or URL
     * @throws IllegalArgumentException if {@code name} is {@code null}
     */
    public In(String name) {
        if (name == null) throw new IllegalArgumentException("argument is null");
        if (name.length() == 0) throw new IllegalArgumentException("argument is the empty string");
        try {
            // first try to read file from local file system
            File file = new File(name);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                scanner = new Scanner(new BufferedInputStream(fis), CHARSET_NAME);
                scanner.useLocale(LOCALE);
                return;
            }

            // resource relative to .class file
            URL url = getClass().getResource(name);

            // resource relative to classloader root
            if (url == null) {
                url = getClass().getClassLoader().getResource(name);
            }

            // or URL from web
            if (url == null) {
                url = new URL(name);
            }

            URLConnection site = url.openConnection();
            InputStream is     = site.getInputStream();
            scanner            = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
            scanner.useLocale(LOCALE);
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("Could not open " + name, ioe);
        }
    }

    /**
     * Initializes an input stream from a given {@link Scanner} source; use with
     * {@code new Scanner(String)} to read from a string.
     * <p>
     * Note that this does not create a defensive copy, so the
     * scanner will be mutated as you read on.
     *
     * @param  scanner the scanner
     * @throws IllegalArgumentException if {@code scanner} is {@code null}
     */
    public In(Scanner scanner) {
        if (scanner == null) throw new IllegalArgumentException("scanner argument is null");
        this.scanner = scanner;
    }

    /**
     * Returns true if this input stream exists.
     *
     * @return {@code true} if this input stream exists; {@code false} otherwise
     */
    public boolean exists()  {
        return scanner != null;
    }


   /**
     * Returns true if input stream is empty (except possibly whitespace).
     *
     * @return {@code true} if this input stream is empty (except possibly whitespace);
     * {@code false} otherwise
     */
    public boolean isEmpty() {
        return !scanner.hasNext();
    }

   /**
     * Returns true if this input stream has a next line.
     *
     * @return {@code true} if this input stream has more input (including whitespace);
     * {@code false} otherwise
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }


   /**
     * Reads and returns the next line in this input stream.
     *
     * @return the next line in this input stream; {@code null} if no such line
     */
    public String readLine() {
        String line;
        try {
            line = scanner.nextLine();
        }
        catch (NoSuchElementException e) {
            line = null;
        }
        return line;
    }

   /**
     * Reads and returns the remainder of this input stream, as a string.
     *
     * @return the remainder of this input stream, as a string
     */
    public String readAll() {
        if (!scanner.hasNextLine())
            return "";

        String result = scanner.useDelimiter(EVERYTHING_PATTERN).next();
        scanner.useDelimiter(WHITESPACE_PATTERN);
        return result;
    }


   /**
     * Reads the next token from this input stream and returns it as a {@code String}.
     *
     * @return the next {@code String} in this input stream
     * @throws NoSuchElementException if the input stream is empty
     */
    public String readString() {
        try {
            return scanner.next();
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("attempts to read a 'String' value from the input stream, "
                                           + "but no more tokens are available");
        }
    }

    /**
     * Reads all remaining tokens from this input stream and returns them as
     * an array of strings.
     *
     * @return all remaining tokens in this input stream, as an array of strings
     */
    public String[] readAllStrings() {
        String[] tokens = WHITESPACE_PATTERN.split(readAll());
        if (tokens.length == 0 || tokens[0].length() > 0)
            return tokens;
        String[] decapitokens = new String[tokens.length-1];
        for (int i = 0; i < tokens.length-1; i++)
            decapitokens[i] = tokens[i+1];
        return decapitokens;
    }

    /**
     * Closes this input stream.
     */
    public void close() {
        scanner.close();
    }
}
// ******* סוף קוד In.java *******
// *****************************************************************************
// ******* מתחיל קוד Wordle.java *******


public class Wordle {

    // --- Constants defined by the assignment ---
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;
    private static final String DICTIONARY_FILE_NAME = "dictionary.txt";

    // --- Global Storage ---
    // Note: The assignment implicitly requires storing guesses/results globally
    // or passing them through main/printCurrentBoard. Using global static arrays
    // as is common in these types of assignments simplifies state management.
    
    // Stores the feedback patterns (MAX_ATTEMPTS x WORD_LENGTH)
    private static char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];
    // Stores the guess words as Strings (easier for printing)
    private static String[] guessWords = new String[MAX_ATTEMPTS];


    /**
     * Reads all words from the dictionary file into a String array.
     * @param fileName the name of the dictionary file.
     * @return a String array containing all words from the file.
     */
    public static String[] readDictionary(String fileName) {
        // שימוש במחלקת In כנדרש לקריאה מהקובץ
        In in = new In(fileName);
        String[] dictionary = in.readAllStrings();
        in.close();
        return dictionary;
    }

    /**
     * Selects a secret word randomly from the provided dictionary.
     * @param dictionary the array of valid words.
     * @return the randomly selected secret word.
     */
    public static String chooseSecretWord(String[] dictionary) {
        if (dictionary == null || dictionary.length == 0) {
            // טיפול במקרה של קובץ מילון ריק או חסר
            throw new IllegalArgumentException("Dictionary cannot be empty.");
        }
        Random random = new Random();
        int index = random.nextInt(dictionary.length);
        return dictionary[index];
    }

    /**
     * Prints the current state of the Wordle board (all previous guesses and feedbacks).
     * @param currentAttempt the current attempt number (0-indexed) *after* the guess was made.
     * The value represents the number of valid guesses made so far.
     * @param secret the secret word (kept as a parameter, though not used in printing the board).
     */
    public static void printCurrentBoard(int currentAttempt, String secret) {
        // הערה: הפרמטר 'secret' אינו נחוץ לצורך הדפסת הלוח עצמו, אך נשמר כנדרש ב-Skeleton.
        System.out.println("Current board:");
        for (int i = 0; i < currentAttempt; i++) {
            // String conversion for cleaner printing
            String resultStr = new String(results[i]);

            System.out.printf("Guess %d: %s Result: %s%n", i + 1, guessWords[i], resultStr);
        }
    }

    /**
     * Computes the feedback pattern for a single guess according to the simplified rules.
     * Simplified rules:
     * 1. If guess[i] == secret[i] -> 'G'
     * 2. Else if secret contains guess[i] anywhere -> 'Y'
     * 3. Else -> '_'
     * @param secret the secret 5-letter word.
     * @param guess the player's current 5-letter guess.
     * @param resultRow a char[] (length 5) to store the computed feedback.
     */
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessedChar = guess.charAt(i);

            // 1. Green Check (Correct position)
            if (guessedChar == secret.charAt(i)) {
                resultRow[i] = 'G';
            }
            // 2. Yellow Check (Correct letter, wrong position)
            // String.indexOf() בודק אם התו מופיע היכן שהוא במחרוזת ה-secret.
            else if (secret.indexOf(guessedChar) != -1) {
                resultRow[i] = 'Y';
            }
            // 3. Gray Check (Not in the word)
            else {
                resultRow[i] = '_';
            }
        }
    }


    /**
     * Main method that runs the Wordle game loop.
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        // --- Initialization ---
        
        // 1. קריאת המילון ובחירת המילה הסודית
        String[] dictionary = readDictionary(DICTIONARY_FILE_NAME);
        String secretWord = chooseSecretWord(dictionary);
        
        // אובייקט In לקריאה מהקלט הסטנדרטי
        In stdIn = new In();

        int attemptsUsed = 0;
        boolean gameWon = false;
        
        // --- Game Loop ---
        while (attemptsUsed < MAX_ATTEMPTS && !gameWon) {
            System.out.print("Enter your guess (5-letter word): ");
            
            // קריאת הקלט והפיכתו לאותיות גדולות כנדרש
            String guess = stdIn.readString().toUpperCase();

            // 3. בדיקת תקינות הקלט (רק אורך 5 נדרש)
            if (guess.length() != WORD_LENGTH) {
                // אם הקלט לא חוקי, אנחנו לא סופרים אותו כניסיון, וממשיכים ללולאה הבאה.
                // הדרישה היא שהקוד ימשיך לבקש קלט עד לקבלת קלט חוקי (אם כי הדוגמאות לא מראות מקרה כזה).
                continue; 
            }
            
            // --- עיבוד ניסיון חוקי ---
            
            // 1. שמירת מילת הניחוש (עבור ההדפסה העתידית בלוח)
            guessWords[attemptsUsed] = guess;

            // 2. חישוב הפידבק ושמירתו במערך ה-results
            computeFeedback(secretWord, guess, results[attemptsUsed]);

            // 3. הדפסת מצב הלוח הנוכחי (כולל הניחוש הנוכחי)
            printCurrentBoard(attemptsUsed + 1, secretWord);

            // 4. בדיקת תנאי ניצחון
            boolean allGreen = true;
            for (char feedback : results[attemptsUsed]) {
                if (feedback != 'G') {
                    allGreen = false;
                    break;
                }
            }

            if (allGreen) {
                gameWon = true;
            }

            // קידום מונה הניסיונות רק לאחר ניסיון חוקי
            attemptsUsed++;
        }
        
        // --- סיום המשחק ---

        if (gameWon) {
            System.out.printf("Congratulations! You guessed the word in %d attempts.%n", attemptsUsed);
        } else {
            System.out.println("Sorry, you did not guess the word.");
            System.out.println("The secret word was: " + secretWord);
        }
        
        stdIn.close();
    }
}
