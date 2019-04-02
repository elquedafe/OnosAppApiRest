
package architecture;

public class Link {
	private String src;
	private String srcPort;
	private String dst;
	private String dstPort;
        private String type;
        private String state;
	private double cost;
	
	public Link(String src, String dst, String srcPort, String dstPort) {
		this.src = src;
		this.dst = dst;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.cost = 0;
	}
        
        public Link(String src, String srcPort, String dst,  String dstPort, String type, String state, double cost) {
            this.src = src;
            this.srcPort = srcPort;
            this.dst = dst;
            this.dstPort = dstPort;
            this.type = type;
            this.state = state;
            this.cost = cost;
	}
	
	public Link(){
		this.src = "";
		this.dst = "";
		this.srcPort = "";
		this.dstPort = "";
		this.cost = 0;
	}
	
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstPort() {
		return dstPort;
	}
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return this.src + "/" + this.srcPort + " <-> " + this.dst + "/" + this.dstPort + "\t Coste: " + this.cost;
	}
	
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
