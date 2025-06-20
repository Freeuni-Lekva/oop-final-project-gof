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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "searchServlet", value = "/search")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String type = req.getParameter("type");

        if (query == null || query.trim().isEmpty() || type == null) {
            resp.sendRedirect("home");
            return;
        }

        ServletContext context = getServletContext();
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        List<Story> stories = new ArrayList<>();

        try {
            switch (type) {
                case "title":
                    stories = storyDAO.searchStoriesByTitle(query);
                    break;
                case "creator":
                    stories = storyDAO.searchStoriesByCreatorName(query);
                    break;
                case "tag":
                    stories = storyDAO.searchStoriesByTag(query);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
        }

        req.setAttribute("stories", stories);
        req.setAttribute("searchQuery", query);
        req.setAttribute("searchType", type);

        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}