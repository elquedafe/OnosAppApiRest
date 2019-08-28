package foo.upm.es;

public class QueueObject{
	private String queueId;
	private double minRate;
	private double maxRate;
	private long burst;
	private String port;

	public QueueObject(){
		queueId = "" ;
		minRate = -1;
		maxRate = -1;
		burst = -1;
		port = "";
	}

	/*public QueueObject(String queueId, double minRate, double maxRate, long burst, String port){
		this.queueId = queueId;
		this.minRate = minRate;
		this.maxRate = maxRate;
		this.burst = burst;
		this.port = port;
	}*/

	public QueueObject(String queueId, double minRate, double maxRate, long burst){
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

	public String getPort(){
		return port;
	}

	public void setPort(String port){
		this.port = port;
	}
}