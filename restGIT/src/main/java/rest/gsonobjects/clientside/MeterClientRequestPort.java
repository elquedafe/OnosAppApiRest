package rest.gsonobjects.clientside;

public class MeterClientRequestPort {
	private String host;
	private int port;
	private String portType;
	private int rate;
	private int burst;
	
	public MeterClientRequestPort(String host, int port, String portType, int rate, int burst) {
		super();
		this.host = host;
		this.port = port;
		this.portType = portType; 
		this.rate = rate;
		this.burst = burst;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
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
			
			return (this.host.equals(((MeterClientRequestPort)o).host) && 
					(this.port == ((MeterClientRequestPort)o).port) &&
					(this.portType == ((MeterClientRequestPort)o).portType) &&
					(this.rate == ((MeterClientRequestPort)o).rate) &&
					(this.burst == ((MeterClientRequestPort)o).burst) );
		}
		else return false;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
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
	

}
