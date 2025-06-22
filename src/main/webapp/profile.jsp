<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList" %>
<%@ page import="model.media.Post, model.story.Story" %>
<%@ page import="data.story.StoryDAO, data.story.TagsDAO" %>

<%!
    // Helper function to shorten text, copied from home.jsp
    String truncate(String text, int length) {
        if (text == null || text.length() <= length) return text;
        return text.substring(0, length) + "...";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Your Profile - StoryAI</title>
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
    String username = (String) session.getAttribute("user");
    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
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
        <section id="published-posts">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Published Posts
            </h2>
            <%
                List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
                if (userPosts != null && !userPosts.isEmpty()) {
                    StoryDAO storyDAO = new StoryDAO();
                    TagsDAO tagsDAO = new TagsDAO();
            %>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <%
                    for (Post post : userPosts) {
                        Story story = storyDAO.getStory(post.getStoryId());
                        List<String> storyTags = new ArrayList<>();
                        if (story != null) { storyTags = tagsDAO.getStoryTags(story.getStoryId()); }
                        String title = (story != null) ? story.getTitle() : "Untitled Story";
                        String prompt = (story != null) ? story.getPrompt() : "No description available.";
                        String imageUrl = request.getContextPath() + "/images/posts/" + post.getImageName();
                        int storyId = (story != null) ? story.getStoryId() : -1;
                %>
                <div class="bg-gray-800 rounded-lg shadow-xl overflow-hidden h-full transform transition-transform duration-300 hover:-translate-y-2">
                    <a href="story.jsp?id=<%= storyId %>" class="block h-full">
                        <img src="<%= imageUrl %>" alt="Post art for <%= title %>" class="w-full h-40 object-cover">
                        <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 10rem);">
                            <div>
                                <h3 class="font-bold text-lg text-white mb-2 truncate" title="<%= title %>"><%= title %></h3>
                                <p class="text-gray-400 text-sm h-20 overflow-hidden"><%= truncate(prompt, 100) %></p>
                            </div>
                            <div class="mt-4 text-xs text-indigo-400">
                                <% if (!storyTags.isEmpty()) { %><%= String.join(" · ", storyTags) %><% } else { %>No Tags<% } %>
                            </div>
                        </div>
                    </a>
                </div>
                <%
                    }
                %>
            </div>
            <%
            } else {
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">No Posts Yet</p>
                <p>You haven't published any posts. Start creating a story to share your work!</p>
            </div>
            <%
                }
            %>
        </section>

        <section id="reading-history" class="mt-12">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Reading History
            </h2>
            <%

                List<Story> readingHistory = (List<Story>) request.getAttribute("readingHistory");

                if (readingHistory != null && !readingHistory.isEmpty()) {
            %>
            <div class="space-y-4">
                <%
                    //Loop through each story in the history
                    for (Story story : readingHistory) {
                %>
                <div class="bg-gray-800 p-4 rounded-lg hover:bg-gray-700 transition-colors duration-300">
                    <a href="story.jsp?id=<%= story.getStoryId() %>" class="font-semibold text-lg text-white hover:text-purple-300 block">
                        <%= story.getTitle() %>
                    </a>
                </div>
                <%
                    }
                %>
            </div>
            <%
            } else {
                //If the list is empty, show this message
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">History is Empty</p>
                <p>You haven't read any stories yet. Fun awaits!</p>
            </div>
            <%
                }
            %>
        </section>
    </main>
</div>
</body>
</html>