package com.fastcampus.ch3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TxService {
    @Autowired
    A1Dao a1Dao;

    @Autowired
    B1Dao b1Dao;

    public void insertA1WithoutTx() throws Exception {
        a1Dao.insertA1(1, 10);
        a1Dao.insertA1(1, 20);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Transactional // RuntimeExcepton, Error 만  rollback 한다.
    public void insertA1WithTxFail() throws Exception {
        a1Dao.insertA1(1, 10);
        a1Dao.insertA1(1, 20);
    }

    @Transactional
    public void insertA1WithTxSuccess() throws Exception {
        a1Dao.insertA1(1, 10);
        a1Dao.insertA1(2, 20);
    }

}
