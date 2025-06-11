package servlets;
import javax.servlet.*;
import javax.servlet.http.*;

public class AppInitializer implements ServletContextListener, HttpSessionListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

}
