package com.fastcampus.ch3;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class A1Dao {

    @Autowired
    DataSource ds;

    public int insertA1(int key, int value) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ds.getConnection();
            String sql = "insert into a1 values(?,?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, key);
            ps.setInt(2, value);

            return ps.executeUpdate();
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }

        return 0;
    }

    private void close(AutoCloseable... acs) {
        for(AutoCloseable ac :acs)
            try { if(ac!=null) ac.close(); } catch(Exception e) { e.printStackTrace(); }
    }
}
