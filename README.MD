# Firestore JDBC Driver
With the growing popularity of firebase as a free hosting for beginner projects. The ability to use firebase in most of the springboot projects is still not supported yet. Typically since firestore is a NoSQL based database hence SQL based JPA wont work there.

This is where firestore-jdbc comes in. It provides the necessary conversions between the SQL from JPA as well as MySQL dialect based SQL statements into queries understandable by firestore. Simply plugging in the library to any springboot/hibernate based project will complete the integration.

## Installation

```xml
TODO : download from maven central
```

## Features

All basic operations available in firestore are supported
https://firebase.google.com/docs/firestore/manage-data/add-data

## Usage
1. Download the firebase service account file for your project
2. Add these to `application.properties`
```properties
spring.datasource.url=test-a0930.json
spring.datasource.driver-class-name=io.shiveshnavin.firestore.jdbc.FirestoreJDBCDriver
```
3. Register the driver
```java
DriverManager.registerDriver(new FirestoreJDBCDriver());
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