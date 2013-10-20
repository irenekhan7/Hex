package com.example.android.skeletonapp;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SchemeInfo extends Activity {

	SchemeView schemeView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheme_info);
		
		schemeView = (SchemeView) findViewById(R.id.schemeView1);
		
		Intent i = getIntent();
        Scheme scheme = (Scheme)i.getSerializableExtra("scheme");
        setColorScheme(scheme);
	}
	
	Scheme colorScheme;
	public void setColorScheme(Scheme setting)
	{
		colorScheme = setting;
		schemeView.setColorScheme(colorScheme);
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
