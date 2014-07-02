/*
 * Copyright (C) 2011 Sony Ericsson Mobile Communications AB.
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
 *
 */
package jp.nita.NowPlayingMusicExtension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ExtensionActivity extends TabActivity implements OnClickListener {
	
	public static final int PICKUP_SEND_TO_APP = 1;
	
	String template1;
	String template2;
	String template3;
	
	String title;
	String artist;
	String album;
	String duration;
	String composer;
	String year;
	String trackno;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.apply_template).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        
        TabHost tabHost = getTabHost();

		TabSpec tab1 = tabHost.newTabSpec(getString(R.string.share_text));
		tab1.setIndicator(getString(R.string.share_text),getResources().getDrawable(drawable.ic_menu_edit));
		tab1.setContent(R.id.tab1);
		tabHost.addTab(tab1);

		TabSpec tab2 = tabHost.newTabSpec(getString(R.string.share_music_file));
		tab2.setIndicator(getString(R.string.share_music_file),getResources().getDrawable(drawable.ic_menu_upload));
		tab2.setContent(R.id.tab2);
		tabHost.addTab(tab2);

		TabSpec tab3 = tabHost.newTabSpec(getString(R.string.song_information));
		tab3.setIndicator(getString(R.string.song_information),getResources().getDrawable(drawable.ic_menu_info_details));
		tab3.setContent(R.id.tab3);
		tabHost.addTab(tab3);

		tabHost.setCurrentTab(0);

        Intent intent = getIntent();

        // Retrieve the URI from the intent, this is a URI to a MediaStore audio
        // file
        Uri trackUri = intent.getData();

        // Use it to query the media provider
        Cursor trackCursor = getContentResolver().query(
                trackUri,
                new String[] {
                		MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.TRACK
                }, null, null, null);

        if (trackCursor != null) {
            try {
                if (trackCursor.moveToFirst()) {
                	Time time = new Time();
					time.set(trackCursor.getLong(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
					duration=time.format("%M:%S");
                	
                	try{
						title=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					}catch (IllegalArgumentException e){
						title="";
					}
					try{
						artist=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					}catch (IllegalArgumentException e){
						artist="";
					}
					try{
						album=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
					}catch (IllegalArgumentException e){
						album="";
					}
					try{
						composer=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER));
					}finally{
						if(composer==null) composer="";
					}
					try{
						year=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
					}finally{
						if(year==null) year="";
					}
					try{
						trackno=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
					}finally{
						if(trackno==null) trackno="";
						else{
							int src;
							try{
								src=Integer.parseInt(trackno);
							}catch(Exception e){
								src=0;
							}
							int disc=src/1000;
							int trk=src%1000;
							if(disc>0) trackno=""+trk+" ("+getString(R.string.disc)+" "+disc+")";
							else trackno=""+trk;
						}
					}

					updatePreferencesValues();
					updateDestinationSpinner();
					updateInformationListView();
					applyDefault();
                }
            } finally {
                trackCursor.close();
            }
        }

    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	Intent intent=new Intent(this,SettingsActivity.class);
	        startActivity(intent);
	    	break;
	    }
	    return true;
	}
	
	public void updatePreferencesValues(){
		SharedPreferences pref=getSharedPreferences(Statics.PREF_KEY,Activity.MODE_PRIVATE);
		template1=pref.getString(Statics.KEY_TEXT_1,getString(R.string.content_default));
		template2=pref.getString(Statics.KEY_TEXT_2,getString(R.string.content_default_2));
		template3=pref.getString(Statics.KEY_TEXT_3,getString(R.string.content_default_3));
	}
	
	public void applyDefault(){
		updatePreferencesValues();
		String content=template1;
		content=applyTemplate(content);
		((TextView)findViewById(R.id.content)).setText(content);
	}
	
	public String applyTemplate(String param){
		param=param.replace("$t",title);
		param=param.replace("$a",artist);
		param=param.replace("$l",album);
		param=param.replace("$d",duration);
		//param=param.replace("$c",composer);
		param=param.replace("$y",year);
		return param;
	}

	@Override
	public void onClick(View v) {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		if(v==(View)findViewById(R.id.send)){
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.content)).getText().toString());
				startActivityForResult(intent,PICKUP_SEND_TO_APP);
			} catch (Exception e) {
				Log.d("ExampleExtensionActivity", "Error");
				e.printStackTrace();
			}
		}
		if(v==(View)findViewById(R.id.apply_template)){
			CharSequence list[]=new String[4];
			updatePreferencesValues();
			list[0]="1: "+applyTemplate(template1);
			list[1]="2: "+applyTemplate(template2);
			list[2]="3: "+applyTemplate(template3);
			list[3]=getString(R.string.settings);
			new AlertDialog.Builder(ExtensionActivity.this)
			.setTitle(getString(R.string.apply_template))
			.setItems(list,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch(arg1){
					case 0:
						((TextView)findViewById(R.id.content)).setText(applyTemplate(template1));
						break;
					case 1:
						((TextView)findViewById(R.id.content)).setText(applyTemplate(template2));
						break;
					case 2:
						((TextView)findViewById(R.id.content)).setText(applyTemplate(template3));
						break;
					case 3:
						Intent intent=new Intent(ExtensionActivity.this,SettingsActivity.class);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						break;
					}
				}
			}).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==PICKUP_SEND_TO_APP){
			// if(quitAfterSharing) finish();
		}
	}
	
	public void updateInformationListView(){
		ListView items=(ListView)findViewById(R.id.information);
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		{
			Map<String,String> map;

			map=new HashMap<String,String>();
			map.put("key", getString(R.string.title));
			map.put("value", title);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.artist));
			map.put("value", artist);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.album));
			map.put("value", album);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.duration));
			map.put("value", duration);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.year));
			map.put("value", year);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.track_no));
			map.put("value", trackno);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.composer));
			map.put("value", composer);
			list.add(map);
			
		}
		SimpleAdapter adapter
		=new SimpleAdapter(this,list
				,android.R.layout.simple_expandable_list_item_2,
				new String[]{"key","value"},
				new int[]{android.R.id.text1,android.R.id.text2});
		items.setAdapter(adapter);

		// items.setOnItemClickListener(this);
	}
	
	public void updateDestinationSpinner(){
		Spinner items=(Spinner)findViewById(R.id.destination);
		List<String> list=new ArrayList<String>();
		{
			list.add(getString(R.string.twitter));
			list.add(getString(R.string.facebook));
			list.add(getString(R.string.other_app));
		}
		String array[]=list.toArray(new String[1]);
		ArrayAdapter<String> adapter
		=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,array);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		items.setAdapter(adapter);

		// items.setOnItemClickListener(this);
	}

}
