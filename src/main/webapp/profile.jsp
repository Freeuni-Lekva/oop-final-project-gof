<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.media.Post" %>
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
        .post-image {
            aspect-ratio: 1 / 1;
            object-fit: cover;
            width: 100%;
            height: 100%;
        }
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
        <%-- Section for Published Posts --%>
        <section id="published-posts">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Published Posts
            </h2>

            <%
                // 1. Get the list of posts from the request object (sent by the servlet)
                List<Post> userPosts = (List<Post>) request.getAttribute("userPosts");

                // 2. Check if the list is null or empty
                if (userPosts != null && !userPosts.isEmpty()) {
            %>
            <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                <%
                    // 3. Loop through each post using a standard Java for-loop
                    for (Post post : userPosts) {
                %>
                <div class="group relative bg-gray-800 rounded-lg overflow-hidden shadow-lg transition-transform duration-300 hover:scale-105">
                    <a href="post.jsp?id=<%= post.getPostId() %>" class="block">
                        <img src="<%= request.getContextPath() %>/images/posts/<%= post.getImageName() %>"
                             alt="Post Image"
                             class="post-image">
                        <div class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                            <div class="text-center text-white p-2">
                                <p class="font-semibold">Likes: <%= post.getLikeCount() %></p>
                                <p class="font-semibold">Comments: <%= post.getCommentCount() %></p>
                            </div>
                        </div>
                    </a>
                </div>
                <%
                    } // End of the for-loop
                %>
            </div>
            <%
            } else {
                // 4. If the list is empty, show this message
            %>
            <div class="bg-gray-800 border-l-4 border-purple-500 text-gray-300 p-4 rounded-md" role="alert">
                <p class="font-bold">No Posts Yet</p>
                <p>You haven't published any posts. Start creating a story to share your work!</p>
            </div>
            <%
                } // End of the if/else block
            %>
        </section>

        <%-- Section for Reading History (still has placeholders) --%>
        <section id="reading-history" class="mt-12">
            <h2 class="text-2xl font-semibold text-gray-200 border-b-2 border-gray-700 pb-2 mb-6">
                Your Reading History
            </h2>
            <div class="space-y-4">
                <div class="bg-gray-800 p-4 rounded-lg">
                    <a href="#" class="font-semibold text-lg text-white hover:text-purple-400">
                        Placeholder: The Mystery of the Lost Artifact
                    </a>
                </div>
                <div class="bg-gray-800 p-4 rounded-lg">
                    <a href="#" class="font-semibold text-lg text-white hover:text-purple-400">
                        Placeholder: Adventures in the Digital Age
                    </a>
                </div>
            </div>
        </section>

    </main>
</div>

</body>
</html>