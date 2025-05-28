package com.fastcampus.ch3;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/root-context.xml" })
public class UserDaoImplTest {

	@Autowired
	UserDao userDao;

	@Autowired
	DataSource ds;

	@Test
	public void updateUserTest() throws Exception {
		deleteAll(); // 테스트 전에 기존 데이터를 모두 삭제

		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2020, 1, 1, 0, 0, 0);
		
		
		
		User user = new User("steve", "1234", "lee", "aaa", new Date(cal.getTimeInMillis()), "fb", new Date());
		int rowCnt = userDao.insertUser(user);
		assertTrue(rowCnt == 1);

		user.setEmail("bbb");
		rowCnt = userDao.updateUser(user);
		System.out.println("rowCnt=" + rowCnt + "============================");
		assertTrue(rowCnt == 1);

		User user2 = userDao.selectUser(user.getId());
		
		System.out.println("user1=" + user);
		System.out.println("user2=" + user2);
		
		assertTrue(user2.getEmail().equals("bbb"));
	}

	private void deleteAll() throws Exception {
		Connection conn = ds.getConnection();

		String sql = "delete from user_info ";

		PreparedStatement pstmt = conn.prepareStatement(sql); // SQL Injection공격 방어, 성능향상
		pstmt.executeUpdate(); // insert, delete, update
	}

}
