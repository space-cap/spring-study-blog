<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>




<html>
<head>
	<title>오늘의 운세</title>
</head>
<body>
	<h1>오늘의 운세</h1>
정보를 입력하세요<br>
	<form action="fortune" method="post">
    	이름 : <input type="text" name="name" value="김자바" /><br>
    	생년월일 : <input type="text" name="birth" value="19800101" /><br>
    	성별 : <input type="radio" name="gender" value="male" checked />남성
	    <input type="radio" name="gender" value="female" />여성<br>
		<input type="submit" value="운세보기" />
	</form>
</body>
</html>
