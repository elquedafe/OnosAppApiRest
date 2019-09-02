package rest.gsonobjects.userside;

import java.util.List;

public class VplsClientRequest {
	private String vplsName;
	private List<String> hosts;
	private int rate = -1;
	private int burst = -1;
	
	/**
	 * @param vplsName
	 * @param hosts
	 */
	public VplsClientRequest(String vplsName, List<String> hosts, int rate, int burst) {
		super();
		this.vplsName = vplsName;
		this.hosts = hosts;
		this.rate = rate;
		this.burst = burst;
	}
	
	public VplsClientRequest(String vplsName, List<String> hosts) {
		super();
		this.vplsName = vplsName;
		this.hosts = hosts;
		this.rate = -1;
		this.burst = -1;
	}
	
	public VplsClientRequest() {
		System.out.println("In default VplsClientConstr");
	}

	/**
	 * @return the vplsName
	 */
	public String getVplsName() {
		return vplsName;
	}

	/**
	 * @param vplsName the vplsName to set
	 */
	public void setVplsName(String vplsName) {
		this.vplsName = vplsName;
	}


	/**
	 * @return the hosts
	 */
	public List<String> getHosts() {
		return hosts;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the rate
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
	
	

}
