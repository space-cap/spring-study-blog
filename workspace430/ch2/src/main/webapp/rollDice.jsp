<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // CORS 헤더 설정 (필요한 경우)
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    
    // 두 개의 주사위 값 랜덤 생성 (1-6 사이)
    int dice1 = (int)(Math.random() * 6) + 1;
    int dice2 = (int)(Math.random() * 6) + 1;
    
    // JSON 형식으로 응답
    out.print("{\"dice1\": " + dice1 + ", \"dice2\": " + dice2 + "}");
%>