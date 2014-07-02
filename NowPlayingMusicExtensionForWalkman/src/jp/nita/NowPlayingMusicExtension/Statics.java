package jp.nita.NowPlayingMusicExtension;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Statics {
	
	public static final String PREF_KEY = "NowPlayingMusicExtension";  
	public static final String KEY_TEXT_1 = "templete";
	public static final String KEY_TEXT_2 = "templete2";
	public static final String KEY_TEXT_3 = "templete3";

	public static void setPreferenceValue(Context context,String key,int val){
		SharedPreferences pref=context.getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		Editor editor=pref.edit();
		editor.putInt(key, val);
		editor.commit();
	}
	
	public static void setPreferenceString(Context context,String key,String str){
		SharedPreferences pref=context.getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		Editor editor=pref.edit();
		editor.putString(key, str);
		editor.commit();
	}
}
