<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 6/23/2025
  Time: 1:36 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.User, model.media.Post" %>

<%
  User profileOwner = (User) request.getAttribute("profileOwner");
  List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");
  List<User> followersList = (List<User>) request.getAttribute("followersList");
  List<User> followingList = (List<User>) request.getAttribute("followingList");

  String pageTitle = (String) request.getAttribute("pageTitle");
  List<User> mainDisplayList = (List<User>) request.getAttribute("mainDisplayList");

  if (profileOwner == null) {
    response.sendRedirect("home.jsp");
    return;
  }
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= pageTitle %> of <%= profileOwner.getUsername() %> - StoryAI</title>
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

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">

  <header class="mb-6">
    <div class="flex items-center space-x-4">
      <%
        String profilePicturePath = request.getContextPath() + "/images/profiles/" + profileOwner.getImageName();
      %>
      <img src="<%= profilePicturePath %>" alt="Profile Picture" class="w-20 h-20 rounded-full object-cover border-2 border-purple-400">
      <h1 class="text-4xl font-bold font-orbitron text-white">
        <%= profileOwner.getUsername() %>
      </h1>
    </div>
  </header>

  <%
    int postCount = (userPosts != null) ? userPosts.size() : 0;
    int followerCount = (followersList != null) ? followersList.size() : 0;
    int followingCount = (followingList != null) ? followingList.size() : 0;
  %>
  <div class="flex space-x-8 mb-10 ml-24">
    <div class="text-center">
      <span class="font-bold text-xl text-white"><%= postCount %></span>
      <span class="block text-sm text-gray-400">Posts</span>
    </div>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=followers" class="text-center block hover:text-purple-300 transition-colors">
      <span class="font-bold text-xl text-white"><%= followerCount %></span>
      <span class="block text-sm text-gray-400">Followers</span>
    </a>
    <a href="<%= request.getContextPath() %>/followList?userId=<%= profileOwner.getUserId() %>&type=following" class="text-center block hover:text-purple-300 transition-colors">
      <span class="font-bold text-xl text-white"><%= followingCount %></span>
      <span class="block text-sm text-gray-400">Following</span>
    </a>
  </div>

  <main>
    <div class="flex justify-between items-center border-b-2 border-gray-700 pb-2 mb-6">
      <h2 class="text-2xl font-semibold text-gray-200">
        <%= pageTitle %>
      </h2>
      <a href="user?username=<%= profileOwner.getUsername() %>" class="text-purple-400 hover:text-purple-300 transition-colors">
        ← Back to Profile
      </a>
    </div>

    <% if (mainDisplayList != null && !mainDisplayList.isEmpty()) { %>
    <div class="space-y-4">
      <% for (User user : mainDisplayList) { %>
      <div class="flex items-center justify-between bg-gray-800 p-3 rounded-lg hover:bg-gray-700/50 transition-colors">
        <a href="user?username=<%= user.getUsername() %>" class="flex items-center space-x-4">
          <img src="<%= request.getContextPath() %>/images/profiles/<%= user.getImageName() %>" alt="Profile picture of <%= user.getUsername() %>" class="w-12 h-12 rounded-full object-cover">
          <span class="font-semibold text-lg text-white"><%= user.getUsername() %></span>
        </a>

      </div>
      <% } %>
    </div>
    <% } else { %>
    <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md">
      <p class="font-bold">List is Empty</p>
      <p>There are no users to display in this list.</p>
    </div>
    <% } %>
  </main>

</div>
</body>
</html>