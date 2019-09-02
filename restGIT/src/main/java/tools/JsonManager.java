package tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Cluster;
import architecture.Environment;
import architecture.Flow;
import architecture.FlowCriteria;
import architecture.FlowInstruction;
import architecture.FlowSelector;
import architecture.FlowTreatment;
import architecture.Host;
import architecture.Link;
import architecture.Port;
import architecture.Queue;
import architecture.Switch;
import architecture.Vpls;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.onosside.QueueOnos;

public class JsonManager {



	public static void parseJsonDevicesGson(String json) {
		Gson gson = new Gson();
		EntornoTools.entorno.getMapSwitches().clear();

		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList node = (ArrayList)jsonObject.get("devices");
		for(Object o : node){
			LinkedTreeMap map = (LinkedTreeMap)o;
			String id = (String)map.get("id");
			String type = (String)map.get("type");
			boolean available = (boolean)map.get("available");
			String role = (String)map.get("role");
			String mfr = (String)map.get("mfr");
			String hw = (String)map.get("hw");
			String sw = (String)map.get("sw");
			String serial = (String)map.get("serial");
			String driver = (String)map.get("driver");
			String chassisId = (String)map.get("chassisId");
			String lastUpdate = (String)map.get("lastUpdate");
			String humanReadableLastUpdate = (String)map.get("humanReadableLastUpdate");
			LinkedTreeMap annotations = (LinkedTreeMap)map.get("annotations");

			Switch s = new Switch(id, type, available, role, mfr, hw, sw, serial, driver, chassisId, lastUpdate, humanReadableLastUpdate, annotations);
			EntornoTools.entorno.getMapSwitches().put(id, s);
		}
	}

	public static void parseJsonPortsGson(String json) {
		String id = "";
		Gson gson = new Gson();

		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList ports = (ArrayList)jsonObject.get("ports");
		id = (String)jsonObject.get("id");
		for(Object o : ports){
			LinkedTreeMap mapPort = (LinkedTreeMap)o;
			String ovs = (String) mapPort.get("element") ;
			String port = (String) mapPort.get("port");
			boolean isEnabled = (boolean) mapPort.get("isEnabled");
			String type = (String) mapPort.get("type");
			double portSpeed = (double)mapPort.get("portSpeed");
			LinkedTreeMap annotations = (LinkedTreeMap)mapPort.get("annotations");
			String portMac = (String)annotations.get("portMac");
			String portName = (String)annotations.get("portName");

			Port p = new Port(ovs, port, isEnabled, type, portSpeed, portMac, portName, annotations);
			EntornoTools.entorno.getMapSwitches().get(id).addPort(p);
		}
	}

	public static void parseJsonClustersGson(String json) {
		Gson gson = new Gson();

		EntornoTools.entorno.getListClusters().clear();

		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList clusters = (ArrayList)jsonObject.get("nodes");
		for(Object o : clusters){
			LinkedTreeMap mapClusters = (LinkedTreeMap)o;
			String id = (String)mapClusters.get("id");
			String ip = (String)mapClusters.get("id");
			int tcpPort = (int)(double)mapClusters.get("tcpPort");
			String status = (String)mapClusters.get("status");
			String lastUpdate = (String)mapClusters.get("lastUpdate");
			String humanReadableLastUpdate = (String)mapClusters.get("humanReadableLastUpdate");
			Cluster c = new Cluster(id, ip, tcpPort, status, lastUpdate, humanReadableLastUpdate);
			EntornoTools.entorno.addCluster(c);
		}
	}

	public static void parseJsonLinksGson(String json) {
		Gson gson = new Gson();

		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList links = (ArrayList)jsonObject.get("links");
		for(Object o : links){
			LinkedTreeMap mapLinks = (LinkedTreeMap)o;

			LinkedTreeMap src = (LinkedTreeMap)mapLinks.get("src");
			String srcPort = (String)src.get("port");
			String srcDevice = (String)src.get("device");
			LinkedTreeMap dst = (LinkedTreeMap)mapLinks.get("dst");
			String dstPort = (String)dst.get("port");
			String dstDevice = (String)dst.get("device");
			String type = (String)mapLinks.get("type");
			String state = (String)mapLinks.get("state");

			//GET COST
			URL urlPaths = null;
			double cost = 0;
			try {
				urlPaths = new URL(EntornoTools.endpoint + "/paths/"+srcDevice+"/"+dstDevice);
				OnosResponse jsonPath = HttpTools.doJSONGet(urlPaths);
				cost = parseJsonPathGson(gson, jsonPath.getMessage());
			} catch (MalformedURLException ex) {
				Logger.getLogger(JsonManager.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(JsonManager.class.getName()).log(Level.SEVERE, null, ex);
			}

			Link l = new Link(srcDevice, srcPort, dstDevice, dstPort, type, state, cost);
			EntornoTools.entorno.getMapSwitches().get(srcDevice).getListLinks().add(l);
		}
	}

	private static double parseJsonPathGson(Gson gson, String jsonPath) {
		double cost = 0;
		LinkedTreeMap jsonPathsObject = gson.fromJson(jsonPath, LinkedTreeMap.class);
		ArrayList paths = (ArrayList)jsonPathsObject.get("paths");
		for(Object obj : paths){
			LinkedTreeMap mapPaths = (LinkedTreeMap)obj;
			cost = (double)mapPaths.get("cost");
		}
		return cost;
	}

	public static void parseJsonFlowGson(String json) {
		Gson gson = new Gson();
		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList flows = (ArrayList)jsonObject.get("flows");
		for(Object o : flows){
			LinkedTreeMap mapFlows = (LinkedTreeMap)o;

			String id = (String)mapFlows.get("id");
			String tableId = (String)mapFlows.get("tableId");
			String appId = (String)mapFlows.get("appId");
			int groupId = (int)(double)mapFlows.get("groupId");
			int priority = (int)(double)mapFlows.get("priority");
			int timeout = (int)(double)mapFlows.get("timeout");
			boolean isPermanent = (boolean)mapFlows.get("isPermanent");
			String deviceId = (String)mapFlows.get("deviceId");
			String state = (String)mapFlows.get("state");
			int life = (int)(double)mapFlows.get("life");
			int packets = (int)(double)mapFlows.get("packets");
			int bytes = (int)(double)mapFlows.get("bytes");
			String liveType = (String)mapFlows.get("liveType");
			double lastSeen = (double)mapFlows.get("lastSeen");
			String type = "";
			FlowTreatment flowTreatment = new FlowTreatment();
			FlowSelector flowSelector = new FlowSelector();

			LinkedTreeMap treatment = (LinkedTreeMap)mapFlows.get("treatment");
			ArrayList instructions = (ArrayList)treatment.get("instructions");
			Map<String,Object> hashMapInstructions = new HashMap<String,Object>();
			for(Object ob : instructions){
				LinkedTreeMap mapInstructions = (LinkedTreeMap)ob;
				Set keys = mapInstructions.keySet();
				for(Object key : keys){
					String k = (String)key;
					if(!k.equals("type")){
						hashMapInstructions.put(k,(Object)mapInstructions.get(k));
					}
					else
						type = (String)mapInstructions.get(k);
				}
				FlowInstruction i = new FlowInstruction(type, hashMapInstructions);
				flowTreatment.getListInstructions().add(i);
			}
			LinkedTreeMap selector = (LinkedTreeMap)mapFlows.get("selector");
			ArrayList criteria = (ArrayList)selector.get("criteria");
			for(Object ob : criteria){
				LinkedTreeMap mapCriteria = (LinkedTreeMap)ob;
				String typeCriteria = (String)mapCriteria.get("type");
				String criteriaKey = "";
				String criteriaValue = "";
				Set keys = mapCriteria.keySet();
				for(Object key :keys){
					String k = (String)key;
					if(!k.equals("type")){
						criteriaKey = k;
						criteriaValue = String.valueOf(mapCriteria.get(k));
					}

				}
				FlowCriteria crit = new FlowCriteria(typeCriteria, new AbstractMap.SimpleEntry<String,String>(criteriaKey, criteriaValue));
				flowSelector.getListFlowCriteria().add(crit);
			}

			Flow flow = new Flow(id, tableId, appId, groupId, priority, timeout, isPermanent, deviceId, state, life, packets, bytes, liveType, lastSeen, flowTreatment, flowSelector);
			EntornoTools.entorno.getMapSwitches().get(deviceId).addFlow(flow);

		}

	}

	public static void parseJsonHostsGson(String json) {
		Gson gson = new Gson();
		
		EntornoTools.entorno.getMapHosts().clear();
		
		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList hosts = (ArrayList)jsonObject.get("hosts");
		for(Object o : hosts){
			LinkedTreeMap mapHosts = (LinkedTreeMap)o;

			String id = (String)mapHosts.get("id");
			String mac = (String)mapHosts.get("mac");
			String vlan = (String)mapHosts.get("vlan");
			String innerVlan = (String)mapHosts.get("innerVlan");
			String outerTpid = (String)mapHosts.get("outerTpid");
			boolean configured = (boolean)mapHosts.get("configured");
			List<String> ipAddresses = (ArrayList)mapHosts.get("ipAddresses");
			Map locations = new HashMap<String,String>();

			ArrayList listLoc = (ArrayList)mapHosts.get("locations");
			for(Object ob : listLoc){
				LinkedTreeMap location = (LinkedTreeMap)ob;
				String elementId = (String)location.get("elementId");
				String port = (String)location.get("port");
				locations.put(elementId, port);
			}

			Host h = new Host(id, mac, vlan, innerVlan, outerTpid, configured, ipAddresses, locations);
			EntornoTools.entorno.addHost(h);
		}
	}

	public static List<Vpls> parseVpls(String json) {
		Gson gson = new Gson();
		List<Vpls> vplsList = new ArrayList<Vpls>();

		LinkedTreeMap jsonObject = (LinkedTreeMap)gson.fromJson(json, LinkedTreeMap.class);
		LinkedTreeMap apps = (LinkedTreeMap)jsonObject.get("apps");
		LinkedTreeMap orgVpls = (LinkedTreeMap)apps.get("org.onosproject.vpls");
		if(orgVpls != null) {
			LinkedTreeMap vpls = (LinkedTreeMap)orgVpls.get("vpls");
			ArrayList vplsL = (ArrayList)vpls.get("vplsList");

			for(Object ob : vplsL){
				LinkedTreeMap vplsNode = (LinkedTreeMap)ob;
				Vpls v = gson.fromJson(gson.toJson(vplsNode), Vpls.class);
				vplsList.add(v);
			}
		}


		return vplsList;
	}

	public static String getPortFromPathJson(String id, String json) {
		Gson gson = new Gson();
		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList paths = (ArrayList)jsonObject.get("paths");
		for(Object o : paths) {
			LinkedTreeMap path = (LinkedTreeMap)o;
			
			ArrayList links = (ArrayList)path.get("links");
			for(Object ob : links) {
				LinkedTreeMap link = (LinkedTreeMap)ob;
				
				LinkedTreeMap src = (LinkedTreeMap)link.get("src");
				
				String portJson = (String)src.get("port");
				String deviceJson = (String)src.get("device");
				if(deviceJson.equals(id)) {
					System.out.println("Puerto parseado: "+portJson);
					return portJson;
				}
			}
		}
		return null;
	}

	public static List<QueueOnos> parseQueues(String json) {
		List<QueueOnos> queuesList = new ArrayList<QueueOnos>();
		QueueOnos q = null;
		Gson gson = new Gson();
		
		
		LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
		ArrayList queues = (ArrayList)jsonObject.get("queues");
		for(Object o : queues) {
			LinkedTreeMap queue = (LinkedTreeMap)o;
			q = gson.fromJson(gson.toJson(queue), QueueOnos.class);
			queuesList.add(q);
		}
		
		return queuesList;
	}

}

