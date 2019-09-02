package architecture;

import java.util.List;

public class Vpls {
	private String name;
	private List<String> interfaces;
	private String encapsulation;
	
	
	
	/**
	 * @param name
	 * @param interfaces
	 */
	public Vpls(String name, List<String> interfaces) {
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
	/**
	 * @return the encapsulation
	 */
	public String getEncapsulation() {
		return encapsulation;
	}
	/**
	 * @param encapsulation the encapsulation to set
	 */
	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}
	
	
}
