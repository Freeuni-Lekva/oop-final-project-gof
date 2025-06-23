<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 6/23/2025
  Time: 2:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList, model.User, model.media.Post, model.story.Story, data.story.StoryDAO, data.story.TagsDAO" %>

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
    body { font-family: 'Poppins', sans-serif; background-color: #111827; color: #E5E7EB; }
    .font-orbitron { font-family: 'Orbitron', sans-serif; }
  </style>
</head>
<body class="bg-gray-900">
<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">

  <header class="mb-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center space-x-4">
        <img src="<%= request.getContextPath() %>/images/profiles/<%= profileOwner.getImageName() %>" alt="Profile Picture" class="w-20 h-20 rounded-full object-cover border-2 border-gray-500">
        <h1 class="text-4xl font-bold font-orbitron text-white">
          <%= profileOwner.getUsername() %>
        </h1>
      </div>
      <%
        boolean isFollowing = (Boolean) request.getAttribute("isFollowing");
      %>
      <form action="user" method="post">
        <input type="hidden" name="profileUsername" value="<%= profileOwner.getUsername() %>">

        <% if (isFollowing) { %>
        <input type="hidden" name="action" value="unfollow">
        <button type="submit" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md transition-colors duration-200">
          Unfollow
        </button>
        <% } else { %>
        <input type="hidden" name="action" value="follow">
        <button type="submit" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md transition-colors duration-200">
          Follow
        </button>
        <% } %>
      </form>
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
  <div class="flex space-x-8 mb-10 ml-24">
    <div class="text-center"><span class="font-bold text-xl"><%= postCount %></span><span class="block text-sm text-gray-400">Posts</span></div>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=followers" class="text-center block hover:text-purple-300"><span class="font-bold text-xl"><%= followerCount %></span><span class="block text-sm text-gray-400">Followers</span></a>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=following" class="text-center block hover:text-purple-300"><span class="font-bold text-xl"><%= followingCount %></span><span class="block text-sm text-gray-400">Following</span></a>
  </div>

  <main>
    <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">Published Posts</h2>
    <% if (userPosts != null && !userPosts.isEmpty()) {
      StoryDAO storyDAO = new StoryDAO();
      TagsDAO tagsDAO = new TagsDAO(); %>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <% for (Post post : userPosts) {
        Story story = storyDAO.getStory(post.getStoryId());
        List<String> storyTags = (story != null) ? tagsDAO.getStoryTags(story.getStoryId()) : new ArrayList<>();
        String prompt = (story != null) ? story.getPrompt() : "No description.";
      %>
      <div class="bg-gray-800 rounded-lg shadow-xl overflow-hidden h-full">
        <a href="story.jsp?id=<%= story.getStoryId() %>" class="block h-full">
          <img src="<%= request.getContextPath() %>/images/posts/<%= post.getImageName() %>" alt="Post art for <%= title %>" class="w-full h-40 object-cover">
          <div class="p-4 flex flex-col justify-between" style="height: calc(100% - 10rem);">
            <div>
              <h3 class="font-bold text-lg text-white mb-2 truncate" title="<%= title %>"><%= title %></h3>
              <p class="text-gray-400 text-sm h-20 overflow-hidden"><%= truncate(prompt, 100) %></p>
            </div>
            <div class="mt-4 text-xs text-indigo-400"><%= String.join(" · ", storyTags) %></div>
          </div>
        </a>
      </div>
      <% } %>
    </div>
    <% } else { %>
    <p class="text-gray-400">This user hasn't published any posts yet.</p>
    <% } %>
  </main>

</div>
</body>
</html>
