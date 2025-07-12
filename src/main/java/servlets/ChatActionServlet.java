package servlets;

import data.chat.ChatDAO;
import data.chat.SharedChatDAO;
import data.user.UserDAO;
import model.User;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles sharing and unsharing chat
 */
@WebServlet(name = "ChatActionServlet", value = "/chat-action")
public class ChatActionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("user") : null;

        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        int chatId;

        try {
            chatId = Integer.parseInt(request.getParameter("chatId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/chat?chatId=" + chatId);
            return;
        }

        ServletContext context = getServletContext();
        UserDAO userDao = (UserDAO) context.getAttribute("userDao");
        ChatDAO chatDao = (ChatDAO) context.getAttribute("chatDao");
        SharedChatDAO sharedChatDao = (SharedChatDAO) context.getAttribute("sharedChatDao");

        try {
            User loggedInUser = userDao.findUser(username);
            if (loggedInUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            int userId = loggedInUser.getUserId();

            int ownerId = chatDao.getUserId(chatId);
            if (ownerId != userId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to perform this action on this chat.");
                return;
            }

            switch (action) {
                case "share_chat":
                    sharedChatDao.shareChat(chatId, userId);
                    break;

                case "unshare_chat":
                    sharedChatDao.unshareChat(chatId);
                    break;

                default:
                    break;
            }

            String fromProfile = request.getParameter("fromProfile");
            if(fromProfile == null || fromProfile.trim().isEmpty() || !fromProfile.equals("true") ) {
                response.sendRedirect(request.getContextPath() + "/chat?chatId=" + chatId);
            } else {
                response.sendRedirect(request.getContextPath() + "/profile#shared-chats");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("A database error occurred while processing the chat action.", e);
        }
    }
}