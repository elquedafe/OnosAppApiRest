/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

/**
 *
 * @author alvaroluismartinez
 */
public class FlowCriteria {
    private String type;
    private SimpleEntry<String,String> criteria;

    public FlowCriteria(String type, SimpleEntry<String,String> criteria) {
        this.type = type;
        this.criteria = criteria;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map.Entry<String,String> getCriteria() {
        return criteria;
    }

    public void setCriteria(SimpleEntry<String,String> criteria) {
        this.criteria = criteria;
    }

     
}
