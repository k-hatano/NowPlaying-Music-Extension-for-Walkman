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
import android.R.drawable;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ExtensionActivity extends TabActivity implements OnClickListener {
	
	public static final int PICKUP_SEND_TO_APP = 1;
	
	String template1;
	
	String title;
	String artist;
	String album;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
                        MediaStore.Audio.Media.ALBUM
                }, null, null, null);

        if (trackCursor != null) {
            try {
                if (trackCursor.moveToFirst()) {
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

					updatePreferencesValues();
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
		SharedPreferences pref=getSharedPreferences(SettingsActivity.PREF_KEY,Activity.MODE_PRIVATE);
		template1=pref.getString(SettingsActivity.KEY_TEXT_1,getString(R.string.content_default));
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
		//param=param.replace("$d",duration);
		//param=param.replace("$c",composer);
		//param=param.replace("$y",year);
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
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode,resultCode,data);
		if(requestCode==PICKUP_SEND_TO_APP){
			// if(quitAfterSharing) finish();
		}
	}

}
