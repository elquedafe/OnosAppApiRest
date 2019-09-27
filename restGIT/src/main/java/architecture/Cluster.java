package architecture;

/**
 * Represents a cluster in ONOS
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Cluster {
	private String id;
	private String ip;
	private int tcpPort;
	private String status;
	private String lastUpdate;
	private String humanReadableLastUpdate;

	/**
	 * Cluster constructor
	 * @param id cluster id
	 * @param port tcp port
	 * @param state cluster state
	 */
	public Cluster(String id, int port, String state) {
		this.id = id;
		this.tcpPort = port;
		this.status = state;
	}

	/**
	 * Cluster constructor
	 * @param id cluster id
	 * @param ip ip address
	 * @param tcpPort tcp port
	 * @param status status
	 * @param lastUpdate last update
	 * @param humanReadableLastUpdate last update
	 */
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

	/**
	 * Default cluster constructor
	 */
	public Cluster(){

	}

	/**
	 * Get cluster id
	 * @return cluster id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set cluster id
	 * @param cluster id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get cluster IP address
	 * @return cluster ip address
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Set cluster IP address
	 * @param ip address
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Get cluster TCP port
	 * @return cluster TCP port
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * Set cluster TCP port
	 * @param tcpPort cluster TCP port
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	/**
	 * Get cluster state
	 * @return cluster status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set status
	 * @param status cluster status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get last update
	 * @return last update
	 */
	public String getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Set last update
	 * @param lastUpdate last update
	 */
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Get last update
	 * @return last update
	 */
	public String getHumanReadableLastUpdate() {
		return humanReadableLastUpdate;
	}

	/**
	 * Set last update
	 * @param humanReadableLastUpdate last update
	 */
	public void setHumanReadableLastUpdate(String humanReadableLastUpdate) {
		this.humanReadableLastUpdate = humanReadableLastUpdate;
	}

	/**
	 * Override toString()
	 */
	@Override
	public String toString() {
		return this.status + "\t" + this.id + ":"+ this.tcpPort;
	}

}
