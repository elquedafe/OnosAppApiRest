/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
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
    public static String controlador;
    //private static ProxyPipe pipe;
    public static Environment entorno;
    public static JsonManager parser;
    
    public static void descubrirEntorno(Environment entorno, String usuario, String passwd, String controller, JsonManager parser) throws IOException{
        entorno = entorno;
        parser = parser;
        String json = "";
        user = usuario;
        password = passwd;
        controlador = controller;
        endpoint = "http://" + controlador + ":8181/onos/v1";
        URL urlClusters = new URL(endpoint + "/cluster");
        URL urlDevices = new URL(endpoint + "/devices");
        URL urlLinks = new URL(endpoint + "/links");
        URL urlFlows = new URL(endpoint + "/flows");
        URL urlHosts = new URL(endpoint + "/hosts");

        // CLUSTERS
        json = parser.getJSONGet(urlClusters, usuario, passwd);
//        parser.parseoJsonClusters(json);
        parser.parseoJsonClustersGson(json);
//        System.out.println(json);
//        System.out.println("***CLUSTERS CARGADOS***");

        // SWITCHES
        json = parser.getJSONGet(urlDevices, usuario, passwd);
        //
        //parser.parseoJsonDevices(json);
        parser.parseoJsonDevicesGson(json);
//        System.out.println(json);
//        System.out.println("\n***SWITCHES CARGADOS***");
        
        //PORTS
        for(Switch s : entorno.getMapSwitches().values()){
            json = parser.getJSONGet(new URL(endpoint+"/devices/"+s.getId()+"/ports"), usuario, passwd);
            //parser.parseoJsonPuertos(json);
            parser.parseoJsonPuertosGson(json);
            //System.out.println(json);
        }
        //System.out.println("\n***PUERTOS CARGADOS***");
        
        //LINKS
        json = parser.getJSONGet(urlLinks, usuario, passwd);
//        parser.parseoJsonLinks(json);
        parser.parseoJsonLinksGson(json);
//        System.out.println(json);
//        System.out.println("\n***ENLACES CARGADOS***");
        
        //FLOWS
        json = parser.getJSONGet(urlFlows, usuario, passwd);
//        parser.parseoJsonFlow(json);
        parser.parseoJsonFlowGson(json);
//        System.out.println(json);
//        System.out.println("\n***FLUJOS CARGADOS***");
//        
        //HOSTS
        json = parser.getJSONGet(urlHosts, usuario, passwd);
//        parser.parseoJsonHosts(json);
        parser.parseoJsonHostsGson(json);
        System.out.println(json);
//        System.out.println("\n***HOSTS CARGADOS***");
    
//      System.out.println("\n***TOPOLOGIA CARGADA***");
        
        
    }
    
    public static void actualizarGUILinks(Environment entorno, DefaultListModel<Link> modeloListaLinks, Map<String, Switch> sws) {
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
    
    public static void doJSONPost(URL url, String usuario, String password, String cuerpo) throws IOException{
        String encoding;
        String line;
        String json="";
        HttpURLConnection connection = null;
        OutputStreamWriter osw = null;
        System.out.println("**URL***"+url.getFile());
        try {
            encoding = Base64.getEncoder().encodeToString((usuario + ":"+ password).getBytes("UTF-8"));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            OutputStream os = connection.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");    
            osw.write(cuerpo);
            osw.flush();
            connection.getInputStream();
        } catch (IOException e) {
                throw new IOException(e);
        }
        finally{
            if(osw != null)
                osw.close();
            if(connection != null)
                connection.disconnect();
        }

    }
    
}
