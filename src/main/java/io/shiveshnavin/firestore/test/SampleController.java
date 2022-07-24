package io.shiveshnavin.firestore.test;

import io.shiveshnavin.firestore.aspect.LoggingOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;


@RestController
public class SampleController {

    @Autowired
    SampleRepo repo;

    @LoggingOperation
    @RequestMapping
    public List<User> home(){


        List<User> all = repo.findAllById(List.of("TlT9KtH5Yt"));
        return all;
    }

//    @PostConstruct
    public void test(){
        List<User> all = repo.findByNameAndUserseq("abcde",0);
        System.out.println(all);
    }
}
