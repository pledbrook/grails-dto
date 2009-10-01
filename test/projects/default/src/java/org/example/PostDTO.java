package org.example;

import org.example.security.SecUserDTO;

public class PostDTO implements grails.plugins.dto.DTO {
    private Long id;
    private String content;
    private CategoryDTO category;
    private int priority;
    private SecUserDTO user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public SecUserDTO getUser() { return user; }
    public void setUser(SecUserDTO user) { this.user = user; }
}
