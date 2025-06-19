package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name="postcreation", value="/postcreation")
public class StoryCreationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);

        if(session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        res.sendRedirect(req.getContextPath() + "/create-post.jsp");
    }
}