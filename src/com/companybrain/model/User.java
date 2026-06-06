package com.companybrain.model;

import java.time.LocalDateTime;

public class User {

    private int           id;
    private String        employeeId;    // NULL for admin
    private String        username;
    private String        passwordHash;
    private String        role;          // ADMIN | MANAGER | EMPLOYEE
    private boolean       active;
    private LocalDateTime createdAt;

    public User() {}

    // ── Legacy 4-arg constructor — keeps existing callers compiling ───────────
    public User(int id, String username, String passwordHash, String role) {
        this.id           = id;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.active       = true;
    }

    // ── Full constructor ──────────────────────────────────────────────────────
    public User(int id, String employeeId, String username,
                String passwordHash, String role,
                boolean active, LocalDateTime createdAt) {
        this.id           = id;
        this.employeeId   = employeeId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.active       = active;
        this.createdAt    = createdAt;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }

    public String getEmployeeId()                   { return employeeId; }
    public void   setEmployeeId(String employeeId)  { this.employeeId = employeeId; }

    public String getUsername()                     { return username; }
    public void   setUsername(String username)      { this.username = username; }

    public String getPasswordHash()                 { return passwordHash; }
    public void   setPasswordHash(String h)         { this.passwordHash = h; }

    public String getRole()                         { return role; }
    public void   setRole(String role)              { this.role = role; }

    public boolean isActive()                       { return active; }
    public void    setActive(boolean active)        { this.active = active; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}