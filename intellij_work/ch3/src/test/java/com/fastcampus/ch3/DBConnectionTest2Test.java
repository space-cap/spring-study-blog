package com.fastcampus.ch3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.*;
import java.sql.*;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
public class DBConnectionTest2Test {

    @Autowired
    DataSource ds;

    @Test
    public void testSomething() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void testDBConnection() throws Exception {
        System.out.println("DBConnectionTest2Test");

//        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
//        DataSource ds = ac.getBean(DataSource.class);

//        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.
//
//        System.out.println("conn = " + conn);
//
//        //assertTrue(conn != null);
//        assertNotNull(conn);

        try (Connection conn = ds.getConnection()) {
            assertNotNull(conn);
            System.out.println("conn = " + conn);
            assertFalse(conn.isClosed());
        }
    }
}