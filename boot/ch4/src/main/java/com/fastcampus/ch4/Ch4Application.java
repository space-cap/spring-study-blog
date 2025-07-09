package com.fastcampus.ch4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class Ch4Application implements CommandLineRunner {

    @Autowired
    EntityManagerFactory emf;

    public static void main(String[] args) {
        // SpringApplication.run(Ch4Application.class, args);
        SpringApplication app = new SpringApplication(Ch4Application.class);
        //app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
       /* //System.out.println("emf: " + emf);
        EntityManager em = emf.createEntityManager();
        System.out.println("em = " + em.toString());

        EntityTransaction tx = em.getTransaction();

        User user = new User();
        user.setId("id-01");
        user.setName("lee");
        user.setPassword("123456");
        user.setEmail("lee@naver.com");
        user.setInDate(new Date());
        user.setUpDate(new Date());
        System.out.println("user1 = " + user.toString());


        tx.begin();
        em.persist(user);
        tx.commit();


        User user2 = em.find(User.class, user.getId());
        System.out.println("user2 = " + user2.toString());

        User user3 = em.find(User.class, "kkk");
        System.out.println("user3 = " + user3);

        tx.begin();
        em.remove(user);
        tx.commit();*/


    }
}
