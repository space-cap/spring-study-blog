package com.fastcampus.ch2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

public class MethodCall4 {
	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap();
		map.put("year", "2021");
		map.put("month", "10");
		map.put("day", "1");

		Model model = null;

		Class clazz = Class.forName("com.fastcampus.ch2.YoilTellerMVC");
		Object obj = clazz.getDeclaredConstructor().newInstance();

		Method main = clazz.getDeclaredMethod("main2", MyDate.class, Model.class);

		Parameter[] paramArr = main.getParameters();
		Object[] argArr = new Object[main.getParameterCount()];

		for (int i = 0; i < paramArr.length; i++) {
			String paramName = paramArr[i].getName();
			Class paramType = paramArr[i].getType();
			Object value = map.get(paramName);

			// paramType중에 MyDate이 있으면, 생성 & 저장
			if (paramType == MyDate.class) {
				Class<?> myDateClass = MyDate.class;
				MyDate myDate = (MyDate) myDateClass.getDeclaredConstructor().newInstance();

				// 각 필드에 대해 setter 메서드 호출
				for (var entry : map.entrySet()) {
					String kname = entry.getKey();
					String kvalue = entry.getValue();

					// setter 메서드 이름 생성 (예: year -> setYear)
					String setterName = "set" + kname.substring(0, 1).toUpperCase() + kname.substring(1);

					try {
						Method setter = myDateClass.getMethod(setterName, int.class);
						setter.invoke(myDate, Integer.parseInt(kvalue));
					} catch (NoSuchMethodException e) {
						System.out.println("Setter not found: " + setterName);
					}
				}

				argArr[i] = myDate; // MyDate 객체를 argArr에 저장

			} else if (paramType == Model.class) { // paramType중에 Model이 있으면, 생성 & 저장
				argArr[i] = model = new BindingAwareModelMap();
			} else if (value != null) { // map에 paramName이 있으면,
				// value와 parameter의 타입을 비교해서, 다르면 변환해서 저장
				argArr[i] = convertTo(value, paramType);
			}
		}

		System.out.println("paramArr=" + Arrays.toString(paramArr));
		System.out.println("argArr=" + Arrays.toString(argArr));

		// Controller의 main()을 호출 - YoilTellerMVC.main(int year, int month, int day,
		// Model model)
		String viewName = (String) main.invoke(obj, argArr);
		System.out.println("viewName=" + viewName);

		// Model의 내용을 출력
		System.out.println("[after] model=" + model);

		// 텍스트 파일을 이용한 rendering
		render(model, viewName);
	} // main

	private static Object convertTo(Object value, Class type) {
		if (type == null || value == null || type.isInstance(value)) // 타입이 같으면 그대로 반환
			return value;

		// 타입이 다르면, 변환해서 반환
		if (String.class.isInstance(value) && type == int.class) { // String -> int
			return Integer.valueOf((String) value);
		} else if (String.class.isInstance(value) && type == double.class) { // String -> double
			return Double.valueOf((String) value);
		}

		return value;
	}

	private static void render(Model model, String viewName) throws IOException {
		String result = "";

		// 1. 뷰의 내용을 한줄씩 읽어서 하나의 문자열로 만든다.
		Scanner sc = new Scanner(new File("src/main/webapp/WEB-INF/views/" + viewName + ".jsp"), "utf-8");

		while (sc.hasNextLine())
			result += sc.nextLine() + System.lineSeparator();

		// 2. model을 map으로 변환
		Map map = model.asMap();

		// 3.key를 하나씩 읽어서 template의 ${key}를 value바꾼다.
		Iterator it = map.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			Object value = map.get(key);
			System.out.println("key=" + key + ", value=" + value);

			// 4. replace()로 key를 value 치환한다.
			if (!(value instanceof MyDate)) {
				result = result.replace("${" + key + "}", "" + value);
			} else {
				MyDate myDate = (MyDate) value;

				result = result.replace("${" + key + ".year}", "" + myDate.getYear());
				result = result.replace("${" + key + ".month}", "" + myDate.getMonth());
				result = result.replace("${" + key + ".day}", "" + myDate.getDay());
			}
		}

		// 5.렌더링 결과를 출력한다.
		System.out.println(result);
	}

}
