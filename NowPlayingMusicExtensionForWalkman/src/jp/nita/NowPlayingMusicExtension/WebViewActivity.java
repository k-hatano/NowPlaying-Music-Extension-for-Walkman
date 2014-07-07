package jp.nita.NowPlayingMusicExtension;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

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
		});
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
