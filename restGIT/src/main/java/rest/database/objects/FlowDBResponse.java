package rest.database.objects;

/**
 * Represents flow object from database
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class FlowDBResponse {
	private String idFlow;
	private String idSwitch;
	private String idUser;
	
	/**
	 * FlowDBResponse constructor
	 * @param idFlow flow id
	 * @param idSwitch switch d
	 * @param idUser user id
	 */
	public FlowDBResponse(String idFlow, String idSwitch, String idUser) {
		super();
		this.idFlow = idFlow;
		this.idSwitch = idSwitch;
		this.idUser = idUser;
	}
	/**
	 * Get flow id
	 * @return flow id
	 */
	public String getIdFlow() {
		return idFlow;
	}
	/**
	 * Set flow id
	 * @param idFlow flow id
	 */
	public void setIdFlow(String idFlow) {
		this.idFlow = idFlow;
	}
	/**
	 * Get switch id
	 * @return switch id
	 */
	public String getIdSwitch() {
		return idSwitch;
	}
	/**
	 * Set switch id
	 * @param idSwitch switch id
	 */
	public void setIdSwitch(String idSwitch) {
		this.idSwitch = idSwitch;
	}
	/**
	 * Get user id
	 * @return user id
	 */
	public String getIdUser() {
		return idUser;
	}
	/**
	 * Set user id
	 * @param idUser user id
	 */
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

}
