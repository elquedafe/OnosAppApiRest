/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents the flow selector
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class FlowSelector {
	private List<FlowCriteria> flowCriterias;

	/**
	 * Flow selector constructor
	 * @param listFlowCriteria flow criterias
	 */
	public FlowSelector(List<FlowCriteria> listFlowCriteria) {
		this.flowCriterias = listFlowCriteria;
	}

	/**
	 * Default flow selector
	 */
	public FlowSelector() {
		this.flowCriterias = new ArrayList<FlowCriteria>();
	}

	/**
	 * Get criteria list
	 * @return criteria list
	 */
	public List<FlowCriteria> getListFlowCriteria() {
		return flowCriterias;
	}

	/**
	 * Set criteria list
	 * @param listFlowCriteria criteria list
	 */
	public void setListFlowCriteria(List<FlowCriteria> listFlowCriteria) {
		this.flowCriterias = listFlowCriteria;
	}



}
