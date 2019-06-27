package rest.gsonobjects.userside;

public class MeterClientRequest {
	private String host;
	private int rate;
	private int burst;
	
	public MeterClientRequest(String host, int rate, int burst) {
		super();
		this.host = host;
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
			
			return (this.host.equals(((MeterClientRequest)o).host) && 
					(this.rate == ((MeterClientRequest)o).rate) && 
					(this.burst == ((MeterClientRequest)o).burst) );
		}
		else return false;
	}
	

}
