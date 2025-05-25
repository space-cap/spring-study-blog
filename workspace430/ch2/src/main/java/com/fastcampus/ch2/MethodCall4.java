package com.fastcampus.ch2;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

public class MethodCall4 {
	public static void main(String[] args) throws Exception{
		Map<String, String> map = new HashMap();
		map.put("year", "2021");
		map.put("month", "10");
		map.put("day", "1");

		Model model = null;
		
		Class clazz = Class.forName("com.fastcampus.ch2.YoilTellerMVC");
		Object obj = clazz.getDeclaredConstructor().newInstance();
		
		Method main = clazz.getDeclaredMethod("main2", MyYoil.class, Model.class);
				
		Parameter[] paramArr = main.getParameters();
		Object[] argArr = new Object[main.getParameterCount()];
		
		for(int i=0;i<paramArr.length;i++) {
			String paramName = paramArr[i].getName();
			Class  paramType = paramArr[i].getType();
			Object value = map.get(paramName);
			
			// paramType중에 Model이 있으면, 생성 & 저장
			if(paramType == MyYoil.class) {
				Class<?> myYoilClass = MyYoil.class;
			    MyYoil myYoil = (MyYoil) myYoilClass.getDeclaredConstructor().newInstance();
			    
			    for(String key : map.keySet()) {
			        String methodName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
			        System.out.println("methodName="+methodName);
			        try {
			            Method setter = myYoilClass.getMethod(methodName, String.class);
			            setter.invoke(myYoil, map.get(key));
			        } catch (NoSuchMethodException e) {
			            System.out.println("Setter method not found for: " + key);
			        }
			    }
			 
			} else if(paramType==Model.class) {
				argArr[i] = model = new BindingAwareModelMap(); 
			} else if(value != null) {  // map에 paramName이 있으면,
				// value와 parameter의 타입을 비교해서, 다르면 변환해서 저장  
				argArr[i] = convertTo(value, paramType);				
			} 
		}
		
		System.out.println("paramArr="+Arrays.toString(paramArr));
		System.out.println("argArr="+Arrays.toString(argArr));
		
	} // main
	
	private static Object convertTo(Object value, Class type) {
		if(type==null || value==null || type.isInstance(value)) // 타입이 같으면 그대로 반환 
			return value;

		// 타입이 다르면, 변환해서 반환
		if(String.class.isInstance(value) && type==int.class) { // String -> int
			return Integer.valueOf((String)value);
		} else if(String.class.isInstance(value) && type==double.class) { // String -> double
			return Double.valueOf((String)value);
		}
			
		return value;
	}
	
}
