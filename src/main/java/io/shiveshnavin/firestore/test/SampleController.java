package io.shiveshnavin.firestore.test;

import io.shiveshnavin.firestore.aspect.LoggingOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SampleController {

    @Autowired
    SampleRepo repo;

    @LoggingOperation
    @RequestMapping
    public List<Product> home(){


        List<Product> all = repo.findAllById(List.of(1615017569716l));
        return all;
    }

//    @PostConstruct
    public void test(){
        List<Product> all = repo.findBypIDAndAmount("abcde",0);
        System.out.println(all);
    }
}
