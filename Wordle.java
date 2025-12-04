import java.util.Random;
import java.util.Arrays;
// יש לוודא שהמחלקה In זמינה (בקובץ In.java נפרד)

public class Wordle {

    // --- קבועים: מגדיר אותם בתוך main כפי שנדרש בתבנית ---
    // ההגדרות הללו הועברו לתוך main:
    // int WORD_LENGTH = 5;
    // int MAX_ATTEMPTS = 6;
    
    // זהו שם הקובץ:
    private static final String DICTIONARY_FILE_NAME = "dictionary.txt";


    // Reads all words from dictionary filename into a String array.
    public static String[] readDictionary(String filename) {
		In in = new In(filename);
        String[] dictionary = in.readAllStrings();
        in.close();
        return dictionary;
    }

    // Choose a random secret word from the dictionary. 
    public static String chooseSecretWord(String[] dict) {
		if (dict == null || dict.length == 0) {
            throw new IllegalArgumentException("Dictionary cannot be empty.");
        }
        Random random = new Random();
        int index = random.nextInt(dict.length);
        return dict[index];
    }

    // Simple helper: check if letter c appears anywhere in secret (true), otherwise
    // return false.
    public static boolean containsChar(String secret, char c) {
		// String.indexOf מחזיר -1 אם התו לא נמצא במחרוזת
        return secret.indexOf(c) != -1;
    }

    // Compute feedback for a single guess into resultRow.
    // G for exact match, Y if letter appears anywhere else, _ otherwise.
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        // ההנחה היא שהניחוש והמילה הסודית תמיד באורך WORD_LENGTH (5).
		int len = secret.length(); 
		
		for (int i = 0; i < len; i++) {
            char guessedChar = guess.charAt(i);

            // 1. G: אות נכונה במיקום נכון
            if (guessedChar == secret.charAt(i)) {
                resultRow[i] = 'G';
            }
            // 2. Y: האות קיימת במילה הסודית (משתמשים ב-containsChar כפי שהוצע)
            else if (containsChar(secret, guessedChar)) {
                resultRow[i] = 'Y';
            }
            // 3. _: האות אינה מופיעה במילה הסודית
            else {
                resultRow[i] = '_';
            }
        }
    }

    // Store guess string (chars) into the given row of guesses 2D array.
    public static void storeGuess(String guess, char[][] guesses, int row) {
		// ההנחה היא ש-guess.length() = 5
        for (int i = 0; i < guess.length(); i++) {
            guesses[row][i] = guess.charAt(i);
        }
    }

    // Prints the game board up to currentRow (inclusive).
    public static void printBoard(char[][] guesses, char[][] results, int currentRow) {
        System.out.println("Current board:");
        // הדפסה מ-row 0 עד currentRow (כולל)
        for (int row = 0; row <= currentRow; row++) {
            System.out.print("Guess " + (row + 1) + ": ");
            
            // הדפסת הניחוש
            for (int col = 0; col < guesses[row].length; col++) {
                System.out.print(guesses[row][col]);
            }
            
            // הדפסת הפידבק (נשמר הרווח הכפול לפני Result כנדרש בדוגמאות הקודמות)
            System.out.print("    Result: ");
            for (int col = 0; col < results[row].length; col++) {
                System.out.print(results[row][col]);
            }
            System.out.println();
        }
        // הדוגמאות הראו רווח/שורה ריקה אחרי הלוח, נשאיר רק שורה חדשה.
    }

    // Returns true if all entries in resultRow are 'G'.
    public static boolean isAllGreen(char[] resultRow) {
		for (char feedback : resultRow) {
            if (feedback != 'G') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        int WORD_LENGTH = 5;
        int MAX_ATTEMPTS = 6;
        
        // Read dictionary
        String[] dict = readDictionary(DICTIONARY_FILE_NAME);

        // Choose secret word
        String secret = chooseSecretWord(dict);
        
        // --- תיקון שגיאות הקומפילציה מהמבנה הקודם: אתחול המערכים ---
        char[][] guesses = new char[MAX_ATTEMPTS][WORD_LENGTH];
        char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];

        // Prepare to read from the standard input 
        // שימוש במחלקה In כנדרש, לא לשכוח לסגור בסוף
        In inp = new In();

        int attempt = 0; // מונה הניסיונות (0-מבוסס)
        boolean won = false;
        String guess = "";
        
        while (attempt < MAX_ATTEMPTS && !won) {

            boolean valid = false;

            // Loop until you read a valid guess
            while (!valid) {
                System.out.print("Enter your guess (5-letter word): ");
                
                // קורא מילה מהקלט הסטנדרטי והופך לאותיות גדולות
                guess = inp.readString().toUpperCase();
                
                // בדיקת תקינות (אורך 5 אותיות בדיוק)
                if (guess.length() == WORD_LENGTH) {
                    valid = true;
                } else {
                    // התבנית המקורית דרשה הדפסת הודעה על קלט לא חוקי
                    System.out.println("Invalid word. Please try again.");
                }
            }

            // Store guess and compute feedback
            // שומר את הניחוש במערך הדו-ממדי
            storeGuess(guess, guesses, attempt);
            
            // מחשב את הפידבק ושומר במערך הדו-ממדי
            computeFeedback(secret, guess, results[attempt]);

            // Print board
            // מדפיס את הלוח עד הניסיון הנוכחי (attempt)
            printBoard(guesses, results, attempt);

            // Check win
            if (isAllGreen(results[attempt])) {
                System.out.println("Congratulations! You guessed the word in " + (attempt + 1) + " attempts.");
                won = true;
            }

            attempt++; // מקדם את מונה הניסיונות (רק אחרי ניסיון חוקי)
        }

        if (!won) {
             // הדפסת הודעת הפסד כפי שנדרש בדוגמאות
             System.out.println("Sorry, you did not guess the word.");
             System.out.println("The secret word was: " + secret);
        }

        inp.close();
    }
}
