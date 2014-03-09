package com.teamhex.colorbird;

import java.util.Locale;
//import android.util.Log;

public class X11Helper {
	public X11Helper() {
		num_values = rgb_values.length;
	}
	
	// Important public function: given a string such as "#FFCC00", 
	// get the equivalent color name from names
	public static String getColorName(String hex) {
		num_values = rgb_values.length;
		// Make sure the hex is in the format of "FFCC00"
    	hex = hex.replaceAll("[^a-zA-Z0-9]", "").toUpperCase(Locale.ENGLISH);
		//Log.i("TeamHex", "Getting Color Name for #" + hex);
		
		// Start the search, assuming the first is the best
		Integer[] input_rgb = getColorRGB(hex);
		int loc_best = 0,
			val_best = getColorDistance(rgb_values[0], input_rgb),
			val_check, i;
		
		// Loop through the rest of rgb_values, finding the closest match
		for(i = 1; i < num_values; ++i) {
			val_check = getColorDistance(rgb_values[i], input_rgb);
			if(val_check < val_best) {
				//Log.i("TeamHex", "Distance between " + hex + " and " + names[i] + " is " + Integer.toString(val_check));
				loc_best = i;
				val_best = val_check;
			}
		}
		
		return names[loc_best];
	}
	
	// Returns the RGB equivalent of a hex color
	public static Integer[] getColorRGB(String hex) {
		
		return new Integer[] {
	        Integer.valueOf( hex.substring(0, 2), 16),
	        Integer.valueOf( hex.substring(2, 4), 16),
	        Integer.valueOf( hex.substring(4, 6), 16)				
		};
	}
	
	// Functions to get color distance between two colors, in hex and/or RGB format
	// Distance is calculated as the (sum of the squares of the (differences in (R,G,B) values) )
	public int getColorDistance(String a, String b) { return getColorDistance(getColorRGB(a), getColorRGB(b)); }
	public int getColorDistance(String a, Integer[] b) { return getColorDistance(getColorRGB(a), b); }
	public int getColorDistance(Integer[] a, String b) { return getColorDistance(a, getColorRGB(b)); }
	public static int getColorDistance(Integer[] a, Integer[] b) {
		return (int) (Math.pow(Math.abs(a[0] - b[0]), 2) +
					  Math.pow(Math.abs(a[1] - b[1]), 2) +
					  Math.pow(Math.abs(a[2] - b[2]), 2)  );
	}
	
	// RGB values are stored by the size-3 integer arrays, for quick lookups
	// Javascript code to convert "FFCC00" to "[255, 204, 0]":
	/*
		function strToHex(str) {
			return rgbToHex(str.substr(0,2), str.substr(2,2), str.substr(4,2));
		}
		function rgbToHex(R,G,B) {
			return toHex(R) +", " + toHex(G) +", " + toHex(B);
		}
		function toHex(n) {
			 n = parseInt(n,16);
			 if (isNaN(n)) return "0";
			 n = Math.max(0,Math.min(n,255));
			 return n;
		}
		console.log("toHex of gold is", strToHex("FFCC00"));
	 */
	
	private static int num_values; // made in constructor
	private static final Integer[][] rgb_values = {
		{0, 0, 128},
		{0, 0, 139},
		{0, 0, 205},
		{0, 0, 255},
		{0, 100, 0},
		{0, 128, 0},
		{0, 128, 128},
		{0, 139, 139},
		{0, 191, 255},
		{0, 206, 209},
		{0, 250, 154},
		{0, 255, 0},
		{0, 255, 0},
		{0, 255, 127},
		{0, 255, 255},
		{0, 255, 255},
		{25, 25, 112},
		{30, 144, 255},
		{32, 178, 170},
		{34, 139, 34},
		{46, 139, 87},
		{47, 79, 79},
		{50, 205, 50},
		{60, 179, 113},
		{64, 224, 208},
		{65, 105, 225},
		{70, 130, 180},
		{72, 61, 139},
		{72, 209, 204},
		{75, 0, 130},
		{85, 107, 47},
		{95, 158, 160},
		{100, 149, 237},
		{102, 205, 170},
		{105, 105, 105},
		{106, 90, 205},
		{107, 142, 35},
		{112, 128, 144},
		{119, 136, 153},
		{123, 104, 238},
		{124, 252, 0},
		{127, 0, 0},
		{127, 0, 127},
		{127, 255, 0},
		{127, 255, 212},
		{128, 128, 0},
		{128, 128, 128},
		{135, 206, 235},
		{135, 206, 250},
		{138, 43, 226},
		{139, 0, 0},
		{139, 0, 139},
		{139, 69, 19},
		{143, 188, 143},
		{144, 238, 144},
		{147, 112, 219},
		{148, 0, 211},
		{152, 251, 152},
		{153, 50, 204},
		{154, 205, 50},
		{160, 32, 240},
		{160, 82, 45},
		{165, 42, 42},
		{169, 169, 169},
		{173, 216, 230},
		{173, 255, 47},
		{175, 238, 238},
		{176, 48, 96},
		{176, 196, 222},
		{176, 224, 230},
		{178, 34, 34},
		{184, 134, 11},
		{186, 85, 211},
		{188, 143, 143},
		{189, 183, 107},
		{190, 190, 190},
		{192, 192, 192},
		{199, 21, 133},
		{205, 92, 92},
		{205, 133, 63},
		{210, 105, 30},
		{210, 180, 140},
		{211, 211, 211},
		{216, 191, 216},
		{218, 112, 214},
		{218, 165, 32},
		{219, 112, 147},
		{220, 20, 60},
		{220, 220, 220},
		{221, 160, 221},
		{222, 184, 135},
		{224, 255, 255},
		{230, 230, 250},
		{233, 150, 122},
		{238, 130, 238},
		{238, 232, 170},
		{240, 128, 128},
		{240, 230, 140},
		{240, 248, 255},
		{240, 255, 240},
		{240, 255, 255},
		{244, 164, 96},
		{245, 222, 179},
		{245, 245, 220},
		{245, 245, 245},
		{245, 255, 250},
		{248, 248, 255},
		{250, 128, 114},
		{250, 235, 215},
		{250, 240, 230},
		{250, 250, 210},
		{253, 245, 230},
		{255, 0, 0},
		{255, 0, 255},
		{255, 0, 255},
		{255, 20, 147},
		{255, 69, 0},
		{255, 99, 71},
		{255, 105, 180},
		{255, 127, 80},
		{255, 140, 0},
		{255, 160, 122},
		{255, 165, 0},
		{255, 182, 193},
		{255, 192, 203},
		{255, 215, 0},
		{255, 218, 185},
		{255, 222, 173},
		{255, 228, 181},
		{255, 228, 196},
		{255, 228, 225},
		{255, 235, 205},
		{255, 239, 213},
		{255, 240, 245},
		{255, 245, 238},
		{255, 248, 220},
		{255, 250, 205},
		{255, 250, 240},
		{255, 250, 250},
		{255, 255, 0},
		{255, 255, 224},
		{255, 255, 240},
		{255, 255, 255}
	};
	
	private static final String[] names = {
	  "Navy",
	  "Dark Blue",
	  "Medium Blue",
	  "Blue",
	  "Dark Green",
	  "Green",
	  "Teal",
	  "Dark Cyan",
	  "Deep Sky Blue",
	  "Dark Turquoise",
	  "Medium Spring Green",
	  "Green",
	  "Lime",
	  "Spring Green",
	  "Aqua",
	  "Cyan",
	  "Midnight Blue",
	  "Dodger Blue",
	  "Light Sea Green",
	  "Forest Green",
	  "Sea Green",
	  "Dark Slate Gray",
	  "Lime Green",
	  "Medium Sea Green",
	  "Turquoise",
	  "Royal Blue",
	  "Steel Blue",
	  "Dark Slate Blue",
	  "Medium Turquoise",
	  "Indigo",
	  "Dark Olive Green",
	  "Cadet Blue",
	  "Cornflower",
	  "Medium Aquamarine",
	  "Dim Gray",
	  "Slate Blue",
	  "Olive Drab",
	  "Slate Gray",
	  "Light Slate Gray",
	  "Medium Slate Blue",
	  "Lawn Green",
	  "Maroon",
	  "Purple ",
	  "Chartreuse",
	  "Aquamarine",
	  "Olive",
	  "Gray ",
	  "Sky Blue",
	  "Light Sky Blue",
	  "Blue Violet",
	  "Dark Red",
	  "Dark Magenta",
	  "Saddle Brown",
	  "Dark Sea Green",
	  "Light Green",
	  "Medium Purple",
	  "Dark Violet",
	  "Pale Green",
	  "Dark Orchid",
	  "Yellow Green",
	  "Purple ",
	  "Sienna",
	  "Brown",
	  "Dark Gray",
	  "Light Blue",
	  "Green Yellow",
	  "Pale Turquoise",
	  "Maroon",
	  "Light Steel Blue",
	  "Powder Blue",
	  "Firebrick",
	  "Dark Goldenrod",
	  "Medium Orchid",
	  "Rosy Brown",
	  "Dark Khaki",
	  "Gray",
	  "Silver",
	  "Medium Violet Red",
	  "Indian Red",
	  "Peru",
	  "Chocolate",
	  "Tan",
	  "Light Gray",
	  "Thistle",
	  "Orchid",
	  "Goldenrod",
	  "Pale Violet Red",
	  "Crimson",
	  "Gainsboro",
	  "Plum",
	  "Burlywood",
	  "Light Cyan",
	  "Lavender",
	  "Dark Salmon",
	  "Violet",
	  "Pale Goldenrod",
	  "Light Coral",
	  "Khaki",
	  "Alice Blue",
	  "Honeydew",
	  "Azure",
	  "Sandy Brown",
	  "Wheat",
	  "Beige",
	  "White Smoke",
	  "Mint Cream",
	  "Ghost White",
	  "Salmon",
	  "Antique White",
	  "Linen",
	  "Light Goldenrod",
	  "Old Lace",
	  "Red",
	  "Fuchsia",
	  "Magenta",
	  "Deep Pink",
	  "Orange Red",
	  "Tomato",
	  "Hot Pink",
	  "Coral",
	  "Dark Orange",
	  "Light Salmon",
	  "Orange",
	  "Light Pink",
	  "Pink",
	  "Gold",
	  "Peach Puff",
	  "Navajo White",
	  "Moccasin",
	  "Bisque",
	  "Misty Rose",
	  "Blanched Almond",
	  "Papaya Whip",
	  "Lavender Blush",
	  "Seashell",
	  "Cornsilk",
	  "Lemon Chiffon",
	  "Floral White",
	  "Snow",
	  "Yellow",
	  "Light Yellow",
	  "Ivory",
	  "White"
	};
}
