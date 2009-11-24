package org.another.client;

public class AttributeDTO implements grails.plugins.dto.DTO {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String type;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AttributeDTO[");
		sb.append("\n\tid: " + this.id);
		sb.append("\n\tname: " + this.name);
		sb.append("\n\ttype: " + this.type);
		sb.append("]");
		return sb.toString();
	}
}
