<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page session="true" %>
<html>
<head>
    <title>Home</title>
    <script src="https://cdn.tailwindcss.com"></script>
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
<a href="profile" class="text-purple-300 hover:text-pink transition-colors duration-300">Go To Profile</a>
<a href="postcreation" class="text-purple-300 hover:text-pink transition-colors duration-300"> CREATE A POST : 3 </a>

</body>
</html>
