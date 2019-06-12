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
import rest.VplsOnosRequestAux;

/**
 *
 * @author alvaroluismartinez
 */
public class EntornoTools {
    public static String endpoint;
    public static String user;
    public static String password;
    public static String onosHost;
    public static String endpointNetConf;
    //private static ProxyPipe pipe;
    public static Environment entorno = new Environment();
    
    public static void descubrirEntorno() throws IOException{
        String json = "";
        URL urlClusters = new URL(endpoint + "/cluster");
        URL urlDevices = new URL(endpoint + "/devices");
        URL urlLinks = new URL(endpoint + "/links");
        URL urlFlows = new URL(endpoint + "/flows");
        URL urlHosts = new URL(endpoint + "/hosts");

        // CLUSTERS
        json = HttpTools.doJSONGet(urlClusters);
//        parser.parseoJsonClusters(json);
        JsonManager.parseoJsonClustersGson(json);
//        System.out.println(json);
//        System.out.println("***CLUSTERS CARGADOS***");

        // SWITCHES
        json = HttpTools.doJSONGet(urlDevices);
        //
        //parser.parseoJsonDevices(json);
        JsonManager.parseoJsonDevicesGson(json);
//        System.out.println(json);
//        System.out.println("\n***SWITCHES CARGADOS***");
        
        //PORTS
        for(Switch s : entorno.getMapSwitches().values()){
            json = HttpTools.doJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"));
            //parser.parseoJsonPuertos(json);
            JsonManager.parseoJsonPuertosGson(json);
            //System.out.println(json);
        }
        //System.out.println("\n***PUERTOS CARGADOS***");
        
        //LINKS
        json = HttpTools.doJSONGet(urlLinks);
//        parser.parseoJsonLinks(json);
        JsonManager.parseoJsonLinksGson(json);
//        System.out.println(json);
//        System.out.println("\n***ENLACES CARGADOS***");
        
        //FLOWS
        json = HttpTools.doJSONGet(urlFlows);
//        parser.parseoJsonFlow(json);
        JsonManager.parseoJsonFlowGson(json);
//        System.out.println(json);
//        System.out.println("\n***FLUJOS CARGADOS***");
//        
        //HOSTS
        json = HttpTools.doJSONGet(urlHosts);
//        parser.parseoJsonHosts(json);
        JsonManager.parseoJsonHostsGson(json);
        //System.out.println(json);
//        System.out.println("\n***HOSTS CARGADOS***");
    
//      System.out.println("\n***TOPOLOGIA CARGADA***");
        
        
    }
    
    public static void actualizarGUILinks(DefaultListModel<Link> modeloListaLinks, Map<String, Switch> sws) {
        List<Link> l = null;
        modeloListaLinks.clear();
        cargarAllLinks(entorno, modeloListaLinks);
//        for(Switch s : sws.values()){
//            for (Link link : s.getListLinks()){
//                //eliminarDuplicado(sws.values(), link, modeloListaLinks);
//                    modeloListaLinks.addElement(link);
//            }
//        }
    }
    
    private static boolean eliminarDuplicado(Collection<Switch> sws, Link nuevoLink, DefaultListModel<Link> modeloListaLinks){
        boolean b = false;
        int i = 0;
        for(Switch s : sws){
            for(Link link : s.getListLinks()){
                if(link.getDst().equals(nuevoLink.getDst()) && link.getDstPort().equals(nuevoLink.getDstPort()) && link.getSrc().equals(nuevoLink.getSrc()) && link.getSrcPort().equals(nuevoLink.getSrcPort())) {
                        b = true;
                        i++;
                        //modeloListaLinks.addElement(link);
                }
                else{
                    if(i > 1)
                        modeloListaLinks.addElement(link);
                }
            }
            
        }
        return b;
    }
    
    public static void actualizarGUIFlows(DefaultListModel<Flow> modeloListaFlows, Collection<Switch> values) {
        modeloListaFlows.clear();
        for (Switch auxswitch : values) { 

                //listaFlows.setListData(new Vector(auxswitch.getMapFlows().values()));
                for(Flow flow : auxswitch.getMapFlows().values()) {
                        //listaFlows.setListData(flow);
                        modeloListaFlows.addElement(flow);
                }
        }

    }

    public static void actualizarGUIFlowsTable(JTable table, Collection<Switch> values) {
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        for (Switch auxswitch : values) { 
            for(Flow flow : auxswitch.getMapFlows().values()) {
                Object[] array = flow.toArray();
                ((DefaultTableModel)table.getModel()).addRow(array);
            }
        }
    }
    
    public static void actualizarBoxSwitches(Environment entorno, JComboBox box){        
        for(Switch s : entorno.getMapSwitches().values()){
            if(s.getAvailable()){
                box.removeItem(s.getId());
                box.addItem(s.getId());
            }
        }
    }

    public static void actualizarGUIFlowsTableSwitch(JTable table, Switch s) {
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        for(Flow flow : s.getMapFlows().values()) {
            Object[] array = flow.toArray();
            ((DefaultTableModel)table.getModel()).addRow(array);
        }
    }
    
    
    
    private static void cargarAllLinks(Environment entorno, DefaultListModel<Link> modelo){
        for(Switch s : entorno.getMapSwitches().values()){
            for(Link l : s.getListLinks()){
                modelo.addElement(l);
                if(duplicado(modelo, l))
                    modelo.removeElement(l);
            }
        }
    }
    
    private static boolean duplicado(DefaultListModel<Link> modelo, Link l){
        boolean b = false;
        Link auxLink = null;
        for(int i = 0; i < modelo.getSize(); i++){
            auxLink = modelo.get(i);
            if(auxLink.getSrc().equals(l.getDst()) && auxLink.getDst().equals(l.getSrc()) && auxLink.getSrcPort().equals(l.getDstPort()) && auxLink.getDstPort().equals(l.getSrcPort())){
                return true;
            }
            
        }
        return b;
            
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
			EntornoTools.descubrirEntorno();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return listSwitches;
		
	}
    
    public static List<Meter> getAllMeters() throws IOException{
    	List<Meter> listMeters = new ArrayList<Meter>();
    	String url = EntornoTools.endpoint+"/meters/";
    	try {
			String json = HttpTools.doJSONGet(new URL(url));
			Gson gson = new Gson();
			LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
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
			String json = HttpTools.doJSONGet(new URL(url));
			Gson gson = new Gson();
			LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
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
    
    public static String addMeter(String switchId, int rate, int burst) throws IOException{
    	String url = EntornoTools.endpoint + "/meters/"+switchId;
    	String onosResponse = "";
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
		//System.out.println(jsonOut);
		
		try {
			onosResponse = HttpTools.doJSONPost(new URL(url), jsonOut);
		} catch (MalformedURLException e) {
			onosResponse = "URL Error";
		}
		
		return onosResponse;
    }
    
    public static List<String> getOutputPorts(String switchId) {
    	Gson gson;
    	List<String> listPorts = new ArrayList<String>();
		String url = EntornoTools.endpoint + "/links?device="+switchId+"&direction=EGRESS";
		try {
			String json = HttpTools.doJSONGet(new URL(url));
			gson = new Gson();
			
			LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
	        ArrayList links = (ArrayList)jsonObject.get("links");
	        for(Object l : links) {
	        	LinkedTreeMap mapLink = (LinkedTreeMap)l;
	        	LinkedTreeMap src = (LinkedTreeMap)mapLink.get("src");
	        	String port = (String)src.get("port");
	        	//System.out.println("Puerto de salida de "+switchId+" encontrado: "+ port);
	        	listPorts.add(port);
	        }
	        
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listPorts;
    }
    
    public static Host getHostByIp(String ip) {
    	Host host = null;
    	for(Host h : entorno.getMapHosts().values()) {
			if(h.getIpList().contains(ip))
				host = h;
		}
    	return host;
    	
    }
    
    public static String addQosFlow(String switchId, String outPort, int meterId, String ip) throws IOException {
    	String respuestaOnos = "";
    	String url = EntornoTools.endpoint+"/flows/"+switchId;
    	String body = "{\"priority\": 1500,\r\n" + 
				"\"timeout\": 60,\r\n" + 
				"\"isPermanent\": false,\r\n" + 
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
			String out = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			respuestaOnos = "IO error";
		}
    	return "success";
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
			String json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
			
			LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			String json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
			
			LinkedTreeMap jsonObject = gson.fromJson(json, LinkedTreeMap.class);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String json = "";
		List<Vpls> vplsList = null;
		try {
			
			json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
			LogTools.info("getVpls", "Json from ONOS: "+json);
			vplsList = JsonManager.parseoVpls(json);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return gson.toJson(vplsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return gson.toJson(vplsList);
		}
		return gson.toJson(vplsList);
	}
	
	public static String deleteVpls(String vplsName) throws IOException{
		String json = "";
		String response = "";
			json = HttpTools.doJSONGet(new URL(EntornoTools.endpointNetConf));
		//DELETE ALL VPLS
		HttpTools.doDelete(new URL(EntornoTools.endpointNetConf));
		
		//ADD ONLY NOT DELETED
		List<Vpls> vplss = JsonManager.parseoVpls(json);
		for(int i = 0; i < vplss.size(); i++) {
			if(!vplss.get(i).getName().equals(vplsName)) {
				json = EntornoTools.addVplsJson(vplss.get(i).getName(), vplss.get(i).getInterfaces());
				response = HttpTools.doJSONPost(new URL(EntornoTools.endpointNetConf), json);
			}
		}
		return response;
	}
}
