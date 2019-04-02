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
        
	private List<Port> ports;
	private Map<String, Flow> flows;
    private List<Link> links;
	
	public Switch(String id) {
            this.id = id;
            this.available = false;
            this.ports = new ArrayList<Port>();
            this.links = new ArrayList<Link>();
            this.flows = new HashMap<String, Flow>();
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
            
            this.ports = new ArrayList<Port>();
            this.links = new ArrayList<Link>();
            this.flows = new HashMap<String, Flow>();
            
        }

	public List<Port> getListPorts() {
		return ports;
	}

	public void setListPorts(List<Port> listPorts) {
		this.ports = listPorts;
	}
        
        public List<Link> getListLinks() {
		return links;
	}

	public void setListLinks(List<Link> listLinks) {
		this.links = listLinks;
	}
        
        public void addPort(Port p){
            this.ports.add(p);
        }
        
        public Port getPortByNumber(String numero){
            Port puerto = null;
            for(Port p : this.ports){
                if(p.getPortNumber().equals(numero)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }
        
        public Port getPortByMac(String mac){
            Port puerto = null;
            for(Port p : this.ports){
                if(p.getPortMac().equals(mac)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }
        
        public Port getPortByName(String nombre){
            Port puerto = null;
            for(Port p : this.ports){
                if(p.getPortName().equals(nombre)){
                    puerto = p;
                    break;
                }
            }
            return puerto;
        }

	public Map<String, Flow> getMapFlows() {
		return flows;
	}

	public void setMapFlows(Map<String, Flow> listFlows) {
		this.flows = listFlows;
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
		this.flows.put(flow.getId(), flow);
	}
        
        
        
        
}
