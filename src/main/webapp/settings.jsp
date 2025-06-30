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

  <main>

    <form action="<%= request.getContextPath() %>/settings" method="POST" enctype="multipart/form-data" class="bg-gray-800 p-8 rounded-lg max-w-2xl mx-auto space-y-8">

      <div class="flex items-center space-x-6">
        <img src="<%= profilePicturePath %>" alt="Current Profile Picture" class="w-24 h-24 rounded-full object-cover border-2 border-purple-400">
        <div>
          <label for="profilePicture" class="block text-sm font-medium text-gray-300">Change Profile Picture</label>
          <input type="file" name="profilePicture" id="profilePicture" class="mt-1 block w-full text-sm text-gray-400
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
            <input type="password" name="currentPassword" id="currentPassword" placeholder="Enter your current password" class="mt-1 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm py-2 px-3 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500">
          </div>
          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-300">New Password</label>
            <input type="password" name="newPassword" id="newPassword" class="mt-1 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm py-2 px-3 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500">
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
            <input type="password" name="confirmPassword" id="confirmPassword" class="mt-1 block w-full bg-gray-700 border border-gray-600 rounded-md shadow-sm py-2 px-3 text-white focus:outline-none focus:ring-purple-500 focus:border-purple-500">
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
  });
</script>
</body>
</html>
