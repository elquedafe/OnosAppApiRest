package rest.gsonobjects.userside;

public class AuthorizationClientRequest {
	

	private String userOnos;
	private String passwordOnos;
	private String onosHost;
	private String ovsdbDevice;
	
	
	/**
	 * @param userRest
	 * @param passwordRest
	 * @param userOnos
	 * @param passwordOnos
	 * @param onosHost
	 */
	public AuthorizationClientRequest(String userOnos, String passwordOnos,
			String onosHost, String ovsdbDevice) {
		super();
		this.userOnos = userOnos;
		this.passwordOnos = passwordOnos;
		this.onosHost = onosHost;
		this.ovsdbDevice = ovsdbDevice;
	}
	
	/**
	 * @return the userOnos
	 */
	public String getUserOnos() {
		return userOnos;
	}
	/**
	 * @param userOnos the userOnos to set
	 */
	public void setUserOnos(String userOnos) {
		this.userOnos = userOnos;
	}
	/**
	 * @return the passwordOnos
	 */
	public String getPasswordOnos() {
		return passwordOnos;
	}
	/**
	 * @param passwordOnos the passwordOnos to set
	 */
	public void setPasswordOnos(String passwordOnos) {
		this.passwordOnos = passwordOnos;
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

	public String getOvsdbDevice() {
		// TODO Auto-generated method stub
		return ovsdbDevice;
	}
	
	/**
	 * @param ovsdbDevice the ovsdbDevice to set
	 */
	public void setOvsdbDevice(String ovsdbDevice) {
		this.ovsdbDevice = ovsdbDevice;
	}
	

	
	
	

}
