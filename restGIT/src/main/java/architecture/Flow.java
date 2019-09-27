package architecture;

/**
 * Represent a network flow.
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Flow {
	private String id;
	private String tableId;
	private String appId;
	private int groupId;
	private int priority;
	private int timeout;
	private boolean isPermanent;
	private String deviceId;
	private String state;
	private int life;
	private int packets;
	private int bytes;
	private String liveType;
	private double lastSeen;
	private FlowTreatment flowTreatment;
	private FlowSelector flowSelector;

	/**
	 * Flow constructor
	 * @param id flow id
	 */
	public Flow(String id) {
		this.id = id;
		this.tableId = "-1";
		this.appId = "";
		this.groupId = -1;
		this.priority = -1;
		this.timeout = 0;
		this.isPermanent = false;
		this.deviceId = "";
		this.state = "";
		this.life = 0;
		this.packets = 0;
		this.bytes = 0;
		this.liveType = "";
		this.lastSeen = 0;
		this.flowTreatment = null;
		this.flowSelector = null;
	}

	/**
	 * Flow constructor
	 * @param id flow id
	 * @param tableId table id
	 * @param appId app id
	 * @param groupId group id
	 * @param priority priority
	 * @param timeout timeout
	 * @param isPermanent is permanent
	 * @param deviceId device id
	 * @param state state
	 * @param life life time
	 * @param packets packets number
	 * @param bytes bytes number
	 * @param liveType live type
	 * @param lastSeen last seen
	 * @param flowTreatment flow treatment
	 * @param flowSelector flow selector
	 */
	public Flow(String id, String tableId, String appId, int groupId, int priority, int timeout, boolean isPermanent, String deviceId, String state, int life, int packets, int bytes, String liveType, double lastSeen, FlowTreatment flowTreatment, FlowSelector flowSelector) {
		this.id = id;
		this.tableId = tableId;
		this.appId = appId;
		this.groupId = groupId;
		this.priority = priority;
		this.timeout = timeout;
		this.isPermanent = isPermanent;
		this.deviceId = deviceId;
		this.state = state;
		this.life = life;
		this.packets = packets;
		this.bytes = bytes;
		this.liveType = liveType;
		this.lastSeen = lastSeen;
		this.flowTreatment = flowTreatment;
		this.flowSelector = flowSelector;
	}

	/**
	 * Return flow id.
	 * @return flow id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set flow id.
	 * @param id 
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return table id,
	 * @return table id
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * Set table id.
	 * @param tableId 
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	/**
	 * Return app id.
	 * @return app id.
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * Set app id.
	 * @param appId 
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * Return group id.
	 * @return group id
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * Set group id.
	 * @param groupId 
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * Return flow priority.
	 * @return 
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set flow priority.
	 * @param priority 
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Return timeout.
	 * @return timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Set timeout.
	 * @param timeout 
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Return if flow is permanent.
	 * @return is permanent
	 */
	public boolean isIsPermanent() {
		return isPermanent;
	}

	/**
	 * Set is permanent.
	 * @param isPermanent 
	 */
	public void setIsPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

	/**
	 * Return switch id.
	 * @return switch id
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Set switch id.
	 * @param deviceId 
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Return flow state.
	 * @return flow state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Set flow state.
	 * @param state 
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Return flow life.
	 * @return flow life
	 */
	public int getLife() {
		return life;
	}

	/**
	 * Set flow life.
	 * @param life 
	 */
	public void setLife(int life) {
		this.life = life;
	}

	/**
	 * Get flow packets number.
	 * @return packets.
	 */
	public int getPackets() {
		return packets;
	}

	/**
	 * Set packet number.
	 * @param packets 
	 */
	public void setPackets(int packets) {
		this.packets = packets;
	}

	/**
	 * Return bytes number.
	 * @return bytes number
	 */
	public int getBytes() {
		return bytes;
	}

	/**
	 * Set bytes.
	 * @param bytes 
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * Return live type.
	 * @return live type
	 */
	public String getLiveType() {
		return liveType;
	}

	/**
	 * Set live type.
	 * @param liveType 
	 */
	public void setLiveType(String liveType) {
		this.liveType = liveType;
	}

	/**
	 * Return last seen
	 * @return last seen
	 */
	public double getLastSeen() {
		return lastSeen;
	}

	/**
	 * Set last seen
	 * @param lastSeen 
	 */
	public void setLastSeen(double lastSeen) {
		this.lastSeen = lastSeen;
	}

	/**
	 * Return floe selector.
	 * @return 
	 */
	public FlowSelector getFlowSelector() {
		return flowSelector;
	}

	/**
	 * Set flow selector.
	 * @param flowSelector 
	 */
	public void setFlowSelector(FlowSelector flowSelector) {
		this.flowSelector = flowSelector;
	}

	/**
	 * Return flow treatment.
	 * @return flow treatment
	 */
	public FlowTreatment getFlowTreatment(){
		return this.flowTreatment;
	}

	/**
	 * Override toString
	 */
	@Override
	public String toString() {
		return this.deviceId+ "=" + this.id + "\t--> " + "\t| " + this.packets+ " paquetes\t|\t" + this.bytes + " bytes" + "\t|\t" + this.priority;
	}

	/**
	 * Override equals
	 */
	@Override
	public boolean equals(Object obj) {
		Flow o = null;
		if(obj == null)
			return false;
		else
			o = (Flow) obj;

		if(this.id.equals(o.getId()))
			return true;

		return false;
	}
}
