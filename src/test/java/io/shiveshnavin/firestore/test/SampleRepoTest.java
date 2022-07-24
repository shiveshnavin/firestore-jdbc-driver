package io.shiveshnavin.firestore.test;

import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;


@SpringBootTest
class SampleRepoTest {

    @Autowired
    SampleRepo repo;

    @Autowired
    EntityManager entityManager;

    @Disabled
    @Test
    void testSelectWhere() {
        List<Product> all = repo.findBypIDAndAmount("4yyqaWziix", 100);
        System.out.println(all);
    }

    @Disabled
    @Test
    void limitTest() {
        List<Product> all = repo.findAll(PageRequest.of(1, 1)).toList();
//        List<User> all = repo.findByNameAndUserseq("shivesh navin",5, PageRequest.of(1,1));
        System.out.println(all);
    }

    @Disabled
    @Test
    void createTest() {
        Product product = new Product();
        product.pID = "abcd";
        product.status = "ok";
        product.amount = 192;
        product.timeStamp = System.currentTimeMillis();
        repo.save(product);
    }

    @Disabled
    @Test
    void deleteTest() {
        Product product = new Product();
        product.pID = "abcd";
        product.status = "ok";
        product.amount = 192;
        product.timeStamp = System.currentTimeMillis();
        repo.save(product);
        repo.deleteById(product.timeStamp);

        Assertions.assertThrows(EmptyResultDataAccessException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                repo.deleteById(product.timeStamp);
            }
        });
    }


    @Disabled
    @Test
    @Transactional
    void updateTest() {
        Product product = new Product();
        product.pID = "abcd";
        product.status = "ok";
        product.amount = 192;
        product.timeStamp = System.currentTimeMillis();
        repo.saveAndFlush(product);

        Assertions.assertNotNull(repo.findById(product.timeStamp));

        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<Product> criteria = builder.createCriteriaUpdate(Product.class);
        Root<Product> root = criteria.from(Product.class);
        criteria.set(root.get("status"), "NOT OK");
        criteria.where(builder.equal(root.get("timeStamp"), product.timeStamp));
        session.createQuery(criteria).executeUpdate();

    }

    @Test
    void orderByTest() {
        List<Product> all = repo.findAll(PageRequest.of(1, 5,
                Sort.by(Sort.Direction.DESC, "amount")
                        .and(Sort.by(Sort.Direction.ASC, "timeStamp"))
        )).toList();

        for(var p:all){
            System.out.println(p);
        }
    }

}