package architecture;

/**
 * Represents a band from meter
 * @author alvaroluismartinez
 *
 */
public class Band {
	private String type;
	private int rate;
	private int packets;
	private int bytes;
	private int burstSize;
	/**
	 * @param type
	 * @param rate
	 * @param packets
	 * @param bytes
	 * @param burstSize
	 */
	public Band(String type, int rate, int packets, int bytes, int burstSize) {
		super();
		this.type = type;
		this.rate = rate;
		this.packets = packets;
		this.bytes = bytes;
		this.burstSize = burstSize;
	}
	/**
	 * 
	 */
	public Band() {
		super();
		this.type = "DROP";
		this.rate = 0;
		this.packets = 0;
		this.bytes = 0;
		this.burstSize = 0;
	}
	/**
	 * @param rate
	 * @param burstSize
	 */
	public Band(int rate, int burstSize) {
		super();
		this.type = "DROP";
		this.rate = rate;
		this.packets = 0;
		this.bytes = 0;
		this.burstSize = burstSize;
	}
	/**
	 * @param rate
	 */
	public Band(int rate) {
		super();
		this.type = "DROP";
		this.rate = rate;
		this.packets = 0;
		this.bytes = 0;
		this.burstSize = 0;
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
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}
	/**
	 * @return the packets
	 */
	public int getPackets() {
		return packets;
	}
	/**
	 * @param packets the packets to set
	 */
	public void setPackets(int packets) {
		this.packets = packets;
	}
	/**
	 * @return the bytes
	 */
	public int getBytes() {
		return bytes;
	}
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}
	/**
	 * @return the burstSize
	 */
	public int getBurstSize() {
		return burstSize;
	}
	/**
	 * @param burstSize the burstSize to set
	 */
	public void setBurstSize(int burstSize) {
		this.burstSize = burstSize;
	}
	
	
}
