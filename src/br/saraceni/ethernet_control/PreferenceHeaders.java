package br.saraceni.ethernet_control;

import java.util.List;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;

public class PreferenceHeaders extends PreferenceActivity {
	
	public static final String MOUSE_SENSIBILITY = "MOUSE_SENSIBILITY";
	public static final String BUTTON_A_KEY = "BUTTON_A_KEY";
	public static final String ARROW_UP_KEY = "ARROW_UP_KEY";
	public static final String ARROW_DOWN_KEY = "ARROW_DOWN_KEY";
	public static final String ARROW_RIGHT_KEY = "ARROW_RIGHT_KEY";
	public static final String ARROW_LEFT_KEY = "ARROW_LEFT_KEY";
	public static final String STATIC_ARROW_LEFT_KEY = "ARROW_LEFT_KEY";
	public static final String STATIC_ARROW_RIGHT_KEY = "ARROW_RIGHT_KEY";
	public static final String STATIC_CENTER_KEY = "STATIC_CENTER_KEY";
	
	@Override
	public void onBuildHeaders(List<Header> target){
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

}
