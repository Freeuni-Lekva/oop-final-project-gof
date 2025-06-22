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
                <div class="relative bg-gray-800 rounded-lg shadow-xl overflow-hidden h-full transform transition-transform duration-300 hover:-translate-y-2">
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

                    <form action="<%= request.getContextPath() %>/profile" method="post" class="absolute top-2 right-2">
                        <input type="hidden" name="action" value="deletePost">
                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                        <button type="submit" class="bg-red-600/70 hover:bg-red-600 p-2 rounded-full transition-colors duration-200" title="Delete this post">
                            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                        </button>
                    </form>
                </div>
                <%
                    }
                %>
            </div>
            <%
            } else {
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">No Posts Yet</p><p>You haven't published any posts. Start creating a story to share your work!</p>
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
                    for (Story story : readingHistory) {
                %>
                <div class="flex items-center justify-between bg-gray-800 p-4 rounded-lg hover:bg-gray-700 transition-colors duration-300">
                    <a href="story.jsp?id=<%= story.getStoryId() %>" class="font-semibold text-lg text-white hover:text-purple-300 flex-grow">
                        <%= story.getTitle() %>
                    </a>

                    <form action="<%= request.getContextPath() %>/profile" method="post" class="ml-4">
                        <input type="hidden" name="action" value="deleteHistory">
                        <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                        <button type="submit" class="bg-red-600/70 hover:bg-red-600 p-2 rounded-full transition-colors duration-200" title="Remove from history">
                            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                        </button>
                    </form>
                </div>
                <%
                    }
                %>
            </div>
            <%
            } else {
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">History is Empty</p><p>You haven't read any stories yet. Fun awaits!</p>
            </div>
            <%
                }
            %>
        </section>

        <section id="bookmarked-stories" class="mt-12">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Bookmarked Stories
            </h2>
            <%
                List<Story> bookmarkedStories = (List<Story>) request.getAttribute("bookmarkedStories");
                if (bookmarkedStories != null && !bookmarkedStories.isEmpty()) {
            %>
            <div class="space-y-4">
                <%
                    for (Story story : bookmarkedStories) {
                %>
                <div class="flex items-center justify-between bg-gray-800 p-4 rounded-lg hover:bg-gray-700 transition-colors duration-300">
                    <a href="story.jsp?id=<%= story.getStoryId() %>" class="font-semibold text-lg text-white hover:text-purple-300 flex-grow">
                        <%= story.getTitle() %>
                    </a>

                    <form action="<%= request.getContextPath() %>/profile" method="post" class="ml-4">
                        <input type="hidden" name="action" value="deleteBookmark">
                        <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                        <button type="submit" class="bg-yellow-600/70 hover:bg-yellow-600 p-2 rounded-full transition-colors duration-200" title="Remove bookmark">
                            <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-2.5L5 18V4z"></path></svg>
                        </button>
                    </form>
                </div>
                <%
                    }
                %>
            </div>
            <%
            } else {
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">No Bookmarks Yet</p><p>You haven't bookmarked any stories. Find one you like and save it for later!</p>
            </div>
            <%
                }
            %>
        </section>
    </main>
</div>
</body>
</html>