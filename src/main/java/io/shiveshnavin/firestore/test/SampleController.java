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
    public List<Message> home(){


        List<Message> all = repo.findAll();
        return all;
    }

    @PostConstruct
    public void test(){
        List<Message> all = repo.findAll();
        System.out.println(all);
    }
}
