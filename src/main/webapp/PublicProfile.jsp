<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 6/23/2025
  Time: 2:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList, model.User, model.media.Post, model.story.Story, data.story.StoryDAO, data.story.TagsDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="jakarta.servlet.ServletException" %>

<%!
  String truncate(String text, int length) {
    if (text == null || text.length() <= length) return text;
    return text.substring(0, length) + "...";
  }
%>

<%
  User profileOwner = (User) request.getAttribute("profileOwner");
  if (profileOwner == null) {
    response.sendRedirect("home.jsp");
    return;
  }
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= profileOwner.getUsername() %>'s Profile - StoryAI</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; color: #E5E7EB; }
    .font-orbitron { font-family: 'Orbitron', sans-serif; }
    /* Gradient text utility */
    .gradient-text {
      background-image: linear-gradient(to right, #a78bfa, #f472b6);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
  </style>
</head>
<body class="bg-black">
<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">

  <header class="mb-8">
    <div class="flex flex-col sm:flex-row justify-between items-center gap-6">
      <div class="flex items-center space-x-6">
        <img src="<%= request.getContextPath() %>/images/profiles/<%= profileOwner.getImageName() %>" alt="Profile Picture" class="w-24 h-24 rounded-full object-cover border-4 border-purple-500">
        <div>
          <h1 class="text-4xl font-bold font-orbitron text-white gradient-text">
            <%= profileOwner.getUsername() %>
          </h1>
        </div>
      </div>
      <%
        boolean isFollowing = (Boolean) request.getAttribute("isFollowing");
      %>
      <form action="user" method="post">
        <input type="hidden" name="profileUsername" value="<%= profileOwner.getUsername() %>">
        <% if (isFollowing) { %>
        <input type="hidden" name="action" value="unfollow">
        <button type="submit" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-6 rounded-lg shadow-md hover:shadow-red-500/50 transition-all duration-300 transform hover:scale-105">
          Unfollow
        </button>
        <% } else { %>
        <input type="hidden" name="action" value="follow">
        <button type="submit" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-6 rounded-lg shadow-md hover:shadow-purple-500/50 transition-all duration-300 transform hover:scale-105">
          Follow
        </button>
        <% } %>
      </form>
      <a href="<%= request.getContextPath() %>/home" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:shadow-purple-500/50 transition-all duration-300 transform hover:scale-105">
        ← Back to Home
      </a>

    </div>
  </header>

  <%
    List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
    List<User> followersList = (List<User>) request.getAttribute("followersList");
    List<User> followingList = (List<User>) request.getAttribute("followingList");
    int postCount = (userPosts != null) ? userPosts.size() : 0;
    int followerCount = (followersList != null) ? followersList.size() : 0;
    int followingCount = (followingList != null) ? followingList.size() : 0;
  %>
  <div class="flex space-x-8 mb-12 ml-4">
    <div class="text-center"><span class="font-bold text-2xl text-white"><%= postCount %></span><span class="block text-sm text-gray-400">Posts</span></div>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=followers" class="text-center block hover:text-purple-300 transition-colors"><span class="font-bold text-2xl text-white"><%= followerCount %></span><span class="block text-sm text-gray-400">Followers</span></a>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=following" class="text-center block hover:text-purple-300 transition-colors"><span class="font-bold text-2xl text-white"><%= followingCount %></span><span class="block text-sm text-gray-400">Following</span></a>
  </div>

  <main>
    <h2 class="text-3xl font-semibold gradient-text pb-2 mb-6 border-b-2 border-gray-800">Published Posts</h2>
    <% if (userPosts != null && !userPosts.isEmpty()) {
      StoryDAO storyDAO = (StoryDAO) application.getAttribute("storyDao");
      TagsDAO tagsDAO = (TagsDAO) application.getAttribute("tagDao");  %>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <% for (Post post : userPosts) {
        Story story = storyDAO.getStory(post.getStoryId());
        if (story == null) {
          throw new jakarta.servlet.ServletException("Story not found");
        }
        List<String> storyTags = tagsDAO.getStoryTags(story.getStoryId());
        String prompt = story.getPrompt();
        String title = story.getTitle();
      %>
      <div class="relative bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg shadow-lg overflow-hidden h-full transform transition-all duration-300 hover:-translate-y-2 hover:shadow-purple-500/40 group">
        <a href="<%= request.getContextPath() %>/post?id=<%= story.getStoryId() %>" class="block h-full">
          <img src="<%= request.getContextPath() %>/images/posts/<%= post.getImageName() %>" alt="Post art" class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105">
          <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 12rem);">
            <div>
              <h3 class="font-bold text-lg text-white mb-2 truncate" title="<%= title %>"><%= title %></h3>
              <p class="text-gray-300 text-sm h-20 overflow-hidden"><%= truncate(prompt, 90) %></p>
            </div>
            <div class="mt-4 text-xs text-indigo-300"><% if (!storyTags.isEmpty()) { %><%= String.join(" · ", storyTags) %><% } else { %>No Tags<% } %></div>
          </div>
        </a>
      </div>
      <% } %>
    </div>
    <% } else { %>
    <div class="bg-gray-800/50 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
      <p class="font-bold">No Posts Yet</p><p>This user hasn't published any stories.</p>
    </div>
    <% } %>
  </main>

</div>
</body>
</html>
