# 🎯 PlacementPro – Placement Preparation Tracking System

> **College Capstone Project** | React.js Frontend + Java Spring Boot Backend + MongoDB Atlas

PlacementPro is a full-stack web application designed to help engineering students systematically prepare for campus placements. It tracks DSA progress, company-specific preparation, mock interviews, study plans, leaderboard rankings, and rewards — all in one unified platform.

---

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Local Setup](#local-setup)
  - [Backend (Spring Boot)](#backend-spring-boot)
  - [Frontend (React)](#frontend-react)
- [API Overview](#api-overview)
- [Database Schema](#database-schema)
- [Deployment](#deployment)
- [Demo Credentials](#demo-credentials)
- [Screenshots](#screenshots)

---

## ✨ Features

| Module | Description |
|---|---|
| 🔐 **Authentication** | Secure JWT-based register/login with BCrypt password hashing |
| 📊 **Student Dashboard** | Activity heatmap, weekly chart, stats, placement readiness score |
| 🧮 **Placement Readiness Score** | Composite score from DSA, aptitude, mock interviews, resume & streak |
| 💻 **DSA Problem Bank** | 15+ curated problems with difficulty filters, search, tags & bookmarks |
| ✅ **DSA Submissions** | Submit solutions, track accepted/rejected status, earn credits |
| 🏢 **Company Tracker** | Company-wise problem sets with progress percentage per company |
| 🎙️ **Mock Interviews** | Timed MCQ + HR + Coding sessions with auto-scored results & feedback |
| 📅 **Study Planner** | AI-generated 7-day weekly plans customized by study hours & targets |
| 🏆 **Leaderboard** | Rank students by credits, problems solved, or streak |
| 🎁 **Rewards System** | Earn credits by solving problems; redeem for resume reviews, badges, etc. |
| 📄 **Resume Analyzer** | ATS keyword scoring with missing/found keyword feedback |
| 🛡️ **Admin Dashboard** | Analytics: total users, problems, submissions; user management |
| 🔄 **Daily Login Streak** | Streak tracking with daily credit bonuses and 7-day streak rewards |

---

## 🛠️ Technology Stack

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React.js | 18.x | UI framework |
| React Router DOM | 6.x | Client-side routing |
| Axios | 1.x | HTTP client |
| Recharts | 2.x | Charts & visualizations |
| Framer Motion | 10.x | Animations |
| React Hot Toast | 2.x | Notifications |
| React Icons | 4.x | Icon library |
| React Calendar Heatmap | 1.x | Activity heatmap |

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17+ | Programming language |
| Spring Boot | 3.2.x | Backend framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data MongoDB | 3.x | Database ORM |
| JSON Web Tokens (JWT) | 0.12.x | Stateless auth tokens |
| BCrypt | — | Password hashing |
| Bean Validation | 3.x | Request validation |
| Maven | 3.9.x | Build tool |

### Database & Infrastructure
| Technology | Purpose |
|---|---|
| MongoDB Atlas | Cloud NoSQL database |
| Vercel / Netlify | Frontend hosting |
| Render / Railway | Spring Boot backend hosting |

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    React.js Frontend                     │
│          (Vercel / Netlify – Static Hosting)             │
│                                                          │
│  Pages: Dashboard, Problems, Companies, Mock Interview,  │
│         Study Plan, Leaderboard, Rewards, Admin, Profile │
└─────────────────────┬───────────────────────────────────┘
                      │  REST API (JSON + JWT Bearer Token)
                      │  CORS-secured HTTPS requests
                      ▼
┌─────────────────────────────────────────────────────────┐
│              Java Spring Boot Backend                    │
│                (Render / Railway)                        │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐  │
│  │Controller│→ │ Service  │→ │Repository│→ │ Model  │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘  │
│                                                          │
│  Security: JWT Filter → Spring Security → Role-based ACL │
└─────────────────────┬───────────────────────────────────┘
                      │  Spring Data MongoDB
                      ▼
┌─────────────────────────────────────────────────────────┐
│                   MongoDB Atlas                          │
│                  (Cloud Database)                        │
│                                                          │
│  Collections: users, problems, submissions, credits,     │
│  companies, mockinterviews, studyplans, rewards,         │
│  redemptions                                             │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
placement-tracker-platform/
│
├── frontend/                        # React.js application
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/
│   │   │   ├── Layout/Layout.jsx    # Sidebar navigation
│   │   │   └── ErrorBoundary.jsx
│   │   ├── context/
│   │   │   └── AuthContext.jsx      # JWT auth context + Axios instance
│   │   ├── pages/
│   │   │   ├── LoginPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── ProblemsPage.jsx
│   │   │   ├── ProblemDetailPage.jsx
│   │   │   ├── CompaniesPage.jsx
│   │   │   ├── MockInterviewPage.jsx
│   │   │   ├── StudyPlanPage.jsx
│   │   │   ├── LeaderboardPage.jsx
│   │   │   ├── RewardsPage.jsx
│   │   │   ├── ProfilePage.jsx
│   │   │   └── AdminPage.jsx
│   │   ├── App.jsx                  # Routes + protected route guards
│   │   ├── index.js
│   │   └── index.css
│   ├── .env.example
│   ├── package.json
│   └── vercel.json
│
├── backend-springboot/              # Java Spring Boot application (PRIMARY BACKEND)
│   ├── src/main/java/com/placementpro/
│   │   ├── PlacementProApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java  # Spring Security + CORS
│   │   │   └── MongoConfig.java     # MongoDB auditing
│   │   ├── controller/              # REST endpoints (12 controllers)
│   │   │   ├── AuthController.java
│   │   │   ├── ProblemController.java
│   │   │   ├── SubmissionController.java
│   │   │   ├── CreditController.java
│   │   │   ├── CompanyController.java
│   │   │   ├── LeaderboardController.java
│   │   │   ├── MockInterviewController.java
│   │   │   ├── StudyPlanController.java
│   │   │   ├── RewardController.java
│   │   │   ├── UserController.java
│   │   │   ├── AdminController.java
│   │   │   ├── DashboardController.java
│   │   │   └── HealthController.java
│   │   ├── service/                 # Business logic layer
│   │   ├── repository/              # Spring Data MongoDB repositories
│   │   ├── model/                   # MongoDB document models
│   │   │   ├── User.java
│   │   │   ├── Problem.java
│   │   │   ├── Submission.java
│   │   │   ├── Credit.java
│   │   │   ├── Company.java
│   │   │   ├── MockInterview.java
│   │   │   ├── StudyPlan.java
│   │   │   ├── Reward.java
│   │   │   └── Redemption.java
│   │   ├── dto/                     # Request/response DTOs
│   │   ├── security/                # JWT filter, utils, UserDetailsService
│   │   ├── exception/               # Global exception handler
│   │   └── util/                    # DataSeeder (auto-seeds on first run)
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   ├── Dockerfile
│   └── .env.example
│
├── backend/                         # Original Node.js/Express backend (reference only)
│   ├── server.js
│   ├── routes/
│   ├── models/
│   ├── middleware/
│   └── seed.js
│
├── docs/
│   ├── API.md                       # Full API documentation
│   └── SCHEMA.md                    # MongoDB schema reference
│
├── .gitignore
├── render.yaml                      # Render deployment config
├── package.json
└── README.md
```

---

## ✅ Prerequisites

Before running this project, install the following:

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17+ | [adoptium.net](https://adoptium.net) |
| Apache Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Node.js | 18+ | [nodejs.org](https://nodejs.org) |
| MongoDB Atlas | Account | [mongodb.com/cloud/atlas](https://mongodb.com/cloud/atlas) |
| Git | Latest | [git-scm.com](https://git-scm.com) |

---

## 🔧 Environment Variables

### Backend – `backend-springboot/.env.example`

```properties
MONGODB_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/placement-tracker
JWT_SECRET=your_super_secret_jwt_key_at_least_32_characters_long
PORT=8080
FRONTEND_URL=http://localhost:3000
```

### Frontend – `frontend/.env.example`

```env
REACT_APP_API_URL=http://localhost:8080/api
```

> ⚠️ **Never commit real `.env` files. Always use `.env.example` as a template.**

---

## 🚀 Local Setup

### Backend (Spring Boot)

```bash
# 1. Navigate to Spring Boot backend
cd backend-springboot

# 2. Copy env template and fill in your MongoDB URI + JWT secret
cp .env.example .env
# Edit .env with your actual values

# 3. Set environment variables (Windows PowerShell)
$env:MONGODB_URI="mongodb+srv://..."
$env:JWT_SECRET="your-secret-key"

# 4. Run the application
./mvnw spring-boot:run

# Or on Windows:
mvnw.cmd spring-boot:run

# The backend will start at: http://localhost:8080
# Health check: http://localhost:8080/api/health
```

> 🌱 **Auto-seeding:** On first startup, the application automatically seeds the database with:
> - 8 sample companies (Google, Amazon, Microsoft, Meta, Flipkart, Infosys, TCS, Wipro)
> - 15 DSA problems
> - 8 reward items
> - Admin user (`admin@placementpro.com` / `admin123`)
> - Demo student (`student@placementpro.com` / `student123`)

### Frontend (React)

```bash
# 1. Navigate to frontend
cd frontend

# 2. Install dependencies
npm install

# 3. Create env file
echo "REACT_APP_API_URL=http://localhost:8080/api" > .env

# 4. Start development server
npm start

# Frontend runs at: http://localhost:3000
```

---

## 📡 API Overview

All API endpoints are prefixed with `/api`. Protected endpoints require `Authorization: Bearer <token>` header.

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new student |
| POST | `/api/auth/login` | Public | Login and get JWT |
| GET | `/api/auth/profile` | 🔒 JWT | Get current user profile |
| PUT | `/api/auth/profile` | 🔒 JWT | Update profile |
| GET | `/api/dashboard` | 🔒 JWT | Dashboard data + heatmap |
| GET | `/api/problems` | 🔒 JWT | List problems (filterable) |
| GET | `/api/problems/:slug` | 🔒 JWT | Get single problem |
| POST | `/api/problems/:id/submit` | 🔒 JWT | Submit solution |
| POST | `/api/problems/:id/bookmark` | 🔒 JWT | Bookmark/unbookmark |
| GET | `/api/submissions` | 🔒 JWT | Submission history |
| GET | `/api/companies` | 🔒 JWT | List all companies |
| GET | `/api/companies/:id/progress` | 🔒 JWT | Company prep progress |
| POST | `/api/companies/:id/select` | 🔒 JWT | Add target company |
| GET | `/api/leaderboard` | 🔒 JWT | Leaderboard rankings |
| POST | `/api/mock-interviews/start` | 🔒 JWT | Start mock interview |
| POST | `/api/mock-interviews/:id/submit` | 🔒 JWT | Submit interview answers |
| GET | `/api/mock-interviews/history` | 🔒 JWT | Interview history |
| POST | `/api/study-plans/generate` | 🔒 JWT | Generate weekly plan |
| GET | `/api/study-plans/current` | 🔒 JWT | Get current plan |
| PUT | `/api/study-plans/:id/task` | 🔒 JWT | Update task completion |
| GET | `/api/rewards` | 🔒 JWT | Available rewards |
| POST | `/api/rewards/:id/redeem` | 🔒 JWT | Redeem reward |
| GET | `/api/credits/history` | 🔒 JWT | Credit transaction history |
| GET | `/api/users/me/stats` | 🔒 JWT | Detailed user stats |
| POST | `/api/users/upload-resume` | 🔒 JWT | Resume ATS analysis |
| GET | `/api/admin/analytics` | 🔒 Admin | Platform analytics |
| GET | `/api/admin/users` | 🔒 Admin | All student accounts |
| GET | `/api/health` | Public | Health check |

---

## 🗄️ Database Schema

MongoDB collections and their purposes:

| Collection | Description |
|---|---|
| `users` | Student/admin accounts with stats, credits, preferences |
| `problems` | DSA problem bank with examples, constraints, hints |
| `submissions` | Per-user code submissions with status |
| `credits` | Credit transaction ledger (earn/spend history) |
| `companies` | Target companies with required topics and difficulty |
| `mockinterviews` | Interview sessions with questions and results |
| `studyplans` | Generated weekly study plans |
| `rewards` | Redeemable reward catalogue |
| `redemptions` | User reward redemption records |

See [`docs/SCHEMA.md`](docs/SCHEMA.md) for full schema details.

---

## 🌐 Deployment

### Frontend → Vercel

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy from frontend/
cd frontend
vercel --prod

# Set environment variable in Vercel dashboard:
# REACT_APP_API_URL = https://your-backend.onrender.com/api
```

### Backend → Render

1. Connect your GitHub repo to [render.com](https://render.com)
2. Create a **Web Service** → select `backend-springboot/`
3. Set build command: `./mvnw clean package -DskipTests`
4. Set start command: `java -jar target/placement-tracker-backend-1.0.0.jar`
5. Add environment variables:
   - `MONGODB_URI` → your Atlas connection string
   - `JWT_SECRET` → random 64-char secret
   - `FRONTEND_URL` → your Vercel URL
   - `PORT` → `8080`

### Database → MongoDB Atlas

1. Create a free cluster at [cloud.mongodb.com](https://cloud.mongodb.com)
2. Create a database user with read/write access
3. Whitelist `0.0.0.0/0` (or specific IPs) in Network Access
4. Copy the connection string to `MONGODB_URI`

---

## 🔑 Demo Credentials

After running the application (auto-seeded on first startup):

| Role | Email | Password |
|---|---|---|
| Admin | `admin@placementpro.com` | `admin123` |
| Student | `student@placementpro.com` | `student123` |

---

## 🏅 Credit System

Students earn credits by:
- ✅ Solving Easy problem → **+10 credits**
- ✅ Solving Medium problem → **+20 credits**
- ✅ Solving Hard problem → **+40 credits**
- 🎙️ Completing mock interview → **+50 credits**
- 📄 Uploading resume → **+25 credits**
- 🔑 Daily login → **+5 credits**
- 🔥 7-day streak → **+50 bonus credits**
- 🌟 Welcome bonus → **+50 credits**

Credits can be redeemed for resume reviews, interview tokens, premium question sets, badges, and certificates.

---

## 🧪 Running Tests

```bash
# Spring Boot tests
cd backend-springboot
./mvnw test

# Frontend (if test suite is added)
cd frontend
npm test
```

---

## 📄 License

This project is developed as a college capstone project. All rights reserved.

---

## 👨‍💻 Team

Developed as a **B.Tech Capstone Project** using the mandatory technology stack:
- **Frontend:** React.js
- **Backend:** Java Spring Boot
- **Database:** MongoDB Atlas

---

*PlacementPro – Empowering students to crack their dream placements* 🚀
