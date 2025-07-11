<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList, model.story.Story" %>
<%@ page import="data.story.TagsDAO, data.media.PostDAO, model.media.Post" %>
<%@ page import="data.user.UserDAO, model.User" %>
<%@ page import="model.story.Tags" %>
<%@ page import="model.chat.SharedChat" %>

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

        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(17, 24, 39, 0.7);
            z-index: -1;
        }

    </style>
</head>
<body class="bg-gray-900 text-gray-200 font-sans bg-cover bg-center bg-fixed" style="background-image: url('<%= request.getContextPath() %>/images/design/img5.jpg');">

<%
    String username = (String) session.getAttribute("user");
    String searchQuery = request.getParameter("query");
    String searchType = request.getParameter("type");
%>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container mx-auto p-4 md:p-8">

    <header class="text-center my-8">
        <h1 class="text-4xl md:text-5xl font-bold text-white">Explore Sagas</h1>
        <p class="text-lg text-gray-400 mt-2">Discover adventures crafted by creators and AI.</p>
    </header>

    <div class="max-w-2xl mx-auto mb-12">
        <form id="search-form" action="<%= request.getContextPath() %>/search" method="GET" class="flex flex-col sm:flex-row gap-2">
            <select id="search-type-select" name="type" class="bg-gray-700 border border-gray-600 text-white rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="title" <%= "title".equals(searchType) ? "selected" : "" %>>Title</option>
                <option value="creator" <%= "creator".equals(searchType) ? "selected" : "" %>>Creator</option>
                <option value="tag" <%= "tag".equals(searchType) ? "selected" : "" %>>Tag</option>
            </select>
            <input id="search-query-input" type="text" name="query" class="flex-grow w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-4 text-white focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="Search for stories..." value="<%= (searchQuery != null) ? searchQuery : "" %>">
            <button type="submit" class="bg-blue-800 hover:bg-blue-900 text-gray-300 font-bold py-2 px-6 rounded-md transition duration-300">Search</button>
        </form>

        <div class="flex flex-wrap justify-center gap-2 mt-4">
            <button type="button" class="tag-button bg-gray-700 hover:bg-blue-600 text-white text-sm font-medium py-1 px-3 rounded-full transition-colors duration-200">
                All
            </button>
            <% for (String tag : Tags.getAllTags()) { %>
            <button type="button" class="tag-button bg-gray-700 hover:bg-blue-600 text-gray-300 text-sm font-medium py-1 px-3 rounded-full transition-colors duration-200">
                <%= tag %>
            </button>
            <% } %>
        </div>
    </div>


    <%!
        String truncate(String text, int length) {
            if (text == null || text.length() <= length) return text;
            return text.substring(0, length) + "...";
        }
    %>
    <%
        List<Story> stories = (List<Story>) request.getAttribute("stories");
        List<User> foundUsers = (List<User>) request.getAttribute("foundUsers");
        if (stories == null) {
            stories = new ArrayList<>();
        }
        TagsDAO tagsDao = (TagsDAO) application.getAttribute("tagDao");
        PostDAO postDao = (PostDAO) application.getAttribute("postDao");
    %>

    <main>
        <% if ("creator".equals(searchType) && foundUsers != null) { %>
        <div id="users-grid" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
            <% for (User user : foundUsers) { %>
            <div class="user-card">
                <%
                    String profileUrl;
                    if (username != null && username.equals(user.getUsername())) {
                        profileUrl = request.getContextPath() + "/profile";
                    } else {
                        profileUrl = request.getContextPath() + "/user?username=" + user.getUsername();
                    }
                %>
                <a href="<%= profileUrl %>" class="block text-center bg-gray-800/70 backdrop-blur-sm rounded-lg shadow-xl p-6 transform transition-transform duration-300 hover:-translate-y-2">
                    <%
                        String userProfilePic = "https://placehold.co/128x128/1F2937/FFFFFF?text=" + user.getUsername().toUpperCase().charAt(0);
                        if (user.getImageName() != null && !user.getImageName().isEmpty()) {
                            userProfilePic = request.getContextPath() + "/images/profiles/" + user.getImageName();
                        }
                    %>
                    <img src="<%= userProfilePic %>" alt="Profile of <%= user.getUsername() %>" class="w-32 h-32 rounded-full mx-auto mb-4 border-4 border-gray-700 object-cover">
                    <h3 class="font-bold text-xl text-white truncate"><%= user.getUsername() %></h3>
                </a>
            </div>
            <% } %>
        </div>

        <% if (foundUsers.isEmpty()) { %>
        <div class="text-center py-20"><p class="text-gray-500">No creators found matching your search for "<%= searchQuery %>".</p></div>
        <% } %>

        <% } else { %>
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
                <a href="<%= request.getContextPath() %>/post?id=<%= story.getStoryId() %>" class="block rounded-lg shadow-xl overflow-hidden h-full transform transition-transform duration-300 hover:-translate-y-2 group">
                    <img src="<%= imageUrl %>" alt="Story Art for <%= story.getTitle() %>" class="w-full h-40 object-cover">

                    <div class="p-4 flex flex-col justify-between bg-gray-900/70 backdrop-blur-sm border-t border-gray-700/50" style="height: calc(100% - 10rem);">
                        <div>
                            <h3 class="font-bold text-lg text-white group-hover:text-blue-400 transition-colors duration-300 mb-2 truncate" title="<%= story.getTitle() %>">
                                <%= story.getTitle() %>
                            </h3>
                            <p class="text-gray-400 text-sm h-20 overflow-hidden">
                                <%= truncate(story.getDescription(), 120) %>
                            </p>
                        </div>
                        <div class="mt-4 text-xs text-blue-400">
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
            <a href="<%= request.getContextPath() %>/home" class="mt-4 inline-block text-blue-400 hover:text-blue-300">Clear Search</a>
            <% } else { %>
            <p class="text-gray-500">Search for a story, or create your own!</p>
            <% if (username != null) { %>
            <a href="<%= request.getContextPath() %>/create-post.jsp" class="mt-4 inline-block bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Create a Story</a>
            <% } else { %>
            <a href="<%= request.getContextPath() %>/login.jsp" class="mt-4 inline-block bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Login to Create</a>
            <% } %>
            <% } %>
        </div>
        <% } %>
        <% } %>

        <div id="show-more-container" class="text-center mt-12">
            <button id="show-more-btn" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg transition duration-300">
                Show More
            </button>
        </div>

        <%
            List<SharedChat> sharedChatsFeed = (List<SharedChat>) request.getAttribute("sharedChatsFeed");
            if (sharedChatsFeed != null && !sharedChatsFeed.isEmpty()) {
        %>
        <br><br>
        <section class="mb-12">
            <h2 class="text-3xl font-bold text-white mb-6 border-b-2 border-blue-800 pb-2">
                Completed stories from people you follow
            </h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                <% for (SharedChat shared : sharedChatsFeed) {
                    String userProfilePic = (shared.getUserImage() != null && !shared.getUserImage().isEmpty())
                            ? request.getContextPath() + "/images/profiles/" + shared.getUserImage()
                            : "https://placehold.co/40x40/1F2937/FFFFFF?text=" + shared.getUsername().toUpperCase().charAt(0);
                %>
                <a href="<%= request.getContextPath() %>/chat?chatId=<%= shared.getChatId() %>"
                   class="block bg-gray-800/70 backdrop-blur-sm rounded-lg shadow-xl p-4 transform transition-all duration-300 hover:-translate-y-2 hover:shadow-blue-500/20 group">
                    <h3 class="font-bold text-lg text-white group-hover:text-blue-400 transition-colors duration-300 truncate" title="<%= shared.getStoryTitle() %>">
                        <%= shared.getStoryTitle() %>
                    </h3>
                    <div class="flex items-center gap-3 mt-3 text-sm text-gray-400 border-t border-gray-700 pt-3">
                        <img src="<%= userProfilePic %>" alt="Profile of <%= shared.getUsername() %>" class="h-8 w-8 rounded-full object-cover">
                        <span class="truncate">Shared by <span class="font-semibold text-gray-300"><%= shared.getUsername() %></span></span>
                    </div>
                </a>
                <% } %>
            </div>
        </section>
        <% } %>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const searchType = "<%= searchType != null ? searchType : "" %>";
        const gridId = (searchType === "creator") ? 'users-grid' : 'stories-grid';
        const cardClass = (searchType === "creator") ? '.user-card' : '.story-card';

        const grid = document.getElementById(gridId);
        if (grid) {
            const showMoreBtn = document.getElementById('show-more-btn');
            const showMoreContainer = document.getElementById('show-more-container');
            const allCards = Array.from(grid.querySelectorAll(cardClass));
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
        } else {
            const showMoreContainer = document.getElementById('show-more-container');
            if(showMoreContainer) showMoreContainer.style.display = 'none';
        }

        const searchInput = document.getElementById('search-query-input');
        const searchTypeSelect = document.getElementById('search-type-select');
        const tagButtons = document.querySelectorAll('.tag-button');
        const searchForm = document.getElementById('search-form');

        tagButtons.forEach(button => {
            button.addEventListener('click', function() {
                const tagText = this.textContent.trim();
                if (tagText === "All") {
                    searchInput.value = "";
                    searchTypeSelect.value = "title";
                } else {
                    searchInput.value = tagText;
                    searchTypeSelect.value = "tag";
                }
                searchForm.submit();
            });
        });
    });
</script>
</body>
</html>