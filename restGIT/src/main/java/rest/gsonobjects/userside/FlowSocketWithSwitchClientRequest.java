
package rest.gsonobjects.userside;

public class FlowSocketWithSwitchClientRequest extends FlowSocketClientRequest{
	private String ingress;
	private String ingressPort;
	private String egress;
	private String egressPort;

	/**
	 * @param ipVersion
	 * @param srcHost
	 * @param srcPort
	 * @param dstHost
	 * @param dstPort
	 * @param portType
	 * @param ingress
	 * @param ingressPort
	 * @param egress
	 * @param egressPort
	 */
	public FlowSocketWithSwitchClientRequest(int ipVersion, String srcHost, String srcPort, String dstHost,
			String dstPort, String portType, String ingress, String ingressPort, String egress, String egressPort) {
		super(ipVersion, srcHost, srcPort, dstHost, dstPort, portType);
		this.ingress = ingress;
		this.ingressPort = ingressPort;
		this.egress = egress;
		this.egressPort = egressPort;
	}
	
	

	
	/**
	 * @return the ingress
	 */
	public String getIngress() {
		return ingress;
	}




	/**
	 * @param ingress the ingress to set
	 */
	public void setIngress(String ingress) {
		this.ingress = ingress;
	}




	/**
	 * @return the ingressPort
	 */
	public String getIngressPort() {
		return ingressPort;
	}




	/**
	 * @param ingressPort the ingressPort to set
	 */
	public void setIngressPort(String ingressPort) {
		this.ingressPort = ingressPort;
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




	/**
	 * @return the egressPort
	 */
	public String getEgressPort() {
		return egressPort;
	}




	/**
	 * @param egressPort the egressPort to set
	 */
	public void setEgressPort(String egressPort) {
		this.egressPort = egressPort;
	}




	@Override
	public String toString() {
		String str = "ipVersion:"+super.getIpVersion()+"\n"+
				"srcHost:"+super.getSrcHost()+"\n" +
				"srcPort:"+super.getSrcPort()+"\n" +
				"dstHost:"+super.getDstHost()+"\n" +
				"dstPort:"+super.getDstPort()+"\n" +
				"portType:"+super.getPortType()+"\n" +
				"ingress:"+this.ingress+"\n" +
				"ingressPort:"+this.ingressPort+"\n" +
				"egress:"+this.egress+"\n" +
				"egressPort:"+this.egressPort;
		return str;
	}

}
