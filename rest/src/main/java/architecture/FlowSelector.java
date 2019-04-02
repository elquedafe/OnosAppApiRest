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
public class FlowSelector {
    private List<FlowCriteria> listFlowCriteria;

    public FlowSelector(List<FlowCriteria> listFlowCriteria) {
        this.listFlowCriteria = listFlowCriteria;
    }

    public FlowSelector() {
        this.listFlowCriteria = new ArrayList<FlowCriteria>();
    }
    
    

    public List<FlowCriteria> getListFlowCriteria() {
        return listFlowCriteria;
    }

    public void setListFlowCriteria(List<FlowCriteria> listFlowCriteria) {
        this.listFlowCriteria = listFlowCriteria;
    }
    
    
    
}
