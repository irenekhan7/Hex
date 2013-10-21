package com.example.android.skeletonapp;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class PaletteInfo extends Activity {

	PaletteView paletteView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_info);
		
		paletteView = (PaletteView) findViewById(R.id.paletteView1);
		
		Intent i = getIntent();
        Palette palette = (Palette)i.getSerializableExtra("palette");
        setColorPalette(palette);
	}
	
	Palette colorPalette;
	public void setColorPalette(Palette setting)
	{
		colorPalette = setting;
		paletteView.setColorPalette(colorPalette);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.palette_info, menu);
		return true;
	}
	
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}

}
