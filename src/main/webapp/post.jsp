<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.story.Story, model.media.Post, model.User, model.media.Comment" %>
<%@ page import="data.story.StoryDAO, data.media.PostDAO, data.user.UserDAO, data.media.CommentsDAO, data.media.LikesDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>

<%
    String username = (String) session.getAttribute("user");
    int storyId = -1;
    try {
        storyId = Integer.parseInt(request.getParameter("id"));
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }

    UserDAO userDao = (UserDAO) application.getAttribute("userDao");
    StoryDAO storyDao = (StoryDAO) application.getAttribute("storyDao");
    PostDAO postDao = (PostDAO) application.getAttribute("postDao");
    CommentsDAO commentsDao = (CommentsDAO) application.getAttribute("commentDao");
    LikesDAO likesDao = (LikesDAO) application.getAttribute("likeDao");

    Story story = null;
    Post post = null;
    User creator = null;
    User loggedInUser = null;
    List<Comment> comments = null;
    boolean userHasLiked = false;

    if (userDao != null) {
        try {
            loggedInUser = userDao.findUser(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    if (storyDao != null && postDao != null && commentsDao != null && likesDao != null) {
        try {
            story = storyDao.getStory(storyId);
            if (story != null) {
                post = postDao.getPostsByStoryId(story.getStoryId());
                creator = userDao.findUserById(story.getCreatorId());

                if (post != null) {
                    comments = commentsDao.getCommentsForPost(post.getPostId());
                    if (loggedInUser != null) {
                        userHasLiked = likesDao.hasUserLikedPost(post.getPostId(), loggedInUser.getUserId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching page details for story ID " + storyId + ": " + e.getMessage());
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
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        body::before { content: ''; position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(17, 24, 39, 0.7); z-index: -1; }
        .whitespace-pre-wrap { white-space: pre-wrap; }
    </style>
</head>
<body class="bg-gray-900 text-gray-200 font-sans bg-cover bg-center bg-fixed" style="background-image: url('<%= request.getContextPath() %>/images/design/img4.jpg');">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />


<div class="container mx-auto p-4 md:p-8 max-w-4xl">

    <% if (story != null && post != null) { %>
    <main class="bg-gray-800/80 backdrop-blur-sm rounded-lg shadow-2xl overflow-hidden">
        <img src="<%= request.getContextPath() + "/images/posts/" + post.getImageName() %>" alt="Story art" class="w-full h-auto max-h-[60vh] object-cover bg-black">

        <div class="p-6 md:p-8">
            <header class="mb-6">
                <h1 class="text-4xl md:text-5xl font-bold text-white mb-2"><%= story.getTitle() %></h1>
                <p class="text-lg text-gray-400">By <a href="/user?username=<%=creator.getUsername()%>" class="text-indigo-400 hover:underline"><%= creator != null ? creator.getUsername() : "Unknown" %></a></p>
            </header>

            <div class="story-content mb-8 border-t border-gray-700 pt-6">
                <p class="text-gray-300 text-lg leading-relaxed whitespace-pre-wrap"><%= story.getDescription() %></p>
            </div>

            <div class="flex items-center gap-6 text-gray-400 mb-6">
                <span class="flex items-center gap-2"><i class="fas fa-heart"></i> <%= post.getLikeCount() %> Likes</span>
                <span class="flex items-center gap-2"><i class="fas fa-comment"></i> <%= post.getCommentCount() %> Comments</span>
            </div>

            <% if (loggedInUser != null) { %>
            <div class="flex items-center flex-wrap gap-4 border-t border-gray-700 pt-6">

                <form action="<%= request.getContextPath() %>/post" method="POST" class="m-0">
                    <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                    <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                    <% if (userHasLiked) { %>
                    <input type="hidden" name="action" value="unlike_post">
                    <button type="submit" class="bg-pink-600 hover:bg-pink-700 text-white font-bold py-2 px-4 rounded-md transition duration-300 flex items-center gap-2">
                        <i class="fas fa-heart-crack"></i> Unlike
                    </button>
                    <% } else { %>
                    <input type="hidden" name="action" value="like_post">
                    <button type="submit" class="bg-gray-600 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded-md transition duration-300 flex items-center gap-2">
                        <i class="far fa-heart"></i> Like
                    </button>
                    <% } %>
                </form>

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
                    <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-md transition duration-300 flex items-center gap-2">
                        <i class="far fa-bookmark"></i> Add to Bookmarks
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
                <a href="<%= request.getContextPath() %>/login.jsp?redirect=post.jsp?id=<%= storyId %>" class="text-indigo-400 hover:underline">Log in to like, comment, or start this story.</a>
            </div>
            <% } %>
        </div>
    </main>

    <section class="mt-8 bg-gray-800/80 backdrop-blur-sm rounded-lg shadow-2xl p-6 md:p-8">
        <h2 class="text-2xl font-bold text-white mb-6">Comments</h2>

        <% if (loggedInUser != null) { %>
        <form action="<%= request.getContextPath() %>/post" method="POST" class="mb-8">
            <input type="hidden" name="action" value="add_comment">
            <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
            <input type="hidden" name="postId" value="<%= post.getPostId() %>">
            <textarea name="commentText" class="w-full bg-gray-900 border border-gray-700 rounded-md p-3 text-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition" rows="3" placeholder="Add your comment..." required></textarea>
            <button type="submit" class="mt-3 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-md transition duration-300">
                Submit Comment
            </button>
        </form>
        <% } %>

        <div class="space-y-6">
            <% if (comments != null && !comments.isEmpty()) { %>
            <% for (Comment comment : comments) {
                User commentAuthor = userDao.findUserById(comment.getAuthorId());
                String authorProfilePic = (commentAuthor != null && commentAuthor.getImageName() != null)
                        ? request.getContextPath() + "/images/profiles/" + commentAuthor.getImageName()
                        : "https://placehold.co/40x40/64748B/FFFFFF?text=" + (commentAuthor != null ? commentAuthor.getUsername().toUpperCase().charAt(0) : "?");
            %>
            <div class="flex items-start gap-4">
                <img src="<%= authorProfilePic %>" alt="Author" class="h-10 w-10 rounded-full object-cover">
                <div class="flex-1">
                    <div class="flex items-center justify-between">
                        <p class="font-semibold text-indigo-400"><%= commentAuthor != null ? commentAuthor.getUsername() : "Unknown User" %></p>

                        <% if (loggedInUser != null && loggedInUser.getUserId() == comment.getAuthorId()) { %>
                        <form action="<%= request.getContextPath() %>/post" method="POST" class="m-0" onsubmit="return confirm('Are you sure you want to delete this comment?');" >
                            <input type="hidden" name="action" value="delete_comment">
                            <input type="hidden" name="storyId" value="<%= story.getStoryId() %>">
                            <input type="hidden" name="commentId" value="<%= comment.getCommentId() %>">
                            <button type="submit" class="text-gray-500 hover:text-red-500 transition" title="Delete Comment">
                                <i class="fas fa-trash"></i>
                            </button>
                        </form>
                        <% } %>
                    </div>
                    <p class="text-gray-300 mt-1"><%= comment.getCommentContents() %></p>
                </div>
            </div>
            <% } %>
            <% } else { %>
            <p class="text-gray-400">No comments yet. Be the first to share your thoughts!</p>
            <% } %>
        </div>
    </section>

    <% } else { %>
    <div class="text-center py-20 bg-gray-800/50 rounded-lg">
        <h1 class="text-4xl font-bold text-white">Oops! Story Not Found</h1>
        <p class="text-gray-400 mt-4">The saga you're looking for doesn't exist or may have been removed.</p>
        <a href="<%= request.getContextPath() %>/home" class="mt-8 inline-block bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-6 rounded-lg">Return to Home</a>
    </div>
    <% } %>

</div>

</body>
</html>