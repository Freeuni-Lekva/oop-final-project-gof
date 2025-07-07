<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Data Management - Admin Dashboard</title>
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
            <a href="<%= request.getContextPath() %>/admin/dashboard.jsp"
               class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50">
                User Management
            </a>
            <a href="<%= request.getContextPath() %>/admin/data_management.jsp"
               class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50 active">
                Data Management
            </a>
        </div>

        <div id="data-content">
            <div class="bg-gray-800/50 border-l-4 border-pink-500 text-gray-300 p-4 rounded-md">
                <p class="font-bold">Data Management Area</p>

            </div>
        </div>
    </main>
</div>
</body>
</html>