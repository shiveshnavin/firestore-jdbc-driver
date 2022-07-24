package io.github.shiveshnavin.firestore;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FJLogger {
    static boolean isLoggingEnabled = false;

    public static void setEnableLogging(boolean loggingEnabled) {
        isLoggingEnabled = loggingEnabled;
    }

    public static void debug(String message) {
        if (isLoggingEnabled)
            Logger.getLogger("FirestoreJDBC").log(new LogRecord(Level.INFO, message));
    }

    public static void debug(Object message) {
        if (isLoggingEnabled)
            Logger.getLogger("FirestoreJDBC").log(new LogRecord(Level.INFO, "FJLogger : " + message.toString()));
    }
}
