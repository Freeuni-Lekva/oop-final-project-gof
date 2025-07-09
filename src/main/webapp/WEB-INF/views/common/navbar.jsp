<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="data.user.UserDAO" %>

<%
    String username = (String) session.getAttribute("user");
    UserDAO userDao = (UserDAO) application.getAttribute("userDao");
%>

<nav class="bg-gray-800/50 backdrop-blur-sm sticky top-0 z-50 shadow-lg">
    <div class="container mx-auto px-4 md:px-8">
        <div class="flex items-center justify-between h-16">
            <a href="<%= request.getContextPath() %>/home" class="text-2xl font-bold text-white hover:text-indigo-400 transition-colors">
                StorySaga AI
            </a>
            <div class="flex items-center space-x-4">
                <% if (username != null) { %>
                <%
                    User loggedInUser = null;
                    if (userDao != null) {
                        try {
                            loggedInUser = userDao.findUser(username);
                        } catch (Exception e) {
                            System.err.println("Could not fetch user details for nav bar: " + e.getMessage());
                        }
                    }
                %>
                <a href="<%= request.getContextPath() %>/create-post" class="hidden sm:inline-block bg-teal-800 hover:bg-teal-900 text-gray-300 font-semibold py-2 px-4 rounded-md transition duration-300">+ Create Story</a>
                <a href="<%= request.getContextPath() %>/profile" class="flex items-center space-x-2 text-gray-300 hover:text-indigo-400 font-medium">
                    <%
                        String profilePicUrl;
                        if (loggedInUser != null && loggedInUser.getImageName() != null && !loggedInUser.getImageName().isEmpty()) {
                            profilePicUrl = request.getContextPath() + "/images/profiles/" + loggedInUser.getImageName();
                        } else {
                            profilePicUrl = "https://placehold.co/40x40/4F46E5/FFFFFF?text=" + username.toUpperCase().charAt(0);
                        }
                    %>
                    <img src="<%= profilePicUrl %>" alt="Profile Picture" class="h-8 w-8 rounded-full object-cover">
                    <span><%= username %></span>
                </a>
                <a href="<%= request.getContextPath() %>/logout" class="bg-teal-600 hover:bg-teal-700 text-gray-300 font-semibold py-2 px-4 rounded-md transition duration-300">Logout</a>
                <% } else { %>
                <a href="<%= request.getContextPath() %>/login" class="text-gray-300 hover:text-white font-medium transition duration-300">Login</a>
                <a href="<%= request.getContextPath() %>/register" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-md transition duration-300">Sign Up</a>
                <% } %>
            </div>
        </div>
    </div>
</nav>