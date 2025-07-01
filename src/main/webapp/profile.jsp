<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList" %>
<%@ page import="model.media.Post, model.story.Story, model.User" %>
<%@ page import="data.story.StoryDAO, data.story.TagsDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="data.media.PostDAO" %>
<%@ page import="model.media.Post" %>

<%!
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
        body {
            font-family: 'Poppins', sans-serif;
            color: #E5E7EB;
        }
        .font-orbitron {
            font-family: 'Orbitron', sans-serif;
        }
        .gradient-text {
            background-image: linear-gradient(to right, #a78bfa, #f472b6);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }
        @keyframes spin {
            to { --angle: 360deg; }
        }
        @property --angle {
            syntax: '<angle>';
            initial-value: 0deg;
            inherits: false;
        }
        .animated-gradient-border {
            --angle: 0deg;
            border: 4px solid;
            border-image: conic-gradient(from var(--angle), #a855f7, #ec4899, #6366f1, #a855f7) 1;
            animation: spin 5s linear infinite;
        }
    </style>
</head>
<body class="bg-black">

<%
    User profileUser = (User) request.getAttribute("profileUser");
    if (profileUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String username = profileUser.getUsername();
%>

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <header class="mb-8">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-6">
            <div class="flex items-center space-x-6">
                <%
                    String profilePicturePath = request.getContextPath() + "/images/profiles/" + profileUser.getImageName();
                %>
                <img src="<%= profilePicturePath %>" alt="Profile Picture" class="w-24 h-24 rounded-full object-cover object-center">
                <div>
                    <h1 class="text-4xl font-bold font-orbitron text-white">
                        <span class="gradient-text"><%= username %></span>
                    </h1>
                    <p class="text-gray-400 mt-2">Manage your creator content and view your history.</p>
                </div>
            </div>

            <div class="flex items-center space-x-4">
                <a href="<%= request.getContextPath() %>/settings" title="Edit Profile Settings" class="text-gray-400 hover:text-white transition-transform duration-300 hover:scale-110">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-7">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M10.343 3.94c.09-.542.56-.94 1.11-.94h1.093c.55 0 1.02.398 1.11.94l.149.894c.07.424.384.764.78.93.398.164.855.142 1.205-.108l.737-.527a1.125 1.125 0 0 1 1.45.12l.773.774c.39.389.44 1.002.12 1.45l-.527.737c-.25.35-.272.806-.107 1.204.165.397.505.71.93.78l.893.15c.543.09.94.559.94 1.109v1.094c0 .55-.397 1.02-.94 1.11l-.894.149c-.424.07-.764.383-.929.78-.165.398-.143.854.107 1.204l.527.738c.32.447.269 1.06-.12 1.45l-.774.773a1.125 1.125 0 0 1-1.449.12l-.738-.527c-.35-.25-.806-.272-1.203-.107-.398.165-.71.505-.781.929l-.149.894c-.09.542-.56.94-1.11.94h-1.094c-.55 0-1.019-.398-1.11-.94l-.148-.894c-.071-.424-.384-.764-.781-.93-.398-.164-.854-.142-1.204.108l-.738.527c-.447.32-1.06.269-1.45-.12l-.773-.774a1.125 1.125 0 0 1-.12-1.45l.527-.737c.25-.35.272-.806.108-1.204-.165-.397-.506-.71-.93-.78l-.894-.15c-.542-.09-.94-.56-.94-1.109v-1.094c0-.55.398-1.02.94-1.11l.894-.149c.424-.07.765-.383.93-.78.165-.398.143-.854-.108-1.204l-.526-.738a1.125 1.125 0 0 1 .12-1.45l.773-.773a1.125 1.125 0 0 1 1.45-.12l.737.527c.35.25.807.272 1.204.107.397-.165.71-.505.78-.929l.15-.894Z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                    </svg>
                </a>
                <a href="home.jsp" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:shadow-purple-500/50 transition-all duration-300 transform hover:scale-105">
                    ← Back to Home
                </a>
            </div>
        </div>
    </header>

    <%
        List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
        List<User> followersList = (List<User>) request.getAttribute("followersList");
        List<User> followingList = (List<User>) request.getAttribute("followingList");
        int followerCount = (followersList != null) ? followersList.size() : 0;
        int followingCount = (followingList != null) ? followingList.size() : 0;
    %>
    <div class="flex space-x-8 mb-12 ml-2">
        <a href="<%= request.getContextPath() %>/followList?userId=<%= profileUser.getUserId() %>&type=followers" class="text-center block hover:text-purple-300 transition-colors">
            <span class="font-bold text-2xl text-white"><%= followerCount %></span>
            <span class="block text-sm text-gray-400">Followers</span>
        </a>
        <a href="<%= request.getContextPath() %>/followList?userId=<%= profileUser.getUserId() %>&type=following" class="text-center block hover:text-purple-300 transition-colors">
            <span class="font-bold text-2xl text-white"><%= followingCount %></span>
            <span class="block text-sm text-gray-400">Following</span>
        </a>
    </div>

    <main>
        <section id="published-posts">
            <h2 class="text-3xl font-semibold gradient-text pb-2 mb-6 border-b-2 border-gray-800">Your Published Posts</h2>
            <% if (userPosts != null && !userPosts.isEmpty()) {
                StoryDAO storyDAO = (StoryDAO) application.getAttribute("storyDao");
                TagsDAO tagsDAO = (TagsDAO) application.getAttribute("tagDao"); %>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <% for (Post post : userPosts) {
                    Story story = storyDAO.getStory(post.getStoryId());
                    List<String> storyTags = (story != null) ? tagsDAO.getStoryTags(story.getStoryId()) : new ArrayList<>();
                    String title = (story != null) ? story.getTitle() : "Untitled Story";
                    String prompt = (story != null) ? story.getPrompt() : "No description.";
                    String imageUrl = request.getContextPath() + "/images/posts/" + post.getImageName();
                    int storyId = (story != null) ? story.getStoryId() : -1;
                %>
                <div class="relative bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg shadow-lg overflow-hidden h-full transform transition-all duration-300 hover:-translate-y-2 hover:shadow-purple-500/40 group">
                    <a href="post.jsp?id=<%= storyId %>" class="block h-full">
                        <img src="<%= imageUrl %>" alt="Post art" class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105">
                        <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 12rem);">
                            <div>
                                <h3 class="font-bold text-lg text-white mb-2 truncate" title="<%= title %>"><%= title %></h3>
                                <p class="text-gray-300 text-sm h-20 overflow-hidden"><%= truncate(prompt, 90) %></p>
                            </div>
                            <div class="mt-4 text-xs text-indigo-300">
                                <% if (!storyTags.isEmpty()) { %><%= String.join(" · ", storyTags) %><% } else { %>No Tags<% } %>
                            </div>
                        </div>
                    </a>
                    <form action="<%= request.getContextPath() %>/profile" method="post" class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300" onsubmit="return confirm('Are you sure you want to delete this post?');">
                        <input type="hidden" name="action" value="deletePost">
                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                        <button type="submit" class="bg-red-600/80 hover:bg-red-600 p-2 rounded-full transition-all duration-200 transform hover:scale-110" title="Delete post">
                            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                        </button>
                    </form>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="bg-gray-800/50 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">No Posts Yet</p><p>You haven't published any posts. Start creating a story to share your work!</p>
            </div>
            <% } %>
        </section>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-12 mt-12">
            <section id="reading-history">
                <h2 class="text-3xl font-semibold gradient-text pb-2 mb-6 border-b-2 border-gray-800">Reading History</h2>
                <% List<Story> readingHistory = (List<Story>) request.getAttribute("readingHistory");
                    if (readingHistory != null && !readingHistory.isEmpty()) { %>
                <div class="space-y-3">
                    <% for (Story story : readingHistory) { %>
                    <div class="group flex items-center justify-between bg-white/10 p-3 rounded-lg hover:bg-white/20 transition-colors duration-300">
                        <a href="post.jsp?id=<%= story.getStoryId() %>" class="font-semibold text-white group-hover:text-purple-300 flex-grow"><%= story.getTitle() %></a>
                        <form action="<%= request.getContextPath() %>/profile" method="post" class="ml-4 opacity-0 group-hover:opacity-100 transition-opacity" onsubmit="return confirm('Are you sure you want to remove this item from your reading history?');">
                            <input type="hidden" name="action" value="deleteHistory">
                            <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                            <button type="submit" class="bg-red-600/80 hover:bg-red-600 p-2 rounded-full transform hover:scale-110" title="Remove">
                                <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                            </button>
                        </form>
                    </div>
                    <% } %>
                </div>
                <% } else { %>
                <div class="bg-gray-800/50 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                    <p class="font-bold">History is Empty</p><p>You haven't read any stories yet. Fun awaits!</p>
                </div>
                <% } %>
            </section>

            <section id="bookmarked-stories">
                <h2 class="text-3xl font-semibold gradient-text pb-2 mb-6 border-b-2 border-gray-800">Bookmarks</h2>
                <% List<Story> bookmarkedStories = (List<Story>) request.getAttribute("bookmarkedStories");
                    if (bookmarkedStories != null && !bookmarkedStories.isEmpty()) { %>
                <div class="space-y-3">
                    <% PostDAO postDao = (PostDAO) application.getAttribute("postDao");
                        for (Story story : bookmarkedStories) {
                            Post coverPost = postDao.getFirstPostForStory(story.getStoryId());
                            String linkUrl = (coverPost != null) ? request.getContextPath() + "/post.jsp?id=" + coverPost.getPostId() : "#";
                    %>
                    <div class="group flex items-center justify-between bg-white/10 p-3 rounded-lg hover:bg-white/20 transition-colors duration-300">
                        <a href="<%= linkUrl %>" class="font-semibold text-white group-hover:text-purple-300 flex-grow"><%= story.getTitle() %></a>
                        <form action="<%= request.getContextPath() %>/profile" method="post" class="ml-4 opacity-0 group-hover:opacity-100 transition-opacity" onsubmit="return confirm('Are you sure you want to remove this bookmark?');">
                            <input type="hidden" name="action" value="deleteBookmark">
                            <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                            <button type="submit" class="bg-yellow-600/80 hover:bg-yellow-500 p-2 rounded-full transform hover:scale-110" title="Remove bookmark">
                                <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20"><path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-2.5L5 18V4z"></path></svg>
                            </button>
                        </form>
                    </div>
                    <% } %>
                </div>
                <% } else { %>
                <div class="bg-gray-800/50 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                    <p class="font-bold">No Bookmarks Yet</p><p>Find a story you like and save it for later!</p>
                </div>
                <% } %>
            </section>
        </div>
    </main>
</div>
</body>
</html>