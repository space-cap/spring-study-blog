package com.fastcampus.ch3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
class UserDaoImplTest {

    @Autowired
    UserDao userDao;

    @Test
    void deleteUser(String id) {

    }

    @Test
    void selectUser() {
    }

    @Test
    void insertUser() {
    }

    @Test
    void updateUser()  {

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2020, 1, 1, 0, 0, 0);

        User user = new User("asdf22", "1234", "smith", "a@a.com", new Date(cal.getTimeInMillis()), "facebook", new Date());
        userDao.deleteUser(user.getId());
        int rowCnt = userDao.insertUser(user);

        assertEquals(1, rowCnt);

        user.setPwd("4545");
        user.setEmail("b@b.com");
        rowCnt = userDao.updateUser(user);
        assertEquals(rowCnt, 1);

        User user2 = userDao.selectUser(user.getId());
        assertEquals(user.getPwd(), user2.getPwd());
        assertEquals(user.getEmail(), user2.getEmail());
    }
}