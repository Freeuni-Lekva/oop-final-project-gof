package servlets;

import data.story.StoryDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}