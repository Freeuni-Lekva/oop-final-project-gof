package servlets;

import data.chat.ChatDAO;
import data.chat.MessageDAO;
import data.user.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import model.chat.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ChatServlet", value = "/chat")
public class ChatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;

        if (username == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        int chatId;
        try {
            chatId = Integer.parseInt(req.getParameter("chatId"));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

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

            List<Message> messages = messageDAO.getMessages(chatId);
            req.setAttribute("messages", messages);
            req.setAttribute("chatId", chatId);

        } catch (SQLException e) {
            throw new ServletException("Error loading chat", e);
        }

        req.getRequestDispatcher("/AIchat.jsp").forward(req, res);
    }
}
