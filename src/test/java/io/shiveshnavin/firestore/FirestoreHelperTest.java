package io.shiveshnavin.firestore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FirestoreHelperTest {

    FirestoreHelper firestoreHelper;

    @BeforeEach
    void setUp() {
        firestoreHelper = new FirestoreHelper(FirestoreHelper.class.getClassLoader().getResourceAsStream("keys/test-a0930.json"));
    }

    @Test
    void getDefaultDatabase() {
        assertNotNull(firestoreHelper.getDefaultDatabase());
    }

    @Test
    void getFirebaseDatabase() {
        assertNotNull(firestoreHelper.getFirebaseDatabase("test-a0930"));
    }
}