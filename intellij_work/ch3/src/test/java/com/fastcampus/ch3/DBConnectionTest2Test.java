package com.fastcampus.ch3;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import javax.sql.*;
import java.sql.*;

public class DBConnectionTest2Test {
    @Test
    public void testSomething() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void testDBConnection() throws Exception {
        System.out.println("DBConnectionTest2Test");

        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
        DataSource ds = ac.getBean(DataSource.class);

        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.

        System.out.println("conn = " + conn);
    }
}