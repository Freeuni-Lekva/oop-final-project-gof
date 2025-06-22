package servlets;

import data.media.PostDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.media.Post;
import model.story.Story;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name="ProfileServlet", value="/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String username = (String) session.getAttribute("user");
        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        StoryDAO storyDAO = new StoryDAO();

        try {
            User user = userDAO.findUser(username);

            List<Post> userPosts = postDAO.getPostsByCreatorId(user.getUserId());
            List<Story> readingHistory = storyDAO.findReadingHistory(user.getUserId());

            req.setAttribute("userPosts", userPosts);
            req.setAttribute("readingHistory", readingHistory); // <-- ADDED: Attach reading history
            req.setAttribute("profileUser", user);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error when fetching profile data.", e);
        }

        req.getRequestDispatcher("/profile.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {//if logged in
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        String username = (String) session.getAttribute("user");

        if ("deletePost".equals(action)) {//check which one user wants to delete
            try {
                int postId = Integer.parseInt(req.getParameter("postId"));
                PostDAO postDAO = new PostDAO();
                postDAO.deletePost(postId);
            } catch (NumberFormatException | SQLException e) {
                throw new ServletException("Error deleting post.", e);
            }
        } else if ("deleteHistory".equals(action)) {
            try {
                UserDAO userDAO = new UserDAO();
                User user = userDAO.findUser(username);
                int userId = user.getUserId();

                int storyId = Integer.parseInt(req.getParameter("storyId"));

                StoryDAO storyDAO = new StoryDAO();
                storyDAO.removeReadingHistory(userId, storyId);

            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error deleting from reading history.", e);
            }
        }

        res.sendRedirect(req.getContextPath() + "/profile");//reload page
    }
}