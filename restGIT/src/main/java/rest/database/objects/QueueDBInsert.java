package rest.database.objects;

public class QueueDBInsert {
	private int queueId;
	private String idSwitch;
	private int qosId;
	private String portName;
	private String portNumber;
	/**
	 * @param queueId
	 * @param idSwitch
	 * @param qosId
	 * @param portName
	 * @param portNumber
	 */
	public QueueDBInsert(int queueId, String idSwitch, int qosId, String portName, String portNumber) {
		super();
		this.queueId = queueId;
		this.idSwitch = idSwitch;
		this.qosId = qosId;
		this.portName = portName;
		this.portNumber = portNumber;
	}
	/**
	 * @return the queueId
	 */
	public int getQueueId() {
		return queueId;
	}
	/**
	 * @param queueId the queueId to set
	 */
	public void setQueueId(int queueId) {
		this.queueId = queueId;
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
	 * @return the qosId
	 */
	public int getQosId() {
		return qosId;
	}
	/**
	 * @param qosId the qosId to set
	 */
	public void setQosId(int qosId) {
		this.qosId = qosId;
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
	
	

}
