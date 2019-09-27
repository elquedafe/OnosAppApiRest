package tools;

import java.util.List;

import rest.database.objects.QueueDBResponse;

/**
 * Represents an id generator
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class Utils {
	static int MAX_QUEUE_ID = 2147483647;
	static int MAX_QOS_ID = 2147483647;
	static int MAX_CONNECTION_ID = 2147483647;
	
	/**
	 * Get first queue id avalable
	 * @return queue id
	 */
	public static int getQueueIdAvailable() {
		int foundId = -1;
		List<Integer> queuesIds = DatabaseTools.getAllQueuesIds();
		for(int i = 0; i < MAX_QUEUE_ID; i++) {
			if(!queuesIds.contains(i)) {
				foundId = i;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get first qos id available
	 * @return qos id
	 */
	public static int getQosIdAvailable() {
		int foundId = -1;
		List<Integer> queuesIds = DatabaseTools.getAllQosIds();
		for(int i = 0; i < MAX_QOS_ID; i++) {
			if(!queuesIds.contains(i)) {
				foundId = i;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get first connection id available
	 * @return connection id
	 */
	public static int getConnectionIdAvailable() {
		int foundId = -1;
		List<Integer> connectionIds = DatabaseTools.getAllConnectionIds();
		if(connectionIds.size() > 0) {
			for(int i = 0; i < MAX_CONNECTION_ID; i++) {
				if(!connectionIds.contains(i)) {
					foundId = i;
					return i;
				}
			}
		}
		else
			return 0;
		return -1;
	}

}
