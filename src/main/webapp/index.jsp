<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
   <title>StoryAI - Craft Your Universe</title>
   <script src="https://cdn.tailwindcss.com"></script>

   <link rel="preconnect" href="https://fonts.googleapis.com">
   <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
   <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400..900&family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">

   <style>
      body {
         font-family: 'Poppins', sans-serif;
         background-color: #111827;
         color: #E5E7EB;
      }

      .font-orbitron {
         font-family: 'Orbitron', sans-serif;
      }

      /* Main hero section with parallax-like effect */
      .hero-section {
         background-image: url('<%= request.getContextPath() %>images/design/img.jpg');
         background-attachment: fixed; /* Creates the parallax effect */
         background-position: center;
         background-repeat: no-repeat;
         background-size: cover;
         position: relative;
      }

      /* Dark overlay for better text readability on background images */
      .dark-overlay::before {
         content: '';
         position: absolute;
         top: 0;
         left: 0;
         right: 0;
         bottom: 0;
         background-color: rgba(0, 0, 0, 0.7);
         z-index: 1;
      }

      /* Ensure content is above the overlay */
      .hero-content {
         position: relative;
         z-index: 2;
      }

      /* Styling for the header */
      header {
         background-color: rgba(10, 10, 10, 0.5);
         backdrop-filter: blur(10px); /* Frosted glass effect */
         -webkit-backdrop-filter: blur(10px);
         border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      }

      @keyframes pulse-purple-glow {
         0%, 100% {
            box-shadow: 0 0 15px rgba(124, 58, 237, 0.6), 0 0 5px rgba(217, 70, 239, 0.8);
            transform: scale(1);
         }
         50% {
            box-shadow: 0 0 30px rgba(124, 58, 237, 0.8), 0 0 10px rgba(217, 70, 239, 1);
            transform: scale(1.05);
         }
      }

      .join-button-animated {
         animation: pulse-purple-glow 3s infinite;
      }


      /* Rules section with its own background */
      .rules-section {
         background-image: url('<%= request.getContextPath() %>images/design/img.jpg');
         background-attachment: fixed;
         background-position: center;
         background-repeat: no-repeat;
         background-size: cover;
         position: relative;
         border-top: 1px solid rgba(167, 139, 250, 0.2);
      }
   </style>
</head>
<body>

<header class="py-4 fixed top-0 w-full z-50">
   <div class="container mx-auto px-6 flex justify-between items-center">
      <h1 class="text-3xl font-bold font-orbitron tracking-widest text-white">StoryAI</h1>
      <nav class="flex items-center space-x-6">
         <a href="login.jsp" class="text-gray-300 hover:text-white transition-colors duration-300">Login</a>
         <a href="register.jsp" class="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-5 rounded-md shadow-lg hover:shadow-purple-600/50 transition-all duration-300">
            Join Now
         </a>
      </nav>
   </div>
</header>

<section class="hero-section min-h-screen flex flex-col justify-center items-center p-6 dark-overlay">
   <div class="hero-content text-center">
      <h2 class="text-5xl md:text-7xl font-bold font-orbitron text-white mb-4 animate-[fadeInUp_1s_ease-out]">
         Craft Worlds with a Whisper
      </h2>
      <p class="text-lg md:text-xl text-gray-300 max-w-2xl mx-auto mb-10 animate-[fadeInUp_1.5s_ease-out]">
         Infinite adventures, powered by AI. Your imagination is the only limit.
      </p>
      <a href="register.jsp" class="join-button-animated bg-purple-600 text-white font-bold py-4 px-10 rounded-full text-xl uppercase tracking-wider transition-transform duration-300 ease-in-out">
         Join the Adventure
      </a>
   </div>
</section>

<section class="rules-section py-20 md:py-32 dark-overlay">
   <div class="hero-content container mx-auto px-6 text-center">
      <div class="bg-gray-900 bg-opacity-70 backdrop-blur-md rounded-xl p-8 md:p-12 max-w-4xl mx-auto border border-gray-700 shadow-2xl">
         <h3 class="text-4xl font-bold font-orbitron mb-8 text-white">Usage Guidelines</h3>
         <div class="grid md:grid-cols-2 gap-8 text-left">

            <div class="flex items-start space-x-4">
               <svg class="w-6 h-6 text-purple-400 mt-1 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M15 21a6 6 0 00-9-5.197m0 0A5.975 5.975 0 0112 13a5.975 5.975 0 013-1.197M15 21a9 9 0 00-6-8.197M15 21a9 9 0 00-6-8.197M12 8c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3z"></path></svg>
               <div>
                  <h4 class="font-semibold text-lg text-white">Be Respectful</h4>
                  <p class="text-gray-400">Interact with kindness. Treat all members of the community with respect.</p>
               </div>
            </div>

            <div class="flex items-start space-x-4">
               <svg class="w-6 h-6 text-purple-400 mt-1 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path></svg>
               <div>
                  <h4 class="font-semibold text-lg text-white">No Inappropriate Content</h4>
                  <p class="text-gray-400">Keep stories and interactions safe for work and all audiences. Overtly explicit content is not permitted.</p>
               </div>
            </div>

            <div class="flex items-start space-x-4">
               <svg class="w-6 h-6 text-purple-400 mt-1 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
               <div>
                  <h4 class="font-semibold text-lg text-white">Follow Community Guidelines</h4>
                  <p class="text-gray-400">Adhere to the full terms of service to ensure a positive environment for everyone.</p>
               </div>
            </div>

            <div class="flex items-start space-x-4">
               <svg class="w-6 h-6 text-purple-400 mt-1 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path></svg>
               <div>
                  <h4 class="font-semibold text-lg text-white">Unleash Creativity</h4>
                  <p class="text-gray-400">This is a space for imagination. Experiment, explore, and have fun building new worlds!</p>
               </div>
            </div>

         </div>
      </div>
   </div>
</section>

</body>
</html>