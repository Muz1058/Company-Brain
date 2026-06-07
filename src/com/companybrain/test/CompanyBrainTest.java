package com.companybrain.test;

import com.companybrain.dao.CategoryDaoImpl;
import com.companybrain.dao.KnowledgeEntryDaoImpl;
import com.companybrain.dao.UserDaoImpl;
import com.companybrain.database.DatabaseManager;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.User;
import com.companybrain.service.*;
import com.companybrain.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class CompanyBrainTest {

    private AuthService authService;
    private UserManagementService userMgmtService;
    private CategoryService categoryService;
    private KnowledgeService knowledgeService;

    // Admin user fetched after login — used as actingAdmin in createUser calls
    private User adminUser;

    @Before
    public void setUp() throws AuthenticationException {
        // Use an isolated test DB
        Config.setDbName("company_brain_test.db");

        File file = new File(Config.resolveDbPath());
        if (file.exists()) file.delete();

        DatabaseManager.initializeDatabase();

        UserDaoImpl userDao = new UserDaoImpl();
        authService = new AuthServiceImpl(userDao);
        userMgmtService = new UserManagementServiceImpl(userDao);
        categoryService = new CategoryServiceImpl(new CategoryDaoImpl());
        knowledgeService = new KnowledgeServiceImpl(new KnowledgeEntryDaoImpl());

        // Log in as admin so we have an actingAdmin for createUser tests
        // Admin login: employeeId blank, username=admin, password=admin123
        adminUser = authService.login("", "admin", "admin123");
    }

    @After
    public void tearDown() {
        authService.logout();
        File file = new File(Config.resolveDbPath());
        if (file.exists()) file.delete();
    }

    // ── Admin Login ───────────────────────────────────────────────────────────

    @Test
    public void testAdminLoginSuccess() throws AuthenticationException {
        authService.logout(); // reset from setUp
        User user = authService.login("", "admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("ADMIN", user.getRole());
        assertEquals(user, authService.getCurrentUser());
    }

    @Test
    public void testLoginEmptyUsername() {
        authService.logout();
        try {
            authService.login("", "", "password");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals("Username and password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testLoginEmptyPassword() {
        authService.logout();
        try {
            authService.login("", "admin", "");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals("Username and password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testLoginWrongPassword() {
        authService.logout();
        try {
            authService.login("", "admin", "wrongpassword");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals("Invalid credentials.", e.getMessage());
        }
    }

    // ── Employee Login ────────────────────────────────────────────────────────

    @Test
    public void testEmployeeLoginSuccess() throws ValidationException, AuthenticationException {
        // Create an employee first
        authService.createUser("EMP001", "alice", "pass123", "EMPLOYEE", adminUser);
        authService.logout();

        User emp = authService.login("EMP001", "alice", "pass123");
        assertNotNull(emp);
        assertEquals("alice", emp.getUsername());
        assertEquals("EMPLOYEE", emp.getRole());
    }

    @Test
    public void testEmployeeLoginMissingEmployeeId() throws ValidationException {
        authService.createUser("EMP002", "bob", "pass123", "EMPLOYEE", adminUser);
        authService.logout();

        try {
            // Employee ID missing for non-admin
            authService.login("", "bob", "pass123");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals("Employee ID is required.", e.getMessage());
        }
    }

    @Test
    public void testEmployeeLoginWrongEmployeeId() throws ValidationException {
        authService.createUser("EMP003", "carol", "pass123", "EMPLOYEE", adminUser);
        authService.logout();

        try {
            authService.login("WRONG01", "carol", "pass123");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals("Invalid credentials.", e.getMessage());
        }
    }

    // ── Disabled Account ──────────────────────────────────────────────────────

    @Test
    public void testDisabledUserCannotLogin() throws ValidationException {
        authService.createUser("EMP004", "dave", "pass123", "EMPLOYEE", adminUser);

        // Find the user and disable them
        User dave = new UserDaoImpl().findByUsername("dave");
        assertNotNull(dave);
        userMgmtService.disableUser(dave.getId(), adminUser);

        authService.logout();

        try {
            authService.login("EMP004", "dave", "pass123");
            fail("Expected AuthenticationException");
        } catch (AuthenticationException e) {
            assertEquals(
                    "Your account has been disabled. Please contact the administrator.",
                    e.getMessage()
            );
        }
    }

    @Test
    public void testReenableUser() throws ValidationException, AuthenticationException {
        authService.createUser("EMP005", "eve", "pass123", "EMPLOYEE", adminUser);
        User eve = new UserDaoImpl().findByUsername("eve");
        userMgmtService.disableUser(eve.getId(), adminUser);
        userMgmtService.enableUser(eve.getId(), adminUser);

        authService.logout();
        User loggedIn = authService.login("EMP005", "eve", "pass123");
        assertNotNull(loggedIn);
        assertEquals("eve", loggedIn.getUsername());
    }

    // ── Create User (admin-only) ──────────────────────────────────────────────

    @Test
    public void testCreateUserSuccess() throws ValidationException {
        authService.createUser("EMP010", "frank", "pass123", "EMPLOYEE", adminUser);
        User frank = new UserDaoImpl().findByUsername("frank");
        assertNotNull(frank);
        assertEquals("EMP010", frank.getEmployeeId());
        assertEquals("EMPLOYEE", frank.getRole());
        assertTrue(frank.isActive());
    }

    @Test
    public void testCreateUserInvalidUsername() {
        try {
            authService.createUser("EMP011", "ab", "pass123", "EMPLOYEE", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("3-20"));
        }
    }

    @Test
    public void testCreateUserInvalidPassword() {
        try {
            authService.createUser("EMP012", "grace", "123", "EMPLOYEE", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("4 characters"));
        }
    }

    @Test
    public void testCreateUserMissingEmployeeId() {
        try {
            authService.createUser("", "henry", "pass123", "EMPLOYEE", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Employee ID is required.", e.getMessage());
        }
    }

    @Test
    public void testCreateUserDuplicateUsername() throws ValidationException {
        authService.createUser("EMP013", "ivan", "pass123", "EMPLOYEE", adminUser);
        try {
            authService.createUser("EMP014", "ivan", "pass456", "EMPLOYEE", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("already taken"));
        }
    }

    @Test
    public void testCreateUserDuplicateEmployeeId() throws ValidationException {
        authService.createUser("EMP015", "judy", "pass123", "EMPLOYEE", adminUser);
        try {
            authService.createUser("EMP015", "kate", "pass456", "EMPLOYEE", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("already assigned"));
        }
    }

    @Test
    public void testCreateUserNonAdminForbidden() throws ValidationException, AuthenticationException {
        authService.createUser("EMP016", "leo", "pass123", "EMPLOYEE", adminUser);
        authService.logout();
        User leo = authService.login("EMP016", "leo", "pass123");

        try {
            authService.createUser("EMP017", "mary", "pass123", "EMPLOYEE", leo);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("Only administrators"));
        }
    }

    // ── Password Reset ────────────────────────────────────────────────────────

    @Test
    public void testResetPassword() throws ValidationException, AuthenticationException {
        authService.createUser("EMP020", "nina", "oldpass", "EMPLOYEE", adminUser);
        User nina = new UserDaoImpl().findByUsername("nina");
        userMgmtService.resetPassword(nina.getId(), "newpass", adminUser);

        authService.logout();
        User loggedIn = authService.login("EMP020", "nina", "newpass");
        assertNotNull(loggedIn);
    }

    @Test
    public void testResetPasswordTooShort() throws ValidationException {
        authService.createUser("EMP021", "omar", "pass123", "EMPLOYEE", adminUser);
        User omar = new UserDaoImpl().findByUsername("omar");
        try {
            userMgmtService.resetPassword(omar.getId(), "123", adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("4 characters"));
        }
    }

    // ── Cannot disable self ───────────────────────────────────────────────────

    @Test
    public void testAdminCannotDisableSelf() {
        try {
            userMgmtService.disableUser(adminUser.getId(), adminUser);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("You cannot disable your own account.", e.getMessage());
        }
    }

    // ── Self-registration blocked ─────────────────────────────────────────────

    @Test
    public void testSelfRegistrationBlocked() {
        try {
            authService.registerUser("anyone", "pass123", "EMPLOYEE");
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("disabled") || e.getMessage().contains("administrator"));
        }
    }

    // ── Category Tests ────────────────────────────────────────────────────────

    @Test
    public void testCreateCategoryEmptyName() {
        try {
            categoryService.createCategory("");
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Category name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateCategoryDuplicateName() {
        try {
            categoryService.createCategory("General");
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Category with this name already exists.", e.getMessage());
        }
    }

    // ── Knowledge Entry Tests ─────────────────────────────────────────────────

    @Test
    public void testCreateEntryEmptyTitle() {
        try {
            knowledgeService.createEntry("", "valid description", 1, 1);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Title cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateEntryEmptyDescription() {
        try {
            knowledgeService.createEntry("valid title", "", 1, 1);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Content cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateEntryInvalidCategory() {
        try {
            knowledgeService.createEntry("valid title", "valid description", -1, 1);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("Please select a valid category.", e.getMessage());
        }
    }

    @Test
    public void testSearchEntriesPartialTitle() throws ValidationException {
        knowledgeService.createEntry("Java Tutorial Basics", "Introduction to Java concepts", 1, 1);
        knowledgeService.createEntry("Python Programming", "Introduction to Python concepts", 1, 1);

        List<KnowledgeEntry> results = knowledgeService.searchEntries("Java");
        assertEquals(1, results.size());
        assertEquals("Java Tutorial Basics", results.get(0).getTitle());
    }

    @Test
    public void testSearchEntriesPartialCategory() throws ValidationException {
        categoryService.createCategory("Development");
        knowledgeService.createEntry("Git Integration Guide", "How to integrate git", 2, 1);

        List<KnowledgeEntry> results = knowledgeService.searchEntries("Dev");
        assertEquals(1, results.size());
        assertEquals("Git Integration Guide", results.get(0).getTitle());
    }

    @Test
    public void testSearchEntriesNoMatch() throws ValidationException {
        knowledgeService.createEntry("General Info Document", "Some random info", 1, 1);
        List<KnowledgeEntry> results = knowledgeService.searchEntries("NonexistentWord");
        assertTrue(results.isEmpty());
    }

    // ── User Management – Search ──────────────────────────────────────────────

    @Test
    public void testSearchUsers() throws ValidationException {
        authService.createUser("EMP030", "zara", "pass123", "MANAGER", adminUser);
        List<User> results = userMgmtService.searchUsers("zara");
        assertEquals(1, results.size());
        assertEquals("zara", results.get(0).getUsername());
    }

    @Test
    public void testGetAllUsersContainsAdmin() {
        List<User> all = userMgmtService.getAllUsers();
        assertTrue(all.stream().anyMatch(u -> "admin".equals(u.getUsername())));
    }
}