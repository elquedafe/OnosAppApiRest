/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alvaroluismartinez
 */
public class FlowInstruction {
    private String type;
    private Map<String,String> instructions;

    public FlowInstruction(String type, Map<String, String> instructions) {
        this.type = type;
        this.instructions = instructions;
    }

    public FlowInstruction() {
        this.type = "";
        this.instructions = new HashMap<String,String>();
    }
    
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getInstructions() {
        return instructions;
    }

    public void setInstructions(Map<String, String> instructions) {
        this.instructions = instructions;
    }
}
