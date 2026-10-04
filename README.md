# 🐍 Snake Game Android App

A modern, production ready Snake Game Android application built with **Kotlin and Jetpack Compose**.

The application supports classic Snake gameplay, guest mode, email authentication, cloud synchronization, premium features, advertisements, records, settings, and Google Play Billing.

---

## 📱 Project Overview

**Snake Game** is a casual arcade game for Android where players control a snake, collect food, grow longer, increase their score, and try to achieve the highest possible record.

The application supports both:

- 👤 Guest Users
- 📧 Registered Users

Registered users can synchronize their game progress and records across supported devices.

---

## ✨ Features

### 🎮 Gameplay

- Classic Snake gameplay
- Smooth snake movement
- Swipe controls
- On screen directional controls
- Food collection
- Snake growth
- Score system
- Level system
- Increasing difficulty
- Wall collision
- Self collision
- Optional obstacle system
- Pause and resume
- Restart game
- Game over screen
- New high score detection

---

### 👤 Guest Mode

Users can play without creating an account.

Guest users can:

- Play the game
- Save local scores
- View records
- Change settings
- Use basic gameplay features

Guest data is stored locally on the device.

Users can create an account later to synchronize supported data.

---

### 🔐 Authentication

The application uses **Firebase Authentication**.

Supported authentication features:

- Create account
- Email/password login
- Logout
- Forgot password
- Password reset
- Email verification
- Authentication session management
- Account deletion

Passwords are handled by Firebase Authentication and are never stored directly by the application.

---

### ☁️ Cloud Synchronization

Registered users can synchronize supported game data using **Firebase Firestore**.

Cloud data can include:

- High score
- Highest level
- Longest snake
- Total games
- Total food collected
- Game history
- Selected settings

The application supports offline gameplay with local storage and synchronizes data when connectivity is available.

---

### 🏆 Records

The Records section displays:

- Highest score
- Highest level
- Longest snake
- Total games
- Total food collected
- Best game time
- Recent game history

Example:

```text
Highest Score: 5250
Highest Level: 18
Longest Snake: 96
Total Games: 142
Food Collected: 3420
```

---

### 👑 Premium

The application includes a Premium system using **Google Play Billing**.

Premium users can unlock:

- 🚫 Remove advertisements
- 🐍 Premium snake skins
- 🎨 Premium themes
- 🎮 Additional game modes
- 🍎 Special food
- 🏆 Advanced challenges
- 🌌 Exclusive backgrounds

Premium snake skins can include:

- Classic
- Neon
- Fire
- Ice
- Galaxy
- Gold

Premium themes can include:

- Classic
- Dark
- Neon
- Space
- Forest

---

### 💳 Premium Plans

The application can support:

- Monthly subscription
- Yearly subscription
- Lifetime purchase

Example product IDs:

```text
snake_premium_monthly
snake_premium_yearly
snake_premium_lifetime
```

Prices should be configured through Google Play Console rather than hardcoded in the application.

The application must support:

- Purchase
- Purchase verification
- Purchase cancellation
- Pending purchases
- Purchase restoration

---

### 📢 Advertisements

Free users can see advertisements using **Google AdMob**.

Supported ad types:

- Banner Ads
- Interstitial Ads
- Rewarded Ads

Advertisements should never interrupt active gameplay in a disruptive way.

Premium users should not see advertisements.

---

## ⚙️ Settings

The Settings screen includes:

### Gameplay

- Control Type
- Difficulty
- Vibration
- Sound Effects
- Music

### Appearance

- Theme
- Snake Skin
- Board Style

### Account

- Login
- Create Account
- Logout
- Delete Account

### Data

- Reset Records
- Restore Purchases

### About

- Privacy Policy
- Terms of Service
- App Version

Settings are stored locally using **DataStore**.

---

## 🔊 Sound & Vibration

The application supports sound effects for:

- Button clicks
- Eating food
- Level up
- Game over
- New record
- Premium purchase

Optional background music is also supported.

Vibration can be triggered for:

- Food collection
- Game over
- New record

Users can disable sound, music, and vibration from Settings.

---

## 🎨 UI & Design

The application uses a modern gaming interface.

### Design Principles

- Modern dark gaming style
- Clean typography
- Rounded buttons
- Large touch targets
- Smooth animations
- Responsive layouts
- Good accessibility
- Light and dark themes

Recommended primary colors:

```text
Background: Dark
Primary: Green
Premium: Gold
Text: White / Light Gray
```

The application should work correctly on different Android screen sizes.

---

# 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Programming Language |
| Jetpack Compose | UI |
| MVVM | Architecture |
| Room | Local Database |
| DataStore | Local Settings |
| Firebase Authentication | User Authentication |
| Firebase Firestore | Cloud Synchronization |
| Google Play Billing | Premium Purchases |
| Google AdMob | Advertisements |
| Jetpack Navigation Compose | Navigation |

---

# 🏗️ Architecture

The project follows an MVVM based architecture.

```text
app/
│
├── ui/
│   ├── home/
│   ├── game/
│   ├── premium/
│   ├── records/
│   ├── settings/
│   └── auth/
│       ├── login/
│       ├── register/
│       ├── forgotpassword/
│       └── verification/
│
├── game/
│   ├── SnakeEngine
│   ├── GameState
│   ├── Snake
│   ├── Food
│   ├── Collision
│   └── GameMode
│
├── data/
│   ├── database/
│   ├── repository/
│   ├── preferences/
│   └── firestore/
│
├── auth/
│   └── AuthManager
│
├── billing/
│   └── BillingManager
│
├── ads/
│   └── AdManager
│
├── navigation/
│
└── utils/
```

---

# 🎮 Game Architecture

The Snake game logic is separated from the UI.

The game engine manages:

- Snake position
- Snake body
- Direction
- Food position
- Score
- Level
- Speed
- Collision detection
- Game state
- Pause state
- Game over state
- Game mode

Game states:

```text
IDLE
PLAYING
PAUSED
GAME_OVER
```

---

# 🗄️ Local Database

Room Database is used for local game data.

### GameRecord

Example fields:

```text
id
score
level
snakeLength
foodCollected
gameDuration
date
gameMode
```

Room allows users to access their records even when offline.

---

# ⚙️ DataStore

DataStore stores application preferences.

Example:

```text
controlType
difficulty
vibrationEnabled
soundEnabled
musicEnabled
theme
snakeSkin
boardStyle
onboardingCompleted
```

---

# 🔥 Firebase

## Firebase Authentication

Used for:

- Registration
- Login
- Logout
- Password reset
- Email verification
- Account management

## Firebase Firestore

Used for:

- Cloud game statistics
- Game history
- User settings
- Cloud synchronization

Example structure:

```text
users/
    {userId}/
        profile/
        statistics/
        settings/
        gameRecords/
```

Firebase Security Rules must ensure users can only access their own data.

---

# 💰 Google Play Billing

Google Play Billing is used for Premium purchases.

Possible products:

```text
snake_premium_monthly
snake_premium_yearly
snake_premium_lifetime
```

Premium access must be based on verified Google Play purchase information.

Do not rely only on a Firestore boolean such as:

```text
isPremium = true
```

for purchase verification.

---

# 📢 Google AdMob

AdMob is used to monetize free users.

Possible placements:

```text
Home
Records
Settings
After Game Over
```

Do not display disruptive advertisements during active gameplay.

Premium users should have advertisements removed.

---

# 🌐 Offline Support

The core Snake game works offline.

Offline functionality includes:

- Gameplay
- Score
- Local records
- Game history
- Settings
- Themes
- Sound
- Vibration

Internet may be required for:

- Firebase Authentication
- Firebase Firestore
- Google Play Billing
- AdMob

The game should continue working even when these external services are unavailable.

---

# 🔄 Cloud Sync

For registered users:

```text
Local Data
     ↓
Firebase Firestore
     ↓
Cloud Data
```

When the device is offline:

```text
Game
 ↓
Room Database
```

When the device reconnects:

```text
Room Database
      ↓
Sync Manager
      ↓
Firestore
```

Conflict resolution should prevent a lower score from overwriting a higher score.

Example:

```text
Local Score = 5000
Cloud Score = 3000

Final Score = 5000
```

---

# 🚀 Getting Started

## Requirements

Install:

- Android Studio
- Android SDK
- JDK compatible with the selected Android Gradle Plugin
- Git

Recommended:

```text
Android Studio
Kotlin
Jetpack Compose
```

---

## Clone the Project

```bash
git clone YOUR_REPOSITORY_URL
```

Open the project in Android Studio.

Allow Gradle to synchronize.

---

# 🔥 Firebase Setup

Create a project in Firebase Console.

Enable:

### Authentication

Enable:

```text
Email/Password
```

### Firestore

Create a Firestore database.

### Android App

Add your Android application to Firebase.

Download:

```text
google-services.json
```

Place it in:

```text
app/google-services.json
```

Do not commit sensitive configuration files to a public repository if they contain secrets or environment-specific credentials.

---

# 💳 Google Play Billing Setup

Create the required products in Google Play Console.

Example:

```text
snake_premium_monthly
snake_premium_yearly
snake_premium_lifetime
```

Configure the product IDs in the application.

Test purchases using Google Play's testing environment.

---

# 📢 AdMob Setup

Create an AdMob application.

Create the required ad units:

```text
Banner
Interstitial
Rewarded
```

Add the appropriate AdMob application ID and ad unit configuration.

Use test ad IDs during development.

Never use production ads for testing.

---

# 🔐 Security Configuration

Never commit sensitive secrets to Git.

Use:

```text
local.properties
environment variables
secure configuration
```

where appropriate.

Protect Firebase data using Firebase Security Rules.

Users should only be able to read and write their own data.

---

# 🧪 Testing

The application should be tested for:

### Gameplay

- Snake movement
- Food generation
- Snake growth
- Score
- Level
- Collision
- Game over
- Pause
- Restart

### Authentication

- Registration
- Login
- Logout
- Password reset
- Email verification
- Invalid credentials
- Network errors
- Account deletion

### Database

- Saving records
- Reading records
- Reset records
- Offline records

### Cloud Sync

- Upload
- Download
- Conflict resolution
- Offline synchronization
- Multi device synchronization

### Billing

- Purchase
- Cancellation
- Pending purchase
- Restore purchase
- Premium access

### Ads

- Banner
- Interstitial
- Rewarded
- Premium ad removal

### Performance

- Memory usage
- FPS
- Battery usage
- App lifecycle
- Background/foreground transitions

---

# 📱 Supported User Flow

## New Guest User

```text
Open App
   ↓
Onboarding
   ↓
Home
   ↓
Play Game
   ↓
Game Over
   ↓
Save Local Record
```

## New Registered User

```text
Open App
   ↓
Create Account
   ↓
Email Verification
   ↓
Login
   ↓
Home
   ↓
Play Game
   ↓
Cloud Sync
```

## Returning User

```text
Open App
   ↓
Check Authentication
   ↓
User Logged In
   ↓
Load Cloud Data
   ↓
Home
```

---

# 📦 MVP

Version 1 should include:

- Home
- Guest Mode
- Email Registration
- Email Login
- Forgot Password
- Email Verification
- Logout
- Account Deletion
- Snake Gameplay
- Swipe Controls
- Button Controls
- Food
- Score
- Levels
- Collision Detection
- Game Over
- Pause
- Restart
- Records
- Game History
- Settings
- Sound
- Vibration
- Themes
- Premium
- Google Play Billing
- AdMob
- Firebase Authentication
- Firebase Firestore
- Room
- DataStore
- Offline Gameplay
- Onboarding
- Privacy Policy
- Terms of Service

---

# 🔮 Future Features

Possible future updates:

- Google Play Games
- Global Leaderboards
- Achievements
- Daily Challenges
- Weekly Challenges
- Daily Rewards
- Multiplayer
- Tournament Mode
- Friends
- More Snake Skins
- More Themes
- Seasonal Events
- Cloud Save Improvements

---

# 📊 Success Metrics

Track:

- Daily Active Users
- Monthly Active Users
- Games per User
- Average Game Duration
- Average Score
- Day 1 Retention
- Day 7 Retention
- Premium Conversion
- Ad Revenue
- Premium Revenue
- Crash Free Sessions
- Games per Day

---

# 📋 Acceptance Criteria

The application is ready for release when:

- The app builds successfully.
- The app launches without crashes.
- Guest users can play.
- Users can register with email.
- Users can log in.
- Users can log out.
- Password reset works.
- Email verification works.
- Account deletion works.
- Snake gameplay works.
- Swipe controls work.
- Button controls work.
- Food works.
- Snake growth works.
- Score works.
- Levels work.
- Collision detection works.
- Game over works.
- Pause works.
- Restart works.
- High scores are saved.
- Game history is saved.
- Settings are saved.
- Firebase synchronization works.
- Offline gameplay works.
- Premium purchases work.
- Restore Purchases works.
- Free users see appropriate advertisements.
- Premium users do not see advertisements.
- The application handles network failures.
- The application handles billing failures.
- The application handles ad failures.
- Firebase Security Rules protect user data.
- Privacy Policy is accessible.
- Terms of Service are accessible.
- The application works on different Android screen sizes.
- No major crashes or ANRs exist.
- The release build is ready for Google Play Store submission.

---

# 📄 License

Add your preferred license here.

Example:

```text
Copyright © 2026 Snake Game

All rights reserved.
```

---

# 👨‍💻 Development Status

```text
Project: Snake Game
Platform: Android
Status: Development
Version: 1.0.0
```

---

# 🐍 Goal

The goal of this project is to build a polished, lightweight, addictive Snake Game that is:

- Easy to play
- Fast
- Offline friendly
- Secure
- Monetizable
- Account enabled
- Cloud synchronized
- Premium enabled
- Ready for Google Play Store
