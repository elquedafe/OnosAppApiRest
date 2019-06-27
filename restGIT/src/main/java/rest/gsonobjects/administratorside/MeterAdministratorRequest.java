package rest.gsonobjects.administratorside;

public class MeterAdministratorRequest {
	private String host;
	private int rate;
	private int burst;
	
	public MeterAdministratorRequest(String host, int rate, int burst) {
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
		if(o instanceof MeterAdministratorRequest) {
			
			return (this.host.equals(((MeterAdministratorRequest)o).host) && 
					(this.rate == ((MeterAdministratorRequest)o).rate) && 
					(this.burst == ((MeterAdministratorRequest)o).burst) );
		}
		else return false;
	}
	

}
