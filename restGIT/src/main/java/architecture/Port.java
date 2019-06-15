package architecture;

import com.google.gson.internal.LinkedTreeMap;


public class Port {
	private String ovs;
	private boolean isEnabled;
	private String type;
	private double speed;
	private String portNumber;
	private LinkedTreeMap annotations;
	private String portMac;
	private String portName;

	public Port() { 
		this.ovs = "";
		this.portName = "";
		this.portNumber = "";
		this.setEnabled(false);
		this.setType("");
		this.speed = 0;
		this.portMac = "";
	}

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
	 * @return the ovs
	 */
	public String getOvs() {
		return ovs;
	}

	/**
	 * @param ovs the ovs to set
	 */
	public void setOvs(String ovs) {
		this.ovs = ovs;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
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
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * @return the portNumber
	 */
	public String getPortNumber() {
		return portNumber;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
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
	 * @return the portMac
	 */
	public String getPortMac() {
		return portMac;
	}

	/**
	 * @param portMac the portMac to set
	 */
	public void setPortMac(String portMac) {
		this.portMac = portMac;
	}

	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	@Override
	public String toString(){
		return this.portNumber+"/"+this.portName+"("+this.speed+")";
	}

}
