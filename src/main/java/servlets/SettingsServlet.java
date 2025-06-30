package servlets;

import data.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet(name = "SettingsServlet", value = "/settings")
@MultipartConfig
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
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        String currentUsernameInSession = (String) session.getAttribute("user");
        User currentUser;
        try {
            currentUser = userDAO.findUser(currentUsernameInSession);
        } catch (SQLException e) {
            throw new ServletException("Database error finding user for update", e);
        }

        if (currentUser == null) {
            session.invalidate();
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String newUsername = req.getParameter("username").trim();
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        Part filePart = req.getPart("profilePicture");

        String newHashedPassword = null;
        boolean isPasswordChangeRequested = newPassword != null && !newPassword.isEmpty();

        if (isPasswordChangeRequested) {
            if (!BCrypt.checkpw(currentPassword, currentUser.getPasswordHash())) {
                session.setAttribute("settingsError", "Incorrect current password.");
                res.sendRedirect(req.getContextPath() + "/settings");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                session.setAttribute("settingsError", "New passwords do not match.");
                res.sendRedirect(req.getContextPath() + "/settings");
                return;
            }
            newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        }

        String newImageName = currentUser.getImageName();
        boolean isImageChanged = (filePart != null && filePart.getSize() > 0);

        if (isImageChanged) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            newImageName = UUID.randomUUID().toString() + "_" + originalFileName;
        }


        try {
            boolean isUsernameChanged = !newUsername.equals(currentUser.getUsername());

            if (isUsernameChanged) {
                User existingUser = userDAO.findUser(newUsername);
                if (existingUser != null) {
                    session.setAttribute("settingsError", "Username '" + newUsername + "' is already taken. Please choose another.");
                    res.sendRedirect(req.getContextPath() + "/settings");
                    return;
                }
            }

            if (newHashedPassword != null) {
                userDAO.updateUserPassword(currentUser.getUserId(), newHashedPassword);
            }

            if (isUsernameChanged) {
                userDAO.updateUsername(currentUser.getUserId(), newUsername);
            }

            if (isImageChanged) {
                userDAO.updateUserImage(currentUser.getUserId(), newImageName);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "images" + File.separator + "profiles";
                filePart.write(uploadPath + File.separator + newImageName);
            }

            if (isUsernameChanged) {
                session.setAttribute("user", newUsername);
            }

            session.setAttribute("profileMessage", "Settings updated successfully!");
            res.sendRedirect(req.getContextPath() + "/profile");

        } catch (SQLException e) {
            throw new ServletException("Database error during profile update", e);
        }
    }
}