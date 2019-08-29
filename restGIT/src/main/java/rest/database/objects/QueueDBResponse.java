package rest.database.objects;

public class QueueDBResponse {
	private String idQueue;
	private String idSwitch;
	private String idQos;
	private String portName;
	private String portNumber;
	private String idUser;
	/**
	 * @param idQueue
	 * @param idSwitch
	 * @param idQos
	 * @param portName
	 * @param portNumber
	 * @param idUser
	 */
	public QueueDBResponse(String idQueue, String idSwitch, String idQos, String portName, String portNumber,
			String idUser) {
		super();
		this.idQueue = idQueue;
		this.idSwitch = idSwitch;
		this.idQos = idQos;
		this.portName = portName;
		this.portNumber = portNumber;
		this.idUser = idUser;
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
	
	
}
