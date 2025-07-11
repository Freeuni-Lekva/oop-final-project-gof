package servlets;

import data.media.CommentsDAO;
import data.media.LikesDAO;
import data.story.StoryDAO;
import data.user.HistoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebServlet(name = "UserStatsServlet", value = "/stats")
public class UserStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;

        if (username == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        LikesDAO likesDAO = (LikesDAO) context.getAttribute("likeDao");
        CommentsDAO commentsDAO = (CommentsDAO) context.getAttribute("commentDao");
        HistoryDAO historyDAO = (HistoryDAO) context.getAttribute("historyDao");

        try {
            User user = userDAO.findUser(username);

            long accountAgeInDays = ChronoUnit.DAYS.between(user.getRegisterTime(), LocalDateTime.now());
            String chartPeriod;
            String chartLabel;

            // Changes the time period so each bar in the chart in userStats.jsp
            // is of the appropriate size. Otherwise, it would get too thin over time.
            if (accountAgeInDays > 365) {
                chartPeriod = "monthly";
                chartLabel = "Comments per Month";
            } else if (accountAgeInDays > 90) {
                chartPeriod = "weekly";
                chartLabel = "Comments per Week";
            } else {
                chartPeriod = "daily";
                chartLabel = "Comments per Day";
            }

            int storiesCreated = storyDAO.countStoriesByCreator(user.getUserId());
            int likesGiven = likesDAO.countLikesMadeByUser(user.getUserId());
            int followingCount = userDAO.countFollowing(user.getUserId());
            int followerCount = userDAO.countFollowers(user.getUserId());
            Map<String, Long> commentStats = commentsDAO.getCommentStatsByUser(user.getUserId(), chartPeriod);
            Map<String, Long> readStats = historyDAO.getReadStatsByUser(user.getUserId(), chartPeriod);
            int totalStoriesRead = historyDAO.countStoriesReadByUser(user.getUserId());

            req.setAttribute("totalStoriesRead", totalStoriesRead);
            req.setAttribute("readStats", readStats);
            req.setAttribute("profileUser", user);
            req.setAttribute("storiesCreated", storiesCreated);
            req.setAttribute("likesGiven", likesGiven);
            req.setAttribute("followingCount", followingCount);
            req.setAttribute("followerCount", followerCount);
            req.setAttribute("commentStats", commentStats);
            req.setAttribute("chartLabel", chartLabel);

            req.getRequestDispatcher("/userStats.jsp").forward(req, resp);

        } catch (SQLException e) {
            System.err.println("Database error loading user statistics for user: " + username);
            e.printStackTrace();
            throw new ServletException("Database error loading user statistics.", e);
        }
    }
}