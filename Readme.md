# EcoEcho - GDSC Solution Challenge '24 Submission

## Codebases
1. Native Android App - SustainTheGlobeApp (EcoEcho-SolutionChallenge24/SustainTheGlobeApp/)
2. React based Progressive Web App - sustaintheglobe-web (EcoEcho-SolutionChallenge24/sustaintheglobe-web/)

## Installation and Usage
1. Android App - To install the app, download the [APK File](https://github.com/UtkarshSingh5474/EcoEcho-SolutionChallenge24/blob/main/SustainTheGlobeApp/app-debug.apk) and install it on your Android device.
2. Progressive Web App - [Visit Site](https://sustain-globe.netlify.app/)

## Problem Statement
In today's world, the global community is facing urgent challenges related to climate change, environmental degradation, and unsustainable consumption patterns. Individuals often lack accessible and engaging tools to participate in meaningful sustainability actions tailored to their daily lives. Despite growing awareness of these issues, there remains a gap in easily adopting and tracking personal sustainability practices.

EcoEcho seeks to address this gap by providing a solution that empowers individuals to take impactful actions towards sustainability in a simple and rewarding way. The lack of a centralized platform for personalized sustainability tasks tailored to users' preferences and locations inhibits many from making consistent eco-friendly choices. Additionally, there is a need for a community-driven approach to encourage, share, and celebrate these efforts among peers. 

EcoEcho aims to inspire and facilitate positive environmental and social impact at the individual level. Through this approach, EcoEcho aims to contribute to the broader global goals of mitigating climate change, promoting responsible consumption, and fostering sustainable communities.

## Solution Statement:
EcoEcho is a personal sustainability app designed to make it easy and rewarding for individuals to adopt eco-friendly habits in their daily lives. Our solution offers a user-friendly platform where users can discover and complete personalized sustainability tasks based on their location, preferences, and lifestyle.

Through EcoEcho, users receive a tailored set of tasks each day, week, and month, ranging from simple actions like using public transport to more impactful tasks such as planting trees or donating to local causes. These tasks are categorized by difficulty level, making it easy for users to choose actions that fit their comfort and availability.

The app provides a calming and positive user interface, encouraging engagement and making sustainability accessible to everyone. Users can upload photos as proof of task completion, fostering accountability and creating a sense of accomplishment.

# README

## High-Level Technical Components and Their Responsibilities:

### 1. Backend:

- **Cloud Firestore:**
  - Database management for storing user data, tasks, and community interactions.
- **Firebase Authentication:**
  - Secure user authentication to access the app's features.
- **Firebase Storage:**
  - Storage of media files such as task completion photos.
- **Cloud Functions:**
  - Executes on the cloud, responsible for task generation based on Gemini 1.0 Pro and other dynamic updates.

### 2. Frontend:

#### 2.1. Android Native (XML + Java):
- Develops the Android app interface for users to interact with tasks, community, and profiles.
- Languages: Java for backend logic, XML for UI design.
- Technologies: Android Native development tools.

#### 2.2. React:
- Develops the Progressive Web App (PWA) accessible on iOS, macOS, Windows, and web browsers.
- Languages: JavaScript for frontend logic.
- Technologies: React framework for PWA development.

### 3. Technologies:

- **Gemini 1.0 Pro:**
  - Utilized within Cloud Functions for task generation.
- **Google Maps API:**
  - Used within backend logic (Cloud Functions) to generate location-based tasks.
- **Google Material Design:**
  - Integrated into Android Native and Progressive Web App UI components ensuring consistency and user-friendliness.
- **Google Analytics:**
  - Integrated into both Android Native and Progressive Web Apps for tracking user behavior.
- **Cloud Messaging:**
  - Utilized within backend logic (Cloud Functions) to trigger notifications based on user activity.
