package io.shiveshnavin.firestore.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class SampleRepoTest {

    @Autowired
    SampleRepo repo;

    @Test
    void testSelectWhere() {
        List<Message> all = repo.findByRecieverIdAndRead("abcde",0);
        System.out.println(all);
    }
}