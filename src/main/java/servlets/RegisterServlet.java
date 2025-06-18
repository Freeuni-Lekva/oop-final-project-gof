package servlets;
import data.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDateTime;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            res.sendRedirect(req.getContextPath() + "/home.jsp");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher reqDispatcher = req.getRequestDispatcher("/register.jsp");
        HttpSession session = req.getSession(false);

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        int age;

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Username and password cannot be empty.");
            reqDispatcher.forward(req, res);
            return;
        }


        try {
            age = Integer.parseInt(req.getParameter("age"));
            if (age < 16) {
                req.setAttribute("error", "You must be at least 16 years old to register.");
                reqDispatcher.forward(req, res);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Please enter a valid age.");
            reqDispatcher.forward(req, res);
            return;
        }

        if(!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match!");
            reqDispatcher.forward(req, res);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        LocalDateTime now = LocalDateTime.now();
        User user = new User(username, hashedPassword, age, now, false);

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

        if(userDAO.findUser(user.getUsername()) != null) {
            req.setAttribute("error", "Username is already taken.");
            reqDispatcher.forward(req, res);
            return;
        }

        userDAO.saveUser(user);
        session.setAttribute("user", user.getUsername());
        res.sendRedirect(req.getContextPath() + "/home.jsp");
    }
}