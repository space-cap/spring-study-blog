package com.fastcampus.ch3.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public class AppMain2 {
    public static void main(String[] args) {

    }
}


public class LoggingAdvice {
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
