package rest.gsonobjects.userside;

import java.util.List;

public class VplsClientRequest {
	private String vplsName;
	private List<String> hosts;
	private int maxRate = -1;
	private int minRate = -1;
	private int burst = -1;
	
	/**
	 * @param vplsName
	 * @param hosts
	 */
	public VplsClientRequest(String vplsName, List<String> hosts, int maxRate, int minRate, int burst) {
		super();
		this.vplsName = vplsName;
		this.hosts = hosts;
		this.maxRate = maxRate;
		this.minRate = minRate;
		this.burst = burst;
	}
	
	public VplsClientRequest(String vplsName, List<String> hosts) {
		super();
		this.vplsName = vplsName;
		this.hosts = hosts;
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
	public int getMinRate() {
		return minRate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setMinRate(int minRate) {
		this.maxRate = minRate;
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
	
	
	
	

}
