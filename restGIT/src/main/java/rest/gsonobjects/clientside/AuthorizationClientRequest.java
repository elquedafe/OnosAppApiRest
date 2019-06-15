package rest.gsonobjects.clientside;

public class AuthorizationClientRequest {
	private String user;
	private String password;
	private String onosHost;
	
	public AuthorizationClientRequest(String user, String password, String onosHost) {
		super();
		this.user = user;
		this.password = password;
		this.onosHost = onosHost;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the onosHost
	 */
	public String getOnosHost() {
		return onosHost;
	}

	/**
	 * @param onosHost the onosHost to set
	 */
	public void setOnosHost(String onosHost) {
		this.onosHost = onosHost;
	}

	
	
	

}
