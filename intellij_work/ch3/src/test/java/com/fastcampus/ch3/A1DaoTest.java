package com.fastcampus.ch3;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
class A1DaoTest {

    @Autowired
    A1Dao dao;

    @Autowired
    DataSource ds;

    @Autowired
    DataSourceTransactionManager tm;

    @Test
    public void insertA1() throws Exception {

        // 트랜젝션 생성 Tx 매니저 생성
        //PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
        TransactionStatus status = tm.getTransaction(new DefaultTransactionDefinition());

        // Tx 시작
        int a = 0;
        int b = 0;
        try {
            dao.deleteAll();
            a = dao.insertA1(1, 10);
            b = dao.insertA1(2, 20);

            tm.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            tm.rollback(status);
        } finally {
        }

        System.out.println("a="+a+", b="+b);
        assertEquals(a, b);
    }

}
