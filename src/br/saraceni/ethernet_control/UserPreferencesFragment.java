package br.saraceni.ethernet_control;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class UserPreferencesFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_preferences);
	}

}
