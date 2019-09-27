package architecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Represents a network OF switch
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
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

	/**
	 * Switch constructor
	 * @param id switch id
	 */
	public Switch(String id) {
		this.id = id;
		this.available = false;
		this.ports = new ArrayList<Port>();
		this.links = new ArrayList<Link>();
		this.flows = new HashMap<String, Flow>();
	}

	/**
	 * Switch constructor
	 * @param id switch id
	 * @param type switch type
	 * @param available if switch is available
	 * @param role switch role
	 * @param mfr switch manufacturer
	 * @param hw switch hardware
	 * @param sw switch software 
	 * @param serial switch serial
	 * @param driver switch driver
	 * @param chassisId switch chassis
	 * @param lastUpdate last update
	 * @param humanReadableLastUpdate last update
	 * @param annotations annotations
	 */
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

	/**
	 * Get switch type
	 * @return switch type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set switch type
	 * @param type switch type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get switch role
	 * @return switch role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Set switch role
	 * @param role switch role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Get switch manufacturer
	 * @return switch manufacturer
	 */
	public String getMfr() {
		return mfr;
	}

	/**
	 * Set switch manufacturer
	 * @param mfr switch manufacturer
	 */
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	/**
	 * Get switch hardware
	 * @return switch hardware
	 */
	public String getHw() {
		return hw;
	}

	/**
	 * Set switch hardware
	 * @param hw switch hardware
	 */
	public void setHw(String hw) {
		this.hw = hw;
	}

	/**
	 * Get switch software
	 * @return switch software
	 */
	public String getSw() {
		return sw;
	}

	/**
	 * Set switch software
	 * @param sw switch software
	 */
	public void setSw(String sw) {
		this.sw = sw;
	}

	/**
	 * Get switch serial
	 * @return switch serial
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * Set serial
	 * @param serial serial
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * Get switch driver
	 * @return switch driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Set switch driver
	 * @param driver switch driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * Get switch chassis
	 * @return switch chassis
	 */
	public String getChassisId() {
		return chassisId;
	}

	/**
	 * Get switch chassis
	 * @param chassisId switch chassis
	 */
	public void setChassisId(String chassisId) {
		this.chassisId = chassisId;
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
	 * Get annotations
	 * @return annotations
	 */
	public LinkedTreeMap getAnnotations() {
		return annotations;
	}

	/**
	 * Set annotations
	 * @param annotations annotations
	 */
	public void setAnnotations(LinkedTreeMap annotations) {
		this.annotations = annotations;
	}

	/**
	 * Get switch  flows
	 * @return switch  flows
	 */
	public Map<String, Flow> getFlows() {
		return flows;
	}

	/**
	 * Set switch  flows
	 * @param flows switch  flows
	 */
	public void setFlows(Map<String, Flow> flows) {
		this.flows = flows;
	}

	/**
	 * Get switch ports
	 * @return switch ports
	 */
	public List<Port> getListPorts() {
		return ports;
	}

	/**
	 * Set switch ports
	 * @param listPorts switch ports
	 */
	public void setListPorts(List<Port> listPorts) {
		this.ports = listPorts;
	}

	/**
	 * Get switch links
	 * @return switch links
	 */
	public List<Link> getListLinks() {
		return links;
	}

	/**
	 * Set switch links
	 * @param listLinks switch links
	 */
	public void setListLinks(List<Link> listLinks) {
		this.links = listLinks;
	}

	/**
	 * Add a port to the switch
	 * @param p port
	 */
	public void addPort(Port p){
		this.ports.add(p);
	}

	/**
	 * Get port given its port number
	 * @param number port number 
	 * @return port
	 */
	public Port getPortByNumber(String number){
		Port puerto = null;
		for(Port p : this.ports){
			if(p.getPortNumber().equals(number)){
				puerto = p;
				break;
			}
		}
		return puerto;
	}

	/**
	 * Get port given its mac address
	 * @param mac mac address
	 * @return port
	 */
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

	/**
	 * Get port given its port name
	 * @param name port name
	 * @return port
	 */
	public Port getPortByName(String name){
		Port puerto = null;
		for(Port p : this.ports){
			if(p.getPortName().equals(name)){
				puerto = p;
				break;
			}
		}
		return puerto;
	}

	/**
	 * Get switch flows
	 * @return flows
	 */
	public Map<String, Flow> getMapFlows() {
		return flows;
	}

	/**
	 * Set switch flows
	 * @param mapFlows flows
	 */
	public void setMapFlows(Map<String, Flow> mapFlows) {
		this.flows = mapFlows;
	}

	/**
	 * Get switch id
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set switch id
	 * @param id switch id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set if switch is available
	 * @return if switch is available
	 */
	public boolean getAvailable() {
		return available;
	}

	/**
	 * Get if switch is available
	 * @param available if switch is available
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * Add flow to the switch
	 * @param flow flow to add
	 */
	public void addFlow(Flow flow) {
		this.flows.put(flow.getId(), flow);
	}




}
