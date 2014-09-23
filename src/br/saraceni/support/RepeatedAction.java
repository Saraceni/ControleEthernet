package br.saraceni.support;

import android.util.Log;

public abstract class RepeatedAction implements Runnable {
	
	public static final String TAG = "RepeatedAction";
	private long action_delay = 100;
	private Thread thread;
	private volatile boolean isWorking = true;
	
	public RepeatedAction()
	{
		
	}
	
	public void setDelay(long new_delay)
	{
		action_delay = new_delay;
	}
	
	public void doRepeatedAction()
	{
		isWorking = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public abstract void repeatedAction();
	
	
	public final void run()
	{
		while(isWorking)
		{
			repeatedAction();
			try
			{
				Thread.sleep(action_delay);
			}
			catch(Exception exc)
			{
				Log.i(TAG, "Exception in Thread.delay(millis)");
				Log.i(TAG, exc.toString());
				Log.i(TAG, exc.getMessage());
			}
			
		}
	}
	
	public final void stopAction()
	{
		isWorking = false;
	}

}
