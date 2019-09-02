package rest.database.objects;

public class FlowDBResponse {
	private String idFlow;
	private String idSwitch;
	private String idUser;
	
	
	
	/**
	 * @param idFlow
	 * @param idSwitch
	 * @param idUser
	 */
	public FlowDBResponse(String idFlow, String idSwitch, String idUser) {
		super();
		this.idFlow = idFlow;
		this.idSwitch = idSwitch;
		this.idUser = idUser;
	}
	/**
	 * @return the idFlow
	 */
	public String getIdFlow() {
		return idFlow;
	}
	/**
	 * @param idFlow the idFlow to set
	 */
	public void setIdFlow(String idFlow) {
		this.idFlow = idFlow;
	}
	/**
	 * @return the idSwitch
	 */
	public String getIdSwitch() {
		return idSwitch;
	}
	/**
	 * @param idSwitch the idSwitch to set
	 */
	public void setIdSwitch(String idSwitch) {
		this.idSwitch = idSwitch;
	}
	/**
	 * @return the idUser
	 */
	public String getIdUser() {
		return idUser;
	}
	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

}
