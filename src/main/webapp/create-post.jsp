<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.story.Tags" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Create a New Story</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .fade-in {
            animation: fadeIn 0.5s ease-in-out;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body class="bg-gray-900 text-white font-sans antialiased">
<%
    String username = (String) session.getAttribute("user");
    if (username == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>

<div class="container mx-auto max-w-4xl p-8">

    <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-indigo-400">Create a New Saga</h1>
        <p class="text-gray-400 mt-2">Welcome, <%= username %>! Let's build a world together.</p>
    </div>

    <form action="<%= request.getContextPath() %>/postcreation" method="POST" enctype="multipart/form-data" class="space-y-8">

        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <label for="title" class="block text-xl font-semibold mb-2 text-gray-300">Story Title</label>
            <input type="text" id="title" name="title" required
                   autofocus tabIndex="1"
                   class="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-4 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                   placeholder="e.g., The Last Dragon of Eldoria">
        </div>

        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <label for="coverImage" class="block text-xl font-semibold mb-2 text-gray-300">Cover Image</label>
            <p class="text-gray-400 mb-4 text-sm">Choose a compelling image to represent your story. (Required)</p>
            <input type="file" id="coverImage" name="coverImage" required accept="image/png, image/jpeg, image/webp" tabIndex="2"
                   class="w-full text-sm text-gray-400 file:mr-4 file:py-2 file:px-4
                          file:rounded-md file:border-0 file:text-sm file:font-semibold
                          file:bg-indigo-600 file:text-white hover:file:bg-indigo-700
                          cursor-pointer"/>
        </div>
        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <label for="description" class="block text-xl font-semibold mb-2 text-gray-300">Story Description</label>
            <p class="text-gray-400 mb-4 text-sm">A brief, enticing summary.</p>
            <textarea id="description" name="description" rows="3" required tabIndex="3"
                      class="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-4 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                      placeholder="e.g., A young hero must find the legendary Sunstone in a realm shrouded in perpetual twilight..."></textarea>
        </div>

        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <label for="worldInfo" class="block text-xl font-semibold mb-2 text-gray-300">World Information</label>
            <p class="text-gray-400 mb-4 text-sm">Describe the setting, the rules of your world, the general tone, and any important history.</p>
            <textarea id="worldInfo" name="worldInfo" rows="8" required tabIndex="4"
                      class="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-4 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                      placeholder="e.g., A world where magic is fading and technology is on the rise..."></textarea>
        </div>

        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <h2 class="text-xl font-semibold mb-3 text-gray-300">Choose Your Tags</h2>
            <div class="flex flex-wrap gap-4">
                <% for (String tag : Tags.getAllTags()) { %>
                <div>
                    <input type="checkbox" id="tag-<%= tag %>" name="storyTags" value="<%= tag %>" tabIndex="5"
                           class="form-checkbox h-5 w-5 text-indigo-600 bg-gray-700 border-gray-600 rounded focus:ring-indigo-500">
                    <label for="tag-<%= tag %>" class="ml-2 text-gray-300"><%= tag %></label>
                </div>
                <% } %>
            </div>
        </div>

        <div class="bg-gray-800 p-6 rounded-lg shadow-lg">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-xl font-semibold text-gray-300">Characters</h2>
                <button type="button" id="add-character-btn"
                        class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-md transition duration-300">
                    + Add Character
                </button>
            </div>
            <div id="characters-container" class="space-y-6">
            </div>
        </div>


        <div class="text-center">
            <button type="submit" tabIndex="11"
                    class="w-full md:w-1/2 bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-6 rounded-lg text-lg transition duration-300 shadow-xl">
                Begin Your Adventure!
            </button>
        </div>

    </form>
</div>


<template id="character-template">
    <div class="p-4 border border-gray-700 rounded-lg bg-gray-800/50 fade-in relative">
        <button type="button" onclick="this.parentElement.remove()"
                class="absolute top-2 right-2 text-gray-500 hover:text-red-500 font-bold text-xl">×</button>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
                <label class="block text-sm font-medium text-gray-400">Name</label>
                <input type="text" name="characterName" required tabIndex="6"
                       class="mt-1 w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-1 focus:ring-indigo-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-400">Age</label>
                <input type="number" name="characterAge" required tabIndex="7"
                       class="mt-1 w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-1 focus:ring-indigo-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-400">Gender</label>
                <input type="text" name="characterGender" required tabIndex="8"
                       class="mt-1 w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-1 focus:ring-indigo-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-400">Species</label>
                <input type="text" name="characterSpecies" required tabIndex="9"
                       class="mt-1 w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-1 focus:ring-indigo-500">
            </div>
            <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-400">Description</label>
                <textarea name="characterDescription" rows="3" required tabindex="10"
                          class="mt-1 w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-1 focus:ring-indigo-500"></textarea>
            </div>
        </div>
    </div>
</template>


<script>
    document.addEventListener('DOMContentLoaded', function () {
        const addCharacterBtn = document.getElementById('add-character-btn');
        const charactersContainer = document.getElementById('characters-container');
        const characterTemplate = document.getElementById('character-template');

        function addCharacterForm() {
            const characterForm = characterTemplate.content.cloneNode(true);
            charactersContainer.appendChild(characterForm);
        }

        addCharacterBtn.addEventListener('click', addCharacterForm);

        addCharacterForm();
    });
</script>

</body>
</html>