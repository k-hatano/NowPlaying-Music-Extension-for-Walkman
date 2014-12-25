package jp.nita.NowPlayingMusicExtension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SongChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, context.getString(R.string.playing_song_has_changed), Toast.LENGTH_LONG).show();
	}

}
