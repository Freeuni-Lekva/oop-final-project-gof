package servlets;
import data.user.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;


@WebServlet(name="login", value="/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher reqDispatcher =  req.getRequestDispatcher("/login.jsp");
        HttpSession session = req.getSession(false);

        if(session != null ) {
            session.invalidate();
        }

        session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        User user = null;
        try {
            user = userDAO.findUser(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            req.setAttribute("error", "Username or Password is incorrect!");
            req.setAttribute("prefillUsername", username);
            reqDispatcher.forward(req, res);
            return;
        }

        session.setAttribute("user", user.getUsername());
        session.setAttribute("loggedIn", true);
        res.sendRedirect(req.getContextPath() + "/home");
    }
}