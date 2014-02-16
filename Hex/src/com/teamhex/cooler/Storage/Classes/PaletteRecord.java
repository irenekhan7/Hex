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

import com.teamhex.cooler.X11Helper;

import android.util.Log;

public class PaletteRecord implements Serializable {
	// Serial ID to stop ADT from complaining
	private static final long serialVersionUID = -8073525651268285421L;

	// Default Constructor
	public PaletteRecord() {
		this("No Name");
	}

	public PaletteRecord(String _name) {
		name = NameFilter(_name);
		description = "";
		colors = new ArrayList<ColorRecord>();
	}

	// Required: String name, BufferedReader br
	public PaletteRecord(String _name, BufferedReader br) throws IOException {
		// Name and description are easy
		name = NameFilter(_name);
		description = br.readLine();

		// Continuously read in new colors while possible
		colors = new ArrayList<ColorRecord>();
		String temp;
		String[] split;
		while ((temp = br.readLine()) != null) {
			Log.i("TeamHex", "      Read line: " + temp);
			split = temp.split("\\s+");
			// Add a new ColorRecord: name , hex , percentage
			colors.add(new ColorRecord(split[0], split[1], Float
					.parseFloat(split[2])));
		}
	}

	// Outputs the string to be used to save to a file
	public String getSaveString() {
		String saver = description;
		for (int i = 0, len = colors.size(); i < len; ++i) {
			saver += "\n" + colors.get(i).getSaveString();
		}
		return saver;
	}

	// Takes in an X11Helper and uses it to 'guess' the ColorRecord names
	public void setX11Names() {
		for (int i = 0, len = colors.size(); i < len; ++i) {
			colors.get(i).setX11Name();
		}
	}

	// Gets
	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public ArrayList<ColorRecord> getColors() {
		return colors;
	}

	// Sets
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setColors(ArrayList<ColorRecord> colors) {
		this.colors = colors;
	}

	// Adds
	public void addColor(ColorRecord color) {
		colors.add(color);
	}

	public void addColor(Integer num) {
		colors.add(new ColorRecord(num));
	}

	public void addColors(ColorRecord[] adders) {
		for (int i = 0, len = adders.length; i < len; ++i)
			colors.add(adders[i]);
	}

	public void addColors(Integer[] adders) {
		for (int i = 0, len = adders.length; i < len; ++i)
			colors.add(new ColorRecord(adders[i]));
	}

	// Replaces out any invalid characters that could mess the OS up
	public String NameFilter(String _name) {
		return _name.replace("[ &]", "_");
	}

	private String name;
	private String description;
	private ArrayList<ColorRecord> colors;
}
