package servlets;
import data.*;
import data.chat.*;
import data.media.*;
import data.story.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class AppInitializer implements ServletContextListener, HttpSessionListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        MySqlConnector mySqlConnector = new MySqlConnector();

        context.setAttribute("userDao", new UserDAO(mySqlConnector));
        context.setAttribute("storyDao", new StoryDAO(mySqlConnector));
        context.setAttribute("chatDao", new ChatDAO());
        context.setAttribute("messageDao", new MessageDAO());
        context.setAttribute("postDao", new PostDAO(mySqlConnector));
        context.setAttribute("commentDao", new CommentsDAO(mySqlConnector));
        context.setAttribute("likeDao", new LikesDAO(mySqlConnector));
        context.setAttribute("tagDao", new TagsDAO(mySqlConnector));

        context.setAttribute("mySqlConnector", mySqlConnector);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

}
