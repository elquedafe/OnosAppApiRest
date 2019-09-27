package rest.gsonobjects.onosside;

import java.util.List;

/**
 * Represents a VPLS request to ONOS
 * @author Alvaro Luis Martinez
 * @version 1.0
 */
public class VplsOnosRequestAux {
	private String name;
	private List<String> interfaces;
	private String encapsulation;
	/**
	 * @param name
	 * @param interfaces
	 */
	public VplsOnosRequestAux(String name, List<String> interfaces) {
		super();
		this.name = name;
		this.interfaces = interfaces;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the interfaces
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}
	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}
	

}
