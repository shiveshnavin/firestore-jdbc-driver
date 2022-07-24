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
        List<Product> all = repo.findBypIDAndAmount("4yyqaWziix",100);
        System.out.println(all);
    }

    @Disabled
    @Test
    void limitTest(){
        List<Product> all =  repo.findAll(PageRequest.of(1,1)).toList();
//        List<User> all = repo.findByNameAndUserseq("shivesh navin",5, PageRequest.of(1,1));
        System.out.println(all);
    }

    @Test
    void createTest() {
        Product product = new Product();
        product.pID="abcd";
        product.status="ok";
        product.amount=192;
        product.timeStamp=System.currentTimeMillis();
        repo.save(product);
    }
}