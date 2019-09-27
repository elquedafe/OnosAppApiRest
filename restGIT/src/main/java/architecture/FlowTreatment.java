/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents the flow treatment
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class FlowTreatment {
	private List<FlowInstruction> flowInstructions;

	/**
	 * Flow treatment constructor
	 * @param listInstructions instructions list
	 */
	public FlowTreatment(List<FlowInstruction> listInstructions) {
		this.flowInstructions = listInstructions;
	}

	/**
	 * Default flow treatment constructor
	 */
	public FlowTreatment() {
		this.flowInstructions = new ArrayList<FlowInstruction>();
	}

	/**
	 * Get instructions list
	 * @return instructions list
	 */
	public List<FlowInstruction> getListInstructions() {
		return flowInstructions;
	}

	/**
	 * Set instructions list
	 * @param listInstructions instructions list
	 */
	public void setListInstructions(List<FlowInstruction> listInstructions) {
		this.flowInstructions = listInstructions;
	}  
}
