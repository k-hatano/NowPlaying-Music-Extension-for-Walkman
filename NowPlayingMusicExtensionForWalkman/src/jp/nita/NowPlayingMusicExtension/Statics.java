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
	public static final String KEY_TEXT_QUIT = "quitAfterSharing";
	public static final String KEY_TWITTER_OAUTH_TOKEN = "twitterOauthToken";
	public static final String KEY_TWITTER_OAUTH_VERIFIER = "twitterOauthVerifier";
	public static final String KEY_FACEBOOK_ACCESS_TOKEN = "facebookAccessToken";
	
	public static final String EXTRA_URL = "url";
	
	public static final String EMPTY = "";
	
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
	
	public static String getOnOrOffString(Context context,int n){
		if(n>0) return context.getString(R.string.on);
		else return context.getString(R.string.off);
	}
	
	public static String getAuthorizedOrUnauthorized(Context context,String token){
		if(token==null||"".equals(token)) return context.getString(R.string.unauthorized);
		else return context.getString(R.string.authorized);
	}
}
