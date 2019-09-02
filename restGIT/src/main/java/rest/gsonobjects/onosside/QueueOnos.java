package rest.gsonobjects.onosside;

public class QueueOnos {
	private String queueId;
	private double minRate;
	private double maxRate;
	private long burst;
	/**
	 * @param queueId
	 * @param minRate
	 * @param maxRate
	 * @param burst
	 */
	public QueueOnos(String queueId, double minRate, double maxRate, long burst) {
		super();
		this.queueId = queueId;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
	}
	/**
	 * @return the queueId
	 */
	public String getQueueId() {
		return queueId;
	}
	/**
	 * @param queueId the queueId to set
	 */
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	/**
	 * @return the minRate
	 */
	public double getMinRate() {
		return minRate;
	}
	/**
	 * @param minRate the minRate to set
	 */
	public void setMinRate(double minRate) {
		this.minRate = minRate;
	}
	/**
	 * @return the maxRate
	 */
	public double getMaxRate() {
		return maxRate;
	}
	/**
	 * @param maxRate the maxRate to set
	 */
	public void setMaxRate(double maxRate) {
		this.maxRate = maxRate;
	}
	/**
	 * @return the burst
	 */
	public long getBurst() {
		return burst;
	}
	/**
	 * @param burst the burst to set
	 */
	public void setBurst(long burst) {
		this.burst = burst;
	}
	
	
	
	
}
