package servlets;

import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.HttpConstraintElement;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.story.Story;
import org.mindrot.jbcrypt.BCrypt;

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
                    res.sendRedirect(req.getContextPath() + "/post.jsp?id=" + storyId);
                    break;

                case "start_story":
                    res.sendRedirect(req.getContextPath() + "/AIchat.jsp?storyId=" + storyId);
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
