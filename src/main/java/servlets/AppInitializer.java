package servlets;
import data.*;
import data.chat.*;
import data.media.*;
import data.story.*;
import data.user.HistoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class AppInitializer implements ServletContextListener, HttpSessionListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        MySqlConnector mySqlConnector = new MySqlConnector();

        context.setAttribute("userDao", new UserDAO());
        context.setAttribute("historyDao", new HistoryDAO());
        context.setAttribute("storyDao", new StoryDAO());
        context.setAttribute("chatDao", new ChatDAO());
        context.setAttribute("messageDao", new MessageDAO());
        context.setAttribute("postDao", new PostDAO());
        context.setAttribute("commentDao", new CommentsDAO());
        context.setAttribute("likeDao", new LikesDAO());
        context.setAttribute("tagDao", new TagsDAO());

        context.setAttribute("mySqlConnector", mySqlConnector);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

}
