
package architecture;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a network host
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Host {
	private String id;
	private String mac;
	private String vlan;
	private String innerVlan;
	private String outerTpid;
	private boolean configured;
	private List<String> ipList;
	private Map<String, String> locations;

	/**
	 * Host constructor
	 */
	public Host() {
		this.id = "";
		this.mac = "";
		this.vlan = "";
		this.innerVlan = "";
		this.outerTpid = "";
		this.configured = false;
		this.ipList = new ArrayList<String>();
		this.locations = new HashMap<String, String>();
	}

	/**
	 * Host constructor
	 * @param name host id
	 */
	public Host(String name) {
		this.id = name;
		this.mac = "";
		this.vlan = "";
		this.innerVlan = "";
		this.outerTpid = "";
		this.configured = false;
		this.ipList = new ArrayList<String>();
		this.locations = new HashMap<String, String>();
	}

	/**
	 * Host constructor
	 * @param id host id
	 * @param mac host mac
	 * @param vlan host vlan tag
	 * @param innerVlan host inner vlan tag
	 * @param outerTpid outer tpid
	 * @param configured if host is configured
	 * @param ipList ip address list
	 * @param mapLocations host locations
	 */
	public Host(String id, String mac, String vlan, String innerVlan, String outerTpid, boolean configured, List<String> ipList, Map<String, String> mapLocations) {
		this.id = id;
		this.mac = mac;
		this.vlan = vlan;
		this.innerVlan = innerVlan;
		this.outerTpid = outerTpid;
		this.configured = configured;
		this.ipList = ipList;
		this.locations = mapLocations;
	}

	/**
	 * Get host id
	 * @return host id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set host id
	 * @param host id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get host mac address
	 * @return host mac address
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * Set mac address
	 * @param mac mac address
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * Get vlan tag
	 * @return vlan tag
	 */
	public String getVlan() {
		return vlan;
	}

	/**
	 * Set vlan tag
	 * @param vlan vlan tag
	 */
	public void setVlan(String vlan) {
		this.vlan = vlan;
	}

	/**
	 * Get inner vlan tag
	 * @return inner vlan tag
	 */
	public String getInnerVlan() {
		return innerVlan;
	}

	/**
	 * Set inner vlan tag
	 * @param innerVlan inner vlan tag
	 */
	public void setInnerVlan(String innerVlan) {
		this.innerVlan = innerVlan;
	}

	/**
	 * Get outer tpid
	 * @return outer tpid
	 */
	public String getOuterTpid() {
		return outerTpid;
	}

	/**
	 * Set outer tpid
	 * @param outerTpid outer tpid
	 */
	public void setOuterTpid(String outerTpid) {
		this.outerTpid = outerTpid;
	}

	/**
	 * Get if host is configured
	 * @return is configured
	 */
	public boolean isConfigured() {
		return configured;
	}

	/**
	 * Set if host is configured
	 * @param is configured
	 */
	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	/**
	 * Get ip list
	 * @return ip list
	 */
	public List<String> getIpList() {
		return ipList;
	}

	/**
	 * Get ip list
	 * @param ipList ip list
	 */
	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}

	/**
	 * Get locations
	 * @return host locations
	 */
	public Map<String, String> getLocations() {
		return locations;
	}

	/**
	 * Set host locations
	 * @param locations hos tlocation
	 */
	public void setLocations(Map<String, String> locations) {
		this.locations = locations;
	}

	/**
	 * Get map locations
	 * @return map locations
	 */
	public Map<String, String> getMapLocations() {
		return locations;
	}

	/**
	 * Set locations map
	 * @param mapLocations locations map
	 */
	public void setMapLocations(Map<String, String> mapLocations) {
		this.locations = mapLocations;
	}

	/**
	 * Overrides toString
	 */
	@Override
	public String toString(){
		String str = "";
		str += this.mac;
		for(String s : ipList){
			str += "/"+s;
		}
		return str;
	}
}
