package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entorno {
    //private int nNodos = 0;
    //private int nSwitches = 0;
    //private int nHosts = 0;
    private Map<String, Switch> switches;
    //private List<Link> listLinks;
    //private List<Switch> listSwitches;
    private List<Cluster> clusters;
    private Map<String, Host> hosts;

    public Entorno() {
            /*this.nNodos = 0;
            this.nSwitches = 0;
            this.nHosts = 0;*/
            this.switches = new HashMap<String, Switch>();
            //this.listLinks = new ArrayList<Link>();
            //this.listSwitches = new ArrayList<Switch>();
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

//    public List<Link> getListLinks() {
//            return listLinks;
//    }
//
//    public void setListLinks(List<Link> listLinks) {
//            this.listLinks = listLinks;
//    }

    public List<Cluster> getListClusters() {
            return clusters;
    }
    /*public List<Switch> getListSwitches() {
            return listSwitches;
    }

    public void setListSwitches(List<Switch> listSwitches) {
            this.listSwitches = listSwitches;
    }*/

//    public void addLink(Link link){
//            this.listLinks.add(link);
//    }

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
	
	
}
