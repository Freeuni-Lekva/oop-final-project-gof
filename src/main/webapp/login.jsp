<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - StoryAI</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; background-color: #111827; color: #E5E7EB; }
        .font-orbitron { font-family: 'Orbitron', sans-serif; }
        .hero-section { background-image: url('<%= request.getContextPath() %>/images/design/img.jpg'); background-attachment: fixed; background-position: center; background-repeat: no-repeat; background-size: cover; position: relative; }
        .dark-overlay::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0, 0, 0, 0.7); z-index: 1; }
        .form-container { position: relative; z-index: 2; }
    </style>
</head>
<body>

<main class="hero-section min-h-screen flex flex-col justify-center items-center p-4 sm:p-6 dark-overlay">
    <div class="form-container w-full max-w-md">
        <div class="bg-gray-900 bg-opacity-75 backdrop-blur-lg rounded-xl p-8 shadow-2xl border border-gray-700">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold font-orbitron tracking-widest text-white">StoryAI</h1>
                <p class="text-gray-400 mt-2">Welcome back, adventurer.</p>
            </div>

            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
            <div id="error-alert" class="bg-red-900/50 border border-red-500 text-red-300 px-4 py-3 rounded-md mb-6 text-sm flex items-start justify-between" role="alert">
                <span><%= error %></span>
                <button type="button" id="close-error-button" class="ml-4 -mt-1 -mr-1 p-1 rounded-md text-red-300 hover:text-white hover:bg-red-500/50 focus:outline-none focus:ring-2 focus:ring-white">
                    <span class="sr-only">Dismiss</span> <!-- For screen readers -->
                    <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                </button>
            </div>
            <%
                }
            %>

            <form action="login" method="post" class="space-y-6">
                <div>
                    <label for="username" class="block text-sm font-bold mb-2 text-gray-300">Username</label>
                    <input type="text" id="username" name="username" placeholder="Enter your username" required
                           class="w-full px-4 py-3 bg-gray-800 border border-gray-600 rounded-md text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-300">
                </div>

                <div>
                    <label for="password" class="block text-sm font-bold mb-2 text-gray-300">Password</label>
                    <div class="relative">
                        <input type="password" id="password" name="password" placeholder="Enter your password" required
                               class="w-full px-4 py-3 bg-gray-800 border border-gray-600 rounded-md text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-300 pr-10">
                        <button type="button" id="togglePassword" class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-200">
                            <svg id="eyeIcon" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
                            <svg id="eyeSlashIcon" class="h-5 w-5 hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg>
                        </button>
                    </div>
                </div>

                <div>
                    <button type="submit" class="w-full bg-purple-600 hover:bg-purple-700 text-white font-bold py-3 px-4 rounded-md shadow-lg hover:shadow-purple-600/50 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-900 focus:ring-purple-500">
                        Login
                    </button>
                </div>
            </form>

            <div class="text-center mt-6">
                <p class="text-sm text-gray-400">Don't have an account? <a href="register" class="font-semibold text-purple-400 hover:text-purple-300 transition-colors duration-300">Join the adventure</a></p>
            </div>
        </div>
    </div>
</main>

<script>
    const passwordInput = document.getElementById('password');
    const togglePasswordButton = document.getElementById('togglePassword');
    if (togglePasswordButton) {
        const eyeIcon = document.getElementById('eyeIcon');
        const eyeSlashIcon = document.getElementById('eyeSlashIcon');
        togglePasswordButton.addEventListener('click', function () {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            eyeIcon.classList.toggle('hidden');
            eyeSlashIcon.classList.toggle('hidden');
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        const errorAlert = document.getElementById('error-alert');
        const closeErrorButton = document.getElementById('close-error-button');

        if (errorAlert && closeErrorButton) {
            closeErrorButton.addEventListener('click', function() {
                errorAlert.classList.add('hidden');
            });
        }
    });
</script>

</body>
</html>