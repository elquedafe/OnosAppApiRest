package rest;

import java.util.List;

public class VplsClientRequest {
	private String vplsName;
	private List<String> hosts;
	
	/**
	 * @param vplsName
	 * @param hosts
	 */
	public VplsClientRequest(String vplsName, List<String> listHosts) {
		super();
		this.vplsName = vplsName;
		this.hosts = hosts;
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
	 * @return the listHosts
	 */
	public List<String> getListHosts() {
		return hosts;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setListHosts(List<String> hosts) {
		this.hosts = hosts;
	}
	
	

}
