package io.shiveshnavin.firestore.test;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message {

    @Id
    public String id;


    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                '}';
    }
}
