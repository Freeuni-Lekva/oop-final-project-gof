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

    boolean isOwner = (Boolean) request.getAttribute("isOwner");
    boolean isShared = (Boolean) request.getAttribute("isShared");
%>
<!DOCTYPE html>
<html>
<head>
    <title>StorySaga AI - A New Adventure</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        #create-story-btn, #logout-btn {
            background-color: #5D3FD3;
            color: white;
            text-shadow: 0 0 5px rgba(255, 255, 255, 0.7);
        }

        #create-story-btn:hover, #logout-btn:hover {
            background-color: #724ae8; /* A slightly lighter purple for hover */
        }
    </style>

    <style>
        @import url('https://fonts.googleapis.com/css2?family=Turret+Road:wght@400;700;800&display=swap');

        :root {
            --background-color: #000000;
            --container-bg: rgba(16, 16, 22, 0.8);
            --border-color: rgba(114, 27, 228, 0.5);
            --glow-color: rgba(173, 82, 255, 0.7);
            --user-text-color: #00e5ff;
            --ai-text-color: #f0f0f0;
            --accent-color: #721be4;
            --accent-hover: #9d4bff;
            --placeholder-color: rgba(240, 240, 240, 0.4);
        }

        body {
            font-family: 'Turret Road', monospace;
            background-color: var(--background-color);
            color: var(--ai-text-color);
            background-image: radial-gradient(circle at top right, var(--accent-color) -100%, transparent 40%),
            radial-gradient(circle at bottom left, var(--user-text-color) -100%, transparent 40%);
            background-attachment: fixed;
            font-weight: 700;
        }

        .custom-scrollbar::-webkit-scrollbar {
            width: 10px;
        }

        .custom-scrollbar::-webkit-scrollbar-track {
            background: transparent;
        }

        .custom-scrollbar::-webkit-scrollbar-thumb {
            background-color: var(--accent-color);
            border-radius: 10px;
            border: 2px solid var(--background-color);
        }

        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
            background-color: var(--accent-hover);
        }

        .message-container {
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }

        .message-base {
            white-space: pre-wrap;
            word-wrap: break-word;
            line-height: 1.8;
            padding: 0.75rem 1.25rem;
            border-radius: 12px;
            max-width: 90%;
            animation: fadeIn 0.5s ease-in-out;
        }

        .message-ai {
            background-color: rgba(40, 42, 54, 0.5);
            align-self: flex-start;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .message-user {
            background-color: transparent;
            color: var(--user-text-color);
            align-self: flex-end;
            text-align: right;
            border: 1px solid rgba(0, 229, 255, 0.2);
            text-shadow: 0 0 5px rgba(0, 229, 255, 0.5);
        }

        .message-user::before {
            content: ' you > ';
            font-weight: 800;
            opacity: 0.7;
        }

        .title-glow {
            text-shadow: 0 0 8px var(--glow-color), 0 0 20px var(--accent-color);
            color: white;
        }

        .chat-input-area {
            position: relative;
            border: 2px solid var(--border-color);
            border-radius: 10px;
            padding: 10px;
            background-color: rgba(0,0,0,0.2);
            transition: all 0.3s ease;
            box-shadow: 0 0 15px rgba(114, 27, 228, 0.0);
        }

        .chat-input-area:focus-within {
            border-color: var(--accent-hover);
            box-shadow: 0 0 15px var(--glow-color);
        }

        #userMessage {
            background: transparent;
            border: none;
            box-shadow: none;
            outline: none;
            resize: none;
            width: 100%;
            color: var(--ai-text-color);
            font-family: 'Turret Road', monospace;
            font-weight: 700;
            caret-color: var(--user-text-color);
        }

        #userMessage::placeholder {
            color: var(--placeholder-color);
            font-weight: 400;
        }

        .send-button {
            background: linear-gradient(45deg, var(--accent-color), var(--accent-hover));
            color: white;
            font-weight: 800;
            border-radius: 8px;
            padding: 10px 20px;
            transition: all 0.3s ease;
            box-shadow: 0 0 10px var(--glow-color);
            border: none;
        }

        .send-button:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 0 20px var(--glow-color);
        }

        .send-button:disabled {
            background: #333;
            color: #666;
            cursor: not-allowed;
            box-shadow: none;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        #typing-indicator {
            align-self: flex-start;
        }

        #typing-cursor {
            display: inline-block;
            width: 10px;
            height: 1.2rem;
            background-color: var(--ai-text-color);
            animation: blink 1s step-end infinite;
            margin-left: 5px;
        }

        @keyframes blink {
            from, to { background-color: transparent }
            50% { background-color: var(--ai-text-color); }
        }

        .action-button {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            width: 100%;
            font-weight: 800;
            border-radius: 8px;
            padding: 10px 20px;
            transition: all 0.3s ease;
            color: white;
            border: none;
        }
        .share-button {
            background: linear-gradient(45deg, #1f4296, #2d64d8);
        }
        .share-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 15px rgba(45, 100, 216, 0.7);
        }
        .unshare-button {
            background: linear-gradient(45deg, #9b2c2c, #e53e3e);
        }
        .unshare-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 15px rgba(229, 62, 62, 0.7);
        }
    </style>
</head>
<body class="bg-black text-gray-200">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container mx-auto p-4 md:p-8 max-w-4xl">
    <div class="bg-container-bg backdrop-blur-md rounded-xl shadow-2xl p-6 flex flex-col h-[85vh] border border-border-color"
         style="box-shadow: 0 0 30px var(--glow-color);">
        <h1 class="text-4xl font-extrabold mb-4 flex-shrink-0 text-center title-glow">
            📜 StorySaga AI 🔮
        </h1>
        <div id="chat-window" class="flex-grow overflow-y-auto p-4 md:p-6 custom-scrollbar">
            <div id="message-container" class="message-container">
                <%
                    if (messages == null || messages.isEmpty()) {
                %>
                <div class="message-base message-ai">
                    <p>The veil of reality thins... A new story awaits your command. What is your first action?</p>
                </div>
                <%
                } else {
                    for (Message m : messages) {
                        if (m.isUser()) {
                %>
                <div class="message-base message-user">
                    <p><%= m.getMessage().trim() %></p>
                </div>
                <%
                } else {
                %>
                <div class="message-base message-ai">
                    <p><%= m.getMessage().trim() %></p>
                </div>
                <%
                            }
                        }
                    }
                %>
            </div>
        </div>

        <% if (isOwner) { %>
        <div class="mt-4 flex-shrink-0 pt-4 border-t border-[var(--border-color)]">
            <form action="<%= request.getContextPath() %>/chat-action" method="POST">
                <input type="hidden" name="chatId" value="<%= chatId %>">
                <% if (isShared) { %>
                <input type="hidden" name="action" value="unshare_chat">
                <button type="submit" class="action-button unshare-button">
                    <i class="fas fa-eye-slash"></i>
                    <span>Unshare Chat</span>
                </button>
                <% } else { %>
                <input type="hidden" name="action" value="share_chat">
                <button type="submit" class="action-button share-button">
                    <i class="fas fa-share-alt"></i>
                    <span>Share Chat</span>
                </button>
                <% } %>
            </form>
        </div>
        <% } %>


        <% if (isOwner) { %>
        <form id="chat-form" class="mt-4 flex-shrink-0 pt-4">
            <input type="hidden" id="chatId" value="<%= chatId %>">
            <div class="chat-input-area">
                <textarea id="userMessage" name="userMessage" rows="1" required
                          placeholder="Declare your next move..."
                          class="text-lg"></textarea>
            </div>
            <div class="flex justify-end items-center mt-3">
                <button id="send-button" type="submit" class="send-button">
                    <span>SEND</span>
                </button>
            </div>
        </form>
        <% } else { %>
        <div class="mt-4 flex-shrink-0 pt-4 border-t border-[var(--border-color)] text-center text-gray-400">
            <p>You are viewing a shared chat. Only the owner can continue the story.</p>
        </div>
        <% } %>
    </div>
</div>

<script>
    const messageContainer = document.getElementById('message-container');
    const chatWindow = document.getElementById('chat-window');
    const chatForm = document.getElementById('chat-form');
    const messageInput = document.getElementById('userMessage');
    const sendButton = document.getElementById('send-button');
    const chatId = document.getElementById('chatId').value;
    const username = "<%= username %>";

    function scrollToBottom() {
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }

    function typeWriter(element, text, onComplete) {
        let i = 0;
        element.innerHTML = "";
        const typingIndicator = document.createElement('span');
        typingIndicator.id = 'typing-cursor';

        function type() {
            if (i < text.length) {
                element.innerHTML = text.substring(0, i + 1);
                element.appendChild(typingIndicator);
                i++;
                setTimeout(type, 30);
                scrollToBottom();
            } else {
                typingIndicator.remove();
                if (onComplete) {
                    onComplete();
                }
            }
        }
        type();
    }

    function appendUserMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message-base message-user';
        const p = document.createElement('p');
        p.textContent = text.trim();
        messageDiv.appendChild(p);
        messageContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    function appendAndAnimateAiMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message-base message-ai';
        const p = document.createElement('p');
        messageDiv.appendChild(p);
        messageContainer.appendChild(messageDiv);

        typeWriter(p, text, () => {
            sendButton.disabled = false;
            messageInput.disabled = false;
            messageInput.focus();
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        scrollToBottom();
        messageInput.focus();
    });

    chatForm.addEventListener('submit', async function(event) {
        event.preventDefault();
        const userMessage = messageInput.value.trim();
        if (!userMessage) return;

        appendUserMessage(userMessage);

        messageInput.value = '';
        messageInput.style.height = 'auto';
        sendButton.disabled = true;
        messageInput.disabled = true;

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

            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }

            const data = await response.json();
            if(data.success && data.aiResponse) {
                appendAndAnimateAiMessage(data.aiResponse);
            } else {
                appendAndAnimateAiMessage(data.error || 'An unexpected error occurred.');
            }
        } catch (error) {
            console.error('Error sending message:', error);
            appendAndAnimateAiMessage('// CONNECTION LOST: Unable to reach the story realm. Please try again. //');
        }
    });

    messageInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            if(!sendButton.disabled) {
                chatForm.dispatchEvent(new Event('submit', { cancelable: true }));
            }
        }
    });

    messageInput.addEventListener('input', () => {
        messageInput.style.height = 'auto';
        let newHeight = messageInput.scrollHeight;
        if(newHeight > 200) newHeight = 200;
        messageInput.style.height = newHeight + 'px';
    });
</script>

</body>
</html>
