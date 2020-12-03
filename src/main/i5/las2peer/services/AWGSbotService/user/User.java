package i5.las2peer.services.AWGSbotService.user;


public class User {
	
	private int authorization;
	private String sub;
	
	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public User () {
		
	}

	public int getAuthorization() {
		return authorization;
	}

	public void setAuthorization(int authorization) {
		this.authorization = authorization;
	}

}	