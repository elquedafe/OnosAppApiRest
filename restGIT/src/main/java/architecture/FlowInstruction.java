/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents a flow instruction
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class FlowInstruction {
	private String type;
	private Map<String,Object> instructions;

	/**
	 * Flow instruction constructor
	 * @param typeinstruction instruciton type
	 * @param instructions flow instructions
	 */
	public FlowInstruction(String type, Map<String, Object> instructions) {
		this.type = type;
		this.instructions = instructions;
	}

	/**
	 * Default flow criteria constructor
	 */
	public FlowInstruction() {
		this.type = "";
		this.instructions = new HashMap<String,Object>();
	}

	/**
	 * Get instruction type
	 * @return instruction type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set instruction type
	 * @param type instruction type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get instructions
	 * @return instructions map
	 */
	public Map<String, Object> getInstructions() {
		return instructions;
	}

	/**
	 * Set instructions
	 * @param instructions instructions map
	 */
	public void setInstructions(Map<String, Object> instructions) {
		this.instructions = instructions;
	}
}
