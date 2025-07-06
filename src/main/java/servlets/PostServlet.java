package servlets;

import data.media.CommentsDAO;
import data.media.LikesDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.story.Story;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name="PostServlet", value="/post")
public class PostServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

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
            res.sendRedirect(req.getContextPath() + "/login.jsp?redirect=post.jsp?id=" + storyIdStr);
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

            String redirectUrl = req.getContextPath() + "/post.jsp?id=" + storyId;

            switch (action) {
                case "bookmark":
                    Story story = storyDAO.getStory(storyId);
                    userDAO.addBookmark(userId, story);
                    break;

                case "start_story":
                    res.sendRedirect(req.getContextPath() + "/AIchat.jsp?storyId=" + storyId);
                    return;

                case "like_post": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    likesDAO.addLikeToPost(postId, userId);
                    break;
                }

                case "unlike_post": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    likesDAO.removeLikePost(postId, userId);
                    break;
                }

                case "add_comment": {
                    int postId = Integer.parseInt(req.getParameter("postId"));
                    String commentText = req.getParameter("commentText");
                    if (commentText != null && !commentText.trim().isEmpty()) {
                        commentsDAO.addComment(commentText, userId, postId);
                    }
                    break;
                }

                case "delete_comment": {
                    int commentId = Integer.parseInt(req.getParameter("commentId"));
                    int authorId = commentsDAO.getAuthorId(commentId);
                    if (authorId == userId) {
                        commentsDAO.deleteComment(commentId);
                    }
                    break;
                }

                default:
                    res.sendRedirect(req.getContextPath() + "/home");
                    return;
            }

            res.sendRedirect(redirectUrl);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            res.sendRedirect(req.getContextPath() + "/post.jsp?id=" + storyIdStr + "&error=true");
        }
    }

}
