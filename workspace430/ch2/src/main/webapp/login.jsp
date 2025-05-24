<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map, java.util.HashMap" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.net.URLDecoder" %>


<%
// 디버그 정보 수집
StringBuilder debugInfo = new StringBuilder();
debugInfo.append("=== 디버그 정보 ===<br>");
debugInfo.append("URL Parameter errorMessage: ").append(request.getParameter("errorMessage")).append("<br>");
debugInfo.append("Request Attribute errorMessage: ").append(request.getAttribute("errorMessage")).append("<br>");
debugInfo.append("Session ID: ").append(request.getSession(false) != null ? request.getSession(false).getId() : "세션 없음").append("<br>");
debugInfo.append("Session 활성화: ").append(request.getSession(false) != null ? "true" : "false").append("<br>");

// 모든 request attributes 확인
debugInfo.append("=== 모든 Request Attributes ===<br>");
java.util.Enumeration<String> attrNames = request.getAttributeNames();
while (attrNames.hasMoreElements()) {
    String attrName = attrNames.nextElement();
    debugInfo.append(attrName).append(" = ").append(request.getAttribute(attrName)).append("<br>");
}

// URL 파라미터로 전달된 errorMessage 받기
String errorMessage = request.getParameter("errorMessage");
// request attribute로 전달된 errorMessage도 확인 (포워드된 경우)
if (errorMessage == null) {
	System.out.println("errorMessage is null");
    errorMessage = (String) request.getAttribute("errorMessage");
	System.out.println("errorMessage from request attribute: " + errorMessage);
} else {
    System.out.println("errorMessage is not null: " + errorMessage);
	//try {
    //    errorMessage = URLDecoder.decode(errorMessage, "UTF-8");
    //} catch (Exception e) {
    //    // 디코딩 실패시 원본 값 유지
    //    System.out.println("URL 디코딩 실패: " + e.getMessage());
    //}
	//System.out.println("errorMessage: " + errorMessage);
	request.setAttribute("errorMessage", errorMessage);
}



String id = null;
String isChecked = null;
Map<String, String> map = new HashMap<>();
StringBuilder cookieDebugInfo = new StringBuilder();

Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        // out.println() 대신 StringBuilder로 디버그 정보 저장
        cookieDebugInfo.append(cookie.getName()).append(" : ").append(cookie.getValue()).append("<br>");
        String key = cookie.getName();
        String value = cookie.getValue();
        map.put(key, value);
    }
}

id = map.get("id");
isChecked = map.get("isChecked");

// EL 표현식에서 사용할 수 있도록 request attribute로 설정
request.setAttribute("id", id);
request.setAttribute("isChecked", isChecked != null ? "checked" : "");
//request.setAttribute("cookieDebugInfo", cookieDebugInfo.toString());
//request.setAttribute("debugInfo", debugInfo.toString());
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login JSP</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }

        .login-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            padding: 40px;
            width: 100%;
            max-width: 400px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            font-size: 14px;
            color: #888;
            margin-bottom: 8px;
            font-weight: 500;
        }

        .form-input {
            width: 100%;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s ease;
            outline: none;
        }

        .form-input:focus {
            border-color: #00c851;
        }

        .form-input.username {
            background-color: #f8f9fa;
        }

        .password-input {
            font-family: 'Courier New', monospace;
            letter-spacing: 2px;
        }

        .remember-section {
            display: flex;
            align-items: center;
            margin-bottom: 30px;
        }

        .remember-group {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .checkbox {
            width: 18px;
            height: 18px;
            accent-color: #00c851;
        }

        .remember-label {
            font-size: 14px;
            color: #666;
        }

        .login-button {
            width: 100%;
            padding: 15px;
            background-color: #00c851;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .login-button:hover {
            background-color: #00a844;
        }

        .login-button:active {
            transform: translateY(1px);
        }

        @media (max-width: 480px) {
            .login-container {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
	
    <div class="login-container">
		<div>
		        <h1>Login JSP</h1>
			</div>
        <form action="/ch2/login" method="post">
            <div class="form-group">
                <label for="username" class="form-label">아이디</label>
                <input type="text" id="id" name="id" class="form-input username" value="${id}" required>
            </div>
            
            <div class="form-group">
                <label for="password" class="form-label">비밀번호</label>
                <input type="password" id="password" name="password" value="" class="form-input password-input" placeholder="••••••••••" required>
            </div>
            
            <div class="remember-section">
                <div class="remember-group">
                    <input type="checkbox" id="remember" name="rememberId" class="checkbox" ${isChecked}>
                    <label for="remember" class="remember-label">아이디 저장</label>
                </div>
            </div>
            
            <button type="submit" class="login-button">로그인</button>
        </form>
		<div>
				<h1> errorMessage: ${errorMessage} </h1>
				<h1> cookie id: ${id} </h1>
				<div style="border: 1px solid #ccc; padding: 10px; margin: 10px 0; background-color: #f9f9f9;">
	                <h3>쿠키 디버그 정보:</h3>
	                ${cookieDebugInfo}
	            </div>
				<div style="border: 1px solid #ff9999; padding: 10px; margin: 10px 0; background-color: #ffe6e6;">
	                ${debugInfo}
	            </div>
			</div>
    </div>
	
    <script>
        // 폼 제출 이벤트
        document.querySelector('form').addEventListener('submit', function(e) {
            const id = document.getElementById('id').value;
            const password = document.getElementById('password').value;
            
            if (!id || !password) {
                e.preventDefault();
                alert('아이디와 비밀번호를 모두 입력해주세요.');
                return;
            }
            
            // 입력값이 모두 있으면 폼을 정상적으로 제출 (loginProcess.jsp로 이동)
        });
    </script>
</body>
</html>


