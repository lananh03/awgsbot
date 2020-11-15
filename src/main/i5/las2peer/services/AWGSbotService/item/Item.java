package i5.las2peer.services.AWGSbotService.item;

import java.sql.Timestamp;

public class Item {
	
	private String id;
	private String name;
	private String description;
	private String url;
	private String type;
	private String owner;
	private Timestamp lastupdate;
	
	public Item() {
		
	}
	
	public Item(String id, String name, String description, String url, String type, String owner, Timestamp lastupdate) {
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
		this.type = type;
		this.owner = owner;
		this.lastupdate = lastupdate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Timestamp getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}
	
	
}
