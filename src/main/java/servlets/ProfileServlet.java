package servlets;

import data.media.PostDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.media.Post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name="ProfileServlet", value="/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false); // Use false to not create a new session

        // 1. Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String username = (String) session.getAttribute("user");
        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();

        try {
            // 2. Fetch the full User object to get their ID
            User user = userDAO.findUser(username);
            List<Post> userPosts = postDAO.getPostsByCreatorId(user.getUserId());

            req.setAttribute("userPosts", userPosts);
            req.setAttribute("profileUser", user);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        req.getRequestDispatcher("/profile.jsp").forward(req, res);
    }
}