package com.fastcampus.ch3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
class A1DaoTest {

    @Autowired
    A1Dao dao;

    @Test
    public void insertA1() throws Exception {
        int a = dao.insertA1(3, 10);
        int b = dao.insertA1(4, 20);

        System.out.println("a="+a+", b="+b);

        assertEquals(1,1);
    }

}
