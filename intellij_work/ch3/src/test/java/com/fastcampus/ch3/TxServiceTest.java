package com.fastcampus.ch3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // ac 를 만드는 것 하고 같다. // ac 만들어 놓고 계속 사용한다.
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/**/root-context.xml")
class TxServiceTest {

    @Autowired
    TxService txService;

    @Test
    public void insertA1WithoutTx() throws Exception {
        System.out.println("TxServiceTest.insertA1WithoutTx()");
        txService.insertA1WithTxFail ();
    }

}