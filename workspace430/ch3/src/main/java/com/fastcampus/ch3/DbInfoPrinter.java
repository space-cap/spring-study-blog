package com.fastcampus.ch3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class DbInfoPrinter {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public void printDbInfo() {
        System.out.println("DB URL: " + dbUrl);
        System.out.println("DB User: " + dbUser);
        // 보안상 password는 출력하지 않는 것이 좋습니다.
    }
}

