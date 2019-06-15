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
        
        /**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the mfr
	 */
	public String getMfr() {
		return mfr;
	}

	/**
	 * @param mfr the mfr to set
	 */
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	/**
	 * @return the hw
	 */
	public String getHw() {
		return hw;
	}

	/**
	 * @param hw the hw to set
	 */
	public void setHw(String hw) {
		this.hw = hw;
	}

	/**
	 * @return the sw
	 */
	public String getSw() {
		return sw;
	}

	/**
	 * @param sw the sw to set
	 */
	public void setSw(String sw) {
		this.sw = sw;
	}

	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * @param serial the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the chassisId
	 */
	public String getChassisId() {
		return chassisId;
	}

	/**
	 * @param chassisId the chassisId to set
	 */
	public void setChassisId(String chassisId) {
		this.chassisId = chassisId;
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

	/**
	 * @return the annotations
	 */
	public LinkedTreeMap getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(LinkedTreeMap annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return the ports
	 */
	public List<Port> getPorts() {
		return ports;
	}

	/**
	 * @param ports the ports to set
	 */
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	/**
	 * @return the flows
	 */
	public Map<String, Flow> getFlows() {
		return flows;
	}

	/**
	 * @param flows the flows to set
	 */
	public void setFlows(Map<String, Flow> flows) {
		this.flows = flows;
	}

	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
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
