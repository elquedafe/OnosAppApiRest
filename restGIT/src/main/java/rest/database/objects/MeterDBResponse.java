package rest.database.objects;

public class MeterDBResponse {
	private String idMeter;
	private String idSwitch;
	private String idUser;
	/**
	 * @param idMeter
	 * @param idSwitch
	 * @param idUser
	 */
	public MeterDBResponse(String idMeter, String idSwitch, String idUser) {
		super();
		this.idMeter = idMeter;
		this.idSwitch = idSwitch;
		this.idUser = idUser;
	}
	/**
	 * @return the idMeter
	 */
	public String getIdMeter() {
		return idMeter;
	}
	/**
	 * @param idMeter the idMeter to set
	 */
	public void setIdMeter(String idMeter) {
		this.idMeter = idMeter;
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
