/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package architecture;


import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

/**
 * Represents a flow criteria
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class FlowCriteria {
	private String type;
	private SimpleEntry<String,String> criteria;

	/**
	 * Flow criteria constructor
	 * @param type criteria type
	 * @param criteria criteria
	 */
	public FlowCriteria(String type, SimpleEntry<String,String> criteria) {
		this.type = type;
		this.criteria = criteria;
	}

	/**
	 * Get criteria type
	 * @return criteria type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set criteria type
	 * @param type criteria type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get criteria
	 * @return criteria
	 */
	public Map.Entry<String,String> getCriteria() {
		return criteria;
	}

	/**
	 * Set criteria
	 * @param criteria criteria
	 */
	public void setCriteria(SimpleEntry<String,String> criteria) {
		this.criteria = criteria;
	}


}
