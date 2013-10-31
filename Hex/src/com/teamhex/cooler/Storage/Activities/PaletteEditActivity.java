package com.teamhex.cooler.Storage.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teamhex.cooler.PaletteView;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;

public class PaletteEditActivity extends Activity {

	PaletteView paletteView;
	EditText nameEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palette_edit);
		
		paletteView = (PaletteView) findViewById(R.id.paletteEditView);
		nameEdit = (EditText) findViewById(R.id.editName);
		
		Intent i = getIntent();
        PaletteRecord palette = (PaletteRecord)i.getSerializableExtra("palette");
        setPaletteRecord(palette);
        
       
        Button editButton = (Button) findViewById(R.id.button_save);
        editButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	System.out.println("DONE EDITING\n");
                	paletteRecord.setName(nameEdit.getText().toString()); //Doesn't do anything because it's editing an object loaded from a serialized object. 
                	finish();
                }
            }
        );
	}
	
	PaletteRecord paletteRecord;
	public void setPaletteRecord(PaletteRecord setting)
	{
		paletteRecord = setting;
		paletteView.setColorScheme(paletteRecord);
		
		nameEdit.setText(setting.getName());
		setTitle(setting.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheme_info, menu);
		return true;
	}
	

}
