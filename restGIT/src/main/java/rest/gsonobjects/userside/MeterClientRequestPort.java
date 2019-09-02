package rest.gsonobjects.userside;

public class MeterClientRequestPort {
	private String ipVersion;
	private String srcHost;
	private String srcPort;
	private String dstHost;
	private String dstPort;
	private String portType;
	private int rate;
	private int burst;
	
	public MeterClientRequestPort(String ipVersion, String srcHost, String srcPort, String dstHost, String dstPort, String portType, int rate, int burst) {
		super();
		this.ipVersion = ipVersion;
		this.srcHost = srcHost;
		this.srcPort = srcPort;
		this.dstHost = dstHost;
		this.dstPort = dstPort;
		this.portType = portType; 
		this.rate = rate;
		this.burst = burst;
	}
	
	public MeterClientRequestPort() {
		super();
		this.ipVersion = "4";
		this.srcHost = "";
		this.srcPort = "";
		this.dstHost = "";
		this.dstPort = "";
		this.portType = ""; 
		this.rate = 0;
		this.burst = 0;
	}

	/**
	 * @return the host
	 */
	public String getSrcHost() {
		return srcHost;
	}

	/**
	 * @param host the host to set
	 */
	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}

	/**
	 * @return the meter rate
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(int rate) {
		this.rate = rate;
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
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof MeterClientRequest) {
			
			return ((this.srcHost.equals(((MeterClientRequestPort)o).srcHost)) && 
					(this.srcPort.equals(((MeterClientRequestPort)o).srcPort)) &&
					(this.dstHost.equals(((MeterClientRequestPort)o).dstHost)) && 
					(this.dstPort.equals(((MeterClientRequestPort)o).dstPort)) &&
					(this.portType.equals(((MeterClientRequestPort)o).portType)) &&
					(this.rate == ((MeterClientRequestPort)o).rate) &&
					(this.burst == ((MeterClientRequestPort)o).burst));
		}
		else return false;
	}

	/**
	 * @return the port
	 */
	public String getSrcPort() {
		return srcPort;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String srcPort) {
		this.srcPort = srcPort;
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
	 * @param srcPort the srcPort to set
	 */
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
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
	

	@Override
	public String toString() {
		String ret = "ipVersion: "+this.ipVersion+"\n"+
					 "srcHost: "+this.srcHost+"\n"+
					 "srcPort: "+this.srcPort+"\n"+
					 "dstHost: "+this.dstHost+"\n"+
					 "dstPort: "+this.dstPort+"\n"+
					 "portType: "+this.portType+"\n"+
					 "rate: "+this.rate+"\n"+
					 "burst: "+this.burst+"\n";
		return ret;
	}
}
