package servlets;

import data.story.StoryDAO;
import data.story.TagsDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.story.Story;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

        try {
            switch (type) {
                case "creator":
                    List<User> foundUsers = userDAO.searchUsersByName(query);
                    req.setAttribute("foundUsers", foundUsers);
                    break;
                case "title":
                    List<Story> storiesByTitle = storyDAO.searchStoriesByTitle(query);
                    req.setAttribute("stories", storiesByTitle);
                    break;
                case "tag":
                    List<String> tagList = Arrays.asList(query.trim().split("\\s+"));
                    List<Integer> storyIds = tagsDAO.findStoryIdsByMultipleTags(tagList);
                    List<Story> storiesByTag = new ArrayList<>();
                    if (storyIds != null && !storyIds.isEmpty()) {
                        storiesByTag = storyDAO.getStoriesByIds(storyIds);
                    }
                    req.setAttribute("stories", storiesByTag);
                    break;
                default:
                    req.setAttribute("stories", new ArrayList<Story>());
                    break;
            }
        } catch (SQLException e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
            try {
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        req.setAttribute("searchQuery", query);
        req.setAttribute("searchType", type);

        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}