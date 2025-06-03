package com.fastcampus.ch3.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class AopMain2 {
    public static void main(String[] args) {
        System.out.println("AopMain2");

        ApplicationContext ac = new GenericXmlApplicationContext("file:src/main/webapp/WEB-INF/spring/**/root-context_aop.xml");
        MyMath mm = (MyMath) ac.getBean("myMath");
        //System.out.println("mm.add(1, 2) = " + mm.add(1, 2));
        //System.out.println("mm.add(1, 2, 3) = " + mm.add(1, 2, 3));

        mm.add(1,2);
        mm.add(1,2,3);
        mm.sub(1,2);
        mm.mul(1,2);

    }
}
