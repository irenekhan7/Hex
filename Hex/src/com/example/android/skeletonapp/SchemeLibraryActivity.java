/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.skeletonapp;


import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import android.graphics.Color;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class SchemeLibraryActivity extends Activity {
    
    //static final private int BACK_ID = Menu.FIRST;
  //  static final private int CLEAR_ID = Menu.FIRST + 1;

  //  private EditText mEditor;
   // private Camera camera;
    
    public SchemeLibraryActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.scheme_list_activity);

        
        Random rnd = new Random();
        
       
        Scheme[] myColorArray = new Scheme[10]; 
        
        ///////TEST myColorArray is filled with set names and randomly generated test colors. Should instead be loaded in from saved user schemes.
        String[] myStringArray = new String[]{"Red", "Blue", "Skyline", "Garden", "Living Room", "Vacation", "Olympics", "Sunset1"
		,"Shades of Magenta", "Buildings in Troy", "Tree Outside"};
        for(int i = 0; i < 10; i++){
        	myColorArray[i] = new Scheme();
        	myColorArray[i].setName(myStringArray[i]);
        	for(int j = 0; j < 5; j++){
        		myColorArray[i].addColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        	}
        }
        //////END TEST
        
        ColorListAdapter adapter = new ColorListAdapter(this,
                R.layout.listcolor, myColorArray);
        ListView listView = (ListView) findViewById(R.id.schemeList);
        listView.setAdapter(adapter);
        
     // Create a message handling object as an anonymous class.
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	SchemeView s = (SchemeView) v.findViewById(R.id.schemeView1);
                Intent i = new Intent(SchemeLibraryActivity.this, SchemeInfo.class);
                i.putExtra("scheme", s.getColorScheme());
                startActivity(i);
            }
        };

        listView.setOnItemClickListener(mMessageClickedHandler); 
        
        // Find the text editor view inside the layout, because we
        // want to do various programmatic things with it.
       // mEditor = (EditText) findViewById(R.id.editor);

        // Hook up button presses to the appropriate event handler.
        //((Button) findViewById(R.id.back)).setOnClickListener(mBackListener);
      //  ((Button) findViewById(R.id.clear)).setOnClickListener(mClearListener);
        
       // SurfaceView mySurface = (SurfaceView)findViewById(R.id.camPreview);
       // SurfaceHolder holder = mySurface.getHolder();
       // if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
         //   holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
       // try
       // {
       // 	camera.setPreviewDisplay(holder);
       // }
       // catch (IOException e){
        	
       // }
       // 
        //camera.startPreview();

       // camera.stopPreview();
      //  mEditor.setText(getText(R.string.main_label));
    }


}
