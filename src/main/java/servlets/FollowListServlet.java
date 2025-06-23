package servlets;

import data.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "FollowListServlet", value = "/followList")
public class FollowListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String type = request.getParameter("type");

        if (userIdStr == null || type == null || userIdStr.isEmpty() || type.isEmpty()) {
            response.sendRedirect("home.jsp");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("home.jsp");
            return;
        }


        try {
            UserDAO userDAO = new UserDAO();

            User profileOwner = userDAO.findUserById(userId);
            if (profileOwner == null) {
                response.sendRedirect("home.jsp");
                return;
            }

            List<User> userList;
            String pageTitle;

            switch (type) {
                case "followers":
                    userList = userDAO.findFollowers(userId);
                    pageTitle = "Followers";
                    break;
                case "following":
                    userList = userDAO.findFollowing(userId);
                    pageTitle = "Following";
                    break;
                default:
                    response.sendRedirect("home.jsp");
                    return;
            }

            request.setAttribute("userList", userList);
            request.setAttribute("pageTitle", pageTitle);
            request.setAttribute("profileOwner", profileOwner);

            request.getRequestDispatcher("/followList.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error fetching follow list", e);
        }
    }
}