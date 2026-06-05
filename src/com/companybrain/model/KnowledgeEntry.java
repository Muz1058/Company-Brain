package com.companybrain.model;

import java.time.LocalDateTime;

public class KnowledgeEntry {

    public enum EntryType {
        TEXT, FILE
    }

    private int id;
    private String title;
    private String description;
    private int categoryId;
    private int authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EntryType entryType;
    private String filePath;

    public KnowledgeEntry() {
        this.entryType = EntryType.TEXT;
    }

    public KnowledgeEntry(int id, String title, String description, int categoryId,
                          int authorId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.entryType = EntryType.TEXT;
        this.filePath = null;
    }

    public KnowledgeEntry(int id, String title, String description, int categoryId,
                          int authorId, LocalDateTime createdAt, LocalDateTime updatedAt,
                          EntryType entryType, String filePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.entryType = entryType != null ? entryType : EntryType.TEXT;
        this.filePath = filePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public EntryType getEntryType() { return entryType; }
    public void setEntryType(EntryType entryType) {
        this.entryType = entryType != null ? entryType : EntryType.TEXT;
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public boolean isFileEntry() {
        return EntryType.FILE.equals(this.entryType) && filePath != null && !filePath.isBlank();
    }

    @Override
    public String toString() {
        return "KnowledgeEntry{id=" + id + ", title='" + title + "', entryType=" + entryType + "}";
    }
}
