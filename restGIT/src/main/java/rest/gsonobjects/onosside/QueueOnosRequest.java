package rest.gsonobjects.onosside;

/**
 * Represents a queue request to ONOS
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class QueueOnosRequest {
	private String portName;
	private String portNumber;
	private String portSpeed;
	private int queueId;
	private String minRate;
	private String maxRate;
	private String burst;
	private int qosId;
	/**
	 * @param portName
	 * @param portNumber
	 * @param portSpeed
	 * @param queueId
	 * @param minRate
	 * @param maxRate
	 * @param burst
	 * @param qosId
	 */
	public QueueOnosRequest(String portName, String portNumber, String portSpeed, int queueId, String minRate,
			String maxRate, String burst, int qosId) {
		super();
		this.portName = portName;
		this.portNumber = portNumber;
		this.portSpeed = portSpeed;
		this.queueId = queueId;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
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
	/**
	 * @return the portSpeed
	 */
	public String getPortSpeed() {
		return portSpeed;
	}
	/**
	 * @param portSpeed the portSpeed to set
	 */
	public void setPortSpeed(String portSpeed) {
		this.portSpeed = portSpeed;
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
	
	
}
