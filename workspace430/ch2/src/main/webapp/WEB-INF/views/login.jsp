<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
	<title>Login</title>
</head>
<body>
<h1>
	Login  
</h1>

<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="username">ID:</label>
    <input type="text" id="id" name="id" required>
    <br>
    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
    <br>
    <input type="submit" value="Login">
</form>

</body>
</html>
