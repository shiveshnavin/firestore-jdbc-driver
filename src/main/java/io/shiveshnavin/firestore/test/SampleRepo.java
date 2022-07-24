package io.shiveshnavin.firestore.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepo extends JpaRepository<Message,String> {

    List<Message> findByRecieverIdAndRead(String id, int read);

}
