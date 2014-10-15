package jp.nita.NowPlayingMusicExtension;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {

	final String CALLBACK_URL = "http://hp.vector.co.jp/authors/VA054317/wait.html";
	
	String twitterOauthToken;
	String twitterOauthVerifier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);

		String url = getIntent().getStringExtra(Statics.EXTRA_URL);
		WebView webView = (WebView)findViewById(R.id.webView);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(url != null && url.startsWith(CALLBACK_URL)){
					String[] urlParameters = url.split("\\?")[1].split("&");

					if(urlParameters[0].startsWith("oauth_token")){
						twitterOauthToken = urlParameters[0].split("=")[1];
					}else if(urlParameters[1].startsWith("oauth_token")){
						twitterOauthToken = urlParameters[1].split("=")[1];
					}

					if(urlParameters[0].startsWith("oauth_verifier")){
						twitterOauthVerifier = urlParameters[0].split("=")[1];
					}else if(urlParameters[1].startsWith("oauth_verifier")){
						twitterOauthVerifier = urlParameters[1].split("=")[1];
					}

					setTwitterOauthToken(twitterOauthToken);
					setTwitterOauthVerifier(twitterOauthVerifier);
					
					Toast.makeText(WebViewActivity.this, getString(R.string.authorization_succeed), Toast.LENGTH_LONG).show();

					finish();
				}
			}
		});
	}
	
	public void setTwitterOauthToken(String oauthToken){
		twitterOauthToken=oauthToken;
		Statics.setPreferenceString(this,Statics.KEY_TWITTER_OAUTH_TOKEN,twitterOauthToken);
	}
	
	public void setTwitterOauthVerifier(String oauthVerifier){
		twitterOauthVerifier=oauthVerifier;
		Statics.setPreferenceString(this,Statics.KEY_TWITTER_OAUTH_VERIFIER,twitterOauthVerifier);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:
			finish();
			break;
		}
		return true;
	}
}
