package jp.nita.NowPlayingMusicExtension;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

public class TweetAsyncTaskCollection {

	final static Handler handler = new Handler();

	static RequestToken requestToken=null;

	public class AuthorizationAsyncTask extends AsyncTask<Void, Void, Void>{

		Activity superview;

		AuthorizationAsyncTask(Activity app){
			super();
			superview=app;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Twitter twitter = new TwitterFactory().getInstance();
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
