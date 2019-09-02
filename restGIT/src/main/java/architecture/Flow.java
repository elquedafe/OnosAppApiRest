package architecture;


import java.util.Vector;

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
        this.setFlowTreatment(null);
        this.flowSelector = null;
    }

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
        this.setFlowTreatment(flowTreatment);
        this.flowSelector = flowSelector;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getPackets() {
        return packets;
    }

    public void setPackets(int packets) {
        this.packets = packets;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public double getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(double lastSeen) {
        this.lastSeen = lastSeen;
    }

    public FlowSelector getFlowSelector() {
        return flowSelector;
    }

    public void setFlowSelector(FlowSelector flowSelector) {
        this.flowSelector = flowSelector;
    }

    
    /*public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdTable() {
        return idTable;
    }

    public void setIdTable(int idTable) {
        this.idTable = idTable;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getnPaquetes() {
        return nPaquetes;
    }

    public void setnPaquetes(int nPaquetes) {
        this.nPaquetes = nPaquetes;
    }

    public int getnBytes() {
        return nBytes;
    }

    public void setnBytes(int nBytes) {
        this.nBytes = nBytes;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getSwitch() {
        return this.deviceId;
    }

    public void setSwitch(String ovs) {
        this.deviceId = deviceId;
    }*/

    public FlowTreatment getFlowTreatment() {
		return flowTreatment;
	}

	public void setFlowTreatment(FlowTreatment flowTreatment) {
		this.flowTreatment = flowTreatment;
	}

	@Override
    public String toString() {
        return this.deviceId+ "=" + this.id + "\t--> " + "\t| " + this.packets+ " paquetes\t|\t" + this.bytes + " bytes" + "\t|\t" + this.priority;
    }

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

    public Object[] toArray(){
        Vector<Object> v = new Vector<Object>();
        v.add(this.deviceId); v.add(this.id); v.add(this.groupId);
        v.add(this.priority); v.add(this.state); v.add(this.packets); v.add(this.bytes);
        Object[] array = v.toArray();
        return array;
    }
}
