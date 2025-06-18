<!-- Very basic register page -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>Create a new account</h1>
<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
<div style="color: red;"><%= error %></div>
<%
    }
%>
<form action="register" method="post">
    <label for="username">Username:</label>
    <input id="username" name="username" type="text" placeholder="username" /> <br><br>
    <label for="age">Age:</label>
    <input id="age" name="age" type="number" placeholder="age" required /> <br><br>
    <label for="password">Password:</label>
    <input id="password" name="password" type="password" placeholder="password" /> <br><br>
    <label for="confirm_password">Confirm Password:</label>
    <input id="confirm_password" name="confirm_password" type="password" placeholder="confirm password" required />
    <button type="submit">Register</button>
</form>
<p>Already have an account? <a href="login">Log in</a></p>
</body>
</html>