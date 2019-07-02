package rest.gsonobjects.onosside;

import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map;
import java.util.LinkedHashMap;

public class FlowOnosRequest {
	private int priority;
	private int timeout;
	private boolean isPermanent;
	private String deviceId;
	private int tableId;
	private int groupId;
	private String appId;
	private Map<String, LinkedList<LinkedHashMap<String,Object>>> treatment;
	private Map<String, LinkedList<LinkedHashMap<String,Object>>> selector;
	
	public FlowOnosRequest() {
		this.priority = 0;
		this.timeout = 0;
		this.isPermanent = true;
		this.deviceId = "";
		this.tableId = 0;
		this.groupId = 0;
		this.appId = "org.onosproject.fwd";
		this.treatment = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
		this.selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
	}
	
	public FlowOnosRequest(int priority, int timeout, boolean isPermanent, String deviceId) {
		super();
		this.priority = priority;
		this.timeout = timeout;
		this.isPermanent = isPermanent;
		this.deviceId = deviceId;
		this.tableId = 0;
		this.groupId = 0;
		this.appId = "org.onosproject.fwd";
		this.treatment = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
		this.selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();
	}
	
	public FlowOnosRequest(int priority, int timeout, boolean isPermanent, String deviceId, int tableId, int groupId,
			String appId, Map<String, LinkedList<LinkedHashMap<String, Object>>> treatment,
			Map<String, LinkedList<LinkedHashMap<String, Object>>> selector) {
		super();
		this.priority = priority;
		this.timeout = timeout;
		this.isPermanent = isPermanent;
		this.deviceId = deviceId;
		this.tableId = tableId;
		this.groupId = groupId;
		this.appId = appId;
		this.treatment = treatment;
		this.selector = selector;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the isPermanent
	 */
	public boolean isPermanent() {
		return isPermanent;
	}

	/**
	 * @param isPermanent the isPermanent to set
	 */
	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
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
	 * @return the tableId
	 */
	public int getTableId() {
		return tableId;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
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
	 * @return the treatment
	 */
	public Map<String, LinkedList<LinkedHashMap<String, Object>>> getTreatment() {
		return treatment;
	}

	/**
	 * @param treatment the treatment to set
	 */
	public void setTreatment(Map<String, LinkedList<LinkedHashMap<String, Object>>> treatment) {
		this.treatment = treatment;
	}

	/**
	 * @return the selector
	 */
	public Map<String, LinkedList<LinkedHashMap<String, Object>>> getSelector() {
		return selector;
	}

	/**
	 * @param selector the selector to set
	 */
	public void setSelector(Map<String, LinkedList<LinkedHashMap<String, Object>>> selector) {
		this.selector = selector;
	}
	
	
}