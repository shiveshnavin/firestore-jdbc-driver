package io.github.shiveshnavin.firestore.exceptions;

public class FirestoreJDBCException extends RuntimeException {

    public FirestoreJDBCException(String s) {
        super(s);
    }

    public FirestoreJDBCException(Exception e) {
        super(e);
    }
}
