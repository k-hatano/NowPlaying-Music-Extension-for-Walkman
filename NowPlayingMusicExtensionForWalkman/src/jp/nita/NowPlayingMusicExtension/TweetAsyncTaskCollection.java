package jp.nita.NowPlayingMusicExtension;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

public class TweetAsyncTaskCollection {

	final static Handler handler = new Handler();

	static RequestToken requestToken=null;
	
	static Twitter twitter = null;

	public class AuthorizationAsyncTask extends AsyncTask<Void, Void, Void>{

		Activity superview;

		AuthorizationAsyncTask(Activity app){
			super();
			superview=app;
		}

		@Override
		protected Void doInBackground(Void... params) {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer("mtBSfpRyqJLPvOEf7DPJpWzsu",
					"7oqTFiiFRHwBnYvpCVua6mCAefATxWkMfO3QLxuuw8hC40AExh");
			twitter.setOAuthAccessToken(null);
			try {
				requestToken = twitter.getOAuthRequestToken();
				String url = requestToken.getAuthorizationURL();

				Intent intent = new Intent(superview,WebViewActivity.class);
				intent.putExtra(Statics.EXTRA_URL,url);
				superview.startActivity(intent);
			} catch (TwitterException e) {
				showToast(superview,superview.getString(R.string.setting_failed));
				e.printStackTrace();
			}
			return null;
		}

	}
	
	public class TweetAsyncTask extends AsyncTask<String, Void, Void>{

		Activity superview;

		TweetAsyncTask(Activity app){
			super();
			superview=app;
		}

		@Override
		protected Void doInBackground(String... params) {
			SharedPreferences pref=superview.getSharedPreferences(Statics.PREF_KEY,superview.MODE_PRIVATE);
			String token=pref.getString(Statics.KEY_TWITTER_OAUTH_TOKEN,"");
			String verifier=pref.getString(Statics.KEY_TWITTER_OAUTH_VERIFIER,"");
			if("".equals(token)||"".equals(verifier)){
				showToast(superview,superview.getString(R.string.posting_failed));
				return null;
			}

			if("".equals(params[0])){
				showToast(superview,superview.getString(R.string.posting_failed));
				return null;
			}
			try {
				requestToken = twitter.getOAuthRequestToken();
				AccessToken oauthToken = twitter.getOAuthAccessToken(requestToken, verifier);  
				
				AccessToken accessToken = new AccessToken(oauthToken.getToken(), oauthToken.getTokenSecret());
				
				twitter.setOAuthConsumer("mtBSfpRyqJLPvOEf7DPJpWzsu",
						"7oqTFiiFRHwBnYvpCVua6mCAefATxWkMfO3QLxuuw8hC40AExh");
				twitter.setOAuthAccessToken(accessToken);
				twitter4j.Status status = twitter.updateStatus(params[0]);
			} catch (TwitterException e) {
				showToast(superview,superview.getString(R.string.posting_failed));
				e.printStackTrace();
				return null;
			}
			showToast(superview,superview.getString(R.string.posting_completed));
			return null;
		}

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
