package servlets;

import data.media.PostDAO;
import data.story.StoryDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.User;
import model.media.Post;
import model.story.Character;
import model.story.PromptBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet(name="postcreation", value="/postcreation")
@MultipartConfig
public class StoryCreationServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "posts";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        req.getRequestDispatcher("/create-post.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ServletContext context = getServletContext();
        StoryDAO storyDAO = (StoryDAO) context.getAttribute("storyDao");
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        PostDAO postDAO = (PostDAO) context.getAttribute("postDao");
        String username = (String) session.getAttribute("user");

        int userId = 0;
        boolean isCreator = false;
        try {
            User user = userDAO.findUser(username);
            userId = user.getUserId();
            isCreator = user.isCreator();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String description = req.getParameter("description");
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

        String[] selectedTags = req.getParameterValues("storyTags");
        List<String> tagList = (selectedTags != null) ? Arrays.asList(selectedTags) : new ArrayList<>();

        PromptBuilder builder = new PromptBuilder(characters, worldInfo);
        String firstPrompt = builder.build();

        int newStoryId = 0;
        try {
            newStoryId = storyDAO.createStoryAndGetId(title, firstPrompt, description, userId);
            if (!isCreator) userDAO.SetCreator(userId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (newStoryId == -1) {
            System.err.println("Failed to create a new story for user " + userId);
            res.sendRedirect(req.getContextPath() + "/create-post.jsp?error=creationFailed");
            return;
        }

        try {
            storyDAO.linkTagsToStory(newStoryId, tagList);
        } catch (SQLException e) {
            System.err.println("Failed to link tags to new story " + newStoryId + ": " + e.getMessage());
            e.printStackTrace();
        }

        Part filePart = req.getPart("coverImage");
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        String applicationPath = getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        filePart.write(uploadFilePath + File.separator + uniqueFileName);

        Post newPost = new Post(0, newStoryId, uniqueFileName, LocalDateTime.now(), 0, 0);
        try {
            postDAO.addPost(newPost);
        } catch (SQLException e) {
            System.err.println("Failed to create post record for story " + newStoryId);
            e.printStackTrace();
        }

        res.sendRedirect(req.getContextPath() + "/home");
    }

}