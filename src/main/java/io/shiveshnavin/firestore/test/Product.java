package io.shiveshnavin.firestore.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    public Long timeStamp;
    @Column(name = "id")
    public String pID;
    public String status;

    public boolean active;
    public int amount;


    @Override
    public String toString() {
        return "Product{" +
                "timeStamp=" + timeStamp +
                ", pID='" + pID + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", amount=" + amount +
                '}';
    }
}
