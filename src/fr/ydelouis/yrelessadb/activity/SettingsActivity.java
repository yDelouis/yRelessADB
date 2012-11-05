package fr.ydelouis.yrelessadb.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.view.MenuItem;
import fr.ydelouis.yrelessadb.R;

public class SettingsActivity extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.settings);
		
		EditTextPreference portPref = (EditTextPreference) findPreference(getString(R.string.pref_port_key));
		portPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
