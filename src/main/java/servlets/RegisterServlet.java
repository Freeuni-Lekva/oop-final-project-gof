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
import util.ValidationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.io.File;
import java.util.Random;


@WebServlet(name="register", value="/register")
public class RegisterServlet extends HttpServlet {

    private static final String DEFAULT_PHOTOS_DIR = "images" + File.separator + "default-photos";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            res.sendRedirect(req.getContextPath() + "/home");
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
            req.setAttribute("prefillUsername", username);
            req.setAttribute("prefillAge", req.getParameter("age"));
            reqDispatcher.forward(req, res);
            return;
        }

        try {
            age = Integer.parseInt(req.getParameter("age"));
            if (age < 16) {
                req.setAttribute("error", "You must be at least 16 years old to register.");
                req.setAttribute("prefillUsername", username);
                reqDispatcher.forward(req, res);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Please enter a valid age.");
            req.setAttribute("prefillUsername", username);
            reqDispatcher.forward(req, res);
            return;
        }

        String validationError = ValidationUtils.validatePassword(password);
        if (validationError != null) {
            req.setAttribute("error", validationError);
            req.setAttribute("prefillUsername", username);
            req.setAttribute("prefillAge", req.getParameter("age"));
            reqDispatcher.forward(req, res);
            return;
        }

        if(!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match!");
            req.setAttribute("prefillUsername", username);
            req.setAttribute("prefillAge", req.getParameter("age"));
            reqDispatcher.forward(req, res);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String defaultPhotosPath = getServletContext().getRealPath("/") + DEFAULT_PHOTOS_DIR;
        File defaultPhotosDir = new File(defaultPhotosPath);
        String[] defaultPhotoFiles = defaultPhotosDir.list();
        String chosenImageName = null;

        if (defaultPhotoFiles != null && defaultPhotoFiles.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(defaultPhotoFiles.length);
            chosenImageName = defaultPhotoFiles[randomIndex];
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User(username, hashedPassword, age, now, false,false, chosenImageName);

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

        try {
            if(userDAO.findUser(user.getUsername()) != null) {
                req.setAttribute("error", "Username is already taken.");
                req.setAttribute("prefillAge", req.getParameter("age"));
                reqDispatcher.forward(req, res);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            userDAO.saveUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        session.setAttribute("user", user.getUsername());
        res.sendRedirect(req.getContextPath() + "/home");
    }
}