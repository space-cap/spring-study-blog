package com.fastcampus.ch3;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.context.support.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

import javax.sql.*;
import java.sql.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/**/root-context.xml"})
public class DBConnectionTest3 {
	
	// @Autowired를 사용하여 DataSource를 자동으로 주입받는다.
    @Autowired
    DataSource ds; // 컨테이너로부터 자동 주입받는다.

    @Test
    public void jdbcConnectionTest() throws Exception {
//        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context.xml");
//        DataSource ds = ac.getBean(DataSource.class); // getBean을 사용하여 빈을 수동으로 가져올 수도 있지만, @Autowired를 사용하여 자동으로 주입받는다.

        Connection conn = ds.getConnection(); // 데이터베이스의 연결을 얻는다.

        System.out.println("conn = " + conn);
        assertTrue(conn!=null);
    }
}
