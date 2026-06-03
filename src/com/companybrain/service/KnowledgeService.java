package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;

import java.util.List;

/**
 * Service interface for managing knowledge base entries.
 */
public interface KnowledgeService {

    /**
     * Gets all knowledge base entries.
     */
    List<KnowledgeEntry> getAllEntries();

    /**
     * Retrieves an entry by ID.
     */
    KnowledgeEntry getEntryById(int id);

    /**
     * Creates and saves a new knowledge entry.
     */
    void createEntry(String title, String description, int categoryId, int authorId) throws ValidationException;

    /**
     * Updates an existing entry.
     */
    void updateEntry(int id, String title, String description, int categoryId) throws ValidationException;

    /**
     * Deletes an entry.
     */
    void deleteEntry(int id);

    /**
     * Filters entries by query keyword.
     */
    List<KnowledgeEntry> searchEntries(String keyword);
}
