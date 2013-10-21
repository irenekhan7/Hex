package com.teamhex.cooler.Storage.Classes;

import java.io.Serializable;

public class ColorRecord implements Serializable {
	// Constructor
	// Required: String name, String hex, Float percentage
	public ColorRecord(String _name, String _hex, float _percentage) {
		name       = _name;
		hex        = _hex;
		percentage = _percentage;
	}
	
	// Colors are stored in the format "name hex percentage"
	public String getSaveString() {
		return name + " " + hex + " " + Float.toString(percentage);
	}
	
	// Gets
	public String getName()       { return name; }
	public String getHex()        { return hex; }
	public float  getPercentage() { return percentage; }
	
	// Sets
	public void setName(String name)            { this.name = name; }
	public void setPercentage(float percentage) { this.percentage = percentage; }
	public void setHex(String hex)              { this.hex = hex; }

	private String name;
	private String hex;
	private float  percentage;
}
