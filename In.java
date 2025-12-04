import java.util.Random;
import java.util.Arrays;

// *****************************************************************************
// הערה: המחלקה In אינה מופיעה בקובץ זה אלא בקובץ In.java נפרד,
// כפי שמקובל בסביבת בדיקה זו.
// *****************************************************************************

public class Wordle {

    // --- קבועים שנדרשו במשימה ---
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;
    private static final String DICTIONARY_FILE_NAME = "dictionary.txt";

    // --- אחסון גלובלי ---
    // results: מערך דו-ממדי לאחסון הפידבק ('G', 'Y', '_').
    private static char[][] results = new char[MAX_ATTEMPTS][WORD_LENGTH];
    // guessWords: מערך מחרוזות לאחסון מילות הניחוש בפועל.
    private static String[] guessWords = new String[MAX_ATTEMPTS];


    /**
     * קורא את כל המילים מקובץ המילון לתוך מערך מחרוזות.
     * @param fileName שם קובץ המילון.
     * @return מערך מחרוזות המכיל את כל המילים מהקובץ.
     */
    public static String[] readDictionary(String fileName) {
        // שימוש במחלקת In כנדרש לקריאה מהקובץ
        In in = new In(fileName);
        String[] dictionary = in.readAllStrings();
        in.close();
        return dictionary;
    }

    /**
     * בוחר מילה סודית באקראי מתוך המילון.
     * @param dictionary מערך המילים החוקיות.
     * @return המילה הסודית שנבחרה.
     */
    public static String chooseSecretWord(String[] dictionary) {
        if (dictionary == null || dictionary.length == 0) {
            throw new IllegalArgumentException("Dictionary cannot be empty.");
        }
        Random random = new Random();
        int index = random.nextInt(dictionary.length);
        return dictionary[index];
    }

    /**
     * מדפיס את המצב הנוכחי של לוח הוורדל.
     * @param currentAttempt מספר הניסיון הנוכחי (0-מבוסס) *לאחר* שהניחוש בוצע.
     * @param secret המילה הסודית (נשמר כפרמטר כנדרש ב-skeleton).
     */
    public static void printCurrentBoard(int currentAttempt, String secret) {
        System.out.println("Current board:");
        for (int i = 0; i < currentAttempt; i++) {
            // המרה ל-String לצורך הדפסה
            String resultStr = new String(results[i]);

            System.out.printf("Guess %d: %s Result: %s%n", i + 1, guessWords[i], resultStr);
        }
    }

    /**
     * מחשב את תבנית הפידבק עבור ניחוש בודד לפי הכללים המפושטים.
     * @param secret המילה הסודית בת 5 אותיות.
     * @param guess הניחוש הנוכחי של השחקן.
     * @param resultRow מערך char[] (באורך 5) לאחסון הפידבק המחושב.
     */
    public static void computeFeedback(String secret, String guess, char[] resultRow) {
        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessedChar = guess.charAt(i);

            // 1. בדיקת ירוק (G): אות נכונה במיקום נכון
            if (guessedChar == secret.charAt(i)) {
                resultRow[i] = 'G';
            }
            // 2. בדיקת צהוב (Y): האות קיימת במילה הסודית, אבל במיקום שונה
            // String.indexOf(char) מחזיר -1 אם התו לא נמצא במחרוזת.
            else if (secret.indexOf(guessedChar) != -1) {
                resultRow[i] = 'Y';
            }
            // 3. אחרת, אפור (_): האות אינה מופיעה במילה הסודית
            else {
                resultRow[i] = '_';
            }
        }
    }

    /**
     * שיטה בוליאנית פשוטה לבדיקת ניצחון (כל הפידבק הוא 'G').
     * @param resultRow מערך הפידבק של הניסיון האחרון.
     * @return true אם הניחוש מושלם, false אחרת.
     */
    public static boolean isAllGreen(char[] resultRow) {
         for (char feedback : resultRow) {
            if (feedback != 'G') {
                return false;
            }
        }
        return true;
    }


    /**
     * השיטה הראשית שמנהלת את לולאת משחק הוורדל.
     * @param args ארגומנטים משורת הפקודה (לא בשימוש).
     */
    public static void main(String[] args) {
        // --- אתחול ---
        
        // 1. קריאת המילון ובחירת המילה הסודית
        String[] dictionary = readDictionary(DICTIONARY_FILE_NAME);
        String secretWord = chooseSecretWord(dictionary);
        
        // אובייקט In לקריאה מהקלט הסטנדרטי
        In stdIn = new In();

        int attemptsUsed = 0;
        boolean gameWon = false;
        
        // --- לולאת המשחק ---
        while (attemptsUsed < MAX_ATTEMPTS && !gameWon) {
            
            System.out.print("Enter your guess (5-letter word): ");
            
            // קריאת הקלט והפיכתו לאותיות גדולות כנדרש
            String guess = stdIn.readString().toUpperCase();

            // 3. בדיקת תקינות הקלט (רק אורך 5 נדרש)
            if (guess.length() != WORD_LENGTH) {
                // אם הקלט לא חוקי, ממשיכים לאיטרציה הבאה (מבקשים קלט שוב).
                // שימו לב: הלולאה החיצונית לא תתקדם.
                continue; 
            }
            
            // --- עיבוד ניסיון חוקי ---
            
            // 1. שמירת מילת הניחוש ושמירת הפידבק
            guessWords[attemptsUsed] = guess;
            computeFeedback(secretWord, guess, results[attemptsUsed]);

            // 2. הדפסת מצב הלוח
            printCurrentBoard(attemptsUsed + 1, secretWord);

            // 3. בדיקת תנאי ניצחון באמצעות הפונקציה המאולתרת isAllGreen (אם נדרשה)
            // נשתמש בבדיקה ישירה על המערך כיוון שisAllGreen לא הוגדרה ב-skeleton המקורי כ-static.
            if (isAllGreen(results[attemptsUsed])) {
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
