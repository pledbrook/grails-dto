package org.example.security;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.example.PostDTO;

public class SecUserDTO implements grails.plugins.dto.DTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private Date dateOfBirth;
    private String email;
    private Set<SecRoleDTO> roles;
    private List<PostDTO> posts;
    private String fullName;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<SecRoleDTO> getRoles() { return roles; }
    public void setRoles(Set<SecRoleDTO> roles) { this.roles = roles; }
    public List<PostDTO> getPosts() { return posts; }
    public void setPosts(List<PostDTO> posts) { this.posts = posts; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
