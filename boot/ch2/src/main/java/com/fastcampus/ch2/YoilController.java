package com.fastcampus.ch2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class YoilController {


    @RequestMapping("/test2")
    public String getTest2(int year, int month, int day, Model model) {

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);

        // 2. 처리
        Calendar cal = Calendar.getInstance();
        cal.clear();  // 모든 필드(날짜, 시간 등)을 초기화
        cal.set(year, month - 1, day);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        char yoil = "일월화수목금토".charAt(dayOfWeek-1); // dayofWeek는 일요일:1, 월요일:2, ...

        model.addAttribute("yoil", yoil);

        return "test2";
    }


    @RequestMapping("/testResponse")
    public void getTestResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");

        System.out.println(year + "-" + month + "-" + day);

        int yyyy = Integer.parseInt(year);
        int mm = Integer.parseInt(month);
        int dd = Integer.parseInt(day);

        // 2. 처리
        Calendar cal = Calendar.getInstance();
        cal.clear();  // 모든 필드(날짜, 시간 등)을 초기화
        cal.set(yyyy, mm - 1, dd);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        char yoil = "일월화수목금토".charAt(dayOfWeek-1); // dayofWeek는 일요일:1, 월요일:2, ...

        // 3. 출력
        response.setContentType("text/html");    // 응답의 형식을 html로 지정
        response.setCharacterEncoding("utf-8");  // 응답의 인코딩을 utf-8로 지정
        PrintWriter out = response.getWriter();  // 브라우저로의 출력 스트림(out)을 얻는다.
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body>");
        out.println(year + "년 " + month + "월 " + day + "일은 ");
        out.println(yoil + "요일입니다.");
        out.println("</body>");
        out.println("</html>");
        out.close();

    }



    @RequestMapping("getYoil")
    public String getYoil(HttpServletRequest req) {
        String year = req.getParameter("year");
        String month = req.getParameter("month");
        String day = req.getParameter("day");

        System.out.println(year + "-" + month + "-" + day);

        // 반환 타입: Map<String, String[]>
        // 값이 항상 String 배열이므로 Arrays.toString() 또는 배열 순회 필요
        // 같은 이름의 파라미터가 여러 개 있을 수 있음 (예: 체크박스)
        var reqMap = req.getParameterMap();
        System.out.println(reqMap.toString());

        for (Map.Entry<String, String[]> entry : reqMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            System.out.println("Key: " + key);
            System.out.println("Values: " + Arrays.toString(values));
            System.out.println("---");
        }

        // keySet() 사용.
        for (String key : reqMap.keySet()) {
            String[] values = reqMap.get(key);
            System.out.println("Key: " + key + ", Values: " + Arrays.toString(values));
        }

        // Stream API 사용.
        reqMap.entrySet().stream()
                .forEach(entry ->
                        System.out.println("Key: " + entry.getKey() +
                                ", Values: " + Arrays.toString(entry.getValue()))
                );


        reqMap.entrySet().stream()
                .forEach(entry -> {
                    String key = entry.getKey();
                    String values = String.join(", ", entry.getValue());
                    System.out.printf("Parameter: %s = [%s]%n", key, values);
                });


        for (Map.Entry<String, String[]> entry : reqMap.entrySet()) {
            String key = entry.getKey();
            String firstValue = entry.getValue().length > 0 ? entry.getValue()[0] : "";
            System.out.println(key + " = " + firstValue);
        }

        reqMap.forEach((key, values) -> {
            String joinedValues = String.join(", ", values);
            System.out.println(key + " = " + joinedValues);
        });



        System.out.println("=== Request Parameters ===");
        System.out.println("Total parameters: " + reqMap.size());

        reqMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 키 순서로 정렬
                .forEach(entry -> {
                    String key = entry.getKey();
                    String[] values = entry.getValue();

                    System.out.println("Parameter: " + key);
                    System.out.println("  Value count: " + values.length);
                    for (int i = 0; i < values.length; i++) {
                        System.out.println("  [" + i + "] = " + values[i]);
                    }
                    System.out.println();
                });


        // 가장 실용적인 출력 방법
        reqMap.forEach((key, values) ->
                System.out.println(key + " = " + String.join(", ", values))
        );


        var names = req.getParameterNames();
        System.out.println(names.toString());

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            System.out.println(name);
        }


        // Collections.list() 사용.
        List<String> nameList = Collections.list(names);

        for (String name : nameList) {
            System.out.println(name);
        }

        Collections.list(names).stream()
                .forEach(System.out::println);

        String paramNames = Collections.list(names).stream()
                .collect(Collectors.joining(", "));
        System.out.println("Parameter names: " + paramNames);

        // 번호와 함께 출력
        List<String> nameList2 = Collections.list(names);

        for (int i = 0; i < nameList2.size(); i++) {
            System.out.println((i + 1) + ". " + nameList2.get(i));
        }


        System.out.println("=== Request Parameter Names ===");
        Collections.list(names).stream()
                .sorted() // 알파벳 순으로 정렬
                .forEach(name -> System.out.println("- " + name));



        // 파라미터 개수와 함께 출력
        List<String> nameList3 = Collections.list(names);

        System.out.println("Total parameters: " + nameList3.size());
        System.out.println("Parameter names:");
        nameList3.forEach(name -> System.out.println("  " + name));


        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = req.getParameter(name);
            System.out.println(name + " = " + value);
        }

        // 방법 1: 각 이름을 한 줄씩 출력
        Collections.list(names).forEach(System.out::println);

        // 방법 2: 한 줄에 모든 이름 출력
        System.out.println("Parameters: " +
                Collections.list(req.getParameterNames()).stream()
                        .collect(Collectors.joining(", ")));


        // Enumeration의 특징
        // Enumeration은 한 번 순회하면 다시 사용할 수 없음
        // 여러 번 사용하려면 Collections.list()로 List로 변환 필요

        // Enumeration을 List로 변환하여 재사용 가능하게 만들기
        List<String> nameList4 = Collections.list(req.getParameterNames());

        // 이제 nameList를 여러 번 사용 가능
        nameList4.forEach(System.out::println);
        System.out.println("Total: " + nameList4.size());

        // 가장 간단하고 실용적인 방법은 Collections.list(names).forEach(System.out::println)






        String yoil = year + "-" + month + "-" + day;


        return yoil;
    }
}
