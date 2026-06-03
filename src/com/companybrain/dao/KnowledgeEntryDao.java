package com.companybrain.dao;

import com.companybrain.model.KnowledgeEntry;

import java.util.List;

/**
 * Data Access Object interface for KnowledgeEntry persistence.
 */
public interface KnowledgeEntryDao {
    /**
     * Retrieves all knowledge base entries.
     */
    List<KnowledgeEntry> findAll();

    /**
     * Finds an entry by its unique ID.
     */
    KnowledgeEntry findById(int id);

    /**
     * Persists a new knowledge entry.
     */
    void save(KnowledgeEntry entry);

    /**
     * Updates an existing knowledge entry.
     */
    void update(KnowledgeEntry entry);

    /**
     * Deletes a knowledge entry by ID.
     */
    void delete(int id);

    /**
     * Searches for entries containing the keyword in the title, content, or tags.
     */
    List<KnowledgeEntry> search(String keyword);
}
