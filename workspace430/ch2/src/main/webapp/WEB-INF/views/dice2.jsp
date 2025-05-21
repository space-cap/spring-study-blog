<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<html>
<head>
	<title>Dice 2</title>
</head>
<body>
<h1>
	<c:forEach var="dice" items="${diceResults}" varStatus="status">
	        주사위 ${status.index + 1} : ${dice} <br>
	    </c:forEach> 
</h1>

</body>
</html>
