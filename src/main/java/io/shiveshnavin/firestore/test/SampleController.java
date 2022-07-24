package io.shiveshnavin.firestore.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SampleController {

    @Autowired
    SampleRepo repo;

    @RequestMapping
    public List<Message> home(){


        return repo.findAll();
    }
}
