<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList, model.story.Story" %>
<%@ page import="data.story.TagsDAO, data.media.PostDAO, model.media.Post" %>
<%@ page import="data.user.UserDAO, model.User, java.lang.String" %>
<%@ page import="java.io.File" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home - Your AI Story Universe</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .story-card { display: none; }
        .fade-in { animation: fadeIn 0.5s ease-in-out forwards; }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body class="bg-gray-900 text-gray-200 font-sans">

<%
    String username = (String) session.getAttribute("user");
    String searchQuery = request.getParameter("query");
    String searchType = request.getParameter("type");
%>

<nav class="bg-gray-800/50 backdrop-blur-sm sticky top-0 z-50 shadow-lg">
    <div class="container mx-auto px-4 md:px-8">
        <div class="flex items-center justify-between h-16">
            <a href="/home" class="text-2xl font-bold text-white hover:text-indigo-400 transition-colors">
                StorySaga AI
            </a>
            <div class="flex items-center space-x-4">
                <% if (username != null) { %>
                <%
                    UserDAO userDao = (UserDAO) application.getAttribute("userDao");
                    User loggedInUser = null;
                    if (userDao != null) {
                        try {
                            loggedInUser = userDao.findUser(username);
                        } catch (Exception e) {
                            System.err.println("Could not fetch user details for nav bar: " + e.getMessage());
                        }
                    }
                %>
                <a href="/create-post.jsp" class="hidden sm:inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-md transition duration-300">+ Create Story</a>
                <a href="/profile" class="flex items-center space-x-2 text-gray-300 hover:text-white font-medium">
                    <%
                        String profilePicUrl;
                        if (loggedInUser != null && loggedInUser.getImageName() != null && !loggedInUser.getImageName().isEmpty()) {
                            profilePicUrl = request.getContextPath() + "/images/profiles/" + loggedInUser.getImageName();
                        } else {
                            profilePicUrl = "https://placehold.co/40x40/4F46E5/FFFFFF?text=" + username.toUpperCase().charAt(0);
                        }
                    %>
                    <img src="<%= profilePicUrl %>" alt="Profile Picture" class="h-8 w-8 rounded-full object-cover border-2 border-gray-600">
                    <span><%= username %></span>
                </a>
                <a href="/logout" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-md transition duration-300">Logout</a>
                <% } else { %>
                <a href="/login.jsp" class="text-gray-300 hover:text-white font-medium transition duration-300">Login</a>
                <a href="/register.jsp" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-md transition duration-300">Sign Up</a>
                <% } %>
            </div>
        </div>
    </div>
</nav>

<div class="container mx-auto p-4 md:p-8">

    <header class="text-center my-8">
        <h1 class="text-4xl md:text-5xl font-bold text-white">Explore Sagas</h1>
        <p class="text-lg text-gray-400 mt-2">Discover adventures crafted by creators and AI.</p>
    </header>

    <div class="max-w-2xl mx-auto mb-12">
        <form action="/search" method="GET" class="flex flex-col sm:flex-row gap-2">
            <select name="type" class="bg-gray-700 border border-gray-600 text-white rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                <option value="title" <%= "title".equals(searchType) ? "selected" : "" %>>Title</option>
                <option value="creator" <%= "creator".equals(searchType) ? "selected" : "" %>>Creator</option>
                <option value="tag" <%= "tag".equals(searchType) ? "selected" : "" %>>Tag</option>
            </select>
            <input type="text" name="query" class="flex-grow w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-4 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500" placeholder="Search for stories..." value="<%= (searchQuery != null) ? searchQuery : "" %>">
            <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-6 rounded-md transition duration-300">Search</button>
        </form>
    </div>

    <%!
        String truncate(String text, int length) {
            if (text == null || text.length() <= length) return text;
            return text.substring(0, length) + "...";
        }
    %>
    <%
        List<Story> stories = (List<Story>) request.getAttribute("stories");
        if (stories == null) {
            stories = new ArrayList<>();
        }
        TagsDAO tagsDao = (TagsDAO) application.getAttribute("tagDao");
        PostDAO postDao = (PostDAO) application.getAttribute("postDao");
    %>

    <main>
        <div id="stories-grid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <% for (Story story : stories) { %>
            <%
                List<String> storyTags = new ArrayList<>();
                if (tagsDao != null) {
                    try {
                        storyTags = tagsDao.getStoryTags(story.getStoryId());
                    } catch (Exception e) {
                        System.err.println("Could not fetch tags for story " + story.getStoryId() + ": " + e.getMessage());
                    }
                }
                Post coverPost = null;
                if (postDao != null) {
                    try {
                        coverPost = postDao.getFirstPostForStory(story.getStoryId());
                    } catch (Exception e) {
                        System.err.println("Could not fetch cover post for story " + story.getStoryId() + ": " + e.getMessage());
                    }
                }
                String imageUrl = (coverPost != null && coverPost.getImageName() != null)
                        ? request.getContextPath() + "/images/posts/" + coverPost.getImageName()
                        : "https://placehold.co/600x400/111827/374151?text=Saga";
            %>
            <div class="story-card">
                <a href="/story.jsp?id=<%= story.getStoryId() %>" class="block bg-gray-800 rounded-lg shadow-xl overflow-hidden h-full transform transition-transform duration-300 hover:-translate-y-2">
                    <img src="<%= imageUrl %>" alt="Story Art for <%= story.getTitle() %>" class="w-full h-40 object-cover">
                    <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 10rem);">
                        <div>
                            <h3 class="font-bold text-lg text-white mb-2 truncate" title="<%= story.getTitle() %>">
                                <%= story.getTitle() %>
                            </h3>
                            <p class="text-gray-400 text-sm h-20 overflow-hidden">
                                <%= truncate(story.getPrompt(), 120) %>
                            </p>
                        </div>
                        <div class="mt-4 text-xs text-indigo-400">
                            <% if (!storyTags.isEmpty()) { %>
                            <%= String.join(" · ", storyTags) %>
                            <% } else { %>
                            No Tags
                            <% } %>
                        </div>
                    </div>
                </a>
            </div>
            <% } %>
        </div>

        <% if (stories.isEmpty()) { %>
        <div class="text-center py-20">
            <% if (searchQuery != null && !searchQuery.trim().isEmpty()) { %>
            <p class="text-gray-500">No stories found matching your search for "<%= searchQuery %>".</p>
            <a href="/home" class="mt-4 inline-block text-indigo-400 hover:text-indigo-300">Clear Search</a>
            <% } else { %>
            <p class="text-gray-500">Search for a story, or create your own!</p>
            <% if (username != null) { %>
            <a href="/create-post.jsp" class="mt-4 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded">Create a Story</a>
            <% } else { %>
            <a href="/login.jsp" class="mt-4 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded">Login to Create</a>
            <% } %>
            <% } %>
        </div>
        <% } %>

        <div id="show-more-container" class="text-center mt-12">
            <button id="show-more-btn" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-8 rounded-lg transition duration-300">
                Show More
            </button>
        </div>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const grid = document.getElementById('stories-grid');
        const showMoreBtn = document.getElementById('show-more-btn');
        const showMoreContainer = document.getElementById('show-more-container');
        const allCards = Array.from(grid.querySelectorAll('.story-card'));
        const itemsPerPage = 8;
        let currentlyVisible = 0;

        function showNextItems() {
            const nextLimit = currentlyVisible + itemsPerPage;
            for (let i = currentlyVisible; i < nextLimit && i < allCards.length; i++) {
                setTimeout(() => {
                    allCards[i].style.display = 'block';
                    allCards[i].classList.add('fade-in');
                }, (i - currentlyVisible) * 50);
            }
            currentlyVisible = nextLimit;
            if (currentlyVisible >= allCards.length) {
                showMoreContainer.style.display = 'none';
            }
        }

        if (allCards.length > 0) {
            showNextItems();
            showMoreBtn.addEventListener('click', showNextItems);
        } else {
            showMoreContainer.style.display = 'none';
        }
    });
</script>

</body>
</html>