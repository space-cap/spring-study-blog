<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="loginUrl" value="${sessionScope.id == null ? '/ch2/login/login' : '/ch2/login/logout'}"/>
<c:set var="loginText" value="${sessionScope.id == null ? 'login' : 'logout'}"/>
<div id="menu">
    <ul>
        <li id="logo">fastcampus</li>
        <li><a href="<c:url value='/'/>">Home</a></li>
        <li><a href="<c:url value='/board/list'/>">Board</a></li>
        <li>
            <a href="${loginUrl}">${loginText}</a>
        </li>
        <c:if test="${sessionScope.id == null}">
            <li><a href="<c:url value='/register/add'/>">Sign in</a></li>
        </c:if>
        <li><a href=""><i class="fas fa-search small"></i></a></li>
    </ul>
</div>
