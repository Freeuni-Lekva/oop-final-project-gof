package Filter;

import data.user.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This filter checks if a logged-in user is an administrator
 * before allowing them to access admin-only pages.
 * It should run AFTER the AuthenticationFilter.
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    private UserDAO userDAO;

    public void init(FilterConfig filterConfig) {
        userDAO = new UserDAO();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("user");
        User user = null;
        try {
            user = userDAO.findUser(username);
        } catch (SQLException e) {
            System.err.println("Error retrieving user from database: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Database error occurred while checking admin access.", e);
        }
        if (user.isAdmin()) {
            filterChain.doFilter(req, res);
        } else {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied. You do not have permission to view this page.");
        }
    }
}
