package architecture;

public class Cluster {
	private String id;
        private String ip;
	private int tcpPort;
        private String status;
        private String lastUpdate;
        private String humanReadableLastUpdate;
	
	public Cluster(String id, int puerto, String estado) {
		this.id = id;
		this.tcpPort = puerto;
		this.status = estado;
	}
        
        public Cluster(String id, 
                String ip, 
                int tcpPort,
                String status,
                String lastUpdate,
                String humanReadableLastUpdate) {
		
            this.id = id;
            this.ip = ip;
            this.tcpPort = tcpPort;
            this.status = status;
            this.lastUpdate = lastUpdate;
            this.humanReadableLastUpdate = humanReadableLastUpdate;
            
	}

	public Cluster(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		return this.status + "\t" + this.id + ":"+ this.tcpPort;
	}
	
}
