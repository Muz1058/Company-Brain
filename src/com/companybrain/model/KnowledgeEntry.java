package com.companybrain.model;

import java.time.LocalDateTime;

/**
 * Represents a knowledge base entry.
 */
public class KnowledgeEntry {
    private int id;
    private String title;
    private String content;
    private String tags; // Comma-separated tags
    private int authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeEntry() {
    }

    public KnowledgeEntry(int id, String title, String content, String tags, int authorId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "KnowledgeEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", authorId=" + authorId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
