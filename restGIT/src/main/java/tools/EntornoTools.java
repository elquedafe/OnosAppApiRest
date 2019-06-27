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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
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

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import architecture.Band;
import architecture.Environment;
import architecture.Flow;
import architecture.Host;
import architecture.Link;
import architecture.Meter;
import architecture.Switch;
import architecture.Vpls;
import rest.gsonobjects.onosside.OnosResponse;
import rest.gsonobjects.onosside.VplsOnosRequestAux;

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
				String point = entry.getKey()+"/"+entry.getValue();
				genJson += "\""+point+"\":{"
						+ "\"interfaces\":["
						+ "{\"name\":\""+h.getIpList().get(i)+"\"}]},";
				i++;
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

	public static OnosResponse deleteVpls(String vplsName) throws IOException{
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

	public static OnosResponse addQosFlowWithPort(String switchId, String outPort, int meterId, String ip, int clientPort, String portType) throws IOException {
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
				"			},\r\n";
		if(portType.equalsIgnoreCase("tcp")){
		body += "			{\n" + 
				"				\"type\": \"IP_PROTO\",\n" + 
				"				\"protocol\": 6\n" + 
				"			},\n"+
				"			{\n" + 
				"				\"type\": \"TCP_SRC\",\n" + 
				"				\"tcpPort\": \""+clientPort+"\"\n" + 
				"			}"+
				"		]\r\n" + 
				"	}\r\n" + 
				"}";
		}
		else if(portType.equalsIgnoreCase("tcp")){
			body += "			{\n" + 
					"				\"type\": \"IP_PROTO\",\n" + 
					"				\"protocol\":11\n" + 
					"			},\n"+
					"			{\n" + 
					"				\"type\": \"UDP_SRC\",\n" + 
					"				\"udpPort\": \""+clientPort+"\"\n" + 
					"			}"+
					"		]\r\n" + 
					"	}\r\n" + 
					"}";
		}
		try {
			//System.out.println("JSON FLUJO QOS: \n"+body+"\n"+switchId+"\n"+outPort+"\n"+meterId+"\n"+ip);
			response = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			response = new OnosResponse("URL error", 404);
		}
		return response;
		
	}

	public static List<Flow> compareFlows(String switchId, Environment oldEnv, Environment newEnv) {
		List<Flow> flows = new ArrayList<Flow>();
		
		for(Flow flow : newEnv.getMapSwitches().get(switchId).getFlows().values()) {
			if(!oldEnv.getMapSwitches().get(switchId).getFlows().containsKey(flow.getId())) {
				flows.add(flow);
			}
		}
		
		return flows;
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
}
