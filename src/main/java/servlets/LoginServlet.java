package servlets;
import data.UserDAO;
import model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session != null) {
            session.setAttribute("error", "You are already logged in!");
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher reqDispatcher =  req.getRequestDispatcher("/index.jsp");
        HttpSession session = req.getSession(false);
        if(session != null) {
            session.setAttribute("error", "You are already logged in!");
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        session = req.getSession();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDAO");
        User user = userDAO.findUser(username); // see RegisterServlet

        if(user == null) {
            req.setAttribute("error", "Username or Password is incorrect!");
            reqDispatcher.forward(req, res);
            return;
        }

        if(!BCrypt.checkpw(password, user.getPasswordHash())) {
            req.setAttribute("error", "Username or Password is incorrect!");
            reqDispatcher.forward(req, res);
            return;
        }

        session.setAttribute("user", user);
        res.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}