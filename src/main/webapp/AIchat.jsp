<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.chat.Message" %>

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
        .custom-scrollbar::-webkit-scrollbar {
            width: 8px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
            background: rgba(0,0,0,0.1);
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
            background-color: #4a5568;
            border-radius: 20px;
            border: 3px solid rgba(0,0,0,0);
        }
        .message-box {
            /*white-space: pre-wrap;*/
            word-wrap: break-word;
        }
        .dot-flashing {
            position: relative;
            width: 10px;
            height: 10px;
            border-radius: 5px;
            background-color: #9ca3af;
            color: #9ca3af;
            animation: dotFlashing 1s infinite linear alternate;
            animation-delay: .5s;
        }
        .dot-flashing::before, .dot-flashing::after {
            content: '';
            display: inline-block;
            position: absolute;
            top: 0;
        }
        .dot-flashing::before {
            left: -15px;
            width: 10px;
            height: 10px;
            border-radius: 5px;
            background-color: #9ca3af;
            color: #9ca3af;
            animation: dotFlashing 1s infinite alternate;
            animation-delay: 0s;
        }
        .dot-flashing::after {
            left: 15px;
            width: 10px;
            height: 10px;
            border-radius: 5px;
            background-color: #9ca3af;
            color: #9ca3af;
            animation: dotFlashing 1s infinite alternate;
            animation-delay: 1s;
        }
        @keyframes dotFlashing {
            0% {
                background-color: #9ca3af;
            } 50%, 100% {background-color: rgba(156, 163, 175, 0.2); }
        }
    </style>
</head>
<body class="bg-gray-900 text-gray-200">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container mx-auto p-4 md:p-8 max-w-3xl">
    <div class="bg-gray-800/80 backdrop-blur-sm rounded-lg shadow-2xl p-6 flex flex-col h-[85vh]">

        <h1 class="text-3xl font-bold text-white mb-4 flex-shrink-0">Story Chat</h1>

        <div id="chat-window" class="flex-grow space-y-4 overflow-y-auto border border-gray-700 rounded-lg p-4 bg-black/10 custom-scrollbar">
            <%
                if (messages == null || messages.isEmpty()) {
            %>
            <p class="text-gray-400">The story begins... what will you do?</p>
            <%
            } else {
                for (Message m : messages) {
                    if (m.isUser()) {
            %>
            <div class="flex justify-end items-end gap-3">
                <div class="max-w-[75%] rounded-lg px-4 py-3 bg-indigo-600 message-box">
                    <p><%= m.getMessage().trim() %></p>
                </div>
                <div class="w-10 h-10 rounded-full bg-indigo-800 flex items-center justify-center font-bold text-lg flex-shrink-0" title="<%= username %>">
                    <%= Character.toUpperCase(username.charAt(0)) %>
                </div>
            </div>
            <%
            } else {
            %>
            <div class="flex justify-start items-end gap-3">
                <div class="w-10 h-10 rounded-full bg-teal-800 flex items-center justify-center font-bold text-lg flex-shrink-0" title="StorySaga AI">
                    AI
                </div>
                <div class="max-w-[75%] rounded-lg px-4 py-3 bg-gray-700 message-box">
                    <p><%= m.getMessage().trim() %></p>
                </div>
            </div>
            <%
                        }
                    }
                }
            %>
        </div>

        <form id="chat-form" class="mt-6 flex-shrink-0">
            <input type="hidden" id="chatId" value="<%= chatId %>">
            <textarea id="userMessage" name="userMessage" rows="3" required
                      placeholder="What do you do next?"
                      class="w-full bg-gray-900 border border-gray-700 rounded-md p-3 text-gray-200 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition resize-none"></textarea>
            <div class="flex justify-between items-center mt-3">
                <p class="text-xs text-gray-500">Shift+Enter for new line</p>
                <button id="send-button" type="submit"
                        class="bg-teal-600 hover:bg-teal-700 text-white font-semibold py-2 px-6 rounded-md transition duration-300 disabled:bg-gray-500 disabled:cursor-not-allowed">
                    Send
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    const chatWindow = document.getElementById('chat-window');
    const chatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('userMessage');
    const sendButton = document.getElementById('send-button');
    const chatId = document.getElementById('chatId').value;
    const username = "<%= username %>";

    function scrollToBottom() {
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }

    function appendMessage(text, isUser) {
        const messageContainer = document.createElement('div');
        messageContainer.className = `flex items-end gap-3 ${isUser ? 'justify-end' : 'justify-start'}`;
        const avatarInitial = isUser ? username[0].toUpperCase() : 'AI';
        const avatarTitle = isUser ? username : 'StorySaga AI';
        const avatarColor = isUser ? 'bg-indigo-800' : 'bg-teal-800';
        const bubbleColor = isUser ? 'bg-indigo-600' : 'bg-gray-700';
        const p = document.createElement('p');
        p.appendChild(document.createTextNode(text));
        const messageBubble = document.createElement('div');
        messageBubble.className = `max-w-[75%] rounded-lg px-4 py-3 ${bubbleColor} message-box`;
        messageBubble.appendChild(p);
        const avatar = document.createElement('div');
        avatar.className = `w-10 h-10 rounded-full ${avatarColor} flex items-center justify-center font-bold text-lg flex-shrink-0`;
        avatar.title = avatarTitle;
        avatar.textContent = avatarInitial;
        if (isUser) {
            messageContainer.appendChild(messageBubble);
            messageContainer.appendChild(avatar);
        } else {
            messageContainer.appendChild(avatar);
            messageContainer.appendChild(messageBubble);
        }
        chatWindow.appendChild(messageContainer);
        scrollToBottom();
    }

    function toggleLoadingIndicator(show) {
        let indicator = document.getElementById('loading-indicator');
        if (show && !indicator) {
            indicator = document.createElement('div');
            indicator.id = 'loading-indicator';
            indicator.className = 'flex justify-start items-end gap-3';
            indicator.innerHTML = `<div class="w-10 h-10 rounded-full bg-teal-800 flex items-center justify-center font-bold text-lg flex-shrink-0"
                                        title="StorySaga AI">AI</div>
                                    <div class="max-w-[75%] rounded-lg px-4 py-3 bg-gray-700 flex items-center">
                                        <div class="dot-flashing"></div>
                                    </div>`;
            chatWindow.appendChild(indicator);
            scrollToBottom();
        } else if (!show && indicator) {
            indicator.remove();
        }
    }

    document.addEventListener('DOMContentLoaded', scrollToBottom);

    chatForm.addEventListener('submit', async function(event) {
        event.preventDefault();
        const userMessage = messageInput.value.trim();
        if (!userMessage) return;
        appendMessage(userMessage, true);
        messageInput.value = '';
        sendButton.disabled = true;
        toggleLoadingIndicator(true);

        try {
            const response = await fetch('<%= request.getContextPath() %>/chat-message', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                },
                body: new URLSearchParams({
                    'chatId': chatId,
                    'userMessage': userMessage
                })
            });
            toggleLoadingIndicator(false);
            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }
            const data = await response.json();
            if(data.success && data.aiResponse) {
                appendMessage(data.aiResponse, false);
            } else {
                appendMessage(data.error || 'An unexpected error occurred.', false);
            }
        } catch (error) {
            console.error('Error sending message:', error);
            toggleLoadingIndicator(false);
            appendMessage('Sorry, there was a problem connecting to the server. Please try again.', false);
        } finally {
            sendButton.disabled = false;
            messageInput.focus();
        }
    });

    messageInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            chatForm.dispatchEvent(new Event('submit', { cancelable: true }));
        }
    });
</script>

</body>
</html>