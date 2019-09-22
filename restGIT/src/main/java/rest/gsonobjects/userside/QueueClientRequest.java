package rest.gsonobjects.userside;

public class QueueClientRequest {
	private String ipVersion;
	private String srcHost;
	private String srcPort;
	private String dstHost;
	private String dstPort;
	private String portType;
	private int minRate;
	private int maxRate;
	private int burst;
	/**
	 * @param ipVersion
	 * @param srcHost
	 * @param srcPort
	 * @param dstHost
	 * @param dstPort
	 * @param portType
	 * @param minRate
	 * @param maxRate
	 * @param burst
	 */
	public QueueClientRequest(String ipVersion, String srcHost, String srcPort, String dstHost, String dstPort,
			String portType, int minRate, int maxRate, int burst) {
		super();
		this.ipVersion = ipVersion;
		this.srcHost = srcHost;
		this.srcPort = srcPort;
		this.dstHost = dstHost;
		this.dstPort = dstPort;
		this.portType = portType;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
	}
	
	public QueueClientRequest() {
		this.ipVersion = "4";
		this.srcHost = "";
		this.srcPort = "";
		this.dstHost = "";
		this.dstPort = "";
		this.portType = ""; 
		this.minRate = 0;
		this.maxRate = 0;
		this.burst = 0;
	}
	/**
	 * @return the ipVersion
	 */
	public String getIpVersion() {
		return ipVersion;
	}
	/**
	 * @param ipVersion the ipVersion to set
	 */
	public void setIpVersion(String ipVersion) {
		this.ipVersion = ipVersion;
	}
	/**
	 * @return the srcHost
	 */
	public String getSrcHost() {
		return srcHost;
	}
	/**
	 * @param srcHost the srcHost to set
	 */
	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}
	/**
	 * @return the srcPort
	 */
	public String getSrcPort() {
		return srcPort;
	}
	/**
	 * @param srcPort the srcPort to set
	 */
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	/**
	 * @return the dstHost
	 */
	public String getDstHost() {
		return dstHost;
	}
	/**
	 * @param dstHost the dstHost to set
	 */
	public void setDstHost(String dstHost) {
		this.dstHost = dstHost;
	}
	/**
	 * @return the dstPort
	 */
	public String getDstPort() {
		return dstPort;
	}
	/**
	 * @param dstPort the dstPort to set
	 */
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	/**
	 * @return the portType
	 */
	public String getPortType() {
		return portType;
	}
	/**
	 * @param portType the portType to set
	 */
	public void setPortType(String portType) {
		this.portType = portType;
	}
	/**
	 * @return the minRate
	 */
	public int getMinRate() {
		return minRate;
	}
	/**
	 * @param minRate the minRate to set
	 */
	public void setMinRate(int minRate) {
		this.minRate = minRate;
	}
	/**
	 * @return the maxRate
	 */
	public int getMaxRate() {
		return maxRate;
	}
	/**
	 * @param maxRate the maxRate to set
	 */
	public void setMaxRate(int maxRate) {
		this.maxRate = maxRate;
	}
	/**
	 * @return the burst
	 */
	public int getBurst() {
		return burst;
	}
	/**
	 * @param burst the burst to set
	 */
	public void setBurst(int burst) {
		this.burst = burst;
	}
	
	public FlowSocketClientRequest toFlowSocketClientRequest() {
		FlowSocketClientRequest fQ = new FlowSocketClientRequest(4, this.srcHost, this.srcPort, this.dstHost, this.dstPort, this.portType);
		return fQ;
	}
	

}
