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
import architecture.Host;
import architecture.Link;
import architecture.Meter;
import architecture.Port;
import architecture.Queue;
import architecture.Switch;
import architecture.Vpls;
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
 *
 * @author alvaroluismartinez
 */
@SuppressWarnings("rawtypes")
public class EntornoTools {
	public static String endpoint;
	public static String user;
	public static String password;
	public static String onosHost;
	public static String endpointNetConf;
	public static String endpointQueues;
	//private static ProxyPipe pipe;
	public static Environment entorno = new Environment();

	public static void getEnvironment() throws IOException{
		//String json = "";
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
	}

	/**
	 * Get Switches connected to host given its Ip
	 * @param hostIp
	 * @return
	 */
	public static List<Switch> getIngressSwitchesByHost(String hostIp) {
		Host host = null;
		Switch s = null;
		List<Switch> listSwitches = new ArrayList<Switch>();
		try {
			EntornoTools.getEnvironment();
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

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return listSwitches;

	}

	public static Switch getIngressSwitchByHost(String hostIp) {
		Host host = null;
		Switch s = null;
		LogTools.info("getIngressSwitchByHost", "host ip "+ hostIp);
		List<Switch> listSwitches = new ArrayList<Switch>();
		boolean found = false;
		try {
			EntornoTools.getEnvironment();
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

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		System.out.format("Switch ingress de la ip %s es %s", hostIp, listSwitches.get(0).getId());
		return listSwitches.get(0);
	}

	public static List<Meter> getAllMeters() throws IOException{
		List<Meter> listMeters = new ArrayList<Meter>();
		String url = EntornoTools.endpoint+"/meters/";
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}


		return listMeters;

	}

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
				//System.out.println("Puerto de salida de "+switchId+" encontrado: "+ port);
				listPorts.add(port);
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listPorts;
	}

	public static Host getHostByIp(String ip) {
		Host host = null;
		for(Host h : entorno.getMapHosts().values()) {
			if(h.getIpList().contains(ip))
				return h;
		}
		return host;

	}

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
			//System.out.println("JSON FLUJO QOS: \n"+body+"\n"+switchId+"\n"+outPort+"\n"+meterId+"\n"+ip);
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;
	}

	private static String getNetConfPorts() {
		// FOR EACH HOST GET switch/port connected and generate json por "port" in network/configuration
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

		//System.out.println(genJson);
		return genJson;
	}

	private static String getVplsStateJsonPostFormat() {
		String json = "{\"ports\":{";
		//PORTS def
		json += EntornoTools.getNetConfPorts();

		json += "},";
		return json;
	}

	@SuppressWarnings("rawtypes")
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

						//vplss.add(new VplsOnosRequestAux(name, listInterfaces));
						//genJson += "\"interfaces\": ";
						//genJson += listInterfaces.toString();
						//genJson += "},";

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
		//DELETE LAST COMMA
		/*if(genJson.endsWith(",")) {
			genJson = genJson.substring(0, genJson.length()-1);
		}*/

		//System.out.println("JSON to generate VPLS: \n"+genJson);
		return genJson;
	}


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
				//genJson += "\"interfaces\": ";
				//genJson += listInterfaces.toString();
				//genJson += "},";



			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
		//DELETE LAST COMMA
		/*if(genJson.endsWith(",")) {
			genJson = genJson.substring(0, genJson.length()-1);
		}*/
		//System.out.println("JSON to generate VPLS: \n"+genJson);
		return genJson;
	}

	public static String getVpls() {
		Gson gson = new Gson();
		String json = "";
		List<Vpls> vplsList = null;
		try {

			OnosResponse response = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
			LogTools.info("getVpls", "Json from ONOS: "+response.getMessage());
			vplsList = JsonManager.parseVpls(response.getMessage());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return gson.toJson(vplsList);
		} catch (IOException e) {
			e.printStackTrace();
			return gson.toJson(vplsList);
		}
		return gson.toJson(vplsList);
	}

	public static List<Vpls> getVplsList() {
		Gson gson = new Gson();
		String json = "";
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

	public static OnosResponse deleteVpls(String vplsName, String authString) throws IOException, ClassNotFoundException, SQLException{
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
				"				\"type\": \"OUTPUT\",\r\n" + 
				"				\"port\": \""+outPort+"\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"type\": \"QUEUE\",\r\n" + 
				"				\"queueId\":"+queueId+"\r\n" + 
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

	public static List<Flow> compareFlows(Map<String, Flow> oldFlowsState, Map<String, Flow> newFlowsState) {
		List<Flow> flows = new ArrayList<Flow>();
		for(Flow flow : newFlowsState.values()) {
			if(!oldFlowsState.containsKey(flow.getId())) {
				flows.add(flow);
			}
		}

		return flows;
	}

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

	//	public static String deleteVpls(String vplsName) throws IOException{
	//		String json = "";
	//		String response = "";
	//			json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
	//		//DELETE ALL VPLS
	//		HttpTools.doDelete(new URL(EntornoTools.endpointNetConf));
	//		
	//		//ADD ONLY NOT DELETED
	//		List<Vpls> vplss = JsonManager.parseoVpls(json);
	//		for(int i = 0; i < vplss.size(); i++) {
	//			if(!vplss.get(i).getName().equals(vplsName)) {
	//				json = EntornoTools.addVplsJson(vplss.get(i).getName(), vplss.get(i).getInterfaces());
	//				response = HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), json);
	//			}
	//		}
	//		return response;
	//	}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

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

						//NEW VPLS. If requested vpls name exists in onos, then replace interfaces for the new ones.
						//					if(reqVplsName.equals(name)) {
						//						sameName = true;
						//						listInterfaces.clear();
						//						listInterfaces.addAll(reqListInterfaces);
						//					}
						vplss.add(new Vpls(name, listInterfaces));

						//vplss.add(new VplsOnosRequestAux(name, listInterfaces));
						//genJson += "\"interfaces\": ";
						//genJson += listInterfaces.toString();
						//genJson += "},";

					}
					//				if(!sameName)
					//					vplss.add(new VplsOnosRequestAux(reqVplsName, reqListInterfaces));
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//DELETE LAST COMMA
		/*if(genJson.endsWith(",")) {
			genJson = genJson.substring(0, genJson.length()-1);
		}*/

		//System.out.println("JSON to generate VPLS: \n"+genJson);
		return vplss;
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Response addMeterAndFlow(String srcHost, String dstHost, String authString, MeterClientRequestPort meterReq) {
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
						//resRest = Response.serverError().build();
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
			resRest = Response.ok("{\"response\":\"URL error\", \"trace\":\"\", \"endpoint\":\""+EntornoTools.endpoint+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			return resRest;
		} catch (IOException e) {
			//resRest = Response.ok("{\"response\":\"IO error\", \"trace\":\""+jsonOut+"\"}", MediaType.APPLICATION_JSON_TYPE).build();
			resRest = Response.ok("IO: "+e.getMessage(), MediaType.TEXT_PLAIN).build();
			//resRest = Response.serverError().build();
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
						response = EntornoTools.addMeter(ingress.getId(), meterReq.getRate(), meterReq.getBurst());

						// Get meter after
						List<Meter> newMetersState = EntornoTools.getMeters(ingress.getId());

						//Compare old and new
						List<Meter> metersToAdd = EntornoTools.compareMeters(oldMetersState, newMetersState);
						
						//Add meter to DDBB
						for(Meter meter : metersToAdd) {
							try {
								System.out.println("Añadiendo meter a la bbdd: "+ meter.getDeviceId() + ":"+meter.getId());
								DatabaseTools.addMeter(meter, authString, vplsName);
								meterId = meter.getId();
							} catch (ClassNotFoundException | SQLException e) {
								
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
												//

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
						//resRest = Response.serverError().build();
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
							String.valueOf(queueOnos.getMinRate()),
							String.valueOf(queueOnos.getMaxRate()),
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

			//Wait for new state
			//				try {
			//					Thread.sleep(200);
			//				} catch (InterruptedException e1) {
			//					// TODO Auto-generated catch block
			//					e1.printStackTrace();
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

	public static OnosResponse addQueue(String authString, QueueClientRequest queueReq) throws IOException {
		Gson gson = new Gson();
		OnosResponse onosResponse = new OnosResponse();
		Host srcHost = EntornoTools.getHostByIp(queueReq.getSrcHost());
		Host dstHost = EntornoTools.getHostByIp(queueReq.getDstHost());
		Switch s = EntornoTools.getIngressSwitchByHost(queueReq.getSrcHost());
		String outputPort = EntornoTools.getOutputPort(queueReq.getSrcHost(), queueReq.getDstHost());
		Port port = s.getPortByNumber(outputPort);
		
		//QUEUE ADD
		int queueId = Utils.getQueueIdAvailable();
		int qosId = DatabaseTools.getQosIdBySwitchPort(s.getId(), outputPort);
		//If -1 -> no qosId in DDBB -> create new one
		if(qosId == -1) {
			qosId = Utils.getQosIdAvailable();
		}
		QueueOnosRequest queueOnosRequest = new QueueOnosRequest(port.getPortName(), 
				port.getPortNumber(), 
				String.valueOf(port.getSpeed()),
				queueId,
				String.valueOf(queueReq.getMinRate()),
				String.valueOf(queueReq.getMaxRate()),
				String.valueOf(queueReq.getBurst()),
				qosId);
		HttpTools.doJSONPost(new URL(EntornoTools.endpointQueues), gson.toJson(queueOnosRequest));
		//DDBB QUEUE ADD
		DatabaseTools.addQueue(authString, String.valueOf(queueId), s.getId(), String.valueOf(qosId), port.getPortName(), port.getPortNumber());
		
		//ADD FLOW QUEUE
			//get old state
		Map<String, Flow> oldFlowsState = new HashMap<String, Flow>();
		for(Map.Entry<String, Switch> auxSwitch : EntornoTools.entorno.getMapSwitches().entrySet()){
			for(Map.Entry<String, Flow> flow : auxSwitch.getValue().getFlows().entrySet())
				if(flow.getValue().getAppId().contains("fwd") || flow.getValue().getAppId().contains("intent"))
					oldFlowsState.put(flow.getKey(), flow.getValue());
		}
		
			//add flow queue
		onosResponse = EntornoTools.addQueueFlowWithPort(queueReq.getIpVersion(), s.getId(), outputPort, String.valueOf(queueId),
				queueReq.getSrcHost(),
				queueReq.getSrcPort(),
				queueReq.getDstHost(),
				queueReq.getDstPort(),
				queueReq.getPortType());
		
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
					DatabaseTools.addFlow(flow, authString, null, null, String.valueOf(queueId));
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					//TODO: Delete flow from onos and send error to client

				}
			}
		}
		
		return onosResponse;
	}

	public static Response deleteQueue(String authString, String qId) throws MalformedURLException, IOException {
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
						+ "portSpeed="+String.valueOf(port.getSpeed())+"&"
						+ "queueId="+queueDb.getIdQueue()+"&"
						+ "minRate="+queueDb.getMinRate()+"&"
						+ "maxRate="+queueDb.getMaxRate()+"&"
						+ "burst="+queueDb.getBurst()+"&"
						+ "qosId="+queueDb.getIdQos()+"&"
						));
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
			}
			else 
				return Response.status(Response.Status.CONFLICT).entity("No queues in the port").build();
		}
		else
			return Response.status(Response.Status.CONFLICT).entity("Queue Not Found").build();
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
