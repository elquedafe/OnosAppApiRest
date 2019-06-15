package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment {
    private Map<String, Switch> switches;
    private List<Cluster> clusters;
    private Map<String, Host> hosts;

    public Environment() {
            this.switches = new HashMap<String, Switch>();
            this.clusters = new ArrayList<Cluster>();
            this.hosts = new HashMap<String, Host>();
    }

    public Map<String, Switch> getMapSwitches() {
            return switches;
    }

    public void setMapSwitches(Map<String, Switch> mapSwitches) {
            this.switches = mapSwitches;
    }

    public Map<String, Host> getMapHosts() {
            return hosts;
    }

    public void setMapHosts(Map<String, Host> mapHosts) {
            this.hosts = mapHosts;
    }

    public List<Cluster> getListClusters() {
            return clusters;
    }

    public void addCluster(Cluster cluster){
            this.clusters.add(cluster);
    }

    public void addSwitch(String nombre) {
            this.switches.put(nombre, new Switch(nombre));
            //this.nSwitches++;
    }

    public void addHost(String nombre) {
            this.hosts.put(nombre, new Host(nombre));
            //this.nSwitches++;
    }
    
    public void addHost(Host host){
        this.hosts.put(host.getId(), host);
    }
	
    public List<Link> getAllLinks(){
    	List<Link> links = new ArrayList<Link>();
    	//ADD LINKS
    	for(Switch sw : switches.values()) {
    		for(Link linkTopo : sw.getListLinks()) {
    			links.add(linkTopo);
    		}
    	}
    	
    	//REMOVE DUPLICATE
    	for(Link link : links) {
    		for(Link link2 : links) {
    			if(link.equals(link2))
    				links.remove(link);
    		}
    	}
    	
    	return links;
    }
}
