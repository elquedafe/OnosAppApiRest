/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Band;
import architecture.Environment;
import architecture.Flow;
import architecture.FlowCriteria;
import architecture.Host;
import architecture.Link;
import architecture.Meter;
import architecture.Port;
import architecture.Queue;
import architecture.Switch;
import architecture.Vpls;
import architecture.FlowInstruction;
import rest.database.objects.FlowDBResponse;
import rest.database.objects.QueueDBResponse;
import rest.gsonobjects.onosside.IntentOnosRequest;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.onosside.Point;
import rest.gsonobjects.onosside.QueueOnos;
import rest.gsonobjects.onosside.QueueOnosRequest;
import rest.gsonobjects.onosside.VplsOnosRequestAux;
import rest.gsonobjects.userside.FlowSocketClientRequest;
import rest.gsonobjects.userside.MeterClientRequestPort;
import rest.gsonobjects.userside.QueueClientRequest;

/**
 * Represents an network environment manager.
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class EntornoTools {
	public static String user = "onos";
	public static String password = "rocks";
	public static String onosHost = "10.0.2.1";
	public static String endpoint = "http://" + onosHost + ":8181/onos/v1";;
	public static String endpointNetConf = endpoint+"/network/configuration/";
	public static String endpointQueues = "http://" + onosHost + ":8181/onos/upm/queues/ovsdb:10.0.2.2";
	public static Environment entorno = new Environment();

	/**
	 * Get environment info from ONOS
	 * @throws IOException network error
	 */
	public static void getEnvironment() throws IOException{
		OnosResponse response = new OnosResponse();
		URL urlClusters = new URL(endpoint + "/cluster");
		URL urlDevices = new URL(endpoint + "/devices");
		URL urlLinks = new URL(endpoint + "/links");
		URL urlFlows = new URL(endpoint + "/flows");
		URL urlHosts = new URL(endpoint + "/hosts");

		// CLUSTERS
		response = HttpTools.doJSONGet(urlClusters);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonClustersGson(response.getMessage());

		// SWITCHES
		response = HttpTools.doJSONGet(urlDevices);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonDevicesGson(response.getMessage());

		//PORTS
		if(entorno.getMapSwitches() != null) {
			for(Switch s : entorno.getMapSwitches().values()){
				response = HttpTools.doJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"));
				if(response.getCode()/100 == 2)
					JsonManager.parseJsonPortsGson(response.getMessage());
			}
		}

		//LINKS
		response = HttpTools.doJSONGet(urlLinks);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonLinksGson(response.getMessage());

		//FLOWS
		response = HttpTools.doJSONGet(urlFlows);
		JsonManager.parseJsonFlowGson(response.getMessage());

		//HOSTS
		response = HttpTools.doJSONGet(urlHosts);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonHostsGson(response.getMessage());    

		//if no queues in ddbb add default queues
		if(DatabaseTools.getAllQueuesIds().size() == 0) {
			try {
				EntornoTools.addQueuesDefault();
			} catch (ClassNotFoundException | SQLException e) {
				throw new IOException();
			}
		}
	}

	/**
	 * Get switches connected to host given its Ip
	 * @param hostIp host ip address
	 * @return
	 */
	public static List<Switch> getIngressSwitchesByHost(String hostIp) {
		Host host = null;
		Switch s = null;
		List<Switch> listSwitches = new ArrayList<Switch>();

		for(Host h : EntornoTools.entorno.getMapHosts().values()) {
			if(h.getIpList().contains(hostIp)) {
				host = h;
				break;
			}
		}
		for(Map.Entry<String, String> location : host.getMapLocations().entrySet()) {
			s = EntornoTools.entorno.getMapSwitches().get(location.getKey());
			listSwitches.add(s);
		}
		return listSwitches;

	}

	/**
	 * Get ingress switch given host ip
	 * @param hostIp host ip address
	 * @return switch
	 */
	public static Switch getIngressSwitchByHost(String hostIp) {
		Host host = null;
		Switch s = null;
		LogTools.info("getIngressSwitchByHost", "host ip "+ hostIp);
		List<Switch> listSwitches = new ArrayList<Switch>();
		boolean found = false;
		for(Host h : EntornoTools.entorno.getMapHosts().values()) {
			if(h.getIpList().contains(hostIp) && !found) {
				host = h;
				found = true;
			}
		}
		for(Map.Entry<String, String> location : host.getMapLocations().entrySet()) {
			s = EntornoTools.entorno.getMapSwitches().get(location.getKey());
			listSwitches.add(s);
		}
		return listSwitches.get(0);
	}

	/**
	 * Get all network meters
	 * @return meters list
	 * @throws IOException network error
	 */
	public static List<Meter> getAllMeters() throws IOException{
		List<Meter> listMeters = new ArrayList<Meter>();
		String url = EntornoTools.endpoint+"/meters/";

		OnosResponse response = HttpTools.doJSONGet(new URL(url));
		Gson gson = new Gson();
		LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
		ArrayList meters = (ArrayList)jsonObject.get("meters");
		for(Object o : meters) {
			LinkedTreeMap mapMeter = (LinkedTreeMap)o;

			String id = (String)mapMeter.get("id");
			int life = (int)(double)mapMeter.get("life");
			int packets = (int)(double)mapMeter.get("packets");
			int bytes = (int)(double)mapMeter.get("bytes");
			int referenceCount = (int)(double)mapMeter.get("referenceCount");
			String unit = (String)mapMeter.get("unit");
			boolean burst = (boolean)mapMeter.get("burst");
			String deviceId = (String)mapMeter.get("deviceId");
			String appId = (String)mapMeter.get("appId");
			String state = (String)mapMeter.get("state");

			ArrayList bandsArray = (ArrayList)mapMeter.get("bands");

			List<Band> bands = new ArrayList<Band>();
			Band band = null;
			for(Object b : bandsArray) {
				LinkedTreeMap mapBand = (LinkedTreeMap)b;

				String type = (String)mapBand.get("type");
				int rate = (int)(double)mapBand.get("rate");
				int packetsBand = (int)(double)mapBand.get("packets");
				int bytesBand = (int)(double)mapBand.get("bytes");
				int burstSize = (int)(double)mapBand.get("burstSize");

				band = new Band(type, rate, packetsBand, bytesBand, burstSize);
				bands.add(band);
			}

			Meter m = new Meter(id, life, packets, bytes, referenceCount, unit, burst, deviceId, appId, state, bands);
			listMeters.add(m);

		}


		return listMeters;

	}

	/**
	 * Get meters of a switch
	 * @param switchId switch id
	 * @return meters list
	 */
	public static List<Meter> getMeters(String switchId){
		List<Meter> listMeters = new ArrayList<Meter>();
		List<Band> listBands = new ArrayList<Band>();
		String url = EntornoTools.endpoint+"/meters/"+switchId;
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(url));
			Gson gson = new Gson();
			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			ArrayList meters = (ArrayList)jsonObject.get("meters");
			for(Object o : meters) {
				LinkedTreeMap mapMeter = (LinkedTreeMap)o;

				String id = (String)mapMeter.get("id");
				int life = (int)(double)mapMeter.get("life");
				int packets = (int)(double)mapMeter.get("packets");
				int bytes = (int)(double)mapMeter.get("bytes");
				int referenceCount = (int)(double)mapMeter.get("referenceCount");
				String unit = (String)mapMeter.get("unit");
				boolean burst = (boolean)mapMeter.get("burst");
				String deviceId = (String)mapMeter.get("deviceId");
				String appId = (String)mapMeter.get("appId");
				String state = (String)mapMeter.get("state");

				ArrayList bands = (ArrayList)mapMeter.get("bands");
				Band band = null;
				for(Object b : bands) {
					LinkedTreeMap mapBand = (LinkedTreeMap)b;

					String type = (String)mapBand.get("type");
					int rate = (int)(double)mapMeter.get("packets");
					int packetsBand = (int)(double)mapMeter.get("packets");
					int bytesBand = (int)(double)mapMeter.get("packets");
					int burstSize = (int)(double)mapMeter.get("packets");

					band = new Band(type, rate, packetsBand, bytesBand, burstSize);
					listBands.add(band);
				}

				Meter m = new Meter(id, life, packets, bytes, referenceCount, unit, burst, deviceId, appId, state, listBands);
				listMeters.add(m);

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listMeters;
	}

	/**
	 * Create meter in ONOS
	 * @param switchId switch id
	 * @param rate maxmum rate
	 * @param burst burst
	 * @return onos response
	 * @throws IOException
	 */
	public static OnosResponse addMeter(String switchId, int rate, int burst) throws IOException{
		String url = EntornoTools.endpoint + "/meters/"+switchId;
		OnosResponse onosResponse;
		String jsonOut = "{\r\n" + 
				"  \"cellId\":1,\r\n" + 
				"  \"deviceId\": \""+switchId+"\",\r\n" + 
				"  \"unit\": \"KB_PER_SEC\",\r\n" + 
				"  \"burst\": true,\r\n" + 
				"  \"bands\": [\r\n" + 
				"    {\r\n" + 
				"      \"type\": \"DROP\",\r\n" + 
				"      \"rate\": "+rate+",\r\n" + 
				"      \"burstSize\": "+burst+"\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		try {
			onosResponse = HttpTools.doJSONPost(new URL(url), jsonOut);
		} catch (MalformedURLException e) {
			onosResponse = new OnosResponse("URL Error", 404);
		}

		return onosResponse;
	}

	/**
	 * Get output ports of a switch
	 * @param switchId switch id
	 * @return ports list
	 */
	public static List<String> getOutputPorts(String switchId) {
		Gson gson;
		List<String> listPorts = new ArrayList<String>();
		String url = EntornoTools.endpoint + "/links?device="+switchId+"&direction=EGRESS";
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(url));
			gson = new Gson();

			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			ArrayList links = (ArrayList)jsonObject.get("links");
			for(Object l : links) {
				LinkedTreeMap mapLink = (LinkedTreeMap)l;
				LinkedTreeMap src = (LinkedTreeMap)mapLink.get("src");
				String port = (String)src.get("port");
				listPorts.add(port);
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listPorts;
	}

	/**
	 * Get host given its ip address
	 * @param ip ip address
	 * @return host
	 */
	public static Host getHostByIp(String ip) {
		Host host = null;
		for(Host h : entorno.getMapHosts().values()) {
			if(h.getIpList().contains(ip))
				return h;
		}
		return host;

	}

	/**
	 * Create Flow with meter treatment in ONOS
	 * @param switchId
	 * @param outPort
	 * @param meterId
	 * @param ip
	 * @return
	 * @throws IOException
	 */
	public static OnosResponse addQosFlow(String switchId, String outPort, int meterId, String ip) throws IOException {
		OnosResponse response = null;
		String url = EntornoTools.endpoint+"/flows/"+switchId;
		String body = "{\"priority\": 1500,\r\n" + 
				"\"timeout\": 0,\r\n" + 
				"\"isPermanent\": true,\r\n" + 
				"\"deviceId\": \""+switchId+"\",\r\n" + 
				"\"tableId\": 0,\"groupId\": 0,\"appId\": \"org.onosproject.fwd\",\r\n" + 
				"\"treatment\": \r\n" + 
				"	{	\r\n" + 
				"		\"instructions\": [\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"OUTPUT\",\r\n" + 
				"				\"port\": \""+outPort+"\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"METER\",\r\n" + 
				"				\"meterId\":"+meterId+"\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	},\r\n" + 
				"\"selector\": \r\n" + 
				"	{\r\n" + 
				"		\"criteria\": [\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"ETH_TYPE\",\r\n" + 
				"				\"ethType\": \"0x800\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"IPV4_SRC\",\r\n" + 
				"				\"ip\": \""+ip+"/32\"\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	}\r\n" + 
				"}";
		try {
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;
	}

	/**
	 * Generate interfaces definition for netconf POST.
	 * @return ports array element netconf
	 */
	private static String getNetConfPorts() {
		// FOR EACH HOST GET switch/port connected and generate json "port" in network/configuration
		String genJson = "";
		for(Host h : entorno.getMapHosts().values()) {
			int i = 0;
			for(Map.Entry<String, String> entry : h.getMapLocations().entrySet()) {
				if(!h.getIpList().isEmpty()) {
					String point = entry.getKey()+"/"+entry.getValue();
					genJson += "\""+point+"\":{"
							+ "\"interfaces\":["
							+ "{\"name\":\""+h.getIpList().get(i)+"\"}]},";
					i++;
				}
			}
		}
		//DELETE LAST COMMA
		if(genJson.endsWith(",")) {
			genJson = genJson.substring(0, genJson.length()-1);
		}
		
		return genJson;
	}

	/**
	 * Generate ports definition for netconf
	 * @return ports netconf json
	 */
	private static String getVplsStateJsonPostFormat() {
		String json = "{\"ports\":{";
		//PORTS def
		json += EntornoTools.getNetConfPorts();

		json += "},";
		return json;
	}

	/**
	 * Create json for VPLS add request
	 * @param reqVplsName vpls name
	 * @param reqListInterfaces hosts list
	 * @return
	 */
	public static String addVplsJson(String reqVplsName, List<String> reqListInterfaces) {
		Gson gson = new Gson();
		boolean sameName = false;
		List<VplsOnosRequestAux> vplss = new ArrayList<VplsOnosRequestAux>();
		String genJson = "";
		genJson += getVplsStateJsonPostFormat();
		genJson+= "\"apps\":{"
				+ "\"org.onosproject.vpls\": {"
				+ "\"vpls\":{"
				+ "\"vplsList\":";
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));

			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			LinkedTreeMap apps =  (LinkedTreeMap)jsonObject.get("apps");
			LinkedTreeMap org =  (LinkedTreeMap)apps.get("org.onosproject.vpls");
			if(org != null) {
				LinkedTreeMap vpls =  (LinkedTreeMap)org.get("vpls");
				if(vpls != null && vpls.containsKey("vplsList")) {
					ArrayList vplsList = (ArrayList)vpls.get("vplsList");
					for(Object o : vplsList) {
						LinkedTreeMap mapVpls = (LinkedTreeMap)o;

						String name = (String)mapVpls.get("name");
						List<String> listInterfaces = new ArrayList<String>();
						ArrayList interfaces = (ArrayList)mapVpls.get("interfaces");
						for(Object ob : interfaces) {
							String interf = (String)ob;
							listInterfaces.add(interf);
						}

						//NEW VPLS. If requested vpls name exists in onos, then replace interfaces for the new ones.
						if(reqVplsName.equals(name)) {
							sameName = true;
							listInterfaces.clear();
							listInterfaces.addAll(reqListInterfaces);
						}
						vplss.add(new VplsOnosRequestAux(name, listInterfaces));
					}
					if(!sameName)
						vplss.add(new VplsOnosRequestAux(reqVplsName, reqListInterfaces));
				}
				else {
					vplss.add(new VplsOnosRequestAux(reqVplsName, reqListInterfaces));
				}
			}
			else {
				//ELSE when org.onosproject.vpls does not exists
				vplss.add(new VplsOnosRequestAux(reqVplsName, reqListInterfaces));
			}
			genJson += gson.toJson(vplss);
			genJson +="}}}}";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return genJson;
	}


	/**
	 * Update vpls json
	 * @param reqVplsName vpls name
	 * @param reqListInterfaces hosts list
	 * @param jsonVplsState netconf json of actual state
	 * @return new netconf json to post
	 * @throws IOException
	 */
	public static String updateVplsJson(String reqVplsName, List<String> reqListInterfaces, String jsonVplsState) throws IOException{
		Gson gson = new Gson();
		List<VplsOnosRequestAux> vplss = new ArrayList<VplsOnosRequestAux>();
		String genJson = "";
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));

			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			LinkedTreeMap apps =  (LinkedTreeMap)jsonObject.get("apps");
			LinkedTreeMap org =  (LinkedTreeMap)apps.get("org.onosproject.vpls");
			LinkedTreeMap vpls =  (LinkedTreeMap)org.get("vpls");
			ArrayList vplsList = (ArrayList)vpls.get("vplsList");
			for(Object o : vplsList) {
				LinkedTreeMap mapVpls = (LinkedTreeMap)o;

				String name = (String)mapVpls.get("name");
				List<String> listInterfaces = new ArrayList<String>();
				ArrayList interfaces = (ArrayList)mapVpls.get("interfaces");
				for(Object ob : interfaces) {
					String interf = "\""+(String)ob+"\"";
					listInterfaces.add(interf);
				}
				//NEW VPLS
				if(reqVplsName.equals(name)) {
					listInterfaces.clear();
					listInterfaces.addAll(reqListInterfaces);
				}
				else {
					vplss.add(new VplsOnosRequestAux(reqVplsName, reqListInterfaces));
				}

				vplss.add(new VplsOnosRequestAux(name, listInterfaces));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
		return genJson;
	}

	/**
	 * Get vpls list
	 * @return vpls list
	 */
	public static List<Vpls> getVplsList() {
		List<Vpls> vplsList = null;
		try {

			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
			LogTools.info("getVpls", "Json from ONOS: "+response.getMessage());
			vplsList = JsonManager.parseVpls(response.getMessage());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return vplsList;
		} catch (IOException e) {
			e.printStackTrace();
			return vplsList;
		}
		return vplsList;
	}


	/**
	 * Delete vpls
	 * @param vplsName vpls name
	 * @param authString authorization string
	 * @return onos response
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static OnosResponse deleteVpls(String vplsName, String authString) throws IOException, ClassNotFoundException, SQLException{
		Gson gson = new Gson();
		List<VplsOnosRequestAux> vplss = new ArrayList<VplsOnosRequestAux>();
		String genJson = "";
		genJson += getVplsStateJsonPostFormat();
		genJson+= "\"apps\":{"
				+ "\"org.onosproject.vpls\": {"
				+ "\"vpls\":{"
				+ "\"vplsList\":";
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));

			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			LinkedTreeMap apps =  (LinkedTreeMap)jsonObject.get("apps");
			LinkedTreeMap org =  (LinkedTreeMap)apps.get("org.onosproject.vpls");
			if(org != null) {
				LinkedTreeMap vpls =  (LinkedTreeMap)org.get("vpls");
				ArrayList vplsList = (ArrayList)vpls.get("vplsList");
				for(Object o : vplsList) {
					LinkedTreeMap mapVpls = (LinkedTreeMap)o;

					//If vpls is the one to delete do not include in the list of new vpls's
					String name = (String)mapVpls.get("name");
					if(!name.equals(vplsName)) {
						List<String> listInterfaces = new ArrayList<String>();
						ArrayList interfaces = (ArrayList)mapVpls.get("interfaces");
						for(Object ob : interfaces) {
							String interf = (String)ob;
							listInterfaces.add(interf);
						}
						vplss.add(new VplsOnosRequestAux(name, listInterfaces));
					}

				}
			}
			genJson += gson.toJson(vplss);
			genJson +="}}}}";

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OnosResponse response = HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), genJson);
		return response;

	}

	/**
	 * Add meter flow socket
	 * @param ipVersion ip version 
	 * @param switchId switch id
	 * @param outPort output port
	 * @param meterId meter id
	 * @param srcIp source ip
	 * @param srcPort source port
	 * @param dstIp destination ip
	 * @param dstPort destination port
	 * @param portType port type, TCP or UDP
	 * @return onos reponse
	 * @throws IOException
	 */
	public static OnosResponse addQosFlowWithPort(String ipVersion, String switchId, String outPort, String meterId, String srcIp, String srcPort, String dstIp, String dstPort, String portType) throws IOException {
		OnosResponse response = null;
		String url = EntornoTools.endpoint+"/flows/"+switchId;
		String body = "{\"priority\": 1500,\r\n" + 
				"\"timeout\": 0,\r\n" + 
				"\"isPermanent\": true,\r\n" + 
				"\"deviceId\": \""+switchId+"\",\r\n" + 
				"\"tableId\": 0,\"groupId\": 0,\"appId\": \"org.onosproject.fwd\",\r\n" + 
				"\"treatment\": \r\n" + 
				"	{	\r\n" + 
				"		\"instructions\": [\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"OUTPUT\",\r\n" + 
				"				\"port\": \""+outPort+"\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"METER\",\r\n" + 
				"				\"meterId\":"+meterId+"\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	},\r\n" + 
				"\"selector\": \r\n" + 
				"	{\r\n" + 
				"		\"criteria\": [\r\n";
		if(ipVersion.equalsIgnoreCase("4")){
			body += "			{\r\n" + 
					"				\"type\": \"ETH_TYPE\",\r\n" + 
					"				\"ethType\": \"0x800\"\r\n" + 
					"			},\n"; 
			if(srcIp != null && !srcIp.isEmpty()) {
				body += "			{\r\n" + 
						"				\"type\": \"IPV4_SRC\",\r\n" + 
						"				\"ip\": \""+srcIp+"/32\"\r\n" + 
						"			},\n";
			}
			if(dstIp != null && !dstIp.isEmpty()) {
				body += "			{\r\n" + 
						"				\"type\": \"IPV4_DST\",\r\n" + 
						"				\"ip\": \""+dstIp+"/32\"\r\n" + 
						"			},\n";
			}
		}
		else if(ipVersion.equalsIgnoreCase("6")) {
			body += "			{\r\n" + 
					"				\"type\": \"ETH_TYPE\",\r\n" + 
					"				\"ethType\": \"0x86DD\"\r\n" + 
					"			},\r\n" + 
					"			{\r\n" + 
					"				\"type\": \"IPV6_SRC\",\r\n" + 
					"				\"ip\": \""+srcIp+"/128\"\r\n" + 
					"			},\r\n"+ 
					"			{\r\n" + 
					"				\"type\": \"IPV6_DST\",\r\n" + 
					"				\"ip\": \""+dstIp+"/128\"\r\n" + 
					"			},\n";
		}
		if(portType != null && !portType.isEmpty() && portType.equalsIgnoreCase("tcp")){
			body += "			{\n" + 
					"				\"type\": \"IP_PROTO\",\n" + 
					"				\"protocol\": 6\n" + 
					"			},\n";
			if(srcPort != null && !srcPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"TCP_SRC\",\n" + 
						"			\"tcpPort\": \""+srcPort+"\"\n" + 
						"		},\n";
			}
			if(dstPort != null && !dstPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"TCP_DST\",\n" + 
						"			\"tcpPort\": \""+dstPort+"\"\n" + 
						"		},\n";
			}
		}
		else if(portType != null && !portType.isEmpty() && portType.equalsIgnoreCase("udp")){
			body += "			{\n" + 
					"				\"type\": \"IP_PROTO\",\n" + 
					"				\"protocol\": 17\n" + 
					"			},\n";
			if(srcPort != null && !srcPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"UDP_SRC\",\n" + 
						"			\"udpPort\": \""+srcPort+"\"\n" + 
						"		},\n";
			}
			if(dstPort != null && !dstPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"UDP_DST\",\n" + 
						"			\"udpPort\": \""+dstPort+"\"\n" + 
						"		},\n";
			}
		}
		//Delete last comma
		body = body.substring(0, body.length() - 2);
		body += "		]\r\n" + 
				"	}\r\n" + 
				"}";
		try {
			System.out.println("JSON FLUJO para meter hacia ONOS: \n"+body);
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;

	}

	/**
	 * Add queue flow socket
	 * @param ipVersion ip verson
	 * @param switchId switch d
	 * @param outPort output port
	 * @param queueId queue d
	 * @param srcIp source ip
	 * @param srcPort source port
	 * @param dstIp destination ip
	 * @param dstPort destination port
	 * @param portType port type, TCP or UDP or empty
	 * @return
	 * @throws IOException
	 */
	public static OnosResponse addQueueFlowWithPort(String ipVersion, String switchId, String outPort, String queueId, String srcIp, String srcPort, String dstIp, String dstPort, String portType) throws IOException {
		OnosResponse response = null;
		String url = EntornoTools.endpoint+"/flows/"+switchId;
		String body = "{\"priority\": 1500,\r\n" + 
				"\"timeout\": 0,\r\n" + 
				"\"isPermanent\": true,\r\n" + 
				"\"deviceId\": \""+switchId+"\",\r\n" + 
				"\"tableId\": 0,\"groupId\": 0,\"appId\": \"org.onosproject.fwd\",\r\n" + 
				"\"treatment\": \r\n" + 
				"	{	\r\n" + 
				"		\"instructions\": [\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"QUEUE\",\r\n" + 
				"				\"queueId\":\""+queueId+"\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"OUTPUT\",\r\n" + 
				"				\"port\": \""+outPort+"\"\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	},\r\n" + 
				"\"selector\": \r\n" + 
				"	{\r\n" + 
				"		\"criteria\": [\r\n";
		if(ipVersion.equalsIgnoreCase("4")){
			body += "			{\r\n" + 
					"				\"type\": \"ETH_TYPE\",\r\n" + 
					"				\"ethType\": \"0x800\"\r\n" + 
					"			},\n"; 
			if(srcIp != null && !srcIp.isEmpty()) {
				body += "			{\r\n" + 
						"				\"type\": \"IPV4_SRC\",\r\n" + 
						"				\"ip\": \""+srcIp+"/32\"\r\n" + 
						"			},\n";
			}
			if(dstIp != null && !dstIp.isEmpty()) {
				body += "			{\r\n" + 
						"				\"type\": \"IPV4_DST\",\r\n" + 
						"				\"ip\": \""+dstIp+"/32\"\r\n" + 
						"			},\n";
			}
		}
		else if(ipVersion.equalsIgnoreCase("6")) {
			body += "			{\r\n" + 
					"				\"type\": \"ETH_TYPE\",\r\n" + 
					"				\"ethType\": \"0x86DD\"\r\n" + 
					"			},\r\n" + 
					"			{\r\n" + 
					"				\"type\": \"IPV6_SRC\",\r\n" + 
					"				\"ip\": \""+srcIp+"/128\"\r\n" + 
					"			},\r\n"+ 
					"			{\r\n" + 
					"				\"type\": \"IPV6_DST\",\r\n" + 
					"				\"ip\": \""+dstIp+"/128\"\r\n" + 
					"			},\n";
		}
		if(portType != null && !portType.isEmpty() && portType.equalsIgnoreCase("tcp")){
			body += "			{\n" + 
					"				\"type\": \"IP_PROTO\",\n" + 
					"				\"protocol\": 6\n" + 
					"			},\n";
			if(srcPort != null && !srcPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"TCP_SRC\",\n" + 
						"			\"tcpPort\": \""+srcPort+"\"\n" + 
						"		},\n";
			}
			if(dstPort != null && !dstPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"TCP_DST\",\n" + 
						"			\"tcpPort\": \""+dstPort+"\"\n" + 
						"		},\n";
			}
		}
		else if(portType != null && !portType.isEmpty() && portType.equalsIgnoreCase("udp")){
			body += "			{\n" + 
					"				\"type\": \"IP_PROTO\",\n" + 
					"				\"protocol\": 17\n" + 
					"			},\n";
			if(srcPort != null && !srcPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"UDP_SRC\",\n" + 
						"			\"udpPort\": \""+srcPort+"\"\n" + 
						"		},\n";
			}
			if(dstPort != null && !dstPort.isEmpty()) {
				body += "		{\n" + 
						"			\"type\": \"UDP_DST\",\n" + 
						"			\"udpPort\": \""+dstPort+"\"\n" + 
						"		},\n";
			}
		}
		//Delete last comma
		body = body.substring(0, body.length() - 2);
		body += "		]\r\n" + 
				"	}\r\n" + 
				"}";
		try {
			System.out.println("JSON FLUJO para queue hacia ONOS: \n"+body);
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;

	}

	/**
	 * Compare two flows 
	 * @param oldFlowsState old flow list
	 * @param newFlowsState new flow list
	 * @return list flow in newFlowsState but not in oldFlowsState
	 */
	public static List<Flow> compareFlows(Map<String, Flow> oldFlowsState, Map<String, Flow> newFlowsState) {
		List<Flow> flows = new ArrayList<Flow>();
		for(Flow flow : newFlowsState.values()) {
			if(!oldFlowsState.containsKey(flow.getId())) {
				flows.add(flow);
			}
		}

		return flows;
	}

	/**
	 * Get environment by user
	 * @param authString authorization http string
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void getEnvironmentByUser(String authString) throws MalformedURLException, IOException {
		//String json = "";
		OnosResponse response = new OnosResponse();
		URL urlClusters = new URL(endpoint + "/cluster");
		URL urlDevices = new URL(endpoint + "/devices");
		URL urlLinks = new URL(endpoint + "/links");
		String urlFlows = endpoint + "/flows";
		URL urlHosts = new URL(endpoint + "/hosts");

		// CLUSTERS
		response = HttpTools.doJSONGet(urlClusters);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonClustersGson(response.getMessage());

		// SWITCHES
		response = HttpTools.doJSONGet(urlDevices);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonDevicesGson(response.getMessage());

		//PORTS
		if(entorno.getMapSwitches() != null) {
			for(Switch s : entorno.getMapSwitches().values()){
				response = HttpTools.doJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"));
				if(response.getCode()/100 == 2)
					JsonManager.parseJsonPortsGson(response.getMessage());
			}
		}

		//LINKS
		response = HttpTools.doJSONGet(urlLinks);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonLinksGson(response.getMessage());

		//FLOWS
		Map<String, FlowDBResponse> userFlows = DatabaseTools.getFlowsByUser(authString);
		for(FlowDBResponse userFlow : userFlows.values()) {
			response = HttpTools.doJSONGet(new URL(urlFlows+"/"+userFlow.getIdSwitch()+"/"+userFlow.getIdFlow()));
			JsonManager.parseJsonFlowGson(response.getMessage());
		}


		//HOSTS
		response = HttpTools.doJSONGet(urlHosts);
		if(response.getCode()/100 == 2)
			JsonManager.parseJsonHostsGson(response.getMessage()); 
	}

	/**
	 * Get ingress point from source host ip
	 * @param srcHost
	 * @return
	 */
	public static Point getIngressPoint(String srcHost) {
		Point point = null;
		Host h = EntornoTools.getHostByIp(srcHost);
		if (h != null) {
			LogTools.info("getIngressPoint", "host not null");
			for(Map.Entry<String, String> location : h.getMapLocations().entrySet()) {
				point = new Point(location.getValue(), location.getKey());
			}

		}
		return point;
	}

	/**
	 * Create flow selector from flow request
	 * @param flowReq flow request
	 * @return selector
	 */
	public static Map<String, LinkedList<LinkedHashMap<String,Object>>> createSelector(FlowSocketClientRequest flowReq) {
		LinkedList<LinkedHashMap<String,Object>> auxList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> auxMap = new LinkedHashMap<String,Object>();
		Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = new LinkedHashMap<String, LinkedList<LinkedHashMap<String,Object>>>();

		auxMap = new LinkedHashMap<String, Object>();
		auxList = new LinkedList<LinkedHashMap<String,Object>>();
		auxMap.put("type", "ETH_TYPE");
		if(flowReq.getIpVersion() == 4)
			auxMap.put("ethType", "0x800");
		else if(flowReq.getIpVersion() == 6)
			auxMap.put("ethType", "0x86DD");
		auxList.add(auxMap);
		//criteria 2
		if(flowReq.getIpVersion() == 4) {
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "IPV4_SRC");
			auxMap.put("ip", flowReq.getSrcHost()+"/32");
			auxList.add(auxMap);
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "IPV4_DST");
			auxMap.put("ip", flowReq.getDstHost()+"/32");
			auxList.add(auxMap);
		}
		else if(flowReq.getIpVersion() == 6) {
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "IPV6_SRC");
			auxMap.put("ip", flowReq.getSrcHost()+"/128");
			auxList.add(auxMap);
			auxMap = new LinkedHashMap<String, Object>();
			auxMap.put("type", "IPV6_DST");
			auxMap.put("ip", flowReq.getDstHost()+"/128");
			auxList.add(auxMap);
		}
		//criteria 3
		if(!flowReq.getPortType().isEmpty()) {
			if(!((flowReq.getSrcPort()==null || flowReq.getSrcPort().isEmpty()) && (flowReq.getDstPort()==null || flowReq.getDstPort().isEmpty()))) {
				if(flowReq.getPortType().equalsIgnoreCase("tcp")) {
					auxMap = new LinkedHashMap<String, Object>();
					auxMap.put("type", "IP_PROTO");
					auxMap.put("protocol", 6);
					auxList.add(auxMap);
					//criteria 4
					if(flowReq.getSrcPort()!=null && !flowReq.getSrcPort().isEmpty()) {
						auxMap = new LinkedHashMap<String, Object>();
						auxMap.put("type", "TCP_SRC");
						auxMap.put("tcpPort", Integer.parseInt(flowReq.getSrcPort()));
						auxList.add(auxMap);
					}
					//criteria 5
					if(flowReq.getDstPort()!=null && !flowReq.getDstPort().isEmpty()) {
						auxMap = new LinkedHashMap<String, Object>();
						auxMap.put("type", "TCP_DST");
						auxMap.put("tcpPort", Integer.parseInt(flowReq.getDstPort()));
						auxList.add(auxMap);
					}
				}
				else if(flowReq.getPortType().equalsIgnoreCase("udp")) {
					auxMap = new LinkedHashMap<String, Object>();
					auxMap.put("type", "IP_PROTO");
					auxMap.put("protocol", 17);
					auxList.add(auxMap);
					//criteria 4
					if(flowReq.getSrcPort()!=null && !flowReq.getSrcPort().isEmpty()) {
						auxMap = new LinkedHashMap<String, Object>();
						auxMap.put("type", "UDP_SRC");
						auxMap.put("udpPort", Integer.parseInt(flowReq.getSrcPort()));
						auxList.add(auxMap);
					}
					//criteria 5
					if(flowReq.getDstPort()!=null && !flowReq.getDstPort().isEmpty()) {
						auxMap = new LinkedHashMap<String, Object>();
						auxMap.put("type", "UDP_DST");
						auxMap.put("udpPort", Integer.parseInt(flowReq.getDstPort()));
						auxList.add(auxMap);
					}
				}
			}
		}

		selector.put("criteria", auxList);
		return selector;
	}

	/**
	 * Compare meters
	 * @param oldMetersState meters old list
	 * @param newMetersState meters new list
	 * @return meters in newMetersState and not in oldMetersState
	 */
	public static List<Meter> compareMeters(List<Meter> oldMetersState, List<Meter> newMetersState) {
		List<Meter> meters = new ArrayList<Meter>();
		if(oldMetersState.isEmpty() && !newMetersState.isEmpty()) {
			for(Meter m : newMetersState)
				meters.add(m);
		}
		else {
			for(Meter newMeter : newMetersState) {
				for(Meter oldMeter : oldMetersState) {
					if(newMeter.getDeviceId().equals(oldMeter.getDeviceId()) && newMeter.getId().equals(oldMeter.getId())) {
						meters.add(newMeter);
					}
				}
			}
		}
		return meters;
	}

	/**
	 * Get output port of ingress switch given the host ip
	 * @param srcHost source ip
	 * @param dstHost destination 
	 * @return output port
	 */
	public static String getOutputPort(String srcHost, String dstHost) {
		Gson gson = new Gson();
		String port="";
		OnosResponse response = null;

		//GET INGRESS
		Switch ingress = EntornoTools.getIngressSwitchByHost(srcHost);
		//GET EGRESS
		Switch egress = EntornoTools.getIngressSwitchByHost(dstHost);
		try {
			// TODO:
			response = HttpTools.doJSONGet(new URL(EntornoTools.endpoint+"/paths/"+ingress.getId()+"/"+egress.getId()));
			String json = response.getMessage();
			System.out.format("Path realizado para ingress %s y egress %s, json obtenido: %s \n", ingress.getId(), egress.getId(), json);
			//PARSE JSON TO GET PORT
			port = JsonManager.getPortFromPathJson(ingress.getId(), json);
			System.out.format("Puerto a devolver al main: "+port);
			return port;


		} catch (Exception e) {
			System.out.println("Excepcion pillada en getOutputPort");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Get vpls list
	 * @return
	 */
	public static List<Vpls> getVplsState() {
		Gson gson = new Gson();
		List<Vpls> vplss = new ArrayList<Vpls>();
		try {
			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));

			LinkedTreeMap jsonObject = gson.fromJson(response.getMessage(), LinkedTreeMap.class);
			LinkedTreeMap apps =  (LinkedTreeMap)jsonObject.get("apps");
			LinkedTreeMap org =  (LinkedTreeMap)apps.get("org.onosproject.vpls");
			if(org != null) {
				LinkedTreeMap vpls =  (LinkedTreeMap)org.get("vpls");
				if(vpls != null && vpls.containsKey("vplsList")) {
					ArrayList vplsList = (ArrayList)vpls.get("vplsList");
					for(Object o : vplsList) {
						LinkedTreeMap mapVpls = (LinkedTreeMap)o;

						String name = (String)mapVpls.get("name");
						List<String> listInterfaces = new ArrayList<String>();
						ArrayList interfaces = (ArrayList)mapVpls.get("interfaces");
						for(Object ob : interfaces) {
							String interf = (String)ob;
							listInterfaces.add(interf);
						}
						vplss.add(new Vpls(name, listInterfaces));
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vplss;
	}

	/**
	 * Compare vpls
	 * @param vplsBefore old vpls list 
	 * @param vplsAfter new vpls list
	 * @return vpls in vplsAfter and not in vplsBefore
	 */
	public static List<Vpls> compareVpls(List<Vpls> vplsBefore, List<Vpls> vplsAfter) {
		List<Vpls> vplss = new ArrayList<Vpls>();
		if(vplsBefore.isEmpty() && !vplsAfter.isEmpty()) {
			for(Vpls v : vplsAfter)
				vplss.add(v);
		}
		else {
			for(Vpls newVpls : vplsAfter) {
				for(Vpls oldVpls : vplsBefore) {
					if(!newVpls.getName().equals(oldVpls))
						vplss.add(newVpls);
				}
			}
		}
		return vplss;
	}

	/**
	 * Get ingress output port given ingress and egress
	 * @param ingressSw
	 * @param egressSw
	 * @return
	 */
	public static String getOutputPortFromSwitches(String ingressSw, String egressSw) {
		Gson gson = new Gson();
		String port="";
		OnosResponse response = null;

		//GET INGRESS
		Switch ingress = EntornoTools.entorno.getMapSwitches().get(ingressSw);
		//GET EGRESS
		Switch egress = EntornoTools.entorno.getMapSwitches().get(egressSw);
		try {
			// TODO:
			response = HttpTools.doJSONGet(new URL(EntornoTools.endpoint+"/paths/"+ingress.getId()+"/"+egress.getId()));
			String json = response.getMessage();
			System.out.format("Path realizado para ingress %s y egress %s, json obtenido: %s \n", ingress.getId(), egress.getId(), json);
			//PARSE JSON TO GET PORT
			port = JsonManager.getPortFromPathJson(ingress.getId(), json);
			System.out.format("Puerto a devolver al main: "+port);
			return port;
		} catch (Exception e) {
			System.out.println("Excepcion pillada en getOutputPort");
			return null;
		}
	}

	/**
	 * Add one meter and its flow
	 * @param srcHost source ip 
	 * @param dstHost destination ip
	 * @param authString authorization http string
	 * @param meterReq meter request
	 * @return Response
	 */
	public static Response addMeterAndFlow(String srcHost, String dstHost, String authString, MeterClientRequestPort meterReq) {
		Response resRest;
		OnosResponse response = null;
		String portAux = "";
		String meterId = "";
		if(srcHost.equals(meterReq.getSrcHost()) && dstHost.equals(meterReq.getDstHost())) {
			//GET HOST
			Host h = EntornoTools.getHostByIp(meterReq.getSrcHost());
			System.out.println("Host: "+h.getIpList().get(0));
			
			//GET switches connected to host
			List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getSrcHost());
			for(Switch s : ingressSwitches)
				System.out.println("Ingress switch: "+s.getId());

			//ADD METERS TO SWITCHES
			if(h != null && (ingressSwitches.size() > 0)){
				for(Switch ingress : ingressSwitches) {
					try {
						// Get meters before
						List<Meter> oldMetersState = EntornoTools.getMeters(ingress.getId());

						//Add meter to onos
						response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());

						// Get meter after
						List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());

						//Compare old and new
						List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);

						//Add meter to DDBB
						for(Meter meter : metersToAdd) {
							try {
								System.out.println("Añadiendo meter a la bbdd: "+ meter.getDeviceId() + ":"+meter.getId());
								DatabaseTools.addMeter(meter, authString, null);
								meterId = meter.getId();
							} catch (ClassNotFoundException | SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						//GET EGRESS PORTS FROM SWITCH
						String outputSwitchPort = EntornoTools.getOutputPort(meterReq.getSrcHost(), meterReq.getDstHost());

						//Install flows for each new meter
						if(outputSwitchPort != null && !outputSwitchPort.isEmpty()) {
							EntornoTools.getEnvironment();
							for(Meter meter : metersToAdd) {
								if(ingress.getId().equals(meter.getDeviceId())){
									//GET OLD STATE
									Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
									for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
										for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
											if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
												oldFlowsState.put(flow.getKey(), flow.getValue());
									}

									// CREATE FLOW
									EntornoTools.addQosFlowWithPort(meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
									//				}

									//GET NEW STATE
									EntornoTools.getEnvironment();
									Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
									for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
										for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
											if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
												newFlowsState.put(flow.getKey(), flow.getValue());


									System.out.println(".");

									// GET FLOWS CHANGED
									List<Flow> flowsNews;
									flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);

									// ADD flows to DDBB
									if(flowsNews.size()>0) {
										for(Flow flow : flowsNews) {
											try {
												//												System.out.format("Añadiend flujo a la bbdd: %s %s %s", flow.getId(), flow.getDeviceId(), flow.getFlowSelector().getListFlowCriteria().get(3));
												System.out.format("Añadiend flujo a la bbdd: %s %s", flow.getId(), flow.getDeviceId());
												DatabaseTools.addFlow(flow, authString, meterId, null, null);
											} catch (ClassNotFoundException | SQLException e) {
												e.printStackTrace();
												//TODO: Delete flow from onos and send error to client

											}
										}
									}

								}
							}
						}

					} catch (MalformedURLException e) {
						resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
						return resRest;
					} catch (IOException e) {
						resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
								"\n"+"\nHOST:"+h.getId()+
								"\ningress: "+ingress.getId()+
								"\nport: "+portAux+
								"\nmeter Host from request: "+meterReq.getSrcHost(), MediaType.TEXT_PLAIN).build();
						
						return resRest;
					}
					return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

				}
			}
			else {
				return resRest = Response.ok("{\"response\":\"error host or switches = 0\"}", MediaType.APPLICATION_JSON_TYPE).build();
			}
		}
		return null;
	}

	/**
	 * Delete meter and its flow
	 * @param switchId switch id
	 * @param meterId meter id
	 * @param authString authorization http string
	 * @return Response
	 */
	public static Response deleteMeterWithFlows(String switchId, String meterId, String authString) {
		Response resRest;
		OnosResponse response = null;
		String url = url = EntornoTools.endpoint + "/meters/"+switchId+"/"+meterId;
		try {
			//DELETE FLOW ASSOCIATED
			Map<String, FlowDBResponse> dbFlows = DatabaseTools.getFlowsByMeterId(switchId, meterId, authString);
			Map<String, Flow> flows = EntornoTools.entorno.getMapSwitches().get(switchId).getFlows();

			//BBDD query to get flows related to the meterId
			for(FlowDBResponse dbFlow : dbFlows.values()) {

				String idFlow = dbFlow.getIdFlow();
				//ONOS DELETE
				HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+switchId+"/"+idFlow));
				//DDBB Delete
				DatabaseTools.deleteFlow(idFlow, authString);
				flows.remove(idFlow);
			}

			//DELETE METER
			response = HttpTools.doDelete(new URL(url));
			DatabaseTools.deleteMeter(meterId, switchId);

		} catch (MalformedURLException e) {
			resRest = Response.ok("MalformedURLException: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			return resRest;
		} catch (IOException e) {
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			return resRest;
		} catch (ClassNotFoundException e) {
			resRest = Response.ok("ClassNotFoundException: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			e.printStackTrace();
			return resRest;
		} catch (SQLException e) {
			resRest = Response.status(400).entity("SQLException"+e.getMessage()).build();
			e.printStackTrace();
			return resRest;
		}
		catch(Exception e) {
			resRest = Response.ok("Exception: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			e.printStackTrace();
			return resRest;
		}


		resRest = Response.ok("{\"response\":\"succesful_"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

		return resRest;
	}

	/**
	 * Add meter, its flow in a vpls
	 * @param vplsName vpls name
	 * @param srcHost source ip
	 * @param dstHost destination ip
	 * @param authString authorization http string
	 * @param meterReq meter request
	 * @return Response
	 */
	public static Response addMeterAndFlowWithVpls(String vplsName, String srcHost, String dstHost, String authString,
			MeterClientRequestPort meterReq) {
		Response resRest;
		OnosResponse response = null;
		String portAux = "";
		String meterId = "";
		if(srcHost.equals(meterReq.getSrcHost()) && dstHost.equals(meterReq.getDstHost())) {
			//GET HOST
			Host h = EntornoTools.getHostByIp(meterReq.getSrcHost());
			System.out.println("Host: "+h.getIpList().get(0));
			//System.out.println("HOST: "+meterReq.getHost());
			//System.out.println("GET HOST: "+h.getId()+" "+h.getIpList().get(0).toString());



			//GET switches connected to host
			List<Switch> ingressSwitches = EntornoTools.getIngressSwitchesByHost(meterReq.getSrcHost());
			for(Switch s : ingressSwitches)
				System.out.println("Ingress switch: "+s.getId());

			//ADD METERS TO SWITCHES
			if(h != null && (ingressSwitches.size() > 0)){
				for(Switch ingress : ingressSwitches) {
					try {
						// Get meters before
						List<Meter> oldMetersState = EntornoTools.getMeters(ingress.getId());

						//Add meter to onos
						if(!DatabaseTools.isMeterInstalled(vplsName, ingress.getId())) {
							response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());
						}
						else {
							response = new OnosResponse("", 200);
						}
						// Get meter after
						List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());

						//Compare old and new
						List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);

						//Add meter to DDBB
						for(Meter meter : metersToAdd) {
							try {
								System.out.println("Añadiendo meter a la bbdd: "+ meter.getDeviceId() + ":"+meter.getId());
								meterId = meter.getId();
								DatabaseTools.addMeter(meter, authString, vplsName);
							} catch (ClassNotFoundException | SQLException e) {

								e.printStackTrace();
							}
						}

						//GET EGRESS PORTS FROM SWITCH
						String outputSwitchPort = EntornoTools.getOutputPort(meterReq.getSrcHost(), meterReq.getDstHost());

						//INSTALL FLOW WITH METER ID
						if(outputSwitchPort != null && !outputSwitchPort.isEmpty()) {
							//GET OLD FLOW STATE
							Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
										oldFlowsState.put(flow.getKey(), flow.getValue());
							}
							for(Meter meter : metersToAdd) {
								if(ingress.getId().equals(meter.getDeviceId())){
									// CREATE FLOW
									EntornoTools.addQosFlowWithPort(meterReq.getIpVersion(), ingress.getId(), outputSwitchPort, meter.getId(), meterReq.getSrcHost(), meterReq.getSrcPort(), meterReq.getDstHost(), meterReq.getDstPort(), meterReq.getPortType());
								}
							}
							//GET NEW FLOW STATE
							EntornoTools.getEnvironment();
							Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
							for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
								for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
									if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
										newFlowsState.put(flow.getKey(), flow.getValue());


							System.out.println(".");

							// GET FLOWS CHANGED
							List<Flow> flowsNews;
							flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);

							// ADD flows to DDBB
							if(flowsNews.size()>0) {
								for(Flow flow : flowsNews) {
									try {
										//												System.out.format("Añadiend flujo a la bbdd: %s %s %s", flow.getId(), flow.getDeviceId(), flow.getFlowSelector().getListFlowCriteria().get(3));
										System.out.format("Añadiend flujo a la bbdd: %s %s", flow.getId(), flow.getDeviceId());
										if(!flow.getAppId().equals("org.onosproject.fwd")) {
											meterId = null;										
											System.err.println("\n***FLUJO ERROR***"+flow.getDeviceId()+"-"+flow.getId()+"-"+flow.getAppId()+"\n");
										}
										DatabaseTools.addFlow(flow, authString, meterId, vplsName, null);
									} catch (ClassNotFoundException | SQLException e) {
										e.printStackTrace();
										//

									}
								}
							}
						}



					} catch (MalformedURLException e) {
						resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+response.getMessage()+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
						return resRest;
					} catch (IOException e) {
						resRest = Response.ok("IO: "+e.getMessage()+"\n"+response.getMessage()+
								"\n"+"\nHOST:"+h.getId()+
								"\ningress: "+ingress.getId()+
								"\nport: "+portAux+
								"\nmeter Host from request: "+meterReq.getSrcHost(), MediaType.TEXT_PLAIN).build();
						
						return resRest;
					}
					return resRest = Response.ok("{\"response\":\"succesful"+ response.getMessage() +"\"}", MediaType.APPLICATION_JSON_TYPE).build();

				}
			}
			else {
				return resRest = Response.ok("{\"response\":\"error host or switches = 0\"}", MediaType.APPLICATION_JSON_TYPE).build();
			}
		}
		return null;

	}

	/**
	 * Get queues given queue DB object
	 * @param queuesDb queues db object list
	 * @return
	 */
	public static List<Queue> getQueues(List<QueueDBResponse> queuesDb) {
		List<Queue> queues = new ArrayList<Queue>();
		List<QueueOnos> queuesOnos = new ArrayList<QueueOnos>();
		Queue queue = null;
		OnosResponse onosResponse = new OnosResponse();
		for(QueueDBResponse queueDb : queuesDb) {
			try {
				onosResponse = HttpTools.doJSONGet(new URL(EntornoTools.endpointQueues+"/"+queueDb.getIdQueue()));
				queuesOnos = JsonManager.parseQueues(onosResponse.getMessage());
				for(QueueOnos queueOnos : queuesOnos) {
					queue = new Queue(Long.parseLong(queueOnos.getQueueId()),
							queueDb.getIdSwitch(),
							String.format("%.0f", queueOnos.getMinRate()),
							String.format("%.0f", queueOnos.getMaxRate()),
							String.valueOf(queueOnos.getBurst()),
							Long.parseLong(queueDb.getIdQos()),
							queueDb.getPortNumber(),
							queueDb.getPortName());
					queues.add(queue);
				}

			} catch (IOException e) {
				return queues;
			}

		}
		return queues;
	}

	/**
	 * Add intent
	 * @param authString authorization http string
	 * @param flowReq flow request
	 * @return Response
	 */
	public static Response addIntent(String authString, FlowSocketClientRequest flowReq) {
		Response resRest = null;
		String messageToClient = "";
		Gson gson = new Gson();

		IntentOnosRequest intentOnos = new IntentOnosRequest();
		IntentOnosRequest intentOnosInversed = new IntentOnosRequest();
		//CREATE INTENT SELECTOR
		Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = EntornoTools.createSelector(flowReq);
		//INGRESS POINT
		Point ingressPoint = EntornoTools.getIngressPoint(flowReq.getSrcHost());
		//EGRESS POINT
		Point egressPoint = EntornoTools.getIngressPoint(flowReq.getDstHost());
		//COMPLETE INTENT
		intentOnos.setIngressPoint(ingressPoint);
		intentOnos.setEgressPoint(egressPoint);
		intentOnos.setSelector(selector);

		//CREATE INTENT SELECTOR INVERSE
		String auxSrcHost = flowReq.getSrcHost();
		String auxSrcPort = flowReq.getSrcPort();
		String auxDstHost = flowReq.getDstHost();
		String auxDstPort = flowReq.getDstPort();
		flowReq.setDstHost(auxSrcHost);
		flowReq.setSrcHost(auxDstHost);
		flowReq.setSrcPort(auxDstPort);
		flowReq.setDstPort(auxSrcPort);
		Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversed = EntornoTools.createSelector(flowReq);
		//COMPLETE INTENT
		intentOnosInversed.setIngressPoint(egressPoint);
		intentOnosInversed.setEgressPoint(ingressPoint);
		intentOnosInversed.setSelector(selectorInversed);

		//Generate JSON to ONOS
		String jsonOut = gson.toJson(intentOnos);
		String jsonOutInversed = gson.toJson(intentOnosInversed);
		LogTools.info("setFlowSocket", "json to create intent: "+jsonOut);
		LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversed);
		String url = EntornoTools.endpoint+"/intents";
		try {

			//GET OLD STATE
			EntornoTools.getEnvironment();
			Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
			for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
				for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
					if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
						oldFlowsState.put(flow.getKey(), flow.getValue());
			}

			// CREATE FLOWS
			HttpTools.doJSONPost(new URL(url), jsonOut);
			HttpTools.doJSONPost(new URL(url), jsonOutInversed);

			//GET NEW STATE
			EntornoTools.getEnvironment();
			Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
			for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
				for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
					if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
						newFlowsState.put(flow.getKey(), flow.getValue());

			// GET FLOWS CHANGED
			List<Flow> flowsNews;
			flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
			if(flowsNews.size()>0) {
				for(Flow flow : flowsNews) {
					try {
						DatabaseTools.addFlow(flow, authString, null, null, null);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						//TODO: Delete flow from onos and send error to client

					}
				}
			}
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
			//			resRest = Response.serverError().build();
			return resRest;
		}
		String jsonToClient = "{\"ingress\":\""+ingressPoint.getDevice()+"\",\"ingressPort\":\""+ingressPoint.getPort()+"\",\"egress\":\""+egressPoint.getDevice()+"\",\"egressPort\":\""+egressPoint.getPort()+"\"}";
		System.out.println("*****INGRESS/EGRESS: "+jsonToClient);
		resRest = Response.ok(jsonToClient, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Add intent queues
	 * @param authString authorization http string
	 * @param flowReq flow request
	 * @return
	 */
	public static Response addIntentForQueues(String authString, FlowSocketClientRequest flowReq) {
		Response resRest = null;
		String messageToClient = "";
		Gson gson = new Gson();

		IntentOnosRequest intentOnos = new IntentOnosRequest();
		IntentOnosRequest intentOnosInversed = new IntentOnosRequest();
		//CREATE INTENT SELECTOR
		Map<String, LinkedList<LinkedHashMap<String,Object>>> selector = EntornoTools.createSelector(flowReq);
		//INGRESS POINT
		Point ingressPoint = EntornoTools.getIngressPoint(flowReq.getSrcHost());
		//EGRESS POINT
		Point egressPoint = EntornoTools.getIngressPoint(flowReq.getDstHost());
		//COMPLETE INTENT
		intentOnos.setIngressPoint(ingressPoint);
		intentOnos.setEgressPoint(egressPoint);
		intentOnos.setSelector(selector);

		//CREATE INTENT SELECTOR INVERSE
		String auxSrcHost = flowReq.getSrcHost();
		String auxSrcPort = flowReq.getSrcPort();
		String auxDstHost = flowReq.getDstHost();
		String auxDstPort = flowReq.getDstPort();
		flowReq.setDstHost(auxSrcHost);
		flowReq.setSrcHost(auxDstHost);
		flowReq.setSrcPort(auxDstPort);
		flowReq.setDstPort(auxSrcPort);
		Map<String, LinkedList<LinkedHashMap<String,Object>>> selectorInversed = EntornoTools.createSelector(flowReq);
		//COMPLETE INTENT
		intentOnosInversed.setIngressPoint(egressPoint);
		intentOnosInversed.setEgressPoint(ingressPoint);
		intentOnosInversed.setSelector(selectorInversed);

		//Generate JSON to ONOS
		String jsonOut = gson.toJson(intentOnos);
		String jsonOutInversed = gson.toJson(intentOnosInversed);
		LogTools.info("setFlowSocket", "json to create intent: "+jsonOut);
		LogTools.info("setFlowSocket", "json to create intent INVERSED: "+jsonOutInversed);
		String url = EntornoTools.endpoint+"/intents";
		try {

			//GET OLD STATE
			EntornoTools.getEnvironment();
			Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
			for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
				for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
					if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
						oldFlowsState.put(flow.getKey(), flow.getValue());
			}

			// CREATE FLOWS
			HttpTools.doJSONPost(new URL(url), jsonOut);
			HttpTools.doJSONPost(new URL(url), jsonOutInversed);

			//GET NEW STATE
			EntornoTools.getEnvironment();
			Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
			for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
				for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
					if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
						newFlowsState.put(flow.getKey(), flow.getValue());


			System.out.println(".");

			// GET FLOWS CHANGED
			List<Flow> flowsNews;
			flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
			if(flowsNews.size()>0) {
				for(Flow flow : flowsNews) {
					try {
						DatabaseTools.addFlow(flow, authString, null, null, null);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						//TODO: Delete flow from onos and send error to client

					}
				}
			}
		} catch (MalformedURLException e) {
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\""+jsonOut+"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage()+"\n"+jsonOut+"\n", MediaType.TEXT_PLAIN).build();
			//			resRest = Response.serverError().build();
			return resRest;
		}
		String jsonToClient = "{\"ingress\":\""+ingressPoint.getDevice()+"\",\"ingressPort\":\""+ingressPoint.getPort()+"\",\"egress\":\""+egressPoint.getDevice()+"\",\"egressPort\":\""+egressPoint.getPort()+"\"}";
		System.out.println("*****INGRESS/EGRESS: "+jsonToClient);
		resRest = Response.ok(jsonToClient, MediaType.APPLICATION_JSON_TYPE).build();
		return resRest;
	}

	/**
	 * Add queue connection socket
	 * @param authString
	 * @param queueReq
	 * @param flowsNews
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static OnosResponse addQueueConnection(String authString, QueueClientRequest queueReq, List<Flow> flowsNews) throws IOException, ClassNotFoundException, SQLException {
		Gson gson = new Gson();
		OnosResponse onosResponse = new OnosResponse();
		List<Queue> queues = new ArrayList<Queue>();
		int connectionId = Utils.getConnectionIdAvailable();;
		int queueId = -1;
		int qosId = -1;

		//READ NEW FLOWS TO GET PATH TO INSTALL QUEUES
		Switch s = null;
		Port p = null;
		Queue queue = null;

		for(Flow f : flowsNews) {
			s = EntornoTools.entorno.getMapSwitches().get(f.getDeviceId());
			p = EntornoTools.getOutPortFromFlow(f);
			if(s != null && p != null) {
				//get next queue id
				queueId = Utils.getQueueIdAvailable();
				qosId = DatabaseTools.getQosIdBySwitchPort(s.getId(), p.getPortNumber());
				//If -1 -> no qosId in DDBB -> create new one
				if(qosId == -1) {
					qosId = Utils.getQosIdAvailable();
				}

				QueueOnosRequest queueOnosRequest = new QueueOnosRequest(p.getPortName(), 
						p.getPortNumber(), 
						String.format("%.0f", p.getSpeed()),
						queueId,
						String.valueOf(queueReq.getMinRate()),
						String.valueOf(queueReq.getMaxRate()),
						String.valueOf(queueReq.getBurst()),
						qosId);
				queue = new Queue(queueId,
						s.getId(),
						String.valueOf(queueReq.getMinRate()),
						String.valueOf(queueReq.getMaxRate()),
						String.valueOf(queueReq.getBurst()),
						Long.parseLong(String.valueOf(qosId)),
						p.getPortNumber(),
						p.getPortName());
				queues.add(queue);
				HttpTools.doJSONPost(new URL(EntornoTools.endpointQueues), gson.toJson(queueOnosRequest));
				//DDBB QUEUE ADD
				DatabaseTools.addQueue(authString, String.valueOf(queueId), s.getId(), String.valueOf(qosId), p.getPortName(), p.getPortNumber(), queueOnosRequest.getMinRate(), queueOnosRequest.getMaxRate(), queueOnosRequest.getBurst(), null, String.valueOf(connectionId));

				//overwrite flow with queueID
				onosResponse = EntornoTools.addQueueIdToFlowFlowWithPort(f, String.valueOf(queueId));

				//TODO:UPDATE FLOW QUEUE ID in DDBB
				DatabaseTools.updateFlowQueueId(f.getId(), queueId);
			}


		}
		onosResponse.setMessage(gson.toJson(queues));
		return onosResponse;

	}

	/**
	 * Add queue id to a flow
	 * @param f flow
	 * @param queueId queue id
	 * @return onos response
	 * @throws IOException
	 */
	private static OnosResponse addQueueIdToFlowFlowWithPort(Flow f, String queueId) throws IOException {
		OnosResponse response = null;
		String url = EntornoTools.endpoint+"/flows/"+f.getDeviceId();
		String body = "{\n" + 
				"  \"priority\": "+f.getPriority()+",\n" + 
				"  \"timeout\": "+f.getTimeout()+",\n" + 
				"  \"isPermanent\": "+f.isIsPermanent()+",\n" + 
				"  \"deviceId\": \""+f.getDeviceId()+",\n" +
				"  \"treatment\": {" +
				"\"instructions\": [";
		body += "{"
				+ "\"type\": \"QUEUE\",\n" + 
				"    \"queueId\": "+queueId+","
				+ "},";
		for(FlowInstruction instruction : f.getFlowTreatment().getListInstructions()) {
			body += "{"
					+ "\"type\": \""+instruction.getType()+"\","
					+ "\"OUTPUT\": \""+instruction.getInstructions().get("OUTPUT")+"\""
					+ "}";
		}

		body +=	"]" +
				"},";

		body +=	"\"selector\": {\n" + 
				"    \"criteria\": [";
		for(FlowCriteria criteria : f.getFlowSelector().getListFlowCriteria()) {
			body += "{"
					+ "\"type\": \""+criteria.getType()+"\","
					+ "\""+criteria.getCriteria().getKey()+"\": \""+criteria.getCriteria().getValue()+"\""
					+ "},";
		}

		//Delete last comma
		body = body.substring(0, body.length() - 2);
		body += "]}}";

		try {
			System.out.println("JSON FLUJO para queue hacia ONOS: \n"+body);
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;
	}

	/**
	 * Add queue connection
	 * @param authString
	 * @param queueReq
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static OnosResponse addQueueConnection(String authString, QueueClientRequest queueReq) throws IOException, ClassNotFoundException, SQLException {
		Gson gson = new Gson();
		OnosResponse onosResponse = new OnosResponse();
		List<Queue> queues = new ArrayList<Queue>();

		int connectionId = Utils.getConnectionIdAvailable();;
		int queueId = -1;
		int qosId = -1;

		//get old state
		Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
			for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
				if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
					oldFlowsState.put(flow.getKey(), flow.getValue());
		}
		//INTENT ADD
		FlowSocketClientRequest flowReq = queueReq.toFlowSocketClientRequest();
		EntornoTools.addIntent(authString, flowReq);
		//GET NEW STATE
		EntornoTools.getEnvironment();
		Map<String, Flow> newFlowsState = new HashMap<String, Flow>();
		for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
			for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
				if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
					newFlowsState.put(flow.getKey(), flow.getValue());
		// GET FLOWS CHANGED
		List<Flow> flowsNews;
		flowsNews = EntornoTools.compareFlows(oldFlowsState, newFlowsState);
		//READ NEW FLOWS TO GET PATH TO INSTALL QUEUES
		Switch s = null;
		Port p = null;
		Queue queue = null;

		for(Flow f : flowsNews) {
			s = EntornoTools.entorno.getMapSwitches().get(f.getDeviceId());
			p = EntornoTools.getOutPortFromFlow(f, queueReq.getSrcHost(), queueReq.getDstHost(), queueReq.getSrcPort(), queueReq.getDstPort(), queueReq.getPortType());
			if(s != null && p != null) {
				//get next queue id
				queueId = Utils.getQueueIdAvailable();
				qosId = DatabaseTools.getQosIdBySwitchPort(s.getId(), p.getPortNumber());
				//If -1 -> no qosId in DDBB -> create new one
				if(qosId == -1) {
					qosId = Utils.getQosIdAvailable();
				}

				QueueOnosRequest queueOnosRequest = new QueueOnosRequest(p.getPortName(), 
						p.getPortNumber(), 
						String.format("%.0f", p.getSpeed()),
						queueId,
						String.valueOf(queueReq.getMinRate()),
						String.valueOf(queueReq.getMaxRate()),
						String.valueOf(queueReq.getBurst()),
						qosId);
				queue = new Queue(queueId,
						s.getId(),
						String.valueOf(queueReq.getMinRate()),
						String.valueOf(queueReq.getMaxRate()),
						String.valueOf(queueReq.getBurst()),
						Long.parseLong(String.valueOf(qosId)),
						p.getPortNumber(),
						p.getPortName());
				queues.add(queue);
				HttpTools.doJSONPost(new URL(EntornoTools.endpointQueues), gson.toJson(queueOnosRequest));
				//DDBB QUEUE ADD
				DatabaseTools.addQueue(authString, String.valueOf(queueId), s.getId(), String.valueOf(qosId), p.getPortName(), p.getPortNumber(), queueOnosRequest.getMinRate(), queueOnosRequest.getMaxRate(), queueOnosRequest.getBurst(), null, String.valueOf(connectionId));



				//add flow queue
				onosResponse = EntornoTools.addQueueFlowWithPort(queueReq.getIpVersion(), s.getId(), p.getPortNumber(), String.valueOf(queueId),
						queueReq.getSrcHost(),
						queueReq.getSrcPort(),
						queueReq.getDstHost(),
						queueReq.getDstPort(),
						queueReq.getPortType());
			}

		}

		//GET NEW STATE
		EntornoTools.getEnvironment();
		Map<String, Flow> newFlowsState2 = new HashMap<String, Flow>();
		for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet())
			for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet()) 
				if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
					newFlowsState2.put(flow.getKey(), flow.getValue());


		System.out.println(".");

		// GET FLOWS CHANGED
		flowsNews = EntornoTools.compareFlows(newFlowsState, newFlowsState2);

		// ADD flows to DDBB
		if(flowsNews.size()>0) {
			for(Flow flow : flowsNews) {
				try {
					System.out.format("Añadiendo flujo a la bbdd: %s %s", flow.getId(), flow.getDeviceId());
					DatabaseTools.addFlow(flow, authString, null, null, String.valueOf(queueId));
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					//TODO: Delete flow from onos and send error to client

				}
			}
		}

		onosResponse.setMessage(gson.toJson(queues));
		return onosResponse;
	}

	/**
	 * Get output port from flow and socket
	 * @param f flow
	 * @param srcHost source ip
	 * @param dstHost destination ip
	 * @param srcPort source port
	 * @param dstPort destination port
	 * @param portType port type
	 * @return Port
	 * @throws IOException
	 */
	private static Port getOutPortFromFlow(Flow f, String srcHost, String dstHost, String srcPort, String dstPort, String portType) throws IOException {
		Port p = null;
		String portNumber = "";
		boolean src = false, dst = false;
		for(FlowCriteria criteria : f.getFlowSelector().getListFlowCriteria()) {
			if(criteria.getType().equals("IPV4_SRC")) {
				if(criteria.getCriteria().getValue().equals(srcHost+"/32"))
					src = true;
			}
			else if(criteria.getType().equals("IPV4_DST")) {
				if(criteria.getCriteria().getValue().equals(dstHost+"/32"))
					dst = true;
			}

		}
		for(FlowInstruction instruction : f.getFlowTreatment().getListInstructions()) {
			if(instruction.getType().equals("OUTPUT")){
				portNumber = instruction.getInstructions().get("port").toString();

			}
		}
		if(src && dst) {
			p = EntornoTools.entorno.getMapSwitches().get(f.getDeviceId()).getPortByNumber(portNumber);
			return p;
		}
		else {
			EntornoTools.addQueueFlowWithPort("4", f.getDeviceId(), portNumber, String.valueOf(DatabaseTools.getDefaultQueueIdBySwitchPort(f.getDeviceId(), portNumber)), dstHost, dstPort, srcHost, srcPort, portType);
		}
		return null;
	}

	/**
	 * Get port given flow
	 * @param f flow
	 * @return port
	 * @throws IOException
	 */
	private static Port getOutPortFromFlow(Flow f) throws IOException {
		Port p = null;
		String portNumber = "";

		for(FlowInstruction instruction : f.getFlowTreatment().getListInstructions()) {
			if(instruction.getType().equals("OUTPUT")){
				portNumber = instruction.getInstructions().get("port").toString();
				break;

			}
		}
		p = EntornoTools.entorno.getMapSwitches().get(f.getDeviceId()).getPortByNumber(portNumber);
		return p;
	}

	/**
	 * Delete queue given its queue id
	 * @param authString authorization http string
	 * @param qId queue id
	 * @return Response
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Response deleteQueue(String authString, String qId) throws MalformedURLException, IOException, ClassNotFoundException, SQLException {
		QueueDBResponse queueDb = DatabaseTools.getQueue(authString, qId);
		List<QueueDBResponse> queuesDb = null;
		String portName = "";
		String portNumber = "";
		String portSpeed = "";
		String queueId = qId;
		String minRate = "";
		String maxRate = "";
		String burst = "";
		String qosId = "";

		int nQueues = -1;
		if(queueDb != null) {
			queuesDb = DatabaseTools.getQueuesBySwitchPort(authString, queueDb.getIdSwitch(), queueDb.getPortNumber());
			if(queuesDb != null) {
				nQueues = queuesDb.size();
				if(nQueues > 1) {
					//TODO Normal delete
					Switch s = EntornoTools.entorno.getMapSwitches().get(queueDb.getIdSwitch());
					Port port = s.getPortByNumber(queueDb.getPortNumber());

					HttpTools.doDelete(new URL(EntornoTools.endpointQueues+"?"
							+ "portName="+queueDb.getPortName()+"&"
							+ "portNumber="+queueDb.getPortNumber()+"&"
							+ "portSpeed="+String.format("%.0f", port.getSpeed())+"&"
							+ "queueId="+queueDb.getIdQueue()+"&"
							+ "minRate="+queueDb.getMinRate()+"&"
							+ "maxRate="+queueDb.getMaxRate()+"&"
							+ "burst="+queueDb.getBurst()+"&"
							+ "qosId="+queueDb.getIdQos()
							));
					DatabaseTools.deleteQueue(authString, queueDb.getIdQueue());
				}
				else if(nQueues == 1) {
					// TODO Delete queue,qos and port
					Switch s = EntornoTools.entorno.getMapSwitches().get(queueDb.getIdSwitch());
					Port port = s.getPortByNumber(queueDb.getPortNumber());
					HttpTools.doDelete(new URL(EntornoTools.endpointQueues+"/port-qos?"
							+ "portName="+queueDb.getPortName()+"&"
							+ "portNumber="+queueDb.getPortNumber()+"&"
							+ "portSpeed="+String.valueOf(port.getSpeed())+"&"
							+ "queueId="+queueDb.getIdQueue()+"&"
							+ "minRate="+queueDb.getMinRate()+"&"
							+ "maxRate="+queueDb.getMaxRate()+"&"
							+ "burst="+queueDb.getBurst()+"&"
							+ "qosId="+queueDb.getIdQos()+"&"
							));
				}
				else {
					return Response.status(Response.Status.CONFLICT).entity("No queues in the port").build();
				}
				//DELETE FLOW WITH QUEUE ID
				Flow flow = EntornoTools.getFlowByQueueId(queueDb.getIdQueue());

				if(flow != null) {
					//DELETE FLOW ONOS
					EntornoTools.deleteFlow(flow.getId(), flow.getDeviceId());
					//DELETE FLOW DDBB
					DatabaseTools.deleteFlow(flow.getId(), authString);
				}
			}
			else 
				return Response.status(Response.Status.CONFLICT).entity("No queues in the port").build();
		}
		else
			return Response.status(Response.Status.CONFLICT).entity("Queue Not Found").build();
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	/**
	 * Delete flow gven its id and swtch id
	 * @param id flow id
	 * @param switchId switch id
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void deleteFlow(String id, String switchId) throws MalformedURLException, IOException {
		HttpTools.doDelete(new URL(EntornoTools.endpoint+"/flows/"+switchId+"/"+id));
	}

	/**
	 * Get flow by queue id
	 * @param idQueue
	 * @return flow
	 */
	private static Flow getFlowByQueueId(String idQueue) {
		for(Switch s : EntornoTools.entorno.getMapSwitches().values()) {
			for(Flow flow : s.getFlows().values()) {
				for(FlowInstruction instr : flow.getFlowTreatment().getListInstructions()) {
					if(instr.getType().equals("QUEUE")) {
						for(Map.Entry<String, Object> o : instr.getInstructions().entrySet()) {
							if(String.format("%.0f",o.getValue()).equals(idQueue)) {
								return flow;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Add default queues
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void addQueuesDefault() throws ClassNotFoundException, SQLException, IOException {
		int id = 0;
		Gson gson = new Gson();
		QueueOnosRequest queueOnosRequest = null;
		for(Switch s : EntornoTools.entorno.getMapSwitches().values()) {
			if(s.getDriver().equals("ovs")) {
				for(Port p : s.getListPorts()) {
					if(p.isEnabled()) {
						try {
							queueOnosRequest = new QueueOnosRequest(p.getPortName(), p.getPortNumber(), String.format("%.0f",p.getSpeed())+"000000", id, "0",
									String.format("%.0f",p.getSpeed())+"000000", String.format("%.0f",p.getSpeed())+"000000", id);
							HttpTools.doJSONPost(new URL(EntornoTools.endpointQueues+"/port-qos"), gson.toJson(queueOnosRequest));
							DatabaseTools.addQueue("Basic YWRtaW46YWRtaW4=", String.valueOf(queueOnosRequest.getQueueId()), s.getId(), String.valueOf(queueOnosRequest.getQosId()), queueOnosRequest.getPortName(), queueOnosRequest.getPortNumber(), queueOnosRequest.getMinRate(), queueOnosRequest.getMaxRate(), queueOnosRequest.getBurst(), null, null);
						} catch (ClassNotFoundException | SQLException | IOException e) {
							// TODO Auto-generated catch block
							throw e;
						}
						id++;
					}
				}
			}
		}


	}
}
