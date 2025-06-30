package servlets;

import data.media.PostDAO;
import data.story.StoryDAO;
import data.user.HistoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
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

        String username = (String) session.getAttribute("user");
        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        StoryDAO storyDAO = new StoryDAO();

        try {
            User user = userDAO.findUser(username);

            List<Post> userPosts = postDAO.getPostsByCreatorId(user.getUserId());
            List<Story> readingHistory = storyDAO.findReadHistory(user.getUserId());
            List<Story> bookmarkedStories = storyDAO.findBookmarkedStories(user.getUserId());
            List<User> followersList = userDAO.findFollowers(user.getUserId());
            List<User> followingList = userDAO.findFollowing(user.getUserId());

            req.setAttribute("userPosts", userPosts);
            req.setAttribute("readingHistory", readingHistory);
            req.setAttribute("bookmarkedStories", bookmarkedStories);
            req.setAttribute("followersList", followersList);
            req.setAttribute("followingList", followingList);
            req.setAttribute("profileUser", user);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error when fetching profile data.", e);
        }

        req.getRequestDispatcher("/profile.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String username = (String) session.getAttribute("user");

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        PostDAO postDAO = (PostDAO) context.getAttribute("postDao");
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        HistoryDAO historyDAO = (HistoryDAO) context.getAttribute("historyDao");

        if (action != null) {
            try {
                User user = userDAO.findUser(username);
                if (user == null) {
                    res.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                int userId = user.getUserId();

                switch (action) {
                    case "deletePost": {
                        int postId = Integer.parseInt(req.getParameter("postId"));
                        postDAO.deletePost(postId);
                        break;
                    }

                    case "deleteHistory": {
                        int storyId = Integer.parseInt(req.getParameter("storyId"));
                        historyDAO.deleteReadHistory(userId, storyId);
                        break;
                    }

                    case "deleteBookmark": {
                        int storyId = Integer.parseInt(req.getParameter("storyId"));
                        storyDAO.removeBookmark(userId, storyId);
                        break;
                    }

                    default:
                        break;
                }
            } catch (NumberFormatException | SQLException e) {
                throw new ServletException("Error processing profile action: " + action, e);
            }
        }
        res.sendRedirect(req.getContextPath() + "/profile");
    }
}