package architecture;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Represents a switch port
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Port {
	private String ovs;
	private boolean isEnabled;
	private String type;
	private double speed;
	private String portNumber;
	private LinkedTreeMap annotations;
	private String portMac;
	private String portName;

	/**
	 * Default Port constructor
	 */
	public Port() { 
		this.ovs = "";
		this.portName = "";
		this.portNumber = "";
		this.setEnabled(false);
		this.setType("");
		this.speed = 0;
		this.portMac = "";
	}

	/**
	 * Port constructor
	 * @param ovs switch id
	 * @param port port number
	 * @param isEnabled port enabled
	 * @param type port type
	 * @param portSpeed port speed
	 * @param portMac port mac
	 * @param portName port name
	 * @param annotations annotations
	 */
	public Port(String ovs, 
			String port, 
			boolean isEnabled, 
			String type, 
			double portSpeed, 
			String portMac, 
			String portName, 
			LinkedTreeMap annotations) {

		this.ovs = ovs;
		this.portNumber = port;
		this.setEnabled(isEnabled);
		this.setType(type);
		this.speed = portSpeed;
		this.portMac = portMac;
		this.portName = portName;
		this.annotations = annotations;
	}

	/**
	 * Get switch id
	 * @return switch id
	 */
	public String getOvs() {
		return ovs;
	}

	/**
	 * Set switch id
	 * @param ovs switch id
	 */
	public void setOvs(String ovs) {
		this.ovs = ovs;
	}

	/**
	 * Get if port is enabled
	 * @return if port is enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Set if port is enabled
	 * @param isEnabled if port is enabled
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Get port type
	 * @return port type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set port type
	 * @param type port type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get port speed
	 * @return port speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set port speed
	 * @param speed port speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Get port number
	 * @return port number
	 */
	public String getPortNumber() {
		return portNumber;
	}

	/**
	 * Set port number
	 * @param portNumber port number
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
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
	 * Get port mac
	 * @return port mac
	 */
	public String getPortMac() {
		return portMac;
	}

	/**
	 * Set port mac
	 * @param portMac port mac
	 */
	public void setPortMac(String portMac) {
		this.portMac = portMac;
	}

	/**
	 * Get port name
	 * @return port name
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * Set port name
	 * @param portName port name
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * Override toString
	 */
	@Override
	public String toString(){
		return this.portNumber+"/"+this.portName+"("+this.speed+")";
	}

}
