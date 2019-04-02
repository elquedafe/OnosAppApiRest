package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

public class Switch {
	private String id;
        private String type;
        private boolean available;
        private String role;
        private String mfr;
        private String hw;
        private String sw;
        private String serial;
        private String driver;
        private String chassisId;
        private String lastUpdate;
        private String humanReadableLastUpdate;
        private LinkedTreeMap annotations;
        
	private List<Port> listPorts;
	private Map<String, Flow> mapFlows;
        private List<Link> listLinks;
	
	public Switch(String id) {
            this.id = id;
            this.available = false;
            this.listPorts = new ArrayList<Port>();
            this.listLinks = new ArrayList<Link>();
            this.mapFlows = new HashMap<String, Flow>();
	}
        
        public Switch(String id, 
                String type, 
                boolean available, 
                String role, 
                String mfr, 
                String hw, 
                String sw, 
                String serial, 
                String driver, 
                String chassisId, 
                String lastUpdate, 
                String humanReadableLastUpdate, 
                LinkedTreeMap annotations){
            this.id = id;
            this.type = type;
            this.available = available;
            this.role = role;
            this.mfr = mfr;
            this.available = available;
            this.hw = hw;
            this.sw = sw;
            this.serial = serial;
            this.driver = driver;
            this.chassisId = chassisId;
            this.lastUpdate = lastUpdate;
            this.humanReadableLastUpdate = humanReadableLastUpdate;
            this.annotations = annotations;
            
            this.listPorts = new ArrayList<Port>();
            this.listLinks = new ArrayList<Link>();
            this.mapFlows = new HashMap<String, Flow>();
            
        }

	public List<Port> getListPorts() {
		return listPorts;
	}

	public void setListPorts(List<Port> listPorts) {
		this.listPorts = listPorts;
	}
        
        public List<Link> getListLinks() {
		return listLinks;
	}

	public void setListLinks(List<Link> listLinks) {
		this.listLinks = listLinks;
	}
        
        public void addPort(Port p){
            this.listPorts.add(p);
        }
        
        public Port getPortByNumber(String numero){
            Port puerto = null;
            for(Port p : this.listPorts){
                if(p.getPortNumber().equals(numero)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }
        
        public Port getPortByMac(String mac){
            Port puerto = null;
            for(Port p : this.listPorts){
                if(p.getPortMac().equals(mac)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }
        
        public Port getPortByName(String nombre){
            Port puerto = null;
            for(Port p : this.listPorts){
                if(p.getPortName().equals(nombre)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }

	public Map<String, Flow> getMapFlows() {
		return mapFlows;
	}

	public void setMapFlows(Map<String, Flow> listFlows) {
		this.mapFlows = listFlows;
	}

	public String getId() {
		return id;
	}
        
        public void setId(String id) {
		this.id = id;
	}
        
        public boolean getAvailable() {
		return available;
	}
        
        public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public void addFlow(Flow flow) {
		this.mapFlows.put(flow.getId(), flow);
	}
        
        
        
        
}
