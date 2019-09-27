package architecture;

/**
 * Represents a band from a meter
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Band {
	private String type;
	private int rate;
	private int packets;
	private int bytes;
	private int burstSize;

	/**
	 * Band constructor
	 * @param type type of band 
	 * @param rate maximum rate
	 * @param packets packets number
	 * @param bytes bytes number
	 * @param burstSize burst size 
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
	 * Default band constructor
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
	 * @param rate maximum rate
	 * @param burstSize burst size
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
	 * @param rate maximum rate
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
	 * Get the band type
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set band type
	 * @param type band type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get band rate
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * Set band rate
	 * @param rate band rate
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}

	/**
	 * Get packets number
	 * @return packets number
	 */
	public int getPackets() {
		return packets;
	}
	
	/**
	 * Set packets number
	 * @param packets packets number to set
	 */
	public void setPackets(int packets) {
		this.packets = packets;
	}
	
	/**
	 * Set bytes number
	 * @return bytes number
	 */
	public int getBytes() {
		return bytes;
	}
	
	/**
	 * Set bytes number
	 * @param bytes bytes number to set
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}
	
	/**
	 * Get the burst size
	 * @return the burst size
	 */
	public int getBurstSize() {
		return burstSize;
	}
	
	/**
	 * Set the burst size
	 * @param burstSize burst size to set
	 */
	public void setBurstSize(int burstSize) {
		this.burstSize = burstSize;
	}


}
