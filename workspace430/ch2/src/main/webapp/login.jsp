<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    <input type="text" id="id" name="id" required>
    <br>
    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
    <br>
    <input type="submit">
</form>
</body>
</html>
