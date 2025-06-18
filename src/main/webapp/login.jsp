<!-- Very basic login page -->

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Welcome to GOF project</h1>
<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
<div style="color: red;"><%= error %></div>
<%
    }
%>
<h3>Log in.</h3>
<form action="login" method="post">
    <label for="username">Username:</label>
    <input id="username" name="username" type="text" placeholder="username" required /> <br><br>
    <label for="password">Password:</label>
    <input id="password" name="password" type="password" placeholder="password" required /> <br><br>
    <button type="submit">Login</button>
</form>
<p><a href="register">Create a new account</a></p>
</body>
</html>