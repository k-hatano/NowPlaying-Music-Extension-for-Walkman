package jp.nita.NowPlayingMusicExtension;

import android.app.Activity;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class PostToFacebookClass {
	
	private static final String ApiKey = "508033875922009";
	
	private static Facebook facebook = null;
	
	public static void authorize(Activity activity){
		if(facebook==null){
			facebook = new Facebook(ApiKey);
		}
		facebook.authorize(activity
                , new String[] {"publish_stream"}
                , new DialogListener(){
			@Override
            public void onComplete(Bundle values) {
				
            }

            @Override
            public void onFacebookError(FacebookError e) {
            	
            }

            @Override
            public void onError(DialogError e) {
            	
            }

			@Override
			public void onCancel() {
				
			}
		});
	}
	
}
