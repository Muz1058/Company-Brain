package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;

import java.util.List;


public interface KnowledgeService {

    
    List<KnowledgeEntry> getAllEntries();

    
    KnowledgeEntry getEntryById(int id);

    
    void createEntry(String title, String description, int categoryId, int authorId) throws ValidationException;

    
    void updateEntry(int id, String title, String description, int categoryId) throws ValidationException;

    
    void deleteEntry(int id);

    
    List<KnowledgeEntry> searchEntries(String keyword);
}
