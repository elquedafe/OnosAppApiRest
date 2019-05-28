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
        System.out.println(json);
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
    
    public static List<Meter> getAllMeters() {
    	List<Meter> listMeters = new ArrayList<Meter>();
    	List<Band> listBands = new ArrayList<Band>();
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
	        	System.out.println("Puerto de salida de "+switchId+" encontrado: "+ port);
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
				"\"timeout\": 10,\r\n" + 
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
    		System.out.println("JSON FLUJO QOS: \n"+body+"\n"+switchId+"\n"+outPort+"\n"+meterId+"\n"+ip);
			String out = HttpTools.doJSONPost(new URL(url), body);
		} catch (MalformedURLException e) {
			respuestaOnos = "IO error";
		}
    	return "success";
    }
}
