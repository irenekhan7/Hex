package com.teamhex.cooler;

import java.util.HashMap;
import java.util.Locale;
import android.util.Log;

public class X11Helper {
	// By default, the constructor doesn't load the map (lazy loading)
	public X11Helper() { loaded = false; }
	// However, if a boolean is passed as true, it will
	public X11Helper(Boolean _load) {
		if(loaded = _load)
			Load();
	}
	
	// Manually load the names
	public void Load() {
		cache = new HashMap<String, String>();
    	names = new HashMap<String, String>() {
    		private static final long serialVersionUID = 1L; // so ADT stops complaining
		{
    		put("#FFB6C1", "LightPink");
    		put("#FFC0CB", "Pink");
    		put("#DC143C", "Crimson");
    		put("#FFF0F5", "LavenderBlush");
    		put("#DB7093", "PaleVioletRed");
    		put("#FF69B4", "HotPink");
    		put("#FF1493", "DeepPink");
    		put("#C71585", "MediumVioletRed");
    		put("#DA70D6", "Orchid");
    		put("#D8BFD8", "Thistle");
    		put("#DDA0DD", "Plum");
    		put("#EE82EE", "Violet");
    		put("#FF00FF", "Magenta");
    		put("#FF00FF", "Fuchsia");
    		put("#8B008B", "DarkMagenta");
    		put("#800080", "Purple");
    		put("#BA55D3", "MediumOrchid");
    		put("#9400D3", "DarkViolet");
    		put("#9932CC", "DarkOrchid");
    		put("#4B0082", "Indigo");
    		put("#8A2BE2", "BlueViolet");
    		put("#9370DB", "MediumPurple");
    		put("#7B68EE", "MediumSlateBlue");
    		put("#6A5ACD", "SlateBlue");
    		put("#483D8B", "DarkSlateBlue");
    		put("#E6E6FA", "Lavender");
    		put("#F8F8FF", "GhostWhite");
    		put("#0000FF", "Blue");
    		put("#0000CD", "MediumBlue");
    		put("#191970", "MidnightBlue");
    		put("#00008B", "DarkBlue");
    		put("#000080", "Navy");
    		put("#4169E1", "RoyalBlue");
    		put("#6495ED", "CornflowerBlue");
    		put("#B0C4DE", "LightSteelBlue");
    		put("#778899", "LightSlateGray");
    		put("#708090", "SlateGray");
    		put("#1E90FF", "DodgerBlue");
    		put("#F0F8FF", "AliceBlue");
    		put("#4682B4", "SteelBlue");
    		put("#87CEFA", "LightSkyBlue");
    		put("#87CEEB", "SkyBlue");
    		put("#00BFFF", "DeepSkyBlue");
    		put("#ADD8E6", "LightBlue");
    		put("#B0E0E6", "PowderBlue");
    		put("#5F9EA0", "CadetBlue");
    		put("#F0FFFF", "Azure");
    		put("#E0FFFF", "LightCyan");
    		put("#AFEEEE", "PaleTurquoise");
    		put("#00FFFF", "Cyan");
    		put("#00FFFF", "Aqua");
    		put("#00CED1", "DarkTurquoise");
    		put("#2F4F4F", "DarkSlateGray");
    		put("#008B8B", "DarkCyan");
    		put("#008080", "Teal");
    		put("#48D1CC", "MediumTurquoise");
    		put("#20B2AA", "LightSeaGreen");
    		put("#40E0D0", "Turquoise");
    		put("#7FFFD4", "Aquamarine");
    		put("#66CDAA", "MediumAquamarine");
    		put("#00FA9A", "MediumSpringGreen");
    		put("#F5FFFA", "MintCream");
    		put("#00FF7F", "SpringGreen");
    		put("#3CB371", "MediumSeaGreen");
    		put("#2E8B57", "SeaGreen");
    		put("#F0FFF0", "Honeydew");
    		put("#90EE90", "LightGreen");
    		put("#98FB98", "PaleGreen");
    		put("#8FBC8F", "DarkSeaGreen");
    		put("#32CD32", "LimeGreen");
    		put("#00FF00", "Lime");
    		put("#228B22", "ForestGreen");
    		put("#008000", "Green");
    		put("#006400", "DarkGreen");
    		put("#7FFF00", "Chartreuse");
    		put("#7CFC00", "LawnGreen");
    		put("#ADFF2F", "GreenYellow");
    		put("#556B2F", "DarkOliveGreen");
    		put("#9ACD32", "YellowGreen");
    		put("#6B8E23", "OliveDrab");
    		put("#F5F5DC", "Beige");
    		put("#FAFAD2", "LightGoldenrodYellow");
    		put("#FFFFF0", "Ivory");
    		put("#FFFFE0", "LightYellow");
    		put("#FFFF00", "Yellow");
    		put("#808000", "Olive");
    		put("#BDB76B", "DarkKhaki");
    		put("#FFFACD", "LemonChiffon");
    		put("#EEE8AA", "PaleGoldenrod");
    		put("#F0E68C", "Khaki");
    		put("#FFD700", "Gold");
    		put("#FFF8DC", "Cornsilk");
    		put("#DAA520", "Goldenrod");
    		put("#B8860B", "DarkGoldenrod");
    		put("#FFFAF0", "FloralWhite");
    		put("#FDF5E6", "OldLace");
    		put("#F5DEB3", "Wheat");
    		put("#FFE4B5", "Moccasin");
    		put("#FFA500", "Orange");
    		put("#FFEFD5", "PapayaWhip");
    		put("#FFEBCD", "BlanchedAlmond");
    		put("#FFDEAD", "NavajoWhite");
    		put("#FAEBD7", "AntiqueWhite");
    		put("#D2B48C", "Tan");
    		put("#DEB887", "BurlyWood");
    		put("#FFE4C4", "Bisque");
    		put("#FF8C00", "DarkOrange");
    		put("#FAF0E6", "Linen");
    		put("#CD853F", "Peru");
    		put("#FFDAB9", "PeachPuff");
    		put("#F4A460", "SandyBrown");
    		put("#D2691E", "Chocolate");
    		put("#8B4513", "SaddleBrown");
    		put("#FFF5EE", "Seashell");
    		put("#A0522D", "Sienna");
    		put("#FFA07A", "LightSalmon");
    		put("#FF7F50", "Coral");
    		put("#FF4500", "OrangeRed");
    		put("#E9967A", "DarkSalmon");
    		put("#FF6347", "Tomato");
    		put("#FFE4E1", "MistyRose");
    		put("#FA8072", "Salmon");
    		put("#FFFAFA", "Snow");
    		put("#F08080", "LightCoral");
    		put("#BC8F8F", "RosyBrown");
    		put("#CD5C5C", "IndianRed");
    		put("#FF0000", "Red");
    		put("#A52A2A", "Brown");
    		put("#B22222", "FireBrick");
    		put("#8B0000", "DarkRed");
    		put("#800000", "Maroon");
    		put("#FFFFFF", "White");
    		put("#F5F5F5", "WhiteSmoke");
    		put("#DCDCDC", "Gainsboro");
    		put("#D3D3D3", "LightGrey");
    		put("#C0C0C0", "Silver");
    		put("#A9A9A9", "DarkGray");
    		put("#808080", "Gray");
    		put("#696969", "DimGray");
    		put("#000000", "Black");
    	}};
		loaded = true;
	}
	
	public String getColorName(String hex) {
		// Make sure the hex is in the format of "FFCC00"
    	hex = hex.replaceAll("[^a-zA-Z0-9]", "").toUpperCase(Locale.ENGLISH);
		Log.i("TeamHex", "Getting Color Name for #" + hex);
		
		// If it isn't yet loaded, do that
		if(!loaded) {
			Log.i("TeamHex", "   X11Helper isn't yet loaded: loading now.");
			Load();
		}
		
		// If the color is already in the hash table, just return that
		if(names.containsKey(hex)) {
			Log.i("TeamHex", "   Names table already contains #" + hex + ", returning " + names.get(hex));
			return names.get(hex);
		}
		
		// Otherwise loop through everything, keeping track of and then returning the best one
		String key_best = "unknown";
		Integer dist_best = 9001,
				dist_curr;
		// Get the RGB values of the current hex
		Integer[] key_values = getColorRGB(hex);
		for(String key_curr : names.keySet()) {
			// Compare to each X11 color:
			dist_curr = getColorDistance(key_values, key_curr);
			// If this is better than before, it's the new best
			if(dist_curr < dist_best) {
				key_best = key_curr;
				dist_best = dist_curr;
			}
		}
		
		// Now that the key with the smallest distance is known, cache and return that
		String result = names.get(key_best);
		cache.put(key_best, result);
		return result;
	}
	
	// Returns the RGB equivalent of a hex color
	public Integer[] getColorRGB(String hex) {
		return new Integer[] {
	        Integer.valueOf( hex.substring( 1, 3 ), 16 ),
	        Integer.valueOf( hex.substring( 3, 5 ), 16 ),
	        Integer.valueOf( hex.substring( 5, 7 ), 16 )				
		};
	}
	
	// Functions to get color distance between two colors, in hex and/or RGB format
	public int getColorDistance(String a, String b) { return getColorDistance(getColorRGB(a), getColorRGB(b)); }
	public int getColorDistance(String a, Integer[] b) { return getColorDistance(getColorRGB(a), b); }
	public int getColorDistance(Integer[] a, String b) { return getColorDistance(a, getColorRGB(b)); }
	public int getColorDistance(Integer[] a, Integer[] b) {
		return Math.abs(a[0] - b[0]) +
			   Math.abs(a[1] - b[1]) +
			   Math.abs(a[2] - b[2]);
	}
	
	private Boolean loaded; 				// Lazy loading checker
	private HashMap<String, String> names;  // Table of X11 color names as { "FFCC00": "Gold" }
	private HashMap<String, String> cache;  // Cache of getColorName lookups as { "FFCC07": "Gold" }
}
