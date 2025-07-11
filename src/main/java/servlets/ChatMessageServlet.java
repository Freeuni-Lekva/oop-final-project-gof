package servlets;

import com.google.gson.Gson;
import data.chat.ChatDAO;
import data.chat.MessageDAO;
import data.user.HistoryDAO;
import data.user.UserDAO;
import gemini.AiAPI;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.chat.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ChatMessageServlet", value = "/chat-message")
public class ChatMessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;

        if (username == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int storyId;
        try {
            storyId = Integer.parseInt(req.getParameter("storyId"));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        ChatDAO chatDAO = (ChatDAO) context.getAttribute("chatDao");
        HistoryDAO historyDAO = (HistoryDAO) context.getAttribute("historyDao");

        try {
            int userId = userDAO.findUser(username).getUserId();
            int chatId = chatDAO.getChatId(userId, storyId);

            if (chatId == -1) {
                chatId = chatDAO.createChat(userId, storyId);
                historyDAO.addReadHistory(userId, storyId);
            }

            res.sendRedirect(req.getContextPath() + "/chat?chatId=" + chatId);
        } catch (SQLException e) {
            throw new ServletException("Failed to create or get chat", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;

        if (username == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        Map<String, Object> response = new HashMap<>();
        Gson gson = new Gson();

        int chatId;
        String userMessage = req.getParameter("userMessage");

        try {
            chatId = Integer.parseInt(req.getParameter("chatId"));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if (userMessage == null || userMessage.trim().isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/chat?chatId=" + chatId);
            return;
        }

        userMessage = userMessage.trim();

        ServletContext context = getServletContext();
        UserDAO userDAO = (UserDAO) context.getAttribute("userDao");
        ChatDAO chatDAO = (ChatDAO) context.getAttribute("chatDao");
        MessageDAO messageDAO = (MessageDAO) context.getAttribute("messageDao");

        try {
            int userId = userDAO.findUser(username).getUserId();
            int ownerId = chatDAO.getUserId(chatId);
            if (ownerId != userId) {
                res.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            messageDAO.addMessage(chatId, userMessage, true);

            List<Message> messages = messageDAO.getMessages(chatId);
            StringBuilder promptBuilder = new StringBuilder();
            for (Message msg : messages) {
                promptBuilder.append(msg.getMessage()).append("\n");
            }

            String prompt = promptBuilder.toString().trim();

            AiAPI ai = (AiAPI) context.getAttribute("AI_API");
            String AIResponse = ai.generateAnswer(prompt);

            AIResponse = AIResponse.trim();

            messageDAO.addMessage(chatId, AIResponse, false);

            response.put("success", true);
            response.put("aiResponse", AIResponse);

            res.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());

            res.getWriter().write(gson.toJson(response));
//            throw new ServletException("Error handling chat message", e);
        }

//        res.sendRedirect(req.getContextPath() + "/chat?chatId=" + chatId);
    }
}
