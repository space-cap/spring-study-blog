package com.fastcampus.ch3.di2;

import com.fastcampus.ch3.di2.Door;
import com.fastcampus.ch3.di2.Engine;
import com.fastcampus.ch3.di2.SportsCar;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AppContext {
    Map map = new HashMap();

    AppContext() {
        map.put("car", new SportsCar());
        map.put("engine", new Engine());
        map.put("door", new Door());
    }

    AppContext(Class clazz) {
        Object config = null;
        try {
            config = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Method[] methods = clazz.getDeclaredMethods();

        for(Method m : methods){
            System.out.println("m = " + m.getName());
            for(Annotation anno : m.getDeclaredAnnotations()){
                if(anno.annotationType()== Bean.class)
                    try {
                        map.put(m.getName(), m.invoke(config, null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                // map.put("car", config.car());
            }
        }
        doAutowired(); // @Autowired를 찾아서 빈(객체)간의 자동 연결처리 byType
        doResource(); // @Resource를 찾아서 빈(객체)간의 자동 연결처리 byName
    }

    private void doResource() {
        for(Object bean : map.values()) {
            for(Field fld : bean.getClass().getDeclaredFields()){
                if(fld.getAnnotation(Resource.class)!=null)
                    try {
                        fld.set(bean, getBean(fld.getName())); // car.engine = obj
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    private void doAutowired() {
        for(Object bean : map.values()) {
            for(Field fld : bean.getClass().getDeclaredFields()){
                if(fld.getAnnotation(Autowired.class)!=null)
                    try {
                        fld.set(bean, getBean(fld.getType())); // car.engine = obj
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    public Object getBean(Class clazz) {
        for(Object obj : map.values())
            if(clazz.isInstance(obj))
                return obj;
        return null;
    }
    public Object getBean(String id) {
        return map.get(id);
    }
}
