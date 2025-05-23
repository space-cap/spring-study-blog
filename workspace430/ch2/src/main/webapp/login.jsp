<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
String id = null;
Map<String, String> map = new HashMap<>();

Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        String key = cookie.getName();
		String value = cookie.getValue();
		map.put(key, value);
    }
}

String isChecked = null;
id = map.get("id");
isCchecked = map.get("isChecked");


%>
<html>
<head>
	<title>Login JSP</title>
</head>
<body>
<h1>
	Login JSP
</h1>
<form action="./login" method="post">
    <label for="username">ID:</label>
    <input type="text" id="id" name="id" value="${id}" required>
    <br>
    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
    <br>
	<input type="checkbox" id="rememberId" name="rememberId" ${isChecked}>
	<label for="rememberId">아이디 저장</label>
	<br>
    <input type="submit">
</form>
<h1> errorMessage: ${errorMessage} </h1>
<h1> cookie id: ${id} </h1>
</body>
</html>
