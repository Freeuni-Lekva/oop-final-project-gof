<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.chat.Message" %>

<%
    String username = (String) session.getAttribute("user");
    if (username == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    int chatId = -1;
    try {
        chatId = Integer.parseInt(request.getParameter("chatId"));
    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }

    List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Interactive Story - StorySaga AI</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        .message-box {
            white-space: pre-wrap;
        }
    </style>
</head>
<body class="bg-gray-900 text-gray-200">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container mx-auto p-4 md:p-8 max-w-3xl">
    <div class="bg-gray-800/80 backdrop-blur-sm rounded-lg shadow-2xl p-6 space-y-6">

        <h1 class="text-3xl font-bold text-white mb-4">Story Chat</h1>

        <div class="space-y-4 max-h-[60vh] overflow-y-auto border border-gray-700 rounded-lg p-4 bg-black/10">
            <% if (messages != null) {
                for (Message m : messages) {
                    String bubbleClass = m.isUser() ? "bg-indigo-600 ml-auto" : "bg-gray-700 mr-auto";
                    String alignment = m.isUser() ? "text-right" : "text-left";
            %>
            <div class="max-w-[75%] rounded-lg px-4 py-3 <%= bubbleClass %> <%= alignment %> message-box">
                <%= m.getMessage() %>
            </div>
            <% } } else { %>
            <p class="text-gray-400">No messages yet.</p>
            <% } %>
        </div>

        <form action="<%= request.getContextPath() %>/chat-message" method="POST" class="mt-6">
            <input type="hidden" name="chatId" value="<%= chatId %>">
            <textarea name="userMessage" rows="4" required
                      placeholder="What do you do next?"
                      class="w-full bg-gray-900 border border-gray-700 rounded-md p-3 text-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition">
            </textarea>
            <button type="submit"
                    class="mt-3 bg-teal-600 hover:bg-teal-700 text-white font-semibold py-2 px-6 rounded-md transition duration-300">
                Send
            </button>
        </form>
    </div>
</div>

</body>
</html>
