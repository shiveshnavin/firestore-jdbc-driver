package io.shiveshnavin.firestore.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepo extends JpaRepository<User,String> {

    List<User> findByNameAndUserseq(String name, int userSeq);

}
