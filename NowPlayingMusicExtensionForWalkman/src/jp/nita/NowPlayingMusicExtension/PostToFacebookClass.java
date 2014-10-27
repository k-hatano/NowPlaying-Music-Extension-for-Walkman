package jp.nita.NowPlayingMusicExtension;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class PostToFacebookClass {
	
	final static Handler handler = new Handler();

	private static final String ApiKey = "686596968102132";

	private static Facebook facebook = null;
	private static AsyncFacebookRunner asyncFbRunner = null;
	
	private static String facebookAccessToken = null;

	public static void authorize(final Activity activity){
		if(facebook==null){
			facebook = new Facebook(ApiKey);
		}
		facebook.authorize(activity
				, new String[] {"publish_stream"}
		, new DialogListener(){
			@Override
			public void onComplete(Bundle values) {
				Statics.setPreferenceString(activity,Statics.KEY_FACEBOOK_ACCESS_TOKEN,"AUTHORIZED");
				showToast(activity,activity.getString(R.string.authorization_succeed));
			}

			@Override
			public void onFacebookError(FacebookError e) {
				showToast(activity,activity.getString(R.string.authorization_failed));
			}

			@Override
			public void onError(DialogError e) {
				showToast(activity,activity.getString(R.string.authorization_failed));
			}

			@Override
			public void onCancel() {
				showToast(activity,activity.getString(R.string.authorization_failed));
			}
		});
	}

	public static void post(final ExtensionActivity activity,final String content){
		if(facebook==null){
			facebook = new Facebook(ApiKey);
		}
		if(asyncFbRunner==null){
			asyncFbRunner = new AsyncFacebookRunner(facebook);
		}
		facebook.authorize(activity
				, new String[] {"publish_stream"}
		, new DialogListener(){
			@Override
			public void onComplete(Bundle values) {
				Statics.setPreferenceString(activity,Statics.KEY_FACEBOOK_ACCESS_TOKEN,"AUTHORIZED");
				Bundle params = new Bundle();
				params.putString("message",content);
				asyncFbRunner.request("me/feed",params,"POST",activity, null);
			}

			@Override
			public void onFacebookError(FacebookError e) {
				activity.showToast(activity,activity.getString(R.string.posting_failed));
			}

			@Override
			public void onError(DialogError e) {
				activity.showToast(activity,activity.getString(R.string.posting_failed));
			}

			@Override
			public void onCancel() {
				activity.showToast(activity,activity.getString(R.string.posting_cancelled));
			}
		});
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

}
