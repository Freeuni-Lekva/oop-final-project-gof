<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.story.Story, model.media.Post, model.User" %>
<%@ page import="data.story.StoryDAO, data.media.PostDAO, data.user.UserDAO" %>


<%
    String username = (String) session.getAttribute("user");
    int storyId = -1;


    try {
        storyId = Integer.parseInt(request.getParameter("id"));
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }


    StoryDAO storyDao = (StoryDAO) application.getAttribute("storyDao");
    PostDAO postDao = (PostDAO) application.getAttribute("postDao");
    UserDAO userDao = (UserDAO) application.getAttribute("userDao");

    Story story = null;
    Post post = null;
    User creator = null;

    if (storyDao != null && postDao != null && userDao != null) {
        try {
            story = storyDao.getStory(storyId);

            if (story != null) {
                post = postDao.getPostsByStoryId(story.getStoryId());
                creator = userDao.findUserById(story.getCreatorId());
            }
        } catch (Exception e) {
            System.err.println("Error fetching story details for ID " + storyId + ": " + e.getMessage());
            e.printStackTrace();
            story = null;
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title><%= (story != null ? story.getTitle() : "Story Not Found") %> - StorySaga AI</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body::before {
            content: '';
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: rgba(17, 24, 39, 0.7);
            z-index: -1;
        }
        .whitespace-pre-wrap {
            white-space: pre-wrap;
        }
    </style>
</head>
<body class="bg-gray-900 text-gray-200 font-sans bg-cover bg-center bg-fixed" style="background-image: url('<%= request.getContextPath() %>/images/design/img4.jpg');">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />


<div class="container mx-auto p-4 md:p-8 max-w-4xl">

    <% if (story != null && post != null) { %>
    <main class="bg-gray-800/80 backdrop-blur-sm rounded-lg shadow-2xl overflow-hidden">

        <%
            String imageUrl = request.getContextPath() + "/images/posts/" + post.getImageName();
        %>
        <img src="<%= imageUrl %>" alt="Story art for <%= story.getTitle() %>" class="w-full h-auto max-h-[60vh] object-cover bg-black">

        <div class="p-6 md:p-8">
            <header class="mb-6">
                <h1 class="text-4xl md:text-5xl font-bold text-white mb-2">
                    <%= story.getTitle() %>
                </h1>
                <p class="text-lg text-gray-400">
                    By <a href="/user?username=<%=creator.getUsername()%>" class="text-indigo-400 hover:underline"><%= creator != null ? creator.getUsername() : "Unknown Creator" %></a>
                </p>
            </header>

            <div class="story-content mb-8 border-t border-gray-700 pt-6">
                <p class="text-gray-300 text-lg leading-relaxed whitespace-pre-wrap"><%= story.getDescription() %></p>
            </div>

            <% if (username != null) { %>
            <div class="flex items-center gap-4 border-t border-gray-700 pt-6">

                <% boolean isBookmarked = (Boolean) request.getAttribute("isBookmarked"); %>
                <% if (isBookmarked) { %>
                <form action="<%= request.getContextPath() %>/post" method="POST" class="m-0">
                    <input type="hidden" name="action" value="unbookmark">
                    <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                    <button type="submit" class="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-md transition duration-300">
                        Remove from Bookmarks
                    </button>
                </form>
                <% } else { %>
                <form action="<%= request.getContextPath() %>/post" method="POST" class="m-0">
                    <input type="hidden" name="action" value="bookmark">
                    <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                    <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-md transition duration-300">
                        Add to Bookmarks
                    </button>
                </form>
                <% } %>

                <form action="<%= request.getContextPath() %>/post" method="POST" class="m-0">
                    <input type="hidden" name="action" value="start_story">
                    <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                    <button type="submit" class="bg-teal-600 hover:bg-teal-700 text-white font-bold py-2 px-4 rounded-md transition duration-300">
                        Start Story
                    </button>
                </form>

            </div>
            <% } else { %>
            <div class="border-t border-gray-700 pt-6 text-center">
                <a href="<%= request.getContextPath() %>/login.jsp?redirect=post.jsp?id=<%= storyId %>" class="text-indigo-400 hover:underline">Log in to bookmark or start this story.</a>
            </div>
            <% } %>
        </div>
    </main>

    <% } else { %>
    <div class="text-center py-20 bg-gray-800/50 rounded-lg">
        <h1 class="text-4xl font-bold text-white">Oops! Story Not Found</h1>
        <p class="text-gray-400 mt-4">The saga you're looking for doesn't exist or may have been removed.</p>
        <a href="<%= request.getContextPath() %>/home" class="mt-8 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-6 rounded-lg">
            Return to Home
        </a>
    </div>
    <% } %>

</div>

</body>
</html>