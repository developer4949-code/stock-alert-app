News-Driven Stock Alert App
A modern Android application that empowers users to create stock watchlists and receive real-time alerts based on financial news events (e.g., earnings, mergers) parsed from NewsAPI. Features secure watchlist sharing with OTP verification and offline capabilities for seamless user experience.
Features

News-Based Alerts: Backend parses financial news from NewsAPI using keyword matching to trigger alerts for user-selected stocks.
Watchlist Management: Create and manage stock watchlists with a sleek UI built using Jetpack Compose, stored locally in Room DB for offline access.
Secure Sharing: Share watchlists via Android Intents (e.g., WhatsApp, email) with OTP verification powered by AWS SES for privacy.
Offline Support: Queue alert preferences offline using WorkManager and cache news data for offline viewing.
Analytics: Track user engagement (e.g., most-watched stocks) with AWS Pinpoint.
Visual Feedback: Color-coded news impact tags (e.g., positive/negative) and swipe-to-remove watchlist items in Jetpack Compose UI.

Tech Stack

Frontend: Kotlin, Jetpack Compose, Room DB, WorkManager, Android Intents
Backend: Spring Boot, NewsAPI
Cloud: AWS (Elastic Beanstalk, DynamoDB, SES, SNS, Pinpoint)

Getting Started
Prerequisites

Android Studio (latest version)
JDK 17+
AWS account with SES, SNS, DynamoDB, Elastic Beanstalk, and Pinpoint configured
NewsAPI key (get from NewsAPI)

Installation

Clone the repository:git clone https://github.com/developer4949-code/stock-alert-app.git


Open the project in Android Studio.
Add your NewsAPI key and AWS credentials in the backend configuration (application.properties).
Build and run the Android app on an emulator or device.
Deploy the Spring Boot backend to AWS Elastic Beanstalk.

Configuration

NewsAPI: Store API key securely in the backend.
AWS SES: Set up for OTP email delivery.
AWS SNS: Configure for push notifications.
Room DB: Automatically handles local storage for watchlists.

Usage

Launch the app and create a watchlist by searching for stock symbols.
Enable alerts for specific news events (e.g., "earnings").
Share watchlists via supported apps; recipients verify access with an OTP sent via email.
View cached news and manage watchlists offline; alerts sync when online.

Challenges and Solutions

Reliable News Parsing: Implemented simple keyword matching for news events, with plans to enhance with NLP in future iterations.
Intent Compatibility: Tested sharing across major apps (WhatsApp, Gmail) to ensure consistent behavior.
Offline Sync: Used WorkManager to queue and sync alert preferences seamlessly.

Future Enhancements

Add sentiment analysis for news impact scoring.
Support for more news APIs for broader coverage.
Introduce customizable alert thresholds and frequencies.

Contributing
Contributions are welcome! Please fork the repo, create a feature branch, and submit a pull request with your changes.
License
This project is licensed under the MIT License - see the LICENSE file for details.
