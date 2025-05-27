<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginPath" value="${sessionScope.id == null ? 'login/login' : 'login/logout'}"/>
<c:set var="loginText" value="${sessionScope.id == null ? 'login' : 'logout'}"/>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <title>fastcampus</title>
    <link rel="stylesheet" href="<c:url value='/css/menu.css'/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.min.css"/>    
</head>
<body>
<jsp:include page="/WEB-INF/views/common/nav.jsp" />
<div style="text-align:center">
	<h1>This is HOME</h1>
	<h1>This is HOME</h1>
	<h1>This is HOME</h1>
</div>