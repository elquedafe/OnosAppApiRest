package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entorno {
    //private int nNodos = 0;
    //private int nSwitches = 0;
    //private int nHosts = 0;
    private Map<String, Switch> mapSwitches;
    //private List<Link> listLinks;
    //private List<Switch> listSwitches;
    private List<Cluster> listClusters;
    private Map<String, Host> mapHosts;

    public Entorno() {
            /*this.nNodos = 0;
            this.nSwitches = 0;
            this.nHosts = 0;*/
            this.mapSwitches = new HashMap<String, Switch>();
            //this.listLinks = new ArrayList<Link>();
            //this.listSwitches = new ArrayList<Switch>();
            this.listClusters = new ArrayList<Cluster>();
            this.mapHosts = new HashMap<String, Host>();
    }

    public Map<String, Switch> getMapSwitches() {
            return mapSwitches;
    }

    public void setMapSwitches(Map<String, Switch> mapSwitches) {
            this.mapSwitches = mapSwitches;
    }

    public Map<String, Host> getMapHosts() {
            return mapHosts;
    }

    public void setMapHosts(Map<String, Host> mapHosts) {
            this.mapHosts = mapHosts;
    }

//    public List<Link> getListLinks() {
//            return listLinks;
//    }
//
//    public void setListLinks(List<Link> listLinks) {
//            this.listLinks = listLinks;
//    }

    public List<Cluster> getListClusters() {
            return listClusters;
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
            this.listClusters.add(cluster);
    }

    public void addSwitch(String nombre) {
            this.mapSwitches.put(nombre, new Switch(nombre));
            //this.nSwitches++;
    }

    public void addHost(String nombre) {
            this.mapHosts.put(nombre, new Host(nombre));
            //this.nSwitches++;
    }
    
    public void addHost(Host host){
        this.mapHosts.put(host.getId(), host);
    }
	
	
}
