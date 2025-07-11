<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, java.util.Map, java.util.List, java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>

<%
  User profileUser = (User) request.getAttribute("profileUser");
  if (profileUser == null) {
    response.sendRedirect(request.getContextPath() + "/profile");
    return;
  }
  int storiesCreated = (Integer) request.getAttribute("storiesCreated");
  int likesGiven = (Integer) request.getAttribute("likesGiven");
  int followingCount = (Integer) request.getAttribute("followingCount");
  int followerCount = (Integer) request.getAttribute("followerCount");
  Map<String, Long> commentStats = (Map<String, Long>) request.getAttribute("commentStats");
  String chartLabel = (String) request.getAttribute("chartLabel");

  Map<String, Long> readStats = (Map<String, Long>) request.getAttribute("readStats");

  List<String> chartLabels = new ArrayList<>(commentStats.keySet());
  List<Long> chartData = new ArrayList<>(commentStats.values());

  List<String> readChartLabels = new ArrayList<>(readStats.keySet());
  List<Long> readChartData = new ArrayList<>(readStats.values());

  Gson gson = new Gson();
%>

<!DOCTYPE html>
<html>
<head>
  <title>Stats for <%= profileUser.getUsername() %> - StoryAI</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
    body::before { content: ''; position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(17, 24, 39, 0.7); z-index: -1; }
  </style>
</head>
<body class="bg-gray-900 text-gray-200 font-sans bg-cover bg-center bg-fixed" style="background-image: url('<%= request.getContextPath() %>/images/design/img4.jpg');">

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container mx-auto p-4 md:p-8 max-w-6xl">
  <header class="flex justify-between items-center mb-10">
    <h1 class="text-4xl font-bold text-white">Your Creator Dashboard</h1>
    <a href="<%= request.getContextPath() %>/profile" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg transition-all duration-300">
      ← Back to Profile
    </a>
  </header>

  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg text-center"><p class="text-4xl font-bold text-teal-400"><%= storiesCreated %></p><p class="text-gray-400 mt-2">Stories Created</p></div>
    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg text-center"><p class="text-4xl font-bold text-pink-400"><%= likesGiven %></p><p class="text-gray-400 mt-2">Likes Given</p></div>
    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg text-center"><p class="text-4xl font-bold text-indigo-400"><%= followingCount %></p><p class="text-gray-400 mt-2">Following</p></div>
    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg text-center"><p class="text-4xl font-bold text-purple-400"><%= followerCount %></p><p class="text-gray-400 mt-2">Followers</p></div>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg">
      <h2 class="text-2xl font-bold text-white mb-4">Your Comment Activity</h2>
      <canvas id="commentChart"></canvas>
    </div>

    <div class="bg-gray-800/80 backdrop-blur-sm p-6 rounded-lg">
      <h2 class="text-2xl font-bold text-white mb-4">Your Reading Activity</h2>
      <canvas id="readChart"></canvas>
    </div>
  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function () {
    const commentLabels = <%= gson.toJson(chartLabels) %>;
    const commentData = <%= gson.toJson(chartData) %>;
    const readLabels = <%= gson.toJson(readChartLabels) %>;
    const readData = <%= gson.toJson(readChartData) %>;
    const chartLabel = "<%= (String) request.getAttribute("chartLabel") %>";
    const readChartLabel = chartLabel.replace("Comments", "Stories Read");

    const commentCtx = document.getElementById('commentChart').getContext('2d');
    new Chart(commentCtx, {
      type: 'bar',
      data: {
        labels: commentLabels,
        datasets: [{
          label: chartLabel,
          data: commentData,
          backgroundColor: 'rgba(129, 140, 248, 0.6)',
          borderColor: 'rgba(165, 180, 252, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              color: 'rgba(209, 213, 219, 1)',
              stepSize: 1
            },
            grid: {
              color: 'rgba(55, 65, 81, 1)'
            }
          },
          x: {
            ticks: {
              color: 'rgba(209, 213, 219, 1)'
            },
            grid: {
              color: 'rgba(55, 65, 81, 1)'
            }
          }
        },
        plugins: {
          legend: {
            labels: {
              color: 'rgba(209, 213, 219, 1)'
            }
          }
        }
      }
    });
    const readCtx = document.getElementById('readChart').getContext('2d');
    new Chart(readCtx, {
      type: 'bar',
      data: {
        labels: readLabels,
        datasets: [{
          label: readChartLabel,
          data: readData,
          backgroundColor: 'rgba(52, 211, 153, 0.6)',
          borderColor: 'rgba(110, 231, 183, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              color: 'rgba(209, 213, 219, 1)',
              stepSize: 1
            },
            grid: {
              color: 'rgba(55, 65, 81, 1)'
            }
          },
          x: {
            ticks: {
              color: 'rgba(209, 213, 219, 1)'
            },
            grid: {
              color: 'rgba(55, 65, 81, 1)'
            }
          }
        },
        plugins: {
          legend: {
            labels: {
              color: 'rgba(209, 213, 219, 1)'
            }
          }
        }
      }
    });
  });
</script>

</body>
</html>