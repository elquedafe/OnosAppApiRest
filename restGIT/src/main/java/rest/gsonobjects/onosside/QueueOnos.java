package rest.gsonobjects.onosside;

public class QueueOnos {
	private String queueId;
	private double minRate;
	private double maxRate;
	private long burst;
	
	public QueueOnos(String queueId, double minRate, double maxRate, long burst){
		this.queueId = queueId;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
	}

	public String getQueueId(){
		return queueId;
	}

	public void setQueueId(String queueId){
		this.queueId = queueId;
		
	}

	public double getMinRate(){
		return minRate;
	}

	public void setMinRate(int minRate){
		this.minRate = minRate;
	}

	public double getMaxRate(){
		return maxRate;
	}

	public void setMaxRate(int maxRate){
		this.maxRate = maxRate;
	}

	public long getBurst(){
		return burst;
	}

	public void setBurst(int burst){
		this.burst = burst;
	}
}
