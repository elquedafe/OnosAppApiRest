package architecture;

/**
 * Represents a cluster in ONOS
 * @author alvaroluismartinez
 *
 */
public class Cluster {
	private String id;
	private String ip;
	private int tcpPort;
	private String status;
	private String lastUpdate;
	private String humanReadableLastUpdate;

	public Cluster(String id, int puerto, String estado) {
		this.id = id;
		this.tcpPort = puerto;
		this.status = estado;
	}

	public Cluster(String id, 
			String ip, 
			int tcpPort,
			String status,
			String lastUpdate,
			String humanReadableLastUpdate) {

		this.id = id;
		this.ip = ip;
		this.tcpPort = tcpPort;
		this.status = status;
		this.lastUpdate = lastUpdate;
		this.humanReadableLastUpdate = humanReadableLastUpdate;

	}

	public Cluster(){

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the tcpPort
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * @param tcpPort the tcpPort to set
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the lastUpdate
	 */
	public String getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the humanReadableLastUpdate
	 */
	public String getHumanReadableLastUpdate() {
		return humanReadableLastUpdate;
	}

	/**
	 * @param humanReadableLastUpdate the humanReadableLastUpdate to set
	 */
	public void setHumanReadableLastUpdate(String humanReadableLastUpdate) {
		this.humanReadableLastUpdate = humanReadableLastUpdate;
	}

	public String toString() {
		return this.status + "\t" + this.id + ":"+ this.tcpPort;
	}

}
