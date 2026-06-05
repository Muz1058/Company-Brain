package com.companybrain.service;

import com.companybrain.dao.KnowledgeEntryDao;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.KnowledgeEntry.EntryType;
import com.companybrain.model.User;
import com.companybrain.util.InputValidator;

import java.util.List;

public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeEntryDao entryDao;

    public KnowledgeServiceImpl(KnowledgeEntryDao entryDao) {
        this.entryDao = entryDao;
    }

    @Override
    public List<KnowledgeEntry> getAllEntries() {
        return entryDao.findAll();
    }

    @Override
    public KnowledgeEntry getEntryById(int id) {
        return entryDao.findById(id);
    }

    @Override
    public void createEntry(String title, String description, int categoryId, int authorId)
            throws ValidationException {
        // Delegates to the richer overload with TEXT defaults
        createEntry(title, description, categoryId, authorId, EntryType.TEXT, null);
    }

    /**
     * Extended create used by the upload flow; sets entry_type to FILE and stores
     * the absolute file path.
     */
    public void createEntry(String title, String description, int categoryId, int authorId,
                            EntryType entryType, String filePath) throws ValidationException {
        try {
            InputValidator.validateKnowledgeEntry(title, description);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }

        if (categoryId <= 0) {
            throw new ValidationException("Please select a valid category.");
        }

        KnowledgeEntry entry = new KnowledgeEntry(
            0, title.trim(), description.trim(), categoryId, authorId, null, null,
            entryType, filePath
        );
        entryDao.save(entry);
    }

    @Override
    public void updateEntry(int id, String title, String description, int categoryId)
            throws ValidationException {
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
        // Preserve entry_type and file_path — editing does not change the file origin
        entryDao.update(existing);
    }

    /**
     * Backend security enforcement: only ADMIN users may delete entries.
     * This check lives in the service layer so it cannot be bypassed by
     * manipulating the UI.
     */
    @Override
    public void deleteEntry(int id, User currentUser) {
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new SecurityException(
                "Access denied: only administrators are allowed to delete entries."
            );
        }
        entryDao.delete(id);
    }

    @Override
    public List<KnowledgeEntry> searchEntries(String keyword) {
        if (InputValidator.isEmpty(keyword)) {
            return getAllEntries();
        }
        return entryDao.search(keyword.trim());
    }
}
