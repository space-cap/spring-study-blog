<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
	<title>Yoil</title>
</head>
<body>
	<h1>${year}년 ${month}월 ${day}일은 ${yoil}요일입니다.</h1>
	<br/>
	<h1>${myDate.year}년 ${myDate.month}월 ${myDate.day}일은 ${yoil}요일입니다.</h1>
	<br/>
	<h1>입력한 날짜는</h1>
	<h1>${inputDate} ${yoil}</h1>
	
	
	<h1>입력한 날짜는 ${lee1}입니다.</h1>
	<h1>요일은 ${lee2}입니다.</h1>
</body>
</html>
