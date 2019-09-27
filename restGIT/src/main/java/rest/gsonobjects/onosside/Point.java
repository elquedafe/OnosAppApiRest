package rest.gsonobjects.onosside;

/**
 * Represents a Point device:port
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Point {
	private String port;
	private String device;
	
	/**
	 * @param port
	 * @param device
	 */
	public Point(String port, String device) {
		super();
		this.port = port;
		this.device = device;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice(String device) {
		this.device = device;
	}
	
	
}
