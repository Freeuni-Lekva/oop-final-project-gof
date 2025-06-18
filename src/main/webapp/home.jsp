<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page session="true" %>
<html>
<head>
    <title>Home</title>
</head>
<body>
<%
    String username = (String) session.getAttribute("user");
    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<h1>Successfully logged in, <%= username %>!</h1>
<a href="logout">Logout</a>
</body>
</html>
