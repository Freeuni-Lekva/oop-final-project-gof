package servlets;

import data.user.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name="admins", value="/admin/dashboard")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;

    private static final int DEFAULT_RECENT_USER_LIMIT = 5;
    private static final int MAX_RECENT_USER_LIMIT = 50;

    @Override
    public void init() {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loggedInUsername = (session != null) ? (String) session.getAttribute("user") : null;

        if (loggedInUsername == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            User loggedInUser = userDAO.findUser(loggedInUsername);
            if (loggedInUser == null || !loggedInUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
                return;
            }

            request.setAttribute("totalUsers", userDAO.getTotalUserCount());
            request.setAttribute("adminCount", userDAO.getAdminCount());
            request.setAttribute("creatorCount", userDAO.getCreatorCount());

            int limit = DEFAULT_RECENT_USER_LIMIT;
            String limitParam = request.getParameter("limit");

            if (limitParam != null && !limitParam.isEmpty()) {
                try {
                    int requestedLimit = Integer.parseInt(limitParam);
                    if (requestedLimit > 0 && requestedLimit <= MAX_RECENT_USER_LIMIT) {
                        limit = requestedLimit;
                    } else if (requestedLimit > MAX_RECENT_USER_LIMIT) {
                        limit = MAX_RECENT_USER_LIMIT;
                    }
                } catch (NumberFormatException e) {
                }
            }

            List<User> recentUsers = userDAO.getRecentUsers(limit);
            request.setAttribute("recentUsers", recentUsers);
            request.setAttribute("currentLimit", limit);


            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error occurred.", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String loggedInUsername = (session != null) ? (String) session.getAttribute("user") : null;

        String successMessage = null;
        String errorMessage = null;

        try {
            if (loggedInUsername == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            User actingAdmin = userDAO.findUser(loggedInUsername);
            if (actingAdmin == null || !actingAdmin.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
                return;
            }

            String usernameToModify = request.getParameter("username");
            String action = request.getParameter("action");

            if (usernameToModify == null || usernameToModify.trim().isEmpty()) {
                errorMessage = "Username field cannot be empty.";
            } else {
                User targetUser = userDAO.findUser(usernameToModify);

                if (targetUser == null) {
                    errorMessage = "User '" + usernameToModify + "' not found.";
                } else {
                    if (actingAdmin.getUserId() == targetUser.getUserId()) {
                        errorMessage = "You cannot modify your own account from this panel.";
                    } else {
                    switch (action) {
                        case "toggleAdmin":
                            boolean newAdminStatus = !targetUser.isAdmin();
                            userDAO.updateAdminStatus(targetUser.getUserId(), newAdminStatus);
                            successMessage = "Admin status for " + targetUser.getUsername() + " updated successfully.";
                            break;

                        case "deleteUser":
                            if (targetUser.isAdmin()) {
                                errorMessage = "Cannot delete another admin user.";
                            } else {
                                userDAO.deleteUser(targetUser.getUserId());
                                successMessage = "User " + targetUser.getUsername() + " deleted successfully.";
                            }
                            break;
                    }
                }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage = "A database error occurred. Please try again.";
        }

        String redirectURL = request.getContextPath() + "/dashboard";
        if (successMessage != null) {
            redirectURL += "?message=" + java.net.URLEncoder.encode(successMessage, "UTF-8");
        } else if (errorMessage != null) {
            redirectURL += "?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8");
        }
        response.sendRedirect(redirectURL);
    }

}
