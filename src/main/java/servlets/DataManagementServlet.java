package servlets;

import data.media.CommentsDAO;
import data.media.PostDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import data.user.UserDAO;
import model.media.Comment;
import model.media.Post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "DataManagementServlet", value = "/admin/data")
public class DataManagementServlet extends HttpServlet {

    private PostDAO postDAO;
    private CommentsDAO commentsDAO;
    private UserDAO userDAO;

    private static final int RECENT_COMMENT_LIMIT = 15;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.postDAO = (PostDAO) context.getAttribute("postDao");
        this.commentsDAO = (CommentsDAO) context.getAttribute("commentsDao");
        this.userDAO = (UserDAO) context.getAttribute("userDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loggedInUsername = (session != null) ? (String) session.getAttribute("user") : null;
        if (loggedInUsername == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            User loggedInUser = userDAO.findUser(loggedInUsername);
            if (loggedInUser == null || !loggedInUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
                return;
            }

            int totalPosts = postDAO.getTotalPostCount();
            int totalComments = commentsDAO.getTotalCommentCount();
            double avgCommentsPerPost = (totalPosts > 0) ? (double) totalComments / totalPosts : 0.0;
            List<Comment> recentComments = commentsDAO.getRecentCommentsWithUsername(RECENT_COMMENT_LIMIT);

            request.setAttribute("totalPosts", totalPosts);
            request.setAttribute("totalComments", totalComments);
            request.setAttribute("avgCommentsPerPost", avgCommentsPerPost);
            request.setAttribute("recentComments", recentComments);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/data_management.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error while loading data management page.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loggedInUsername = (session != null) ? (String) session.getAttribute("user") : null;
        if (loggedInUsername == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to perform this action.");
            return;
        }

        String action = request.getParameter("action");
        String successMessage = null;
        String errorMessage = null;

        try {
            if ("deleteComment".equals(action)) {
                int commentId = Integer.parseInt(request.getParameter("commentId"));
                commentsDAO.deleteComment(commentId);
                successMessage = "Comment #" + commentId + " has been deleted successfully.";

            } else if ("cleanupComments".equals(action)) {
                int deletedCount = commentsDAO.deleteUnengagedComments();
                successMessage = "Cleanup successful. " + deletedCount + " unengaged comments were deleted.";

            } else if ("deletePost".equals(action)) {
                String postIdStr = request.getParameter("postId");
                if (postIdStr == null || postIdStr.trim().isEmpty()) {
                    errorMessage = "Post ID field cannot be empty.";
                } else {
                    int postId = Integer.parseInt(postIdStr);

                    Post postToDelete = postDAO.getPostById(postId);
                    if (postToDelete == null) {
                        errorMessage = "Post with ID " + postId + " was not found.";
                    } else {
                        postDAO.deletePost(postId);
                        successMessage = "Post #" + postId + " and its parent story have been deleted successfully.";
                    }
                }

            } else {
                errorMessage = "Invalid or unspecified action.";
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid ID format. Please enter a number.";
            e.printStackTrace();
        } catch (SQLException e) {
            errorMessage = "A database error occurred during the operation.";
            e.printStackTrace();
        }

        String redirectURL = request.getContextPath() + "/admin/data";
        if (successMessage != null) {
            redirectURL += "?message=" + java.net.URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null) {
            redirectURL += "?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }
}