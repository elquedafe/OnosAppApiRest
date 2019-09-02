/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alvaroluismartinez
 */
public class FlowTreatment {
    private List<FlowInstruction> flowInstructions;

    /**
     *
     * @param listInstructions
     */
    public FlowTreatment(List<FlowInstruction> listInstructions) {
        this.flowInstructions = listInstructions;
    }

    public FlowTreatment() {
        this.flowInstructions = new ArrayList<FlowInstruction>();
    }

    public List<FlowInstruction> getListInstructions() {
        return flowInstructions;
    }

    public void setListInstructions(List<FlowInstruction> listInstructions) {
        this.flowInstructions = listInstructions;
    }  
}
