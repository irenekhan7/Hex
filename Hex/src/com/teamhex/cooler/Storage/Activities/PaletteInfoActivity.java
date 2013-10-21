package com.teamhex.cooler.Storage.Activities;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.R.id;
import com.teamhex.cooler.R.layout;
import com.teamhex.cooler.R.menu;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

public class PaletteInfoActivity extends Activity {

	PaletteView paletteView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheme_info);
		
		paletteView = (PaletteView) findViewById(R.id.schemeView1);
		
		Intent i = getIntent();
        PaletteRecord palette = (PaletteRecord)i.getSerializableExtra("palette");
        setPaletteRecord(palette);
	}
	
	PaletteRecord paletteRecord;
	public void setPaletteRecord(PaletteRecord setting)
	{
		paletteRecord = setting;
		paletteView.setColorScheme(paletteRecord);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheme_info, menu);
		return true;
	}
	
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}

}
