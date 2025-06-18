package servlets;
import data.*;
import data.chat.*;
import data.media.*;
import data.story.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.SQLException;

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
