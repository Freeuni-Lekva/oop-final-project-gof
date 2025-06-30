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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "SettingsServlet", value = "/settings")
@MultipartConfig
public class SettingsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            User currentUser = authenticateAndGetUser(req, res);
            if (currentUser != null) {
                req.setAttribute("currentUser", currentUser);
                req.getRequestDispatcher("/settings.jsp").forward(req, res);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error fetching user for settings page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            User currentUser = authenticateAndGetUser(req, res);
            if (currentUser == null) {
                return;
            }

            String newUsername = req.getParameter("username").trim();
            String newPassword = req.getParameter("newPassword");
            Part filePart = req.getPart("profilePicture");

            boolean isPasswordChangeRequested = newPassword != null && !newPassword.isEmpty();
            boolean isImageChanged = filePart != null && filePart.getSize() > 0;
            boolean isUsernameChanged = !newUsername.equals(currentUser.getUsername());

            if (isPasswordChangeRequested && !validatePasswordChange(req, currentUser)) {
                res.sendRedirect(req.getContextPath() + "/settings");
                return;
            }
            if (isImageChanged && !validateImageUpload(req, filePart)) {
                res.sendRedirect(req.getContextPath() + "/settings");
                return;
            }
            if (isUsernameChanged && !validateUsernameAvailability(req, newUsername)) {
                res.sendRedirect(req.getContextPath() + "/settings");
                return;
            }
            if (isPasswordChangeRequested) {
                String newHashedPassword = BCrypt.hashpw(req.getParameter("newPassword"), BCrypt.gensalt());
                userDAO.updateUserPassword(currentUser.getUserId(), newHashedPassword);
            }
            if (isUsernameChanged) {
                userDAO.updateUsername(currentUser.getUserId(), newUsername);
            }
            if (isImageChanged) {
                String newImageName = generateUniqueImageName(filePart);
                userDAO.updateUserImage(currentUser.getUserId(), newImageName);
                saveUploadedFile(filePart, newImageName);
            }

            HttpSession session = req.getSession();
            if (isUsernameChanged) {
                session.setAttribute("user", newUsername);
            }
            session.setAttribute("profileMessage", "Settings updated successfully!");
            res.sendRedirect(req.getContextPath() + "/profile");

        } catch (SQLException e) {
            throw new ServletException("Database error during profile update", e);
        }
    }

    private User authenticateAndGetUser(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }

        String username = (String) session.getAttribute("user");
        User currentUser = userDAO.findUser(username);
        if (currentUser == null) {
            session.invalidate();
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return currentUser;
    }

    private boolean validatePasswordChange(HttpServletRequest req, User currentUser) {
        HttpSession session = req.getSession();
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (currentPassword == null || !BCrypt.checkpw(currentPassword, currentUser.getPasswordHash())) {
            session.setAttribute("settingsError", "Incorrect current password.");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("settingsError", "New passwords do not match.");
            return false;
        }
        return true;
    }

    private boolean validateImageUpload(HttpServletRequest req, Part filePart) {
        String submittedType = filePart.getContentType();
        List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

        if (!allowedMimeTypes.contains(submittedType)) {
            req.getSession().setAttribute("settingsError", "Invalid file type. Please upload a PNG, JPG, or GIF image.");
            return false;
        }
        return true;
    }

    private boolean validateUsernameAvailability(HttpServletRequest req, String newUsername) throws SQLException {
        User existingUser = userDAO.findUser(newUsername);
        if (existingUser != null) {
            req.getSession().setAttribute("settingsError", "Username '" + newUsername + "' is already taken. Please choose another.");
            return false;
        }
        return true;
    }

    private String generateUniqueImageName(Part filePart) {
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    private void saveUploadedFile(Part filePart, String fileName) throws IOException {
        String uploadPath = getServletContext().getRealPath("") + File.separator + "images" + File.separator + "profiles";
        filePart.write(uploadPath + File.separator + fileName);
    }
}