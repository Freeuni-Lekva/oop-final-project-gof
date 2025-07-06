package servlets;

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

        if (username == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
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
            Story story = storyDAO.getStory(storyId);

            switch (action) {
                case "bookmark":

                    userDAO.addBookmark(userId,story);
                    res.sendRedirect(req.getContextPath() + "/post?id=" + storyId);
                    break;

                case "start_story":
                    res.sendRedirect(req.getContextPath() + "/AIchat?storyId=" + storyId);
                    break;

                default:
                    res.sendRedirect(req.getContextPath() + "/home");
                    break;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
