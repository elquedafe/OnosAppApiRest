package rest.database.objects;

/**
 * Represents meter object from database
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class MeterDBResponse {
	private String idMeter;
	private String idSwitch;
	private String idUser;
	/**
	 * MeterDBResponse
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
	 * Get meter id
	 * @return meter id
	 */
	public String getIdMeter() {
		return idMeter;
	}
	/**
	 * Set meter id
	 * @param idMeter meter id
	 */
	public void setIdMeter(String idMeter) {
		this.idMeter = idMeter;
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
