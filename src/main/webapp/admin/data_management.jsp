<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.media.Comment" %>
<!DOCTYPE html>
<html>
<head>
    <title>Data Management - Admin Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; color: #E5E7EB; }
        .font-orbitron { font-family: 'Orbitron', sans-serif; }
        .gradient-text { background-image: linear-gradient(to right, #a78bfa, #f472b6); -webkit-background-clip: text; background-clip: text; color: transparent; }
        .nav-link.active {
            background-color: #9333ea;
            color: #ffffff;
            box-shadow: 0 4px 14px 0 rgba(147, 51, 234, 0.39);
        }
    </style>
</head>
<body class="bg-black">

<%
    String loggedInUser = (String) session.getAttribute("user");
    if (loggedInUser == null) { loggedInUser = "Admin"; }
%>

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <header class="mb-8">
        <div class="flex flex-col sm:flex-row justify-between items-center gap-6">
            <div class="flex items-center space-x-6">
                <div class="w-24 h-24 rounded-full bg-gray-800 flex items-center justify-center border-2 border-purple-500">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-12 text-purple-400"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.286Zm0 13.036h.008v.008h-.008v-.008Z" /></svg>
                </div>
                <div>
                    <h1 class="text-4xl font-bold font-orbitron text-white"><span class="gradient-text">Admin Dashboard</span></h1>
                    <p class="text-gray-400 mt-2">Site management panel. Welcome, <%= loggedInUser %>!</p>
                </div>
            </div>
            <div class="flex items-center space-x-4">
                <a href="<%= request.getContextPath() %>/home.jsp" class="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:shadow-purple-500/50 transition-all duration-300 transform hover:scale-105">
                    ← Back to Site
                </a>

            </div>
        </div>
    </header>

    <main>
        <div class="mb-8 flex space-x-2 border-b-2 border-gray-800 pb-2">
            <a href="<%= request.getContextPath() %>/admin/dashboard"
               class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50">
                User Management
            </a>
            <a href="<%= request.getContextPath() %>/admin/data"
               class="nav-link px-6 py-2 font-semibold text-lg rounded-t-lg transition-colors duration-300 hover:bg-purple-600/50 active">
                Data Management
            </a>
        </div>

        <div id="data-content">
            <%
                Integer totalPosts = (Integer) request.getAttribute("totalPosts");
                Integer totalComments = (Integer) request.getAttribute("totalComments");
                Double avgCommentsPerPost = (Double) request.getAttribute("avgCommentsPerPost");
                List<Comment> recentComments = (List<Comment>) request.getAttribute("recentComments");

                if (totalPosts == null) totalPosts = 0;
                if (totalComments == null) totalComments = 0;
                if (avgCommentsPerPost == null) avgCommentsPerPost = 0.0;

                String message = request.getParameter("message");
                String error = request.getParameter("error");
            %>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <div class="bg-white/10 p-6 rounded-lg border-l-4 border-cyan-500">
                    <p class="text-sm font-medium text-gray-400 uppercase">Total Posts</p>
                    <p class="mt-1 text-4xl font-semibold text-white"><%= totalPosts %></p>
                </div>
                <div class="bg-white/10 p-6 rounded-lg border-l-4 border-amber-500">
                    <p class="text-sm font-medium text-gray-400 uppercase">Total Comments</p>
                    <p class="mt-1 text-4xl font-semibold text-white"><%= totalComments %></p>
                </div>

                <div class="bg-white/10 p-6 rounded-lg border-l-4 border-teal-500">
                    <p class="text-sm font-medium text-gray-400 uppercase">Avg Comments/Post</p>
                    <p class="mt-1 text-4xl font-semibold text-white"><%= String.format("%.2f", avgCommentsPerPost) %></p>
                </div>
            </div>

            <div class="bg-white/5 p-6 rounded-lg border border-white/20 mb-8">
                <h3 class="text-xl font-semibold text-white mb-4">Manual Post Deletion</h3>
                <form action="<%= request.getContextPath() %>/admin/data" method="POST" class="flex flex-col sm:flex-row items-center gap-4">
                    <div class="w-full sm:w-1/2">
                        <label for="postId" class="sr-only">Post ID</label>
                        <input type="text" name="postId" id="postId"
                               class="bg-gray-900/50 border border-gray-600 text-white text-sm rounded-lg focus:ring-red-500 focus:border-red-500 block w-full p-2.5"
                               placeholder="Enter Post ID to delete..." required>
                    </div>
                    <div class="flex gap-4">
                        <input type="hidden" name="action" value="deletePost">
                        <button type="submit"
                                onclick="return confirm('WARNING: Deleting a post also deletes its entire story and all associated comments. This action cannot be undone. Are you sure?')"
                                class="text-white bg-red-600 hover:bg-red-700 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center transition-colors">
                            Delete Post by ID
                        </button>
                    </div>
                </form>
            </div>
            <% if (message != null) { %>
            <div id="success-alert" class="relative flex items-center justify-between bg-green-800/50 border-l-4 border-green-400 text-white p-4 rounded-md mb-6" role="alert">
                <p><%= message %></p>
                <button type="button" onclick="this.parentElement.style.display='none'" aria-label="Close">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>
                </button>
            </div>
            <% } %>
            <% if (error != null) { %>
            <div id="error-alert" class="relative flex items-center justify-between bg-red-800/50 border-l-4 border-red-400 text-white p-4 rounded-md mb-6" role="alert">
                <p><%= error %></p>
                <button type="button" onclick="this.parentElement.style.display='none'" aria-label="Close">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>
                </button>
            </div>
            <% } %>

            <div class="bg-white/5 p-6 rounded-lg border border-white/20 mb-8 flex flex-col sm:flex-row justify-between items-center">
                <div>
                    <h3 class="text-xl font-semibold text-white">Automated Cleanup</h3>
                    <p class="text-gray-400 mt-1">Delete comments older than 3 months with 0 likes.</p>
                </div>
                <form action="<%= request.getContextPath() %>/admin/data" method="POST" class="mt-4 sm:mt-0">
                    <input type="hidden" name="action" value="cleanupComments">
                    <button type="submit"
                            onclick="return confirm('Are you sure you want to permanently delete all unengaged comments? This action cannot be undone.')"
                            class="bg-red-600 hover:bg-amber-700 text-white font-semibold py-2 px-5 rounded-lg shadow-md hover:shadow-amber-500/50 transition-all duration-300">
                        Run Cleanup
                    </button>
                </form>
            </div>

            <div class="bg-white/5 p-6 rounded-lg border border-white/20">
                <h3 class="text-xl font-semibold text-white mb-4">Recent Comments</h3>
                <div class="overflow-x-auto">
                    <table class="min-w-full text-sm text-left text-gray-300">
                        <thead class="text-xs text-gray-400 uppercase bg-gray-900/50">
                        <tr>
                            <th scope="col" class="px-6 py-3">ID</th>
                            <th scope="col" class="px-6 py-3">Author</th>
                            <th scope="col" class="px-6 py-3">Comment</th>
                            <th scope="col" class="px-6 py-3">Post ID</th>
                            <th scope="col" class="px-6 py-3 text-center">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% if (recentComments != null && !recentComments.isEmpty()) { %>
                        <% for (Comment c : recentComments) { %>
                        <tr class="border-b border-gray-700 hover:bg-gray-800/50">
                            <td class="px-6 py-4 font-mono"><%= c.getCommentId() %></td>
                            <td class="px-6 py-4 font-medium text-white"><%= c.getAuthorUsername() %></td>
                            <td class="px-6 py-4 max-w-sm truncate" title="<%= c.getCommentContents() %>"><%= c.getCommentContents() %></td>
                            <td class="px-6 py-4"><%= c.getPostId() %></td>
                            <td class="px-6 py-4 text-center">
                                <form action="<%= request.getContextPath() %>/admin/data" method="POST" class="inline-block">
                                    <input type="hidden" name="action" value="deleteComment">
                                    <input type="hidden" name="commentId" value="<%= c.getCommentId() %>">
                                    <button type="submit"
                                            class="font-medium text-red-500 hover:text-red-400 transition-colors">
                                        Delete
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                        <% } else { %>
                        <tr>
                            <td colspan="6" class="px-6 py-8 text-center text-gray-400">No recent comments to display.</td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>
</body>
</html>