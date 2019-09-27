package rest.database.objects;

/**
 * Represents queue database object
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class QueueDBResponse {
	private String idQueue;
	private String idSwitch;
	private String idQos;
	private String portName;
	private String portNumber;
	private String idUser;
	private String minRate;
	private String maxRate;
	private String burst;
	private String vplsName;
	/**
	 * @param idQueue
	 * @param idSwitch
	 * @param idQos
	 * @param portName
	 * @param portNumber
	 * @param idUser
	 * @param minRate
	 * @param maxRate
	 * @param burst
	 * @param vplsName
	 */
	public QueueDBResponse(String idQueue, String idSwitch, String idQos, String portName, String portNumber,
			String idUser, String minRate, String maxRate, String burst, String vplsName) {
		super();
		this.idQueue = idQueue;
		this.idSwitch = idSwitch;
		this.idQos = idQos;
		this.portName = portName;
		this.portNumber = portNumber;
		this.idUser = idUser;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
		this.vplsName = vplsName;
	}
	/**
	 * @return the idQueue
	 */
	public String getIdQueue() {
		return idQueue;
	}
	/**
	 * @param idQueue the idQueue to set
	 */
	public void setIdQueue(String idQueue) {
		this.idQueue = idQueue;
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
	 * @return the idQos
	 */
	public String getIdQos() {
		return idQos;
	}
	/**
	 * @param idQos the idQos to set
	 */
	public void setIdQos(String idQos) {
		this.idQos = idQos;
	}
	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}
	/**
	 * @return the portNumber
	 */
	public String getPortNumber() {
		return portNumber;
	}
	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
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
	/**
	 * @return the minRate
	 */
	public String getMinRate() {
		return minRate;
	}
	/**
	 * @param minRate the minRate to set
	 */
	public void setMinRate(String minRate) {
		this.minRate = minRate;
	}
	/**
	 * @return the maxRate
	 */
	public String getMaxRate() {
		return maxRate;
	}
	/**
	 * @param maxRate the maxRate to set
	 */
	public void setMaxRate(String maxRate) {
		this.maxRate = maxRate;
	}
	/**
	 * @return the burst
	 */
	public String getBurst() {
		return burst;
	}
	/**
	 * @param burst the burst to set
	 */
	public void setBurst(String burst) {
		this.burst = burst;
	}
	/**
	 * @return the vplsName
	 */
	public String getVplsName() {
		return vplsName;
	}
	/**
	 * @param vplsName the vplsName to set
	 */
	public void setVplsName(String vplsName) {
		this.vplsName = vplsName;
	}
	
	
	
	
	
}
