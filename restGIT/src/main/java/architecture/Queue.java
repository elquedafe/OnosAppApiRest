package architecture;

/**
 * Represents a queue in a port
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Queue {
	private long queueId;
	private String switchId;
	private String minRate;
	private String maxRate;
	private String burst;
	private long qosId;
	private String portNumber;
	private String portName;

	/**
	 * Queue constructor
	 * @param queueId queue id
	 * @param switchId switch id
	 * @param minRate minimum rate
	 * @param maxRate maximum rate
	 * @param burst burst
	 * @param qosId qos id
	 * @param portNumber port number port name
	 * @param portName
	 */
	public Queue(long queueId, String switchId, String minRate, String maxRate, String burst, long qosId, String portNumber,
			String portName) {
		super();
		this.queueId = queueId;
		this.switchId = switchId;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
		this.qosId = qosId;
		this.portNumber = portNumber;
		this.portName = portName;
	}
	/**
	 * Get queue id
	 * @return queue id
	 */
	public long getQueueId() {
		return queueId;
	}
	/**
	 * Set queue id
	 * @param queueId queue id
	 */
	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}
	/**
	 * Get switch id
	 * @return switch id
	 */
	public String getSwitchId() {
		return switchId;
	}
	/**
	 * Set switch id
	 * @param switchId switch id
	 */
	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}
	/**
	 * Get minimum rate
	 * @return minimum rate
	 */
	public String getMinRate() {
		return minRate;
	}
	/**
	 * Set minimum rate
	 * @param minRate minimum rate
	 */
	public void setMinRate(String minRate) {
		this.minRate = minRate;
	}
	/**
	 * Get maximum rate
	 * @return maximum rate
	 */
	public String getMaxRate() {
		return maxRate;
	}
	/**
	 * Set maximum rate
	 * @param maxRate maximum rate
	 */
	public void setMaxRate(String maxRate) {
		this.maxRate = maxRate;
	}
	/**
	 * Get queue burst
	 * @return queue burst
	 */
	public String getBurst() {
		return burst;
	}
	/**
	 * Set queue burst
	 * @param burst queue burst
	 */
	public void setBurst(String burst) {
		this.burst = burst;
	}
	/**
	 * Get qos ovsdb-port id
	 * @return the qosId
	 */
	public long getQosId() {
		return qosId;
	}
	/**
	 * Set qos ovsdb-port id
	 * @param qosId qos ovsdb-port id
	 */
	public void setQosId(long qosId) {
		this.qosId = qosId;
	}
	/**
	 * Get port number
	 * @return port number
	 */
	public String getPortNumber() {
		return portNumber;
	}
	/**
	 * Set port number
	 * @param portNumber port number
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * Get port name
	 * @return port name
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * Set port name
	 * @param portName port name
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}
}
