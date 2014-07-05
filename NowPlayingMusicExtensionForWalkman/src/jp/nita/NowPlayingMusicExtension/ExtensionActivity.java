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
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ExtensionActivity extends TabActivity implements OnClickListener, OnItemClickListener {
	final static Handler handler = new Handler();
	
	public static final int PICKUP_SEND_TO_APP = 1;
	
	String template1;
	String template2;
	String template3;
	int quitAfterSharing;
	
	String title;
	String artist;
	String album;
	String duration;
	String composer;
	String year;
	String trackno;
	String data;
	String albumArtwork;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.apply_template).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.share_music_file).setOnClickListener(this);
        findViewById(R.id.share_album_artwork_file).setOnClickListener(this);
        
        TabHost tabHost = getTabHost();

		TabSpec tab1 = tabHost.newTabSpec(getString(R.string.share_text));
		tab1.setIndicator(getString(R.string.share_text),getResources().getDrawable(drawable.ic_menu_edit));
		tab1.setContent(R.id.tab1);
		tabHost.addTab(tab1);

		TabSpec tab2 = tabHost.newTabSpec(getString(R.string.share_file));
		tab2.setIndicator(getString(R.string.share_file),getResources().getDrawable(drawable.ic_menu_upload));
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
						MediaStore.Audio.Media.TRACK,	MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.ALBUM_ID
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
					}catch (IllegalArgumentException e){
						if(composer==null) composer="";
					}
					try{
						year=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
					}catch (IllegalArgumentException e){
						if(year==null) year="";
					}
					try{
						trackno=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
					}catch (IllegalArgumentException e){
						if(trackno==null) trackno="";
					}
					int src;
					try{
						src=Integer.parseInt(trackno);
					}catch(Exception e2){
						src=0;
					}
					int disc=src/1000;
					int trk=src%1000;
					if(disc>0) trackno=""+trk+" ("+getString(R.string.disc)+" "+disc+")";
					else trackno=""+trk;
					try{
						data=trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
					}catch (IllegalArgumentException e){
						if(data==null) data="";
					}
					
					Cursor albumCursor = getContentResolver().query(
							MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							null, MediaStore.Audio.Albums._ID + "=?", 
							new String[]{ trackCursor.getString(trackCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)) }, null);

					if(albumCursor!=null && albumCursor.getCount()>0){
						albumCursor.moveToFirst();
						int albumArtworkIndex = albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
						albumArtwork = albumCursor.getString(albumArtworkIndex);
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
	    case R.id.action_close:
	    	finish();
	    	break;
	    }
	    return true;
	}
	
	public void updatePreferencesValues(){
		SharedPreferences pref=getSharedPreferences(Statics.PREF_KEY,Activity.MODE_PRIVATE);
		template1=pref.getString(Statics.KEY_TEXT_1,getString(R.string.content_default));
		template2=pref.getString(Statics.KEY_TEXT_2,getString(R.string.content_default_2));
		template3=pref.getString(Statics.KEY_TEXT_3,getString(R.string.content_default_3));
		quitAfterSharing=pref.getInt(Statics.KEY_TEXT_QUIT,0);
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
		if(v==findViewById(R.id.send)){
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.content)).getText().toString());
				startActivityForResult(intent,PICKUP_SEND_TO_APP);
			} catch (Exception e) {
				showToast(this,getString(R.string.sharing_failed));
				Log.d("ExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(v==findViewById(R.id.apply_template)){
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
		}else if(v==findViewById(R.id.settings)){
			Intent intent=new Intent(ExtensionActivity.this,SettingsActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else if(v==findViewById(R.id.close)){
			finish();
		}else if(v==findViewById(R.id.share_music_file)){
			try {
				String ext=data.substring(data.lastIndexOf(".")+1);
				String type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
				if(type==null||type.equals("")) type="application/octet-stream";
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType(type);
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(data));
				startActivityForResult(intent,PICKUP_SEND_TO_APP);
			} catch (Exception e) {
				showToast(this,getString(R.string.sharing_failed));
				Log.d("ExtensionActivity", "Error");
				e.printStackTrace();
			}
		}else if(v==findViewById(R.id.share_album_artwork_file)){
			try {
				String ext=albumArtwork.substring(data.lastIndexOf(".")+1);
				String type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
				if(type==null||type.equals("")) type="application/octet-stream";
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType(type);
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(albumArtwork));
				startActivityForResult(intent,PICKUP_SEND_TO_APP);
			} catch (Exception e) {
				showToast(this,getString(R.string.sharing_failed));
				Log.d("ExtensionActivity", "Error");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==PICKUP_SEND_TO_APP){
			if(quitAfterSharing>0) finish();
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
		
		if(data==null||data.equals("")){
			((TextView)findViewById(R.id.music_file_path)).setText(getString(R.string.not_available));
			((Button)findViewById(R.id.share_music_file)).setVisibility(View.INVISIBLE);
		}else{
			((TextView)findViewById(R.id.music_file_path)).setText(data);
			((Button)findViewById(R.id.share_music_file)).setVisibility(View.VISIBLE);
		}
		
		if(albumArtwork==null||albumArtwork.equals("")){
			((TextView)findViewById(R.id.album_artwork_file_path)).setText(getString(R.string.not_available));
			((Button)findViewById(R.id.share_album_artwork_file)).setVisibility(View.INVISIBLE);
		}else{
			((TextView)findViewById(R.id.album_artwork_file_path)).setText(albumArtwork);
			((Button)findViewById(R.id.share_album_artwork_file)).setVisibility(View.VISIBLE);
		}

		items.setOnItemClickListener(this);
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
	
	public static void showToast(final Context context,final String title){
		new Thread(new Runnable(){
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(context, title, Toast.LENGTH_LONG).show();
					}
				});
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg0==findViewById(R.id.information)){
			String obj=null;
			switch(arg2){
			case 0:
				obj=title;
				break;
			case 1:
				obj=artist;
				break;
			case 2:
				obj=album;
				break;
			case 3:
				obj=duration;
				break;
			case 4:
				obj=year;
				break;
			case 5:
				obj=trackno;
				break;
			case 6:
				obj=composer;
				break;
			}
			if(obj==null||obj.equals("")) return;
			CharSequence list[]=new String[2];
			list[0]=getString(R.string.copy);
			list[1]=getString(R.string.search);
			final String string=obj;
			new AlertDialog.Builder(ExtensionActivity.this)
			.setTitle(obj)
			.setItems(list,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch(arg1){
					case 0:{
						ClipboardManager clipboard=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
						clipboard.setText(string);
						ExtensionActivity.showToast(ExtensionActivity.this, ""+getString(R.string.copied)+" "+string);
						break;
					}
					case 1:{
						Intent intent=new Intent(Intent.ACTION_WEB_SEARCH);
						intent.putExtra(SearchManager.QUERY,string);
						startActivityForResult(intent,PICKUP_SEND_TO_APP);
						break;
					}
					}
				}
			}).show();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		updatePreferencesValues();
	}

}
