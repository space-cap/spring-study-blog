<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String id = request.getParameter("id");
	String password = request.getParameter("password");
	request.setAttribute("id", id);
	request.setAttribute("password", password);
%>



<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>User Info JSP</title>
    <style>
        body {
            font-family: 'Noto Sans KR', Arial, sans-serif;
            background: #fafafa;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 400px;
            margin: 60px auto;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.08);
            padding: 40px 30px;
        }
        h1 {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 24px;
        }
        .userinfo {
            font-size: 1.2rem;
            margin-bottom: 18px;
        }
        .userinfo span {
            font-weight: bold;
        }
        .link-login {
            color: #7c3aed;
            font-size: 1.1rem;
            font-weight: bold;
            text-decoration: underline;
            cursor: pointer;
        }
        .link-login:hover {
            color: #4c1d95;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>User Info JSP</h1>
        <div class="userinfo">
            ID: <span>${id}</span>
        </div>
        <div class="userinfo">
            Password: <span>${password}</span>
        </div>
        <a href="login.jsp" class="link-login">로그인 페이지로 이동</a>
    </div>
</body>
</html>


