package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.User;

import java.util.List;

public interface KnowledgeService {

    List<KnowledgeEntry> getAllEntries();

    KnowledgeEntry getEntryById(int id);

    /** Creates a TEXT-type entry (default). */
    void createEntry(String title, String description, int categoryId, int authorId)
            throws ValidationException;

    /**
     * Creates an entry with an explicit type and file path.
     * Used by the upload flow to persist FILE-type entries.
     */
    void createEntry(String title, String description, int categoryId, int authorId,
                     KnowledgeEntry.EntryType entryType, String filePath)
            throws ValidationException;

    void updateEntry(int id, String title, String description, int categoryId)
            throws ValidationException;

    /**
     * Deletes an entry. Enforces ADMIN-only at the service layer — throws
     * SecurityException before touching the DAO if the role check fails.
     */
    void deleteEntry(int id, User currentUser);

    List<KnowledgeEntry> searchEntries(String keyword);
}