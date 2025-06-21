<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Your Profile - StoryAI</title>
    <%-- Copied from login.jsp for styling --%>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; background-color: #111827; color: #E5E7EB; }
        .font-orbitron { font-family: 'Orbitron', sans-serif; }
    </style>
</head>
<body class="bg-gray-900">

<%
    // Ensure user is logged in
    String username = (String) session.getAttribute("user");
    if (username == null) {
        response.sendRedirect("login.jsp");
        return; // Important to stop further processing of the page
    }
%>

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <header class="mb-10">
        <div class="flex justify-between items-center">
            <h1 class="text-4xl font-bold font-orbitron text-white">
                Profile: <span class="text-purple-400"><%= username %></span>
            </h1>
            <a href="home.jsp" class="text-purple-400 hover:text-purple-300 transition-colors duration-300 font-semibold">
                ← Back to Home
            </a>
        </div>
        <p class="text-gray-400 mt-2">Manage your creator content and view your history.</p>
    </header>

    <main>
        <%-- Section for Published Posts --%>
        <section id="published-posts">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Published Posts
            </h2>
            <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                <%-- Placeholder for posts. We will populate this with data later. --%>
                <div class="bg-gray-800 p-4 rounded-lg text-center">
                    <p class="text-gray-400">Post 1 will be here</p>
                </div>
                <div class="bg-gray-800 p-4 rounded-lg text-center">
                    <p class="text-gray-400">Post 2 will be here</p>
                </div>
            </div>
            <%-- Message to show if user has no posts --%>
            <%-- <p class="text-gray-500">You haven't published any posts yet.</p> --%>
        </section>

        <%-- Section for Reading History --%>
        <section id="reading-history" class="mt-12">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Reading History
            </h2>
            <div class="space-y-4">
                <%-- Placeholder for read stories. We will populate this later. --%>
                <div class="bg-gray-800 p-4 rounded-lg">
                    <a href="#" class="font-semibold text-lg text-white hover:text-purple-400">
                        Placeholder: The Mystery of the Lost Artifact
                    </a>
                </div>
                <div class="bg-gray-800 p-4 rounded-lg">
                    <a href="#" class="font-semibold text-lg text-white hover:text-purple-400">
                        Placeholder: Adventures in the Digital Age
                    </a>
                </div>
            </div>
            <%-- Message to show if user has no history --%>
            <%-- <p class="text-gray-500">You haven't read any stories yet.</p> --%>
        </section>

    </main>

</div>

</body>
</html>