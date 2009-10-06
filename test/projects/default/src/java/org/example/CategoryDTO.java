package org.example;

import java.util.Set;

public class CategoryDTO implements grails.plugins.dto.DTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Set<PostDTO> posts;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<PostDTO> getPosts() { return posts; }
    public void setPosts(Set<PostDTO> posts) { this.posts = posts; }
}
