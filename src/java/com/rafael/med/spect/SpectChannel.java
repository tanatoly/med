package com.rafael.med.spect;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SpectChannel 
{	
	public boolean isSelected;
	public final int number;	
	
	private final AtomicInteger up 		= new AtomicInteger(0);
	private final AtomicInteger down 	= new AtomicInteger(0);
	
	
	private final AtomicBoolean isOnce = new AtomicBoolean(false);
	
	public SpectChannel(int number)
	{
		this.number = number;
	}
	
	public int getValue() 
	{
		if(!isOnce.get())
		{
			return -1;
		}
		
		double x = up.get();
		double y = down.get();
		
		int result = 0;
		if(y != 0)
		{
			result = (int) (x/y * 100.0);
		}	
		
		up.set(0);
		down.set(0);
		return result;
	}
	
	public void setValue(int ok,int fail)
	{
		isOnce.compareAndSet(false, true);
		
		up.addAndGet(ok);
		down.addAndGet(ok + fail);
	}
}
