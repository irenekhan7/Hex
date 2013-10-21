package com.teamhex.cooler.Storage.Classes;

/* PaletteRecord class
 * 
 * Stores the name, description, and list of colors for a color scheme.
 * 
 * Data is stored in a .txt file using the following format:
 * """
 * Description
 * ColorName1 ColorHex1 ColorPercentage1
 * ColorName2 ColorHex2 ColorPercentage2
 * ColorName3 ColorHex3 ColorPercentage3
 * """
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


import android.util.Log;

public class PaletteRecord implements Serializable {
	// Constructor
	// Required: String name, BufferedReader br
	public PaletteRecord() { Log.w("TeamHex", "A PaletteRecord is being created without a name or BufferedReader!"); }
	public PaletteRecord(String _name, BufferedReader br) throws IOException {
		// Name and description are easy
		name = _name;
		description = br.readLine();
		
		// Continuously read in new colors while possible
		colors = new ArrayList<ColorRecord>();
		String temp;
		String[] split;
		while((temp = br.readLine()) != null) {
			Log.i("TeamHex", "      Read line: " + temp);
			split = temp.split("\\s+");
			// Add a new ColorRecord:  name    , hex     , percentage
			colors.add(new ColorRecord(split[0], split[1], Float.parseFloat(split[2])));
		}
	}
	
	// Outputs the string to be used to save to a file
	public String getSaveString() {
		String saver = description;
		for(int i = 0, len = colors.size(); i < len; ++i) {
			saver += "\n" + colors.get(i).getSaveString();
		}
		return saver;
	}

	// Gets
	public String getDescription() 	  	 	  { return description; }
	public String getName() 	    		  { return name; }
	public ArrayList<ColorRecord> getColors() { return colors; 	}
	
	// Sets
	public void setName(String name) 			         { this.name = name; }
	public void setDescription(String description)  	 { this.description = description; }
	public void setColors(ArrayList<ColorRecord> colors) { this.colors = colors; }

	private String name;
	private String description;
	private ArrayList<ColorRecord> colors;
}
