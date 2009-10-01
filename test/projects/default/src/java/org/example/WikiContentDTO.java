package org.example;

public class WikiContentDTO implements grails.plugins.dto.DTO {
    private Long id;
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
