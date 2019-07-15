/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alvaroluismartinez
 */
public class FlowInstruction {
    private String type;
    private Map<String,Object> instructions;

    public FlowInstruction(String type, Map<String, Object> instructions) {
        this.type = type;
        this.instructions = instructions;
    }

    public FlowInstruction() {
        this.type = "";
        this.instructions = new HashMap<String,Object>();
    }
    
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getInstructions() {
        return instructions;
    }

    public void setInstructions(Map<String, Object> instructions) {
        this.instructions = instructions;
    }
}
