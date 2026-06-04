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
    private CategoryService categoryService;
    private KnowledgeService knowledgeService;

    @Before
    public void setUp() {
        File file = new File("company_brain_test.db");
        if (file.exists()) {
            file.delete();
        }

        Config.setDbName("company_brain_test.db");
        DatabaseManager.initializeDatabase();

        authService = new AuthServiceImpl(new UserDaoImpl());
        categoryService = new CategoryServiceImpl(new CategoryDaoImpl());
        knowledgeService = new KnowledgeServiceImpl(new KnowledgeEntryDaoImpl());
    }

    @After
    public void tearDown() {
        File file = new File("company_brain_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testLoginEmptyUsername() {
        String username = "";
        String password = "password";

        try {
            authService.login(username, password);
            fail("Expected AuthenticationException was not thrown");
        } catch (AuthenticationException e) {
            assertEquals("Username and password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testLoginEmptyPassword() {
        String username = "admin";
        String password = "";

        try {
            authService.login(username, password);
            fail("Expected AuthenticationException was not thrown");
        } catch (AuthenticationException e) {
            assertEquals("Username and password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testLoginInvalidCredentials() {
        String username = "nonexistent";
        String password = "wrongpassword";

        try {
            authService.login(username, password);
            fail("Expected AuthenticationException was not thrown");
        } catch (AuthenticationException e) {
            assertEquals("Invalid username or password.", e.getMessage());
        }
    }

    @Test
    public void testLoginSuccess() throws AuthenticationException {
        String username = "admin";
        String password = "admin";

        User user = authService.login(username, password);

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("ADMIN", user.getRole());
        assertEquals(user, authService.getCurrentUser());
    }

    @Test
    public void testRegisterUserInvalidUsername() {
        String username = "ab";
        String password = "password";
        String role = "VIEWER";

        try {
            authService.registerUser(username, password, role);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Username must be alphanumeric and 3-20 characters long.", e.getMessage());
        }
    }

    @Test
    public void testRegisterUserInvalidPassword() {
        String username = "validuser";
        String password = "123";
        String role = "VIEWER";

        try {
            authService.registerUser(username, password, role);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Password must be at least 4 characters long.", e.getMessage());
        }
    }

    @Test
    public void testRegisterUserDuplicateUsername() {
        String username = "admin";
        String password = "password";
        String role = "VIEWER";

        try {
            authService.registerUser(username, password, role);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Username already exists.", e.getMessage());
        }
    }

    @Test
    public void testCreateCategoryEmptyName() {
        String name = "";

        try {
            categoryService.createCategory(name);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Category name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateCategoryDuplicateName() {
        String name = "General";

        try {
            categoryService.createCategory(name);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Category with this name already exists.", e.getMessage());
        }
    }

    @Test
    public void testCreateEntryEmptyTitle() {
        String title = "";
        String description = "valid description";
        int categoryId = 1;
        int authorId = 1;

        try {
            knowledgeService.createEntry(title, description, categoryId, authorId);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Title cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateEntryEmptyDescription() {
        String title = "valid title";
        String description = "";
        int categoryId = 1;
        int authorId = 1;

        try {
            knowledgeService.createEntry(title, description, categoryId, authorId);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Content cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCreateEntryInvalidCategory() {
        String title = "valid title";
        String description = "valid description";
        int categoryId = -1;
        int authorId = 1;

        try {
            knowledgeService.createEntry(title, description, categoryId, authorId);
            fail("Expected ValidationException was not thrown");
        } catch (ValidationException e) {
            assertEquals("Please select a valid category.", e.getMessage());
        }
    }

    @Test
    public void testSearchEntriesPartialTitle() throws ValidationException {
        String title1 = "Java Tutorial Basics";
        String desc1 = "Introduction to Java concepts";
        int catId = 1;
        int authorId = 1;
        knowledgeService.createEntry(title1, desc1, catId, authorId);

        String title2 = "Python Programming";
        String desc2 = "Introduction to Python concepts";
        knowledgeService.createEntry(title2, desc2, catId, authorId);

        List<KnowledgeEntry> results = knowledgeService.searchEntries("Java");

        assertEquals(1, results.size());
        assertEquals("Java Tutorial Basics", results.get(0).getTitle());
    }

    @Test
    public void testSearchEntriesPartialCategory() throws ValidationException {
        categoryService.createCategory("Development");
        String title = "Git Integration Guide";
        String desc = "How to integrate git in your team";
        int devCatId = 2;
        int authorId = 1;
        knowledgeService.createEntry(title, desc, devCatId, authorId);

        List<KnowledgeEntry> results = knowledgeService.searchEntries("Dev");

        assertEquals(1, results.size());
        assertEquals("Git Integration Guide", results.get(0).getTitle());
    }

    @Test
    public void testSearchEntriesNoMatch() throws ValidationException {
        String title = "General Info Document";
        String desc = "Some random info";
        int catId = 1;
        int authorId = 1;
        knowledgeService.createEntry(title, desc, catId, authorId);

        List<KnowledgeEntry> results = knowledgeService.searchEntries("NonexistentWord");

        assertTrue(results.isEmpty());
    }
}
