package io.shiveshnavin.firestore.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepo extends JpaRepository<Product,Long> {

    List<Product> findBypIDAndAmount(String name, int userSeq);

}
