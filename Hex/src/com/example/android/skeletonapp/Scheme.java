package com.example.android.skeletonapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Palette implements Serializable{

	//I wonder if serializing a list can cause performance problems?
	private List<Integer> colors;
	private String name;
	
	public Palette()
	{
		colors = new ArrayList<Integer>();
	}
	
	//Adds a color to the display
    public void addColor(int color)
    {
    	colors.add(color);
    }
    
    public List<Integer> getColors()
    {
    	return colors;
    }
    
    public void setName(String setTo)
    {
    	name = setTo;
    }
    public String getName()
    {
    	return name;
    }
    
}
