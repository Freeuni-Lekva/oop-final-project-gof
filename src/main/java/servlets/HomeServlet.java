package servlets;

import data.chat.SharedChatDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.chat.SharedChat;
import model.story.Story;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This servlet handles the logic for the home page.
 * It retrieves a list of all stories and forwards them to home.jsp for display.
 */
@WebServlet(name = "homeServlet", value = "/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");

        List<Story> stories;
        try {
            stories = storyDAO.getAllStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("stories", stories);

        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;
        User loggedInUser = null;
        try {
            loggedInUser = userDAO.findUser(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (loggedInUser != null) {
            try {
                SharedChatDAO sharedChatDao = (SharedChatDAO) getServletContext().getAttribute("sharedChatDao");
                List<SharedChat> sharedChatsFeed = sharedChatDao.getSharedChatsFeedForUser(loggedInUser.getUserId());
//                System.out.println("Shared chats length: " + sharedChatsFeed.size());
                req.setAttribute("sharedChatsFeed", sharedChatsFeed);
            } catch (SQLException e) {
                System.err.println("Error fetching shared chats feed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}