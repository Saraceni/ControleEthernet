package br.saraceni.ethernet_control;


import java.net.InetAddress;
import java.util.HashMap;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.saraceni.ethernet_comm.EthernetClient;
import br.saraceni.support.RepeatedAction;

public class MainActivity extends Activity implements OnTouchListener {
	
	private static EthernetClient client;
	private static final String TAG = "EthernetControl";
	private static HashMap<String,String> keys_map = new HashMap<String,String>();
	private static String bt_a_KEY;
	private static String bt_up_KEY;
	private static String bt_down_KEY;
	private static String bt_right_KEY;
	private static String bt_left_KEY;
	private static String bt_acel_left_KEY;
	private static String bt_acel_right_KEY;
	private static final long SEND_DELAY = 300;
	
	private NFSAccelerometerListener accelerometerListener;
	private float mX;
	private float mY;
	private boolean has_move = false;
	private static final int SHOW_PREFERENCES =	1;
	private static int MOUSE_SENSIBILITY;
	private final long SLEEP_TIME = 80;
	private Button staticRightBt;
	private Button staticCenterBt;
	private Button staticLeftBt;
	private SendUpMovement sendUp;
	private SendDownMovement sendDown;
	private SendRightMovement sendRight;
	private SendLeftMovement sendLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadHashMap();
		setContentView(R.layout.activity_main);
		TextView mouseScroll = (TextView) findViewById(R.id.mouse_scroll);
		mouseScroll.setOnTouchListener(this);
		initializeRepeaters();
		setButtons();
		updateFromPreferences();
		adjustSensorListener();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		accelerometerListener.resumeListener();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		accelerometerListener.stopListener();
	}
	
	private void adjustSensorListener()
	{

		staticRightBt = (Button) findViewById(R.id.bt_static_right);
		staticLeftBt = (Button) findViewById(R.id.bt_static_left);
		staticCenterBt = (Button) findViewById(R.id.bt_static_center);
		accelerometerListener = new NFSAccelerometerListener(this);
		accelerometerListener.initAccelerometerListener();
	}
	
	private void updateFromPreferences()
	{
		Context context = getApplicationContext();
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(context);
		MOUSE_SENSIBILITY = Integer.parseInt(prefs.getString(PreferenceHeaders.MOUSE_SENSIBILITY, "1"));
		bt_a_KEY = prefs.getString(PreferenceHeaders.BUTTON_A_KEY, "A");
		bt_up_KEY = prefs.getString(PreferenceHeaders.ARROW_UP_KEY, "{UP}");
		bt_down_KEY = prefs.getString(PreferenceHeaders.ARROW_DOWN_KEY, "{DOWN}");
		bt_right_KEY = prefs.getString(PreferenceHeaders.ARROW_RIGHT_KEY, "{RIGHT}");
		bt_left_KEY = prefs.getString(PreferenceHeaders.ARROW_LEFT_KEY, "{LEFT}");
		bt_acel_left_KEY = prefs.getString(PreferenceHeaders.STATIC_ARROW_LEFT_KEY, "{LEFT}");
		bt_acel_right_KEY = prefs.getString(PreferenceHeaders.STATIC_ARROW_RIGHT_KEY, "{RIGHT}");
		
	}
	
	private void loadHashMap(){
		String[] keys_name = getResources().getStringArray(R.array.keys_name);
		String[] keys_code = getResources().getStringArray(R.array.keys_code);
		for(int i = 0; i < keys_name.length; i++){
			keys_map.put(keys_name[i], keys_code[i]);
		}
	}
	
	private void initializeRepeaters()
	{
		sendDown = new SendDownMovement(SEND_DELAY);
		sendUp = new SendUpMovement(SEND_DELAY);
		sendRight = new SendRightMovement(SEND_DELAY);
		sendLeft = new SendLeftMovement(SEND_DELAY);
	}
	
	private void setButtons()
	{
		Button bt = (Button) findViewById(R.id.bt_down);
		bt.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					sendDown.doRepeatedAction();
					break;
				case MotionEvent.ACTION_UP:
					sendDown.stopAction();
					break;
				}
				return false;
			}
		
		});
		bt = (Button) findViewById(R.id.bt_up);
		bt.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					sendUp.doRepeatedAction();
					break;
				case MotionEvent.ACTION_UP:
					sendUp.stopAction();
					break;
				}
				return false;
			}
		
		});
		bt = (Button) findViewById(R.id.bt_left);
		bt.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					sendLeft.doRepeatedAction();
					break;
				case MotionEvent.ACTION_UP:
					sendLeft.stopAction();
					break;
				}
				return false;
			}
		
		});
		bt = (Button) findViewById(R.id.bt_right);
		bt.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					sendRight.doRepeatedAction();
					break;
				case MotionEvent.ACTION_UP:
					sendRight.stopAction();
					break;
				}
				return false;
			}
		
		});
		bt = (Button) findViewById(R.id.bt_a);
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(client != null && client.isConnected()){
					client.enviaDados(bt_a_KEY);
				}
			}
			
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
		case SHOW_PREFERENCES:
			updateFromPreferences();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		switch(id){
		case R.id.menu_connect:
			DialogFragment connectFragment = new ConnectFragmentDialog();
			connectFragment.show(getFragmentManager(), "ConnectFragment");
			break;
		case R.id.menu_disconnect:
			if(client != null)
				client.desconectar();
			break;
		case R.id.menu_set_bt:
			SetButtonChar dialog_setBt = SetButtonChar.newInstance(R.id.bt_a);
			dialog_setBt.show(getFragmentManager(), "SetBtDialog");
			break;
		case R.id.menu_preferences:
			Intent intent = new Intent(this, PreferenceHeaders.class);
			startActivityForResult(intent, SHOW_PREFERENCES);
			break;
		}
		return true;
	}
	
	public static class SetButtonChar extends DialogFragment {
		
		private int id;
		
		static SetButtonChar newInstance(int id){
			SetButtonChar setBtChar = new SetButtonChar();
			Bundle args = new Bundle();
			args.putSerializable("id", id);
			setBtChar.setArguments(args);
			return setBtChar;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			id = getArguments().getInt("id");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.set_button_fragment, container);
			final Spinner char_spinner = (Spinner) view.findViewById(R.id.spinner_char);
			Button bt_define = (Button) view.findViewById(R.id.bt_fragment_ok);
			bt_define.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String item = (String) char_spinner.getSelectedItem();
					switch(id){
					case R.id.bt_a:
						bt_a_KEY = keys_map.get(item);
					}
					dismiss();
				}
			
			});
			Button bt_cancela = (Button) view.findViewById(R.id.bt_frament_cancel);
			bt_cancela.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {					
					dismiss();
				}
			
			});
			return view;
		}
	}

    public static class ConnectFragmentDialog extends DialogFragment implements OnClickListener {
		
		private EditText editIP;
		private EditText editPorta;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.connect_layout, container);
			editIP = (EditText) view.findViewById(R.id.et_ip);
			editPorta = (EditText) view.findViewById(R.id.et_port);
			Button bt = (Button) view.findViewById(R.id.bt_conectar);
			bt.setOnClickListener(this);
			String conectar = getResources().getString(R.string.conectar);
			getDialog().setTitle(conectar);
			return view;
		}

		@Override
		public void onClick(View v) {
			try{
				InetAddress ip = InetAddress.getByName(editIP.getText().toString());
				Log.i("ethernet_client", "Converteu o ip. Vai tentar converter porta");
				int port = Integer.parseInt(editPorta.getText().toString());
			    client = new EthernetClient(ip, port);
			    Log.i("ethernet_client","Converteu a porta. vai conectar");
			    Thread th = new Thread(new Runnable(){

					@Override
					public void run() {
						client.conectar();
						
					}
			    	
			    });
			    th.start();
			    th.join();
			    dismiss();
			}
			catch(Exception exc){
				Toast.makeText(getActivity(), "Digite valores validos", Toast.LENGTH_SHORT).show();
				//Log.i("ethernet_client", exc.getMessage());
				if(exc.getMessage() != null){
					Log.i("ethernet_client", exc.getMessage());
				}
				else{
					Log.i("ethernet_client", "exc.getMessage() = null");
				}
				if(exc.getCause() != null)
					Log.i("ethernet_client", exc.getCause().toString());
				else
					Log.i("ethernet_client", "exc.getCause() = null");
				//Log.i("ethernet_client", exc.getCause().toString());
				dismiss();
			}
			
		}
}
    
    private void handleMotionEvent(float x, float y)
    {
    	if(client != null && client.isConnected())
    	{
    		int dx = (int) (x - mX);
    		int dy = (int) (y - mY);
    		//if(Math.abs(dy) >= 2 || Math.abs(dx) >= 2)
    		if(Math.sqrt(Math.pow(dx, 2)) + Math.sqrt(Math.pow(dy, 2)) >= 2)
    		{
    			has_move = true;
    			dx = dx * MOUSE_SENSIBILITY;
    			dy = dy * MOUSE_SENSIBILITY;
    			String str = '*' + String.valueOf(dx) + '*' + String.valueOf(dy) + '*';
        		Log.i(TAG, "Vai tentar enviar: " + str);
        		client.enviaDados(str);
        		mX = x;
            	mY = y;
    		}
    		try{
    			Thread.sleep(SLEEP_TIME);
    		}
    		catch(Exception exc)
    		{
    			Log.i(TAG, "Exc no Thread.sleep()");
    			Log.i(TAG, exc.toString());
    		}
    	}
    	mX = x;
    	mY = y;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.mouse_scroll)
		{
			float x = event.getX();
			float y = event.getY();
			
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				mX = x;
				mY = y;
				has_move = false;
				break;
			case MotionEvent.ACTION_MOVE:
				handleMotionEvent(x, y);
				break;
			case MotionEvent.ACTION_UP:
				if(!has_move)
				{
					if(client != null && client.isConnected()){
						String str = "Click";
						client.enviaDados(str);
					}
				}
				break;
			}
		}
		return true;
	}	
	
	public class NFSAccelerometerListener extends AccelerometerListener
	{
		private AccelSendRightMovement sendRightMove;
		private AccelSendLeftMovement sendLeftMove;
		private long l_repeatersDelay = 300;
		private Thread thread;
		private int i_blue = getResources().getColor(android.R.color.holo_blue_dark);
		private int i_orange = getResources().getColor(android.R.color.holo_orange_dark);

		public NFSAccelerometerListener(Context context) {
			super(context);
			initializeRepeaters();
		}
		
		private void initializeRepeaters()
		{
			sendRightMove = new AccelSendRightMovement(l_repeatersDelay);
			sendLeftMove = new AccelSendLeftMovement(l_repeatersDelay);
		}
		
		public void handleOnCenter()
		{
			Log.i(TAG, "handleOnCenter");
			sendRightMove.stopAction();
			sendLeftMove.stopAction();
			staticCenterBt.setBackgroundColor(i_orange);
			staticRightBt.setBackgroundColor(i_blue);
			staticLeftBt.setBackgroundColor(i_blue);
			
		}
		
		public void handleOnRight()
		{
			Log.i(TAG, "handleOnRight");
			staticCenterBt.setBackgroundColor(i_blue);
			staticRightBt.setBackgroundColor(i_orange);
			staticLeftBt.setBackgroundColor(i_blue);
			sendLeftMove.stopAction();
			sendRightMove.doRepeatedAction();
		}
		
		public void handleOnLeft()
		{
			Log.i(TAG, "handleOnLeft");
			staticCenterBt.setBackgroundColor(i_blue);
			staticRightBt.setBackgroundColor(i_blue);
			staticLeftBt.setBackgroundColor(i_orange);
			sendRightMove.stopAction();
			sendLeftMove.doRepeatedAction();
		}
		
		private class AccelSendLeftMovement extends RepeatedAction
		{
			private AccelSendLeftMovement(long delay)
			{
				setDelay(delay);
			}

			@Override
			public void repeatedAction() {
				
				if(client != null && client.isConnected())
				{
					client.enviaDados(bt_acel_left_KEY);
				}
				
			}
			
		}
		
		private class AccelSendRightMovement extends RepeatedAction
		{
			
			private AccelSendRightMovement(long delay)
			{
				setDelay(delay);
			}

			@Override
			public void repeatedAction() {
				
				if(client != null && client.isConnected())
				{
					client.enviaDados(bt_acel_right_KEY);
				}
			}
			
		}
		
	}
	
	private class SendRightMovement extends RepeatedAction
	{
		
		private SendRightMovement(long delay)
		{
			setDelay(delay);
		}

		@Override
		public void repeatedAction() {
			
			if(client != null && client.isConnected())
			{
				client.enviaDados(bt_right_KEY);
			}
		}
		
	}
	
	private class SendLeftMovement extends RepeatedAction
	{
		private SendLeftMovement(long delay)
		{
			setDelay(delay);
		}

		@Override
		public void repeatedAction() {
			
			if(client != null && client.isConnected())
			{
				client.enviaDados(bt_left_KEY);
			}
			
		}
		
	}
	
	private class SendUpMovement extends RepeatedAction
	{
		private SendUpMovement(long delay)
		{
			setDelay(delay);
		}

		@Override
		public void repeatedAction() {
			
			if(client != null && client.isConnected())
			{
				client.enviaDados(bt_up_KEY);
			}
			
		}
		
	}
	
	private class SendDownMovement extends RepeatedAction
	{
		private SendDownMovement(long delay)
		{
			setDelay(delay);
		}

		@Override
		public void repeatedAction() {
			
			if(client != null && client.isConnected())
			{
				client.enviaDados(bt_down_KEY);
			}
			
		}
		
	}
}








