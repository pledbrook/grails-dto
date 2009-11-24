package org.example;

import org.example.security.SecUserDTO;

public class PostDTO implements grails.plugins.dto.DTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String content;
    private CategoryDTO category;
    private int priority;
    private PostType type;
    private SecUserDTO user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }
    public SecUserDTO getUser() { return user; }
    public void setUser(SecUserDTO user) { this.user = user; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PostDTO[");
        sb.append("\n\tid: " + this.id);
        sb.append("\n\tcontent: " + this.content);
        sb.append("\n\tcategory: " + this.category);
        sb.append("\n\tpriority: " + this.priority);
        sb.append("\n\ttype: " + this.type);
        sb.append("\n\tuser: " + this.user);
        sb.append("]");
        return sb.toString();
    }
}
