package br.saraceni.ethernet_control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerListener implements SensorEventListener {
	
	private boolean onRight, onLeft, onCenter;
	private Context context;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	public static final String TAG = "AccelerometerListener";
	
	
	public AccelerometerListener(Context context)
	{
		this.context = context;
	}
	
	public void initAccelerometerListener()
	{
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float y = event.values[1];
		Log.i(TAG, "Sensor Value " + y);
		if(y >= 5.0)
		{
			//Is going right
			if(!onRight)
			{
				onRight = true;
				onLeft = false;
				onCenter = false;
				handleOnRight();
			}
		}
		else if(y <= -5.0)
		{
			//Is going left
			if(!onLeft)
			{
				onLeft = true;
				onRight = false;
				onCenter = false;
				handleOnLeft();
			}
		}
		else
		{
			//Is centered
			onCenter = true;
			onRight = false;
			onLeft = false;
			handleOnCenter();
		}
	}
	
	public void resumeListener()
	{
		if(sensorManager != null)
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void stopListener()
	{
		if(sensorManager != null)
			sensorManager.unregisterListener(this, accelerometer);
	}
	
	public void handleOnCenter()
	{
		
	}
	
	public void handleOnRight()
	{
		
	}
	
	public void handleOnLeft()
	{
		
	}

}















