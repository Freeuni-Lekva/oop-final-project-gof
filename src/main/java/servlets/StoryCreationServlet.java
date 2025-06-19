package servlets;

import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.story.PromptBuilder;
import model.story.Character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);


        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        ServletContext context = getServletContext();
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        String username = (String) session.getAttribute("user");
        int userId = userDAO.findUser(username).getUserId();

        String title = req.getParameter("title");
        String worldInfo = req.getParameter("worldInfo");

        List<Character> characters = new ArrayList<>();

        String[] names = req.getParameterValues("characterName");
        String[] agesStr = req.getParameterValues("characterAge");
        String[] genders = req.getParameterValues("characterGender");
        String[] species = req.getParameterValues("characterSpecies");
        String[] descriptions = req.getParameterValues("characterDescription");

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                try {
                    String charName = names[i];
                    int charAge = Integer.parseInt(agesStr[i]);
                    String charGender = genders[i];
                    String charSpecies = species[i];
                    String charDesc = descriptions[i];

                    Character character = new Character(charName, charAge, charGender, charSpecies, charDesc);
                    characters.add(character);
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse age for character: " + names[i] + ". Value was: " + agesStr[i]);
                }
            }
        }

        PromptBuilder builder = new PromptBuilder(characters, worldInfo);
        String firstPrompt = builder.build();
        storyDAO.createStory(title, firstPrompt, userId);

        res.sendRedirect(req.getContextPath() + "/home.jsp");
    }

}