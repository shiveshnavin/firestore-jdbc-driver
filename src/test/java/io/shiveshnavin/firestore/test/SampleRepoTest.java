package io.shiveshnavin.firestore.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;


@SpringBootTest
class SampleRepoTest {

    @Autowired
    SampleRepo repo;

    @Disabled
    @Test
    void testSelectWhere() {
        List<User> all = repo.findByNameAndUserseq("shivesh navin",5);
        System.out.println(all);
    }

    @Test
    void limitTest(){
        List<User> all =  repo.findAll(PageRequest.of(1,1)).toList();
//        List<User> all = repo.findByNameAndUserseq("shivesh navin",5, PageRequest.of(1,1));
        System.out.println(all);
    }
}