<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management - Admin Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; color: #E5E7EB; }
        .font-orbitron { font-family: 'Orbitron', sans-serif; }
        .gradient-text { background-image: linear-gradient(to right, #a78bfa, #f472b6); -webkit-background-clip: text; background-clip: text; color: transparent; }
        .nav-link.active {
            background-color: #9333ea;
            color: #ffffff;
            box-shadow: 0 4px 14px 0 rgba(147, 51, 234, 0.39);
        }
    </style>
</head>
<body class="bg-black">

<%
    String loggedInUser = (String) session.getAttribute("user");
    if (loggedInUser == null) { loggedInUser = "Admin"; }

    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer adminCount = (Integer) request.getAttribute("adminCount");
    Integer creatorCount = (Integer) request.getAttribute("creatorCount");
    if (totalUsers == null) totalUsers = 0;
    if (adminCount == null) adminCount = 0;
    if (creatorCount == null) creatorCount = 0;

    String message = request.getParameter("message");
    String error = request.getParameter("error");
    List<User> recentUsers = (List<User>) request.getAttribute("recentUsers");
    Integer currentLimit = (Integer) request.getAttribute("currentLimit");
    if (currentLimit == null) currentLimit = 5;
%>

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <header class="mb-8">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-6">
            <div class="flex items-center space-x-6">
                <div class="w-24 h-24 rounded-full bg-gray-800 flex items-center justify-center border-2 border-purple-500">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-12 text-purple-400"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.286Zm0 13.036h.008v.008h-.008v-.008Z" /></svg>
                </div>
                <div>
                    <h1 class="text-4xl font-bold font-orbitron text-white"><span class="gradient-text">Admin Dashboard</span></h1>
                    <p class="text-gray-400 mt-2">Site management panel. Welcome, <%= loggedInUser %>!</p>
                </div>
            </div>
            <div class="flex items-center space-x-4">
                <a href="<%= request.getContextPath() %>/home.jsp" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:shadow-purple-500/50 transition-all duration-300 transform hover:scale-105">
                    ← Back to Site
                </a>
            </div>
        </div>
    </header>

    <main>
        <div class="mb-8 flex space-x-2 border-b-2 border-gray-800 pb-2">
            <a href="<%= request.getContextPath() %>/admin/dashboard.jsp" class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50 active">User Management</a>
            <a href="<%= request.getContextPath() %>/admin/data_management.jsp" class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50">Data Management</a>
        </div>

        <% if (message != null) { %><div id="success-alert" class="relative flex items-center justify-between bg-green-800/50 border-l-4 border-green-400 text-white p-4 rounded-md mb-6" role="alert"><p><%= message %></p><button type="button" class="dismiss-btn" data-dismiss-target="#success-alert" aria-label="Close"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg></button></div><% } %>
        <% if (error != null) { %><div id="error-alert" class="relative flex items-center justify-between bg-red-800/50 border-l-4 border-red-400 text-white p-4 rounded-md mb-6" role="alert"><p><%= error %></p><button type="button" class="dismiss-btn" data-dismiss-target="#error-alert" aria-label="Close"><svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg></button></div><% } %>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="bg-white/10 p-6 rounded-lg border-l-4 border-purple-500"><p class="text-sm font-medium text-gray-400 uppercase">Total Users</p><p class="mt-1 text-4xl font-semibold text-white"><%= totalUsers %></p></div>
            <div class="bg-white/10 p-6 rounded-lg border-l-4 border-pink-500"><p class="text-sm font-medium text-gray-400 uppercase">Admins</p><p class="mt-1 text-4xl font-semibold text-white"><%= adminCount %></p></div>
            <div class="bg-white/10 p-6 rounded-lg border-l-4 border-teal-500"><p class="text-sm font-medium text-gray-400 uppercase">Creators</p><p class="mt-1 text-4xl font-semibold text-white"><%= creatorCount %></p></div>
        </div>

        <div class="bg-white/5 p-6 rounded-lg border border-white/20 mb-8">
            <h3 class="text-xl font-semibold text-white mb-4">User Actions</h3>
            <form action="<%= request.getContextPath() %>/dashboard" method="POST" class="flex flex-col sm:flex-row items-center gap-4">
                <div class="w-full sm:w-1/2">
                    <label for="username" class="sr-only">Username</label>
                    <input type="text" name="username" id="username" class="bg-gray-900/50 border border-gray-600 text-white text-sm rounded-lg focus:ring-purple-500 focus:border-purple-500 block w-full p-2.5" placeholder="Enter username..." required>
                </div>
                <div class="flex gap-4">
                    <button type="submit" name="action" value="toggleAdmin" class="text-white bg-purple-600 hover:bg-purple-700 focus:ring-4 focus:outline-none focus:ring-purple-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center transition-colors">Make/Remove Admin</button>
                    <button type="submit" name="action" value="deleteUser" class="text-white bg-red-600 hover:bg-red-700 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center transition-colors">Delete User</button>
                </div>
            </form>
        </div>

        <div class="mb-8">
            <div class="flex flex-col sm:flex-row justify-between items-center mb-4">
                <h3 class="text-xl font-semibold text-white">Recently Joined Users</h3>

                <form action="<%= request.getContextPath() %>/dashboard" method="GET" class="flex items-center gap-2 mt-4 sm:mt-0">
                    <label for="limit" class="text-sm text-gray-400">Show:</label>
                    <input type="number" name="limit" id="limit"
                           value="<%= currentLimit %>"
                           class="w-20 bg-gray-900/50 border border-gray-600 text-white text-sm rounded-lg focus:ring-purple-500 focus:border-purple-500 p-2"
                           min="1" max="50">
                    <button type="submit" class="text-white bg-blue-600 hover:bg-blue-700 font-medium rounded-lg text-sm px-4 py-2 text-center transition-colors">Update</button>
                </form>
            </div>

            <% if (recentUsers != null && !recentUsers.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy"); %>

            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
                <% for (User user : recentUsers) { %>

                <a href="<%= request.getContextPath() %>/user?username=<%= user.getUsername() %>"
                   class="block text-center bg-gray-800/70 backdrop-blur-sm rounded-lg shadow-xl p-6 transform transition-transform duration-300 hover:-translate-y-2"
                   title="View <%= user.getUsername() %>'s public profile">

                    <img src="<%= request.getContextPath() %>/images/profiles/<%= user.getImageName() %>"
                         alt="Profile of <%= user.getUsername() %>"
                         class="w-32 h-32 rounded-full mx-auto mb-4 border-4 border-gray-700 object-cover">

                    <h3 class="font-bold text-xl text-white truncate"><%= user.getUsername() %></h3>

                    <p class="text-xs text-gray-400 mt-1">
                        Joined <%= sdf.format(java.sql.Timestamp.valueOf(user.getRegisterTime())) %>
                    </p>
                </a>
                <% } %>
            </div>

            <% } else { %>
            <div class="bg-gray-800/50 border-l-4 border-gray-500 text-gray-300 p-4 rounded-md">
                <p>No recent user registrations to display.</p>
            </div>
            <% } %>
        </div>

    </main>
</div>

<script>
    const dismissButtons = document.querySelectorAll('.dismiss-btn');
    dismissButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-dismiss-target');
            const alertToDismiss = document.querySelector(targetId);
            if (alertToDismiss) {
                alertToDismiss.style.display = 'none';
            }
        });
    });
</script>

</body>
</html>