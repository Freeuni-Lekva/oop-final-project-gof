package servlets;

import data.media.PostDAO;
import data.user.UserDAO;
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
            response.sendRedirect("login.jsp");
            return;
        }


        String profileUsername = request.getParameter("username");


        if (profileUsername == null || profileUsername.equals(viewerUsername)) {
            response.sendRedirect("profile");
            return;
        }


        try {
            UserDAO userDAO = new UserDAO();


            User viewer = userDAO.findUser(viewerUsername);
            if (viewer == null) {
                session.invalidate();
                response.sendRedirect("login.jsp");
                return;
            }

            User profileOwner = userDAO.findUser(profileUsername);

            if (profileOwner == null) {
                response.sendRedirect("home.jsp");
                return;
            }

            PostDAO postDAO = new PostDAO(); // Moved DAO instantiation here
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
}