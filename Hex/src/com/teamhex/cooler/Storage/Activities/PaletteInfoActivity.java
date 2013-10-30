package com.teamhex.cooler.Storage.Activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.ColorRecord;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

public class PaletteInfoActivity extends Activity {

	PaletteView paletteView;
	TextView nameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_info);
		
		paletteView = (PaletteView) findViewById(R.id.paletteEditView);
		nameView = (TextView) findViewById(R.id.paletteName);
		
		Intent i = getIntent();
        PaletteRecord palette = (PaletteRecord)i.getSerializableExtra("palette");
        setPaletteRecord(palette);
        
       
        Button editButton = (Button) findViewById(R.id.button_edit);
        editButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	System.out.println("EDITING\n");
                	
                	Intent i = new Intent(PaletteInfoActivity.this, PaletteEditActivity.class);
                    i.putExtra("palette", paletteRecord);
                    startActivity(i);
                }
            }
        );
	}
	
	PaletteRecord paletteRecord;
	String infoString;
	public void setPaletteRecord(PaletteRecord setting)
	{
		paletteRecord = setting;
		paletteView.setColorScheme(paletteRecord);
		
		ArrayList<ColorRecord> colors = setting.getColors();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(setting.getName());
		sb.append("\n");
		int max = colors.size();
		for(int i = 0; i < max; i++)
		{
			sb.append(colors.get(i).getName());
			sb.append("  ");
			sb.append(colors.get(i).getHex());
			sb.append("\n");
		}
		
		infoString = sb.toString();
		nameView.setText(infoString);
		setTitle(setting.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheme_info, menu);
		return true;
	}
	
	/*
	@Override
	public void onBackPressed(){
		
		  finish();
		  super.onBackPressed();
	}*/

}
