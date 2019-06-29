package rest.gsonobjects.userside;

public class FlowSocketClientRequest {
	private int ipVersion;
	private String srcHost;
	private String srcPort;
	private String dstHost;
	private String dstPort;
	
	
	/**
	 * @param ipVersion
	 * @param srcHost
	 * @param srcPort
	 * @param dstHost
	 * @param dstPort
	 */
	public FlowSocketClientRequest(int ipVersion, String srcHost, String srcPort, String dstHost, String dstPort) {
		super();
		this.ipVersion = ipVersion;
		this.srcHost = srcHost;
		this.srcPort = srcPort;
		this.dstHost = dstHost;
		this.dstPort = dstPort;
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
	 * @return the ipVersion
	 */
	public int getIpVersion() {
		return ipVersion;
	}
	/**
	 * @param ipVersion the ipVersion to set
	 */
	public void setIpVersion(int ipVersion) {
		this.ipVersion = ipVersion;
	}
	
	
}
