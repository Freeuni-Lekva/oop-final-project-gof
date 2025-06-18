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

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if(session != null && session.getAttribute("user") != null) {
            session.setAttribute("error", "You are already logged in!");
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher reqDispatcher =  req.getRequestDispatcher("/login.jsp");
        HttpSession session = req.getSession(false);
        System.out.println("aq movedi ra");
        System.out.println("Session: " + session);
        if(session != null && session.getAttribute("user") != null) {
            System.out.println("aq rato movdivar");
            session.setAttribute("error", "You are already logged in!");
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        System.out.println("ar yopila shemosuli user");
        session = req.getSession();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        User user = userDAO.findUser(username); // see RegisterServlet

        if(user == null) {
            System.out.println("araswori saxeli shemoutania");
            req.setAttribute("error", "Username or Password is incorrect!");
            reqDispatcher.forward(req, res);
            return;
        }

        if(!BCrypt.checkpw(password, user.getPasswordHash())) {
            System.out.println("araswori paroli shemoutania");
            req.setAttribute("error", "Username or Password is incorrect!");
            reqDispatcher.forward(req, res);
            return;
        }

        session.setAttribute("user", user);
        session.setAttribute("loggedIn", true);
        res.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}