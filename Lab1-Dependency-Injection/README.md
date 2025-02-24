# Dependency Injection Lab - Detailed Implementation Guide

## Introduction
This lab demonstrates various dependency injection patterns in Java, showcasing the evolution from traditional coupling to Spring Framework's dependency injection capabilities.

## Core Components

### 1. Data Access Layer
The data layer is defined by the `IDao` interface:
```java
package dao;

public interface IDao {
    double getData();
}
```
This interface abstracts the data retrieval mechanism, allowing different implementations to be used interchangeably.

Two implementations are provided:
- `DaoImpl`: Database simulation returning 23
- `DaoImplV2`: Web service simulation returning 12

#### DaoImpl
```java
package dao;

import org.springframework.stereotype.Repository;

@Repository("dao")
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Database version!");
        return 23;
    }
}
```
This implementation simulates data retrieval from a database.

#### DaoImplV2
```java
package extension;

import dao.IDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("dao2")
@Primary
public class DaoImplV2 implements IDao {
    @Override
    public double getData() {
        System.out.println("Web Service version");
        return 12;
    }
}
```
This implementation simulates data retrieval from a web service.


### 2. Business Layer
The business logic is defined by the `IMetier` interface:
```java
package metier;

public interface IMetier {
    double calcul();
}
```
This interface abstracts the business logic, allowing different implementations to be used interchangeably

Implemented by `MetierImpl` which:
- Has a dependency on IDao
- Multiplies the dao's data by 23
- Supports both constructor and setter injection

#### MetierImpl
```java
package metier;

import dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metier")
public class MetierImpl implements IMetier {
    private IDao dao;

    @Autowired
    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    public MetierImpl() {
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 23;
    }
}
```
This implementation performs a calculation using data retrieved from the `IDao` implementation. It supports both constructor and setter injection for flexibility.


## Implementation Details

### 1. Static Instantiation (PresentationStatic)
- Direct instantiation showing tight coupling
- Demonstrates both constructor and setter injection
- Least flexible approach as changes require code modification
```java
package pres;

import dao.DaoImpl;
import metier.MetierImpl;

public class PresentationStatic {
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao); // Constructor injection
        // metier.setDao(dao); // Setter injection
        System.out.println("Result: " + metier.calcul());
    }
}
```

**Explanation:** This approach directly instantiates the `DaoImpl` and `MetierImpl` classes, leading to tight coupling. Any change in the implementation requires code modification.

### 2. Dynamic Instantiation (PresentationDynamic)
- Uses Java Reflection API
- Reads class names from `config.txt`
- More flexible as changing implementation only requires config file modification
- Supports both constructor and setter injection
```java
package pres;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.util.Scanner;

public class PresentationDynamic {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("config.txt"))) {
            String daoClassName = scanner.nextLine();
            Class<?> daoClass = Class.forName(daoClassName);
            IDao dao = (IDao) daoClass.getConstructor().newInstance();

            String metierClassName = scanner.nextLine();
            Class<?> metierClass = Class.forName(metierClassName);
            IMetier metier = (IMetier) metierClass.getConstructor(IDao.class).newInstance(dao);

            System.out.println("Result: " + metier.calcul());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```
`config.txt`
```txt
extension.DaoImplV2
metier.MetierImpl
```
**Explanation:** This approach uses reflection to dynamically instantiate the classes based on the configuration file. It decouples the implementation from the code, allowing changes without modifying the source code.

### 3. Spring Framework Implementation

#### XML Configuration (config.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="dao" class="dao.DaoImpl"></bean>
    <bean id="metier" class="metier.MetierImpl">
        <property name="dao" ref="dao"></property>
    </bean>
</beans>
```

#### PresentationSpringXML
```java
package pres;

import metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PresentationSpringXML {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("config.xml");
        IMetier metier = (IMetier) ctx.getBean("metier");
        System.out.println("Result: " + metier.calcul());
    }
}
```
**Explanation:** This approach uses Spring's XML configuration to define beans and their dependencies. It allows for easy configuration and management of dependencies without modifying the source code.

#### Annotation-based Configuration (PresentationSpringAnnotation)
- Uses component scanning
- Annotations include @Service, @Repository, @Autowired, and @Primary
- Most modern and flexible approach

Example annotations:
```java
package dao;

import org.springframework.stereotype.Repository;

@Repository("dao")
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Database version!");
        return 23;
    }
}
```

```java
package extension;

import dao.IDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("dao2")
@Primary
public class DaoImplV2 implements IDao {
    @Override
    public double getData() {
        System.out.println("Web Service version");
        return 12;
    }
}
```
```java
package extension;

import dao.IDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("dao2")
@Primary
public class DaoImplV2 implements IDao {
    @Override
    public double getData() {
        System.out.println("Web Service version");
        return 12;
    }
}
```
```java
package metier;

import dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metier")
public class MetierImpl implements IMetier {
    private IDao dao;

    @Autowired
    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    public MetierImpl() {
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double data = dao.getData();
        return data * 23;
    }
}
```

Usage:

```java
package pres;

import metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PresentationSpringAnnotation {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("dao", "metier", "extension");
        IMetier metier = (IMetier) context.getBean("metier");
        System.out.println("Result: " + metier.calcul());
    }
}
```
**Explanation:** This approach uses Spring annotations for dependency injection. It simplifies configuration by using annotations directly in the code, making it more readable and maintainable.


## Technical Details

### Project Configuration
The project uses Maven for dependency management (pom.xml):

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>7.0.0-M2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>7.0.0-M2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
        <version>7.0.0-M2</version>
    </dependency>
</dependencies>
```

### Design Patterns Used
- Dependency Injection Pattern
- Factory Pattern (via Spring Framework)
- Interface Segregation Principle
- Inversion of Control

This implementation demonstrates the progression from tightly coupled code to loosely coupled, maintainable, and flexible applications using Spring Framework's dependency injection capabilities.