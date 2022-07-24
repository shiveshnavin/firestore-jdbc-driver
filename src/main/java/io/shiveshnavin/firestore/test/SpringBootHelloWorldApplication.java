package io.shiveshnavin.firestore.test;


import io.shiveshnavin.firestore.jdbc.FirestoreJDBCDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DriverManager;
import java.sql.SQLException;

@RestController
@SpringBootApplication(scanBasePackages = {"io.shiveshnavin.firestore"})
@EnableJpaRepositories
public class SpringBootHelloWorldApplication {

    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new FirestoreJDBCDriver());
        SpringApplication.run(SpringBootHelloWorldApplication.class, args);
    }
}