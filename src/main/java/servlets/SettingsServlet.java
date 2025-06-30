package servlets;

import data.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "SettingsServlet", value = "/settings")
public class SettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("user");
        UserDAO userDAO = new UserDAO();

        try {
            User currentUser = userDAO.findUser(username);

            if (currentUser == null) {
                session.invalidate();
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            req.setAttribute("currentUser", currentUser);

        } catch (SQLException e) {
            throw new ServletException("Database error fetching user for settings page", e);
        }

        req.getRequestDispatcher("/settings.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


    }
}