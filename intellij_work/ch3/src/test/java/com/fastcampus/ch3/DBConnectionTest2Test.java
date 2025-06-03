package com.fastcampus.ch3;

import org.junit.jupiter.api.DisplayName;
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
import java.util.Date;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
public class DBConnectionTest2Test {

    @Autowired
    DataSource ds;
    @Autowired
    private UserDao userDao;

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

    @Test
    @DisplayName("정상적인 회원 정보를 입력하면 회원이 등록된다")
    public void insertUserWithValidData() throws Exception {

        User user = new User("aaa", "aaa", "홍길동", "a@a.com", new Date(), "kakao", new Date());
        deleteUser(user.getId());

        int rowCnt = insertUser(user);

        assertEquals(1, rowCnt);
    }

    public int deleteUser(String id) throws Exception {
        Connection conn = ds.getConnection();

        String sql = "delete from user_info where id= ? ";

        PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격 방어, 성능향상
        pstmt.setString(1, id);
//        int rowCnt = pstmt.executeUpdate(); //  insert, delete, update
//        return rowCnt;
        return pstmt.executeUpdate(); //  insert, delete, update
    }

    // 사용자 정보를 user_info테이블에 저장하는 메서드
    public int insertUser(User user) throws Exception {
        Connection conn = ds.getConnection();

//        insert into user_info (id, pwd, name, email, birth, sns, reg_date)
//        values ('asdf22', '1234', 'smith', 'aaa@aaa.com', '2022-01-01', 'facebook', now());

        String sql = "insert into user_info values (?, ?, ?, ?,?,?, now()) ";

        PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격, 성능향상
        pstmt.setString(1, user.getId());
        pstmt.setString(2, user.getPwd());
        pstmt.setString(3, user.getName());
        pstmt.setString(4, user.getEmail());
        pstmt.setDate(5, new java.sql.Date(user.getBirth().getTime()));
        pstmt.setString(6, user.getSns());

        int rowCnt = pstmt.executeUpdate(); //  insert, delete, update

        return rowCnt;
    }


    @Test
    public void transactionTest() throws Exception {
        Connection conn = null;

        try {
            deleteUser("asdf22");
            conn = ds.getConnection();
            conn.setAutoCommit(false); // default true

            String sql = "insert into user_info values (?, ?, ?, ?,?,?, now()) ";

            PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격, 성능향상
            pstmt.setString(1, "asdf22");
            pstmt.setString(2, "1234");
            pstmt.setString(3, "smith");
            pstmt.setString(4,  "a@a.com");
            pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
            pstmt.setString(6, "facebook");

            int rowCnt = pstmt.executeUpdate(); //  insert, delete, update

            pstmt.setString(1, "asdf23");
            rowCnt = pstmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
        } finally {
            conn.close();
        }
    }

}