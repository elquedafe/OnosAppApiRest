package rest.gsonobjects.userside;

/**
 * Represents a meter client response
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class MeterClientRequestPortWithSwitch extends MeterClientRequestPort{

	private String ingress;
	private String egress;
	
	/**
	 * @param ipVersion
	 * @param srcHost
	 * @param srcPort
	 * @param dstHost
	 * @param dstPort
	 * @param portType
	 * @param rate
	 * @param burst
	 * @param ingress
	 * @param egress
	 */
	public MeterClientRequestPortWithSwitch(String ipVersion, String srcHost, String srcPort, String dstHost,
			String dstPort, String portType, int rate, int burst, String ingress, String egress) {
		super(ipVersion, srcHost, srcPort, dstHost, dstPort, portType, rate, burst);
		this.ingress = ingress;
		this.egress = egress;
	}

	/**
	 * @param ipVersion
	 * @param srcHost
	 * @param srcPort
	 * @param dstHost
	 * @param dstPort
	 * @param portType
	 * @param rate
	 * @param burst
	 */
	public MeterClientRequestPortWithSwitch(String ipVersion, String srcHost, String srcPort, String dstHost,
			String dstPort, String portType, int rate, int burst) {
		super(ipVersion, srcHost, srcPort, dstHost, dstPort, portType, rate, burst);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ingress the ingress to set
	 */
	public void setIngress(String ingress) {
		this.ingress = ingress;
	}

	/**
	 * @return the ingress
	 */
	public String getIngress() {
		return ingress;
	}

	/**
	 * @return the egress
	 */
	public String getEgress() {
		return egress;
	}

	/**
	 * @param egress the egress to set
	 */
	public void setEgress(String egress) {
		this.egress = egress;
	}

	
}
