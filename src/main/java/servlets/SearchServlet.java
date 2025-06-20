package servlets;

import data.story.StoryDAO;
import data.story.TagsDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.story.Story;

import java.io.IOException;
import java.sql.SQLException;
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
        TagsDAO tagsDAO = (TagsDAO) context.getAttribute("tagDao");
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
                    List<Integer> storyIds = tagsDAO.findStoryIdsByTag(query);
                    if (storyIds != null && !storyIds.isEmpty()) {
                        stories = storyDAO.getStoriesByIds(storyIds);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
            try {
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        req.setAttribute("stories", stories);
        req.setAttribute("searchQuery", query);
        req.setAttribute("searchType", type);

        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}