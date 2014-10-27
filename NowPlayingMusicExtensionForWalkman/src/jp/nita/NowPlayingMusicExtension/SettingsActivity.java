package jp.nita.NowPlayingMusicExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnItemClickListener,OnClickListener {
	
	String template1;
	String template2;
	String template3;
	int quitAfterSharing;
	
	String twitterOauthToken;
	String twitterOauthVerifier;
	String facebookAccessToken;
	
	int position=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		findViewById(R.id.reset_all_settings).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_ok) {
			finish();
		}else if (id == R.id.action_reset_all_settings) {
			confirmResetAllSettings();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updatePreferencesValues(){
		SharedPreferences pref=getSharedPreferences(Statics.PREF_KEY,Activity.MODE_PRIVATE);
		template1=pref.getString(Statics.KEY_TEXT_1,getString(R.string.content_default));
		template2=pref.getString(Statics.KEY_TEXT_2,getString(R.string.content_default_2));
		template3=pref.getString(Statics.KEY_TEXT_3,getString(R.string.content_default_3));
		quitAfterSharing=pref.getInt(Statics.KEY_TEXT_QUIT,0);
		twitterOauthToken=pref.getString(Statics.KEY_TWITTER_OAUTH_TOKEN,Statics.EMPTY);
		twitterOauthVerifier=pref.getString(Statics.KEY_TWITTER_OAUTH_VERIFIER,Statics.EMPTY);
		facebookAccessToken=pref.getString(Statics.KEY_FACEBOOK_ACCESS_TOKEN,Statics.EMPTY);
	}
	
	public void updateSettingsListView(){
		ListView items=(ListView)findViewById(R.id.settings);
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		{
			Map<String,String> map;

			map=new HashMap<String,String>();
			map.put("key", getString(R.string.template_1));
			map.put("value", template1);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.template_2));
			map.put("value", template2);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.template_3));
			map.put("value", template3);
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.quit_after_sharing));
			map.put("value", Statics.getOnOrOffString(this, quitAfterSharing));
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.twitter));
			map.put("value", Statics.getAuthorizedOrUnauthorized(this, twitterOauthToken));
			list.add(map);
			
			map=new HashMap<String,String>();
			map.put("key", getString(R.string.facebook));
			map.put("value", Statics.getAuthorizedOrUnauthorized(this, facebookAccessToken));
			list.add(map);
			
		}
		SimpleAdapter adapter
		=new SimpleAdapter(this,list
				,android.R.layout.simple_expandable_list_item_2,
				new String[]{"key","value"},
				new int[]{android.R.id.text1,android.R.id.text2});
		items.setAdapter(adapter);

		items.setOnItemClickListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();

		updatePreferencesValues();
		updateSettingsListView();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		position = ((ListView)arg0).getFirstVisiblePosition();
		switch(arg2){
		case 0:{
			final TextView explainView1 = new TextView(this);
			explainView1.setText(getString(R.string.template_explain));
			explainView1.setTextAppearance(this,android.R.style.TextAppearance_Small);
			final EditText editText1 = new EditText(this);
			editText1.setText(template1);
			editText1.setLines(3);
			editText1.setGravity(Gravity.TOP);
			final LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(explainView1);
			layout.addView(editText1);
			layout.setPadding(8,8,8,8);
			new AlertDialog.Builder(SettingsActivity.this)
			.setTitle(getString(R.string.template_1))
			.setView(layout)
			.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setTemplate1(editText1.getText().toString());
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.show();
			break;
		}case 1:{
			final TextView explainView2 = new TextView(this);
			explainView2.setText(getString(R.string.template_explain));
			explainView2.setTextAppearance(this,android.R.style.TextAppearance_Small);
			final EditText editText2 = new EditText(this);
			editText2.setText(template2);
			editText2.setLines(3);
			editText2.setGravity(Gravity.TOP);
			final LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(explainView2);
			layout.addView(editText2);
			layout.setPadding(8,8,8,8);
			new AlertDialog.Builder(SettingsActivity.this)
			.setTitle(getString(R.string.template_2))
			.setView(layout)
			.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setTemplate2(editText2.getText().toString());
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.show();
			break;
		}case 2:{
			final TextView explainView3 = new TextView(this);
			explainView3.setText(getString(R.string.template_explain));
			explainView3.setTextAppearance(this,android.R.style.TextAppearance_Small);
			final EditText editText3 = new EditText(this);
			editText3.setText(template3);
			editText3.setLines(3);
			editText3.setGravity(Gravity.TOP);
			final LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(explainView3);
			layout.addView(editText3);
			layout.setPadding(8,8,8,8);
			new AlertDialog.Builder(SettingsActivity.this)
			.setTitle(getString(R.string.template_3))
			.setView(layout)
			.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setTemplate3(editText3.getText().toString());
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			})
			.show();
			break;
		}case 3:{
			CharSequence list[]=new String[2];
			list[0]=getString(R.string.off);
			list[1]=getString(R.string.on);
			new AlertDialog.Builder(SettingsActivity.this)
			.setTitle(getString(R.string.quit_after_sharing))
			.setSingleChoiceItems(list,quitAfterSharing,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					setQuitAfterSharing(arg1);
					arg0.dismiss();
					((ListView)findViewById(R.id.settings)).setSelection(position);
				}
			}).show();
			break;
		}case 4:{
			TweetAsyncTaskCollection collection=new TweetAsyncTaskCollection();
			TweetAsyncTaskCollection.AuthorizationAsyncTask task=collection.new AuthorizationAsyncTask(this);
			task.execute();
			break;
		}case 5:{
			PostToFacebookClass.authorize(this);
			break;
		}
		}
	}
	
	public void setTemplate1(String tmp1){
		template1=tmp1;
		Statics.setPreferenceString(this,Statics.KEY_TEXT_1,template1);
		updatePreferencesValues();
		updateSettingsListView();
	}
	
	public void setTemplate2(String tmp2){
		template2=tmp2;
		Statics.setPreferenceString(this,Statics.KEY_TEXT_2,template2);
		updatePreferencesValues();
		updateSettingsListView();
	}
	
	public void setTemplate3(String tmp3){
		template3=tmp3;
		Statics.setPreferenceString(this,Statics.KEY_TEXT_3,template3);
		updatePreferencesValues();
		updateSettingsListView();
	}
	
	public void setQuitAfterSharing(int qas){
		quitAfterSharing=qas;
		Statics.setPreferenceValue(this,Statics.KEY_TEXT_QUIT,quitAfterSharing);
		updatePreferencesValues();
		updateSettingsListView();
	}

	@Override
	public void onClick(View arg0) {
		if(arg0==findViewById(R.id.ok)){
			finish();
		}else if(arg0==findViewById(R.id.reset_all_settings)){
			confirmResetAllSettings();
		}
	}
	
	public void confirmResetAllSettings(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.reset_all_settings));
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	resetAllSettings();
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
	}
	
	public void resetAllSettings(){
		Statics.setPreferenceString(this,Statics.KEY_TEXT_1,getString(R.string.content_default));
		Statics.setPreferenceString(this,Statics.KEY_TEXT_2,getString(R.string.content_default_2));
		Statics.setPreferenceString(this,Statics.KEY_TEXT_3,getString(R.string.content_default_3));
		Statics.setPreferenceValue(this,Statics.KEY_TEXT_QUIT,0);
		Statics.setPreferenceString(this,Statics.KEY_TWITTER_OAUTH_TOKEN,Statics.EMPTY);
		Statics.setPreferenceString(this,Statics.KEY_TWITTER_OAUTH_VERIFIER,Statics.EMPTY);
		updatePreferencesValues();
		updateSettingsListView();
	}
	
}
