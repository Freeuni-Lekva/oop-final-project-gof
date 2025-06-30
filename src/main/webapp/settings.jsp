<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 6/30/2025
  Time: 1:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
  <title>Profile Settings - StoryAI</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
  <style>
    body { font-family: 'Poppins', sans-serif; background-color: #111827; color: #E5E7EB; }
    .font-orbitron { font-family: 'Orbitron', sans-serif; }
  </style>
</head>
<body class="bg-gray-900">

<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
  <header class="mb-6 flex justify-between items-center">
    <h1 class="text-4xl font-bold font-orbitron text-white">
      Profile Settings
    </h1>
    <a href="<%= request.getContextPath() %>/profile" class="text-purple-400 hover:text-purple-300 transition-colors duration-300 font-semibold">
      ← Back to Profile
    </a>
  </header>

  <%
    User currentUser = (User) request.getAttribute("currentUser");

    if (currentUser == null) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }
    String profilePicturePath = request.getContextPath() + "/images/profiles/" + currentUser.getImageName();
  %>
  <%
    String error = (String) session.getAttribute("settingsError");
    if (error != null) {
  %>
  <div id="error-alert" class="bg-red-900/50 border border-red-500 text-red-300 px-4 py-3 rounded-md mb-6 text-sm flex items-start justify-between max-w-2xl mx-auto" role="alert">
    <span><%= error %></span>
    <button type="button" id="close-error-button" class="ml-4 -mt-1 -mr-1 p-1 rounded-md text-red-300 hover:text-white hover:bg-red-500/50 focus:outline-none focus:ring-2 focus:ring-white">
      <span class="sr-only">Dismiss</span>
      <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
    </button>
  </div>
  <%
      session.removeAttribute("settingsError");
    }
  %>
  <main>

    <form action="<%= request.getContextPath() %>/settings" method="POST" enctype="multipart/form-data" class="bg-gray-800 p-8 rounded-lg max-w-2xl mx-auto space-y-8">

      <div class="flex items-center space-x-6">
        <img src="<%= profilePicturePath %>" alt="Current Profile Picture" class="w-24 h-24 rounded-full object-cover border-2 border-purple-400">
        <div>
          <label for="profilePicture" class="block text-sm font-medium text-gray-300">Change Profile Picture</label>
          <input type="file" name="profilePicture" id="profilePicture" accept="image/png, image/jpeg, image/gif" class="mt-1 block w-full text-sm text-gray-400
                      file:mr-4 file:py-2 file:px-4
                      file:rounded-md file:border-0
                      file:text-sm file:font-semibold
                      file:bg-purple-600 file:text-white
                      hover:file:bg-purple-700">
        </div>
      </div>

      <div>
        <label for="username" class="block text-sm font-medium text-gray-300">Username</label>
        <input type="text" name="username" id="username" value="<%= currentUser.getUsername() %>" class="mt-1 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm py-2 px-3 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500">
      </div>

      <div class="border-t border-gray-700 pt-8">
        <h3 class="text-lg font-semibold text-white">Change Password</h3>
        <p class="text-sm text-gray-400 mb-4">Leave these fields blank to keep your current password.</p>

        <div class="space-y-4">
          <div>
            <label for="currentPassword" class="block text-sm font-medium text-gray-300">Current Password</label>
            <div class="relative mt-1">
              <input type="password" name="currentPassword" id="currentPassword" placeholder="Enter your current password" class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-300 pr-10">
              <button type="button" id="toggleCurrentPasswordBtn" class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-200">
                <svg id="currentEye" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
                <svg id="currentEyeSlash" class="h-5 w-5 hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg>
              </button>
            </div>
          </div>

          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-300">New Password</label>
            <div class="relative mt-1">
              <input type="password" name="newPassword" id="newPassword" class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-300 pr-10">
              <button type="button" id="toggleNewPasswordBtn" class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-200">
                <svg id="newEye" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
                <svg id="newEyeSlash" class="h-5 w-5 hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg>
              </button>
            </div>
            <div id="strength-meter" class="mt-4 space-y-2 hidden">
              <div class="w-full bg-gray-700 rounded-full h-2"><div id="strength-bar" class="h-2 rounded-full transition-all duration-300" style="width: 0%;"></div></div>
              <p class="text-xs font-bold" id="strength-text"></p>
              <ul class="text-xs text-gray-400 space-y-1">
                <li id="length-check" class="flex items-center"><svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg><span>At least 8 characters</span></li>
                <li id="upper-check" class="flex items-center"><svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg><span>One uppercase letter (A-Z)</span></li>
                <li id="number-check" class="flex items-center"><svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg><span>One number (0-9)</span></li>
                <li id="special-check" class="flex items-center"><svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg><span>One special character (!, @, #, etc.)</span></li>
              </ul>
            </div>
          </div>
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-300">Confirm New Password</label>
            <div class="relative mt-1">
              <input type="password" name="confirmPassword" id="confirmPassword" class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-300 pr-10">
              <button type="button" id="toggleConfirmPasswordBtn" class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-200">
                <svg id="confirmEye" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
                <svg id="confirmEyeSlash" class="h-5 w-5 hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="flex justify-end">
        <button type="submit" id="save-changes-button"
                class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-6 rounded-md transition-colors duration-300
               disabled:bg-gray-600 disabled:shadow-none disabled:cursor-not-allowed">
          Save Changes
        </button>
      </div>
    </form>
  </main>

</div>
<script>


  document.addEventListener('DOMContentLoaded', function () {
    const passwordInput = document.getElementById('newPassword');
    const strengthMeter = document.getElementById('strength-meter');
    const strengthBar = document.getElementById('strength-bar');
    const strengthText = document.getElementById('strength-text');
    const saveButton = document.getElementById('save-changes-button');
    const errorAlert = document.getElementById('error-alert');
    const closeErrorButton = document.getElementById('close-error-button');

    if (errorAlert && closeErrorButton) {
      closeErrorButton.addEventListener('click', function() {
        errorAlert.classList.add('hidden');
      });
    }


    if(passwordInput) {
      passwordInput.addEventListener('input', function() {
        const password = this.value;

        if (password.length === 0) {
          strengthMeter.classList.add('hidden');
          saveButton.disabled = false;
          return;
        }
        strengthMeter.classList.remove('hidden');

        let score = 0;
        const hasUpper = /[A-Z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasSpecial = /[^A-Za-z0-9]/.test(password);
        const isLongEnough = password.length >= 8;

        if (hasUpper) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;
        if (isLongEnough) score++;

        updateChecklistItem(document.getElementById('length-check'), isLongEnough);
        updateChecklistItem(document.getElementById('upper-check'), hasUpper);
        updateChecklistItem(document.getElementById('number-check'), hasNumber);
        updateChecklistItem(document.getElementById('special-check'), hasSpecial);

        switch (score) {
          case 0: case 1:
            strengthText.textContent = 'Strength: Weak';
            strengthText.className = 'text-xs font-bold text-red-500';
            strengthBar.className = 'h-2 rounded-full bg-red-500 transition-all duration-300';
            strengthBar.style.width = '25%';
            saveButton.disabled = true;
            break;
          case 2: case 3:
            strengthText.textContent = 'Strength: Medium';
            strengthText.className = 'text-xs font-bold text-yellow-500';
            strengthBar.className = 'h-2 rounded-full bg-yellow-500 transition-all duration-300';
            strengthBar.style.width = '66%';
            saveButton.disabled = !isLongEnough;
            break;
          case 4:
            strengthText.textContent = 'Strength: Strong';
            strengthText.className = 'text-xs font-bold text-green-500';
            strengthBar.className = 'h-2 rounded-full bg-green-500 transition-all duration-300';
            strengthBar.style.width = '100%';
            saveButton.disabled = false;
            break;
        }
      });
    }

    function updateChecklistItem(element, isValid) {
      const checkIcon = `<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>`;
      const crossIcon = `<svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>`;
      const icon = isValid ? checkIcon : crossIcon;
      const textClass = isValid ? 'text-green-400' : 'text-red-400';
      element.innerHTML = icon + `<span>${element.lastElementChild.textContent}</span>`;
      element.className = 'flex items-center ' + textClass;
    }

    function setupPasswordToggle(inputId, toggleButtonId, eyeIconId, eyeSlashIconId) {
      const passwordInput = document.getElementById(inputId);
      const toggleButton = document.getElementById(toggleButtonId);

      if(toggleButton) {
        const eyeIcon = document.getElementById(eyeIconId);
        const eyeSlashIcon = document.getElementById(eyeSlashIconId);
        toggleButton.addEventListener('click', function() {
          const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
          passwordInput.setAttribute('type', type);
          eyeIcon.classList.toggle('hidden');
          eyeSlashIcon.classList.toggle('hidden');
        });
      }
    }

    setupPasswordToggle('currentPassword', 'toggleCurrentPasswordBtn', 'currentEye', 'currentEyeSlash');
    setupPasswordToggle('newPassword', 'toggleNewPasswordBtn', 'newEye', 'newEyeSlash');
    setupPasswordToggle('confirmPassword', 'toggleConfirmPasswordBtn', 'confirmEye', 'confirmEyeSlash');

  });
</script>
</body>
</html>
