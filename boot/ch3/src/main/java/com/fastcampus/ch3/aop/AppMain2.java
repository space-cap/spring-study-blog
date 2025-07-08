package com.fastcampus.ch3.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public class AppMain2 {
    public static void main(String[] args) {
        //ApplicationContext context = SpringApplication.run(Config.class, args);

        SpringApplication app = new SpringApplication(Config.class);
        app.setWebApplicationType(WebApplicationType.NONE); // 웹 애플리케이션 비활성화
        ApplicationContext context = app.run(args);


        MyMath math = context.getBean(MyMath.class);
        math.add(2,3);



    }
}


@EnableAspectJAutoProxy // AOP 자동 설정.
@ComponentScan
@Configuration
class Config {

}

@Aspect
@Component
class LoggingAdvice {
    @Around("execution(* com.fastcampus.ch3.aop.MyMath.add*(..))") // pointcut - 부가기능이 적용될 메서드의 패턴
    public Object methodCallLog(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("<<[start] "+pjp.getSignature().getName()+ Arrays.toString(pjp.getArgs()));

        Object result = pjp.proceed(); // target의 메서드를 호출

        System.out.println("result="+result);
        System.out.println("[end]>> "+ (System.currentTimeMillis() - start)+"ms");
        return result; // 메서드 호출결과 반환
    }
}

@Component
class MyMath {
    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int mul(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
}
