package com.fastcampus.ch3.aop;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppMain {
    public static void main(String[] args) throws Exception {
        Class myClass = Class.forName("com.fastcampus.ch3.aop.MyClass");
        Object o = myClass.newInstance();

        MyAdvice myAdvice = new MyAdvice();

        for(Method m: myClass.getDeclaredMethods()) {
            myAdvice.invoke(m, o, null);
        }
    }

}

class MyAdvice {
    // Pattern 사용
    Pattern p = Pattern.compile("a.*"); // a로 시작하는 문자열

    // no usages
    boolean matches(Method m) { // 지정된 메서드가 패턴에 일치하는지 알려줌.
        Matcher matcher = p.matcher(m.getName());
        return matcher.matches();
    }

    public void invoke(Method m, Object o, Object[] args) throws Exception {
        System.out.println("before invoke");
        m.invoke(o, args);
        System.out.println("after invoke");
    }
}

class MyClass {
    public void aaa() {
        System.out.println("aaa");
    }

    public void bbb() {
        System.out.println("bbb");
    }
}