package io.shiveshnavin.firestore.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@EnableAspectJAutoProxy
//@EnableLoadTimeWeaving(aspectjWeaving= EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
public class Config {

    @Autowired
    DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "none");
        factory.setJpaProperties(props);

        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("io.shiveshnavin.firestore.test");
        factory.setDataSource(dataSource);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }


}
