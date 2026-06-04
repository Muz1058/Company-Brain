package com.companybrain.dao;

import com.companybrain.model.KnowledgeEntry;

import java.util.List;


public interface KnowledgeEntryDao {
    
    List<KnowledgeEntry> findAll();

    
    KnowledgeEntry findById(int id);

    
    void save(KnowledgeEntry entry);

    
    void update(KnowledgeEntry entry);

    
    void delete(int id);

    
    List<KnowledgeEntry> search(String keyword);
}
