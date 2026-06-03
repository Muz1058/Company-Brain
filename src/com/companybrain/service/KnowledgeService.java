package com.companybrain.service;

import com.companybrain.dao.KnowledgeEntryDao;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.util.InputValidator;

import java.util.List;

/**
 * Business service handling knowledge base domain logic.
 */
public class KnowledgeService {
    private final KnowledgeEntryDao entryDao;

    public KnowledgeService(KnowledgeEntryDao entryDao) {
        this.entryDao = entryDao;
    }

    /**
     * Retrieves all knowledge entries.
     */
    public List<KnowledgeEntry> getAllEntries() {
        return entryDao.findAll();
    }

    /**
     * Finds a single entry by ID.
     */
    public KnowledgeEntry getEntryById(int id) {
        return entryDao.findById(id);
    }

    /**
     * Creates and saves a new knowledge entry.
     */
    public void createEntry(String title, String description, int categoryId, int authorId) throws ValidationException {
        try {
            InputValidator.validateKnowledgeEntry(title, description);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }

        if (categoryId <= 0) {
            throw new ValidationException("Please select a valid category.");
        }

        KnowledgeEntry entry = new KnowledgeEntry(0, title.trim(), description.trim(), categoryId, authorId, null, null);
        entryDao.save(entry);
    }

    /**
     * Updates an existing entry.
     */
    public void updateEntry(int id, String title, String description, int categoryId) throws ValidationException {
        try {
            InputValidator.validateKnowledgeEntry(title, description);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }

        if (categoryId <= 0) {
            throw new ValidationException("Please select a valid category.");
        }

        KnowledgeEntry existing = entryDao.findById(id);
        if (existing == null) {
            throw new ValidationException("Knowledge entry not found.");
        }

        existing.setTitle(title.trim());
        existing.setDescription(description.trim());
        existing.setCategoryId(categoryId);
        entryDao.update(existing);
    }

    /**
     * Deletes an entry by ID.
     */
    public void deleteEntry(int id) {
        entryDao.delete(id);
    }

    /**
     * Searches entries based on keyword matching titles or descriptions.
     */
    public List<KnowledgeEntry> searchEntries(String keyword) {
        if (InputValidator.isEmpty(keyword)) {
            return getAllEntries();
        }
        return entryDao.search(keyword.trim());
    }
}
