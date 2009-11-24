package org.example;

public class WikiContentDTO implements grails.plugins.dto.DTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WikiContentDTO[");
        sb.append("\n\tid: " + this.id);
        sb.append("\n\tcontent: " + this.content);
        sb.append("]");
        return sb.toString();
    }
}
