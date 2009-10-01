package org.example;

public class PageDTO {
    private Long id;
    private WikiContentDTO content;
    private String title;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WikiContentDTO getContent() { return content; }
    public void setContent(WikiContentDTO content) { this.content = content; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
