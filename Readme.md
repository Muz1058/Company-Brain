# Company Brain
### Internal Knowledge Management System

> A secure, role-based desktop application for managing and sharing organizational knowledge.

---

## Overview

Company Brain is an internal knowledge base built for organizations that need structured, access-controlled management of information. Built with Java Swing and SQLite, it provides a clean desktop experience with role-based access control enforced at every layer of the application.

---

## Features

**Knowledge Management**
- Create, view, edit, and delete knowledge entries
- Organize entries by category
- Upload and attach files to entries
- Search entries by title or category

**User Administration**
- Admin-only user creation with Employee ID assignment
- Disable and re-enable accounts without data loss
- Secure password reset by administrator
- Full user search and status overview

---

## Roles & Permissions

| Feature          | ADMIN | MANAGER | EMPLOYEE |
|------------------|:-----:|:-------:|:--------:|
| View Entry       |  ✓   |   ✓    |   ✓     |
| New Entry        |  ✓   |   ✓    |   ✗     |
| Edit Entry       |  ✓   |   ✓    |   ✗     |
| Delete Entry     |  ✓   |   ✗    |   ✗     |
| Upload File      |  ✓   |   ✓    |   ✗     |
| User Management  |  ✓   |   ✗    |   ✗     |

Unauthorized actions are hidden entirely — not just disabled.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java JDK    | 21      |
| Eclipse IDE | 2022 or higher |
| SQLite JDBC | 3.36 or higher |
| JUnit       | 4.13.2  |

---

## Setup

1. Import project into Eclipse — File → Import → Existing Projects into Workspace
2. Right-click project → Build Path → Add External JARs → select `sqlite-jdbc.jar` from `lib/`
3. Right-click project → Build Path → Add Libraries → JUnit 4
4. Right-click `Main.java` → Run As → Java Application

The database initializes automatically on first launch.

---

## Login

### Admin
Username and password only. Employee ID is not required.

### Manager / Employee
Employee ID, Username, and Password are all required. Any mismatch returns a generic error.

---

## User Management

Accessible exclusively to **ADMIN** via the User Management button on the dashboard.

| Action          | Description                                          |
|-----------------|------------------------------------------------------|
| Create User     | Assigns Employee ID, username, password, and role    |
| Disable User    | Blocks login access — account and data are preserved |
| Enable User     | Restores login access for a disabled account         |
| Reset Password  | Admin sets a new password for any user               |

---

## Tests

31 JUnit 4 test cases covering login flows, role enforcement, user management, category and entry validation, and search functionality.

```
Right-click CompanyBrainTest.java → Run As → JUnit Test
```

Tests run against an isolated database — production data is never affected.

---

## Technology Stack

| Layer        | Technology              |
|--------------|-------------------------|
| Language     | Java 21                 |
| UI           | Java Swing              |
| Database     | SQLite                  |
| DB Driver    | Xerial SQLite JDBC      |
| Testing      | JUnit 4                 |
| Architecture | MVC                     |

---

*Company Brain — Built for teams that value structured knowledge.*