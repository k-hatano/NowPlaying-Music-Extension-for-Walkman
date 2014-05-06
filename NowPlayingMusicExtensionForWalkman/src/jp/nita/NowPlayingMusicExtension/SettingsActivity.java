package jp.nita.NowPlayingMusicExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingsActivity extends Activity {

	public static final String PREF_KEY = "NowPlayingMusicExtension";  
	public static final String KEY_TEXT_1 = "templete";
	
	String template1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updatePreferencesValues(){
		SharedPreferences pref=getSharedPreferences(PREF_KEY,Activity.MODE_PRIVATE);
		template1=pref.getString(KEY_TEXT_1,getString(R.string.content_default));
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
			
		}
		SimpleAdapter adapter
		=new SimpleAdapter(this,list
				,android.R.layout.simple_expandable_list_item_2,
				new String[]{"key","value"},
				new int[]{android.R.id.text1,android.R.id.text2});
		items.setAdapter(adapter);

		// items.setOnItemClickListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();

		updatePreferencesValues();
		updateSettingsListView();
	}
}
