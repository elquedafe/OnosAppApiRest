package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the network environment
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Environment {
	private Map<String, Switch> switches;
	private List<Cluster> clusters;
	private Map<String, Host> hosts;

	/**
	 * Default environment constructor
	 */
	public Environment() {
		this.switches = new HashMap<String, Switch>();
		this.clusters = new ArrayList<Cluster>();
		this.hosts = new HashMap<String, Host>();
	}

	/**
	 * Get switches map
	 * @return switches map
	 */
	public Map<String, Switch> getMapSwitches() {
		return switches;
	}

	/**
	 * Set switches map
	 * @param mapSwitches switches map
	 */
	public void setMapSwitches(Map<String, Switch> mapSwitches) {
		this.switches = mapSwitches;
	}

	/**
	 * Get hosts map
	 * @return hosts map
	 */
	public Map<String, Host> getMapHosts() {
		return hosts;
	}

	/**
	 * Set hosts map
	 * @param mapHosts hosts map
	 */
	public void setMapHosts(Map<String, Host> mapHosts) {
		this.hosts = mapHosts;
	}

	/**
	 * Get clusters list
	 * @return cluster lists
	 */
	public List<Cluster> getListClusters() {
		return clusters;
	}

	/**
	 * Add cluster to the environment
	 * @param cluster cluster to add
	 */
	public void addCluster(Cluster cluster){
		this.clusters.add(cluster);
	}

	/**
	 * Add switch to the environment
	 * @param name switch name
	 */
	public void addSwitch(String name) {
		this.switches.put(name, new Switch(name));
		//this.nSwitches++;
	}

	/**
	 * Add a host to the environment
	 * @param name host name
	 */
	public void addHost(String name) {
		this.hosts.put(name, new Host(name));
		//this.nSwitches++;
	}

	/**
	 * Add a host to the environment
	 * @param host host to add
	 */
	public void addHost(Host host){
		this.hosts.put(host.getId(), host);
	}

	/**
	 * Get all links in the network
	 * @return
	 */
	public List<Link> getAllLinks(){
		List<Link> links = new ArrayList<Link>();

		//Add links
		for(Switch sw : switches.values()) {
			for(Link linkTopo : sw.getListLinks()) {
				links.add(linkTopo);
			}
		} 	

		//Remove duplicated
		for(Link link : links) {
			for(Link link2 : links) {
				if(link.equals(link2))
					links.remove(link);
			}
		}

		return links;
	}
}
