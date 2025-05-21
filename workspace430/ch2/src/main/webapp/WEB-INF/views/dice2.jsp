<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    java.util.List diceResults = (java.util.List) request.getAttribute("diceResults");
    int dice1 = (Integer) diceResults.get(0);
    int dice2 = (Integer) diceResults.get(1);
%>

<html>
<head>
	<title>Dice 2</title>
</head>
<body>
<h1>
	주사위 1 : ${dice1} <br>
	주사위 2 : ${dice2} <br>  
</h1>

</body>
</html>
