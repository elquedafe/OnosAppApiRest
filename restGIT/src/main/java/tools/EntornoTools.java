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

import com.google.gson.JsonParser;

import architecture.Environment;
import architecture.Flow;
import architecture.Link;
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
        json = JsonManager.getJSONGet(urlClusters, user, password);
//        parser.parseoJsonClusters(json);
        JsonManager.parseoJsonClustersGson(json);
//        System.out.println(json);
//        System.out.println("***CLUSTERS CARGADOS***");

        // SWITCHES
        json = JsonManager.getJSONGet(urlDevices, user, password);
        //
        //parser.parseoJsonDevices(json);
        JsonManager.parseoJsonDevicesGson(json);
//        System.out.println(json);
//        System.out.println("\n***SWITCHES CARGADOS***");
        
        //PORTS
        for(Switch s : entorno.getMapSwitches().values()){
            json = JsonManager.getJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"), user, password);
            //parser.parseoJsonPuertos(json);
            JsonManager.parseoJsonPuertosGson(json);
            //System.out.println(json);
        }
        //System.out.println("\n***PUERTOS CARGADOS***");
        
        //LINKS
        json = JsonManager.getJSONGet(urlLinks, user, password);
//        parser.parseoJsonLinks(json);
        JsonManager.parseoJsonLinksGson(json);
//        System.out.println(json);
//        System.out.println("\n***ENLACES CARGADOS***");
        
        //FLOWS
        json = JsonManager.getJSONGet(urlFlows, user, password);
//        parser.parseoJsonFlow(json);
        JsonManager.parseoJsonFlowGson(json);
//        System.out.println(json);
//        System.out.println("\n***FLUJOS CARGADOS***");
//        
        //HOSTS
        json = JsonManager.getJSONGet(urlHosts, user, password);
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
    
    public static String doJSONPost(URL url, String body) throws IOException{
        String encoding;
        String line;
        String response="";
        HttpURLConnection connection = null;
        OutputStreamWriter osw = null;
        System.out.println("**URL***"+url.getFile());
        BufferedReader in = null;
        BufferedReader inError = null;
        try {
            encoding = Base64.getEncoder().encodeToString((user + ":"+ password).getBytes("UTF-8"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            OutputStream os = connection.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");    
            osw.write(body);
            osw.flush();
            InputStream content = (InputStream)connection.getInputStream();
            in = new BufferedReader (new InputStreamReader (content));
            while ((line = in.readLine()) != null) {
                response += line+"\n";
            }
            
            InputStream contentError = (InputStream)connection.getErrorStream();
            inError = new BufferedReader (new InputStreamReader (content));
            while ((line = inError.readLine()) != null) {
                response += line+"\n";
            }
        } catch (IOException e) {
                throw new IOException(e);
        }
        finally{
            if(osw != null)
                osw.close();
            if(connection != null)
                connection.disconnect();
            if(in != null)
            	in.close();
            if(inError != null)
            	inError.close();
        }
        return response;
    }
    
}
