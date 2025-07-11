package servlets;

import data.media.PostDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.media.Post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "PublicProfileServlet", value = "/user")
public class PublicProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        String viewerUsername = (session != null) ? (String) session.getAttribute("user") : null;

        if (viewerUsername == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String profileUsername = request.getParameter("username");

        if (profileUsername == null || profileUsername.equals(viewerUsername)) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        try {
            ServletContext context = getServletContext();
            UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

            User viewer = userDAO.findUser(viewerUsername);
            if (viewer == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User profileOwner = userDAO.findUser(profileUsername);

            if (profileOwner == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            PostDAO postDAO = (PostDAO) context.getAttribute("postDao");
            List<Post> userPosts = postDAO.getPostsByCreatorId(profileOwner.getUserId());
            List<User> followersList = userDAO.findFollowers(profileOwner.getUserId());
            List<User> followingList = userDAO.findFollowing(profileOwner.getUserId());
            boolean isFollowing = userDAO.isFollowing(viewer.getUserId(), profileOwner.getUserId());

            request.setAttribute("profileOwner", profileOwner);
            request.setAttribute("userPosts", userPosts);
            request.setAttribute("followersList", followersList);
            request.setAttribute("followingList", followingList);
            request.setAttribute("isFollowing", isFollowing);

            request.getRequestDispatcher("/PublicProfile.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error loading public profile for " + profileUsername, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String viewerUsername = (session != null) ? (String) session.getAttribute("user") : null;

        if (viewerUsername == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String profileOwnerUsername = request.getParameter("profileUsername");

        try {
            ServletContext context = getServletContext();
            UserDAO userDAO = (UserDAO) context.getAttribute("userDao");

            User viewer = userDAO.findUser(viewerUsername);
            User profileOwner = userDAO.findUser(profileOwnerUsername);

            if (viewer == null || profileOwner == null || action == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            if ("follow".equals(action)) {
                userDAO.followUser(viewer.getUserId(), profileOwner.getUserId());
            } else if ("unfollow".equals(action)) {
                userDAO.unfollowUser(viewer.getUserId(), profileOwner.getUserId());
            }

            response.sendRedirect("user?username=" + profileOwnerUsername);

        } catch (SQLException e) {
            throw new ServletException("Database error performing follow/unfollow action.", e);
        }
    }
}