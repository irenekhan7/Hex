package com.teamhex.cooler.Storage.Classes;

import java.io.Serializable;

import android.graphics.Color;

public class ColorRecord implements Serializable {
	// Constructor
	// Required: String hex
	public ColorRecord(String _hex) {
		name = "No Name";
		hex  = _hex;
		percentage = 0;
	}
	// Required: Integer hex
	public ColorRecord(Integer _value) {
		name      = "No Name";
		
		int r = Color.red(_value);
		int g = Color.green(_value);
		int b = Color.blue(_value);
		hex        = String.format("#%2o%2o%2o", r, g, b);
		
		percentage = 0;
	}
	// Required: String name, String hex, Float percentage
	public ColorRecord(String _name, String _hex, float _percentage) {
		name       = _name;
		hex        = _hex;
		percentage = _percentage;
	}
	// Required: String name, Integer hex, Float percentage
	public ColorRecord(String _name, int _value, float _percentage) {
		name       = _name;
		
		int r = Color.red(_value);
		int g = Color.green(_value);
		int b = Color.blue(_value);
		hex        = String.format("#%2o%2o%2o", r, g, b);
		
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
