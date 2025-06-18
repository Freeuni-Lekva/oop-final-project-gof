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
        System.out.println("RegisterServlet.doGet()");
        HttpSession session = req.getSession(false);
        if(session != null && session.getAttribute("user") != null) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RequestDispatcher reqDispatcher = req.getRequestDispatcher("/register.jsp");
        HttpSession session = req.getSession(false);
        System.out.println("RegisterServlet.doPost()");
        if(session != null && session.getAttribute("user") != null) {
            session.setAttribute("error", "You are already logged in!");
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        session = req.getSession();

        String username = req.getParameter("username");
        int age;
        try {
            age = Integer.parseInt(req.getParameter("age"));
            if(age < 0 || age > 130) {
                throw new IllegalArgumentException("Invalid age.");
            } else if(age < 16) {
                throw new IllegalArgumentException("You must be 16 years old or older to use this website.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            reqDispatcher.forward(req, res);
            return;
        }
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        System.out.println("paswsord: " + password);
        System.out.println("confirmPassword: " + confirmPassword);

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
            req.getRequestDispatcher("/register.jsp").forward(req, res);
            return;
        }
        userDAO.saveUser(user);
        session.setAttribute("user", user);
        res.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}