package servlets;

import data.media.CommentsDAO;
import data.media.LikesDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import data.media.CommentsDAO;
import data.media.PostDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.story.Story;
import model.media.Comment;
import model.media.Post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="PostServlet", value="/post")
public class PostServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String storyIdStr = req.getParameter("id");
        if (storyIdStr == null || storyIdStr.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        try {
            int storyId = Integer.parseInt(storyIdStr);
            ServletContext context = getServletContext();
            StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
            PostDAO postDAO = (PostDAO) context.getAttribute("postDao");
            UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
            CommentsDAO commentsDAO = (CommentsDAO) context.getAttribute("commentDao");

            Story story = storyDAO.getStory(storyId);
            Post post = postDAO.getFirstPostForStory(storyId);
            List<Comment> comments = (post != null) ? commentsDAO.getCommentsForPost(post.getPostId()) : new ArrayList<>();

            if (story == null) {
                res.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            req.setAttribute("story", story);
            req.setAttribute("post", post);
            req.setAttribute("comments", comments);

            boolean isBookmarked = false;
            String loggedInUsername = (String) req.getSession().getAttribute("user");
            if (loggedInUsername != null) {
                User loggedInUser = userDAO.findUser(loggedInUsername);
                if (loggedInUser != null) {
                    isBookmarked = storyDAO.isBookmarked(loggedInUser.getUserId(), storyId);
                }
            }
            req.setAttribute("isBookmarked", isBookmarked);

            req.getRequestDispatcher("/post.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException("Database error fetching post data.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        String storyIdStr = req.getParameter("storyId");
        String username = (String) req.getSession().getAttribute("user");

        ServletContext context = getServletContext();
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        LikesDAO likesDAO = (LikesDAO) context.getAttribute("likeDao");
        CommentsDAO commentsDAO = (CommentsDAO) context.getAttribute("commentDao");

        if (username == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=post?id=" + storyIdStr);
            return;
        }
        if (action == null || storyIdStr == null) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            User user = userDAO.findUser(username);
            int userId = user.getUserId();
            int storyId = Integer.parseInt(storyIdStr);
            String redirectUrl = req.getContextPath() + "/post?id=" + storyId;

            switch (action) {
                case "bookmark":
                    storyDAO.addBookmark(userId, storyId);
                    res.sendRedirect(redirectUrl);
                    return;

                case "unbookmark":
                    storyDAO.removeBookmark(userId, storyId);
                    res.sendRedirect(redirectUrl);
                    return;

                case "start_story":
                    res.sendRedirect(req.getContextPath() + "/AIchat.jsp?storyId=" + storyId);
                    return;

                case "like_post": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    likesDAO.addLikeToPost(postId, userId);
                    res.sendRedirect(redirectUrl);
                    return;
                }

                case "unlike_post": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    likesDAO.removeLikePost(postId, userId);
                    res.sendRedirect(redirectUrl);
                    return;
                }

                case "add_comment": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    String commentText = req.getParameter("commentText");
                    if (commentText != null && !commentText.trim().isEmpty()) {
                        commentsDAO.addComment(commentText, userId, postId);
                    }
                    res.sendRedirect(redirectUrl);
                    return;
                }

                case "delete_comment": {
                    int commentId = Integer.parseInt(req.getParameter("commentId"));
                    int authorId = commentsDAO.getAuthorId(commentId);
                    if (authorId == userId) {
                        commentsDAO.deleteComment(commentId);
                    }
                    res.sendRedirect(redirectUrl);
                    return;
                }

                default:
                    res.sendRedirect(req.getContextPath() + "/home");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            res.sendRedirect(req.getContextPath() + "/post?id=" + storyIdStr + "&error=true");
        }
    }
}