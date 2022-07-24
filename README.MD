[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Release](https://github.com/shiveshnavin/firestore-jdbc-driver/actions/workflows/maven-publish-release.yml/badge.svg)](https://github.com/shiveshnavin/firestore-jdbc-driver/actions/workflows/maven-publish-release.yml)
[![Maven Deploy](https://github.com/shiveshnavin/firestore-jdbc-driver/actions/workflows/maven-publish-snapshot.yml/badge.svg?branch=master)](https://github.com/shiveshnavin/firestore-jdbc-driver/actions/workflows/maven-publish-snapshot.yml)

# Firestore JDBC Driver
With the growing popularity of firebase as a free hosting for beginner projects. The ability to use firebase in most of the springboot projects is still not supported yet. Typically since firestore is a NoSQL based database hence SQL based JPA wont work there.

This is where firestore-jdbc comes in. It provides the necessary conversions between the SQL from JPA as well as MySQL dialect based SQL statements into queries understandable by firestore. Simply plugging in the library to any springboot/hibernate based project will complete the integration.

## Features

All basic operations available in firestore are supported
https://firebase.google.com/docs/firestore/manage-data/add-data

## Installation
Release
```xml
<dependency>
   <groupId>io.github.shiveshnavin</groupId>
   <artifactId>firestore-jdbc</artifactId>
   <version>1.0.3</version>
</dependency>
```
Snapshot (Experimental)
```xml
<dependency>
   <groupId>io.github.shiveshnavin</groupId>
   <artifactId>firestore-jdbc</artifactId>
   <version>1.0.4-SNAPSHOT</version>
</dependency>
```
## Usage
1. Download the firebase service account file for your project
   https://firebase.google.com/docs/admin/setup
2. Add these to `application.properties`

```properties
spring.datasource.url=path/to/your/service-account.json
spring.datasource.driver-class-name=FirestoreJDBCDriver
```
3. Create a `LocalSessionFactoryBean` bean in your `@Configuration` class and replace `com.example.domain.model` with your domain model package
```java
@Configuration
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
      
      // Add the packages containing your model POJO classes
      factory.setPackagesToScan(new String[]{"com.example.domain.model"});

      factory.setDataSource(dataSource);
      return factory;
   } 
}
```
4. In all your Entity classes make sure to specify `@Table` and table name (case sensitive)
```java
@Entity
@Table(name="users")
public class User{
    @Id
    String id;
    String name;
}
```
