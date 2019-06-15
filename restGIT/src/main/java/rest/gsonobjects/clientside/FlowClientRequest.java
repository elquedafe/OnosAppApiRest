package rest.gsonobjects.clientside;

public class FlowClientRequest {
	private String switchId;
	private int priority;
	private int timeout;
	private boolean isPermanent;
	private String srcPort;
	private String dstPort;
	private String srcHost;
	private String dstHost;
	
	public FlowClientRequest(String switchId, int priority, int timeout, boolean isPermanent, String srcPort, String dstPort,
			String srcHost, String dstHost) {
		super();
		this.switchId = switchId;
		this.priority = priority;
		this.timeout = timeout;
		this.isPermanent = isPermanent;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.srcHost = srcHost;
		this.dstHost = dstHost;
	}

	public String getSwitchId() {
		return switchId;
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstPort() {
		return dstPort;
	}

	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}

	public String getSrcHost() {
		return srcHost;
	}

	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}

	public String getDstHost() {
		return dstHost;
	}

	public void setDstHost(String dstHost) {
		this.dstHost = dstHost;
	}

	public boolean isPermanent() {
		return isPermanent;
	}

	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}
	
	
	

}
