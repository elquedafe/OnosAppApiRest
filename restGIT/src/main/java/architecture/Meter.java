package architecture;

import java.util.List;

/**
 * Represents a meter in a network OF switch
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Meter {

	private String id;
	private int life;
	private int packets;
	private int bytes;
	private int referenceCount;
	private String unit;
	private boolean burst;
	private String deviceId;
	private String appId;
	private String state;
	private List<Band> bands;

	/**
	 * Meter constructor
	 * @param id meter id
	 * @param life meter life
	 * @param packets meter packets number
	 * @param bytes meter bytes number
	 * @param referenceCount meter count
	 * @param unit meter units
	 * @param burst meter burst
	 * @param deviceId switch id
	 * @param appId app id
	 * @param state meter state
	 * @param bands bands list
	 */
	public Meter(String id, int life, int packets, int bytes, int referenceCount, String unit, boolean burst,
			String deviceId, String appId, String state, List<Band> bands) {
		super();
		this.id = id;
		this.life = life;
		this.packets = packets;
		this.bytes = bytes;
		this.referenceCount = referenceCount;
		this.unit = unit;
		this.burst = burst;
		this.deviceId = deviceId;
		this.appId = appId;
		this.state = state;
		this.bands = bands;
	}

	/**
	 * Get meter id
	 * @return meter id
	 */
	public String getId() {
		return id;
	}
	/**
	 * Set meter id
	 * @param id meter id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Get meter life
	 * @return meter life
	 */
	public int getLife() {
		return life;
	}
	/**
	 * Set meter life
	 * @param life meter life
	 */
	public void setLife(int life) {
		this.life = life;
	}
	/**
	 * Get meter packets number
	 * @return packets number
	 */
	public int getPackets() {
		return packets;
	}
	/**
	 * Set meter packets number
	 * @param packets packets number
	 */
	public void setPackets(int packets) {
		this.packets = packets;
	}
	/**
	 * Get meter bytes number
	 * @return bytes number
	 */
	public int getBytes() {
		return bytes;
	}
	/**
	 * Set meter bytes
	 * @param bytes meter bytes
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}
	/**
	 * Get meter reference count
	 * @return reference count
	 */
	public int getReferenceCount() {
		return referenceCount;
	}
	/**
	 * Set reference count
	 * @param referenceCount reference count
	 */
	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
	}
	/**
	 * Get meter units
	 * @return meter units
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * Set meter units
	 * @param unit meter units
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * Get burst
	 * @return burst
	 */
	public boolean isBurst() {
		return burst;
	}
	/**
	 * @param burst burst
	 */
	public void setBurst(boolean burst) {
		this.burst = burst;
	}
	/**
	 * Get switch id
	 * @return switch id
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * Set switch id
	 * @param deviceId switch id
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * Get meter app id
	 * @return app id
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * Set app id
	 * @param appId app id
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * Get meter state
	 * @return meter state
	 */
	public String getState() {
		return state;
	}
	/**
	 * Set meter state
	 * @param state meter state
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * Get meter bands
	 * @return meter bands
	 */
	public List<Band> getBands() {
		return bands;
	}
	/**
	 * Set meter bands
	 * @param bands meter bands
	 */
	public void setBands(List<Band> bands) {
		this.bands = bands;
	}






}
