package architecture;

import java.util.List;

public class Meter {
	String id;
	int life;
	int packets;
	int bytes;
	int referenceCount;
	String unit;
	boolean burst;
	String deviceId;
	String appId;
	String state;
	List<Band> bands;
	/**
	 * @param id
	 * @param life
	 * @param packets
	 * @param bytes
	 * @param referenceCount
	 * @param unit
	 * @param burst
	 * @param deviceId
	 * @param appId
	 * @param state
	 * @param bands
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the life
	 */
	public int getLife() {
		return life;
	}
	/**
	 * @param life the life to set
	 */
	public void setLife(int life) {
		this.life = life;
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
	 * @return the referenceCount
	 */
	public int getReferenceCount() {
		return referenceCount;
	}
	/**
	 * @param referenceCount the referenceCount to set
	 */
	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
	}
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * @return the burst
	 */
	public boolean isBurst() {
		return burst;
	}
	/**
	 * @param burst the burst to set
	 */
	public void setBurst(boolean burst) {
		this.burst = burst;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the bands
	 */
	public List<Band> getBands() {
		return bands;
	}
	/**
	 * @param bands the bands to set
	 */
	public void setBands(List<Band> bands) {
		this.bands = bands;
	}
	
	
	
	
	

}
