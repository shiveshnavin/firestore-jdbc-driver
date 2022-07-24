package io.github.shiveshnavin.firestore;

public class FJLogger {
    public static void debug(String message){
        System.out.println(message);
    }

    public static void debug(Object message){
        System.out.println("FJLogger : "+message.toString());
    }
}
