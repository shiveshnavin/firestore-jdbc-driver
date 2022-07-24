package io.shiveshnavin.firestore.test;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    public String id;
    public String name;
    public boolean isAnonymous;
    public int userseq;


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isAnonymous=" + isAnonymous +
                ", userseq=" + userseq +
                '}';
    }
}
