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
                this.isEnabled = false;
                this.type = "";
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
            this.isEnabled = isEnabled;
            this.type = type;
            this.speed = portSpeed;
            this.portMac = portMac;
            this.portName = portName;
            this.annotations = annotations;
	}
        
	public String getOvs() {
		return ovs;
	}

	public void setOvs(String ovs) {
		this.ovs = ovs;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public String getPortMac() {
		return portMac;
	}

	public void setPortMac(String mac) {
		this.portMac = portMac;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String nombrePuerto) {
		this.portName = portName;
	}
        
        @Override
        public String toString(){
            return this.portNumber+"/"+this.portName+"("+this.speed+")";
        }

}
