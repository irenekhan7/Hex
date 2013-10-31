package com.teamhex.cooler.Storage.Classes;

import java.io.Serializable;

import com.teamhex.cooler.X11Helper;

import android.graphics.Color;
import android.util.Log;

public class ColorRecord implements Serializable {
	// Serial ID to stop ADT from complaining
	private static final long serialVersionUID = -7643562291502389977L;
	
	// Constructor: String hex
	public ColorRecord(String _hex) {
		name = "No_Name";
		hex  = _hex;
		percentage = 0;
	}
	// Constructor: Integer hex
	public ColorRecord(Integer _value)  {
		name      = "No_Name";

		int r = Color.red(_value);
		int g = Color.green(_value);
		int b = Color.blue(_value);
		hex        = String.format("#%02x%02x%02x", r, g, b);
		
		percentage = 0;
	}
	// Constructor: String name, String hex, Float percentage
	public ColorRecord(String _name, String _hex, float _percentage) {
		name       = _name;
		hex        = _hex;
		percentage = _percentage;
	}
	// Constructor: String name, Integer hex, Float percentage
	public ColorRecord(String _name, int _value, float _percentage) {
		name       = _name;
		
		int r = Color.red(_value);
		int g = Color.green(_value);
		int b = Color.blue(_value);
		hex        = String.format("#%02x%02x%02x", r, g, b);

		percentage = _percentage;
	}
	
	// Colors are stored in the format "name hex percentage"
	public String getSaveString() {
		return name.replace(" ", "_") + " " + hex + " " + Float.toString(percentage);
	}
	
	// Takes in an X11Helper and uses it to 'guess' the appropriate name
	public void setX11Name(X11Helper helper) {
		this.name = helper.getColorName(this.hex);
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
