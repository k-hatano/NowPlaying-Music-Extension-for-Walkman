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
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class ExtensionActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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

                    // And retrieve the wanted information
                	/*
                    String trackName = trackCursor.getString(trackCursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String albumName = trackCursor.getString(trackCursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artistName = trackCursor.getString(trackCursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                    ((TextView)findViewById(R.id.track)).setText(trackName);
                    ((TextView)findViewById(R.id.album)).setText(albumName);
                    ((TextView)findViewById(R.id.artist)).setText(artistName);
                    */

                }
            } finally {
                trackCursor.close();
            }
        }

    }

}
