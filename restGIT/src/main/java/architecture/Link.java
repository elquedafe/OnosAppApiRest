
package architecture;

/**
 * Represents a network link
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Link {
	private String src;
	private String srcPort;
	private String dst;
	private String dstPort;
	private String type;
	private String state;
	private double cost;

	/**
	 * Link constructor
	 * @param src source address
	 * @param srcPort source port
	 * @param dst destination address 
	 * @param dstPort destination port
	 * @param type link type
	 * @param state link state
	 * @param cost link cost
	 */
	public Link(String src, String srcPort, String dst,  String dstPort, String type, String state, double cost) {
		this.src = src;
		this.srcPort = srcPort;
		this.dst = dst;
		this.dstPort = dstPort;
		this.type = type;
		this.state = state;
		this.cost = cost;
	}

	/**
	 * Default link constructor
	 */
	public Link(){
		this.src = "";
		this.dst = "";
		this.srcPort = "";
		this.dstPort = "";
		this.cost = 0;
	}

	/**
	 * Get source address
	 * @return source address
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * Set source address
	 * @param src source address
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * Get source port
	 * @return source port
	 */
	public String getSrcPort() {
		return srcPort;
	}

	/**
	 * Set source port
	 * @param srcPort source port
	 */
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	/**
	 * Get destination address
	 * @return destination address
	 */
	public String getDst() {
		return dst;
	}

	/**
	 * Set destination address
	 * @param dst destination address
	 */
	public void setDst(String dst) {
		this.dst = dst;
	}

	/**
	 * Get destination port
	 * @return destination port
	 */
	public String getDstPort() {
		return dstPort;
	}

	/**
	 * Set destination port
	 * @param dstPort the dstPort to set
	 */
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}

	/**
	 * Get link cost
	 * @return link cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Set link cost
	 * @param cost link cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Override toString
	 */
	@Override
	public String toString() {
		return this.src + "/" + this.srcPort + " <-> " + this.dst + "/" + this.dstPort + "\t Coste: " + this.cost;
	}

	/**
	 * Override equals
	 */
	@Override
	public boolean equals(Object obj) {
		Link o = null;
		if(obj == null)
			return false;
		else
			o = (Link) obj;

		if(this.dst.equals(o.getDst()) && this.src.equals(o.getSrc()) && this.dstPort.equals(o.getDstPort()) && this.srcPort.equals(o.getSrcPort()))
			return true;

		return false;


	}

}
