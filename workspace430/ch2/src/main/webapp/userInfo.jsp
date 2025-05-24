<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String id = request.getParameter("id");
	String password = request.getParameter("password");
	request.setAttribute("id", id);
	request.setAttribute("password", password);
%>

<html>
<head>
	<title>User Info JSP</title>
</head>
<body>
	<h1>
		User Info JSP
	</h1>
	<h1>
	    ID: ${id}
	</h1>
	<h1>
    	Password: ${password}
	</h1>
	<h1><a href="login.jsp">로그인 페이지로 이동</a></h1>
</body>
</html>
