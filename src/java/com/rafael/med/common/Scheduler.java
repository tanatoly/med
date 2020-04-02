package com.rafael.med.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scheduler
{

	private ScheduledExecutorService schedulerThread;
	private ByteBufferRing.IN inRing;
	private ByteBuffer buffer;
	private long minimalPeriod;
	private long counter;
	private MessageHandler handler;
	private AtomicBoolean isRunning;


	public Scheduler(AtomicBoolean isRunning,ByteBufferRing.IN inRing,ByteOrder byteOrder, long schedulerPeriodInMs, MessageHandler handler)
	{
		this.inRing = inRing;
		this.minimalPeriod = schedulerPeriodInMs;
		this.handler = handler;
		this.buffer = ByteBuffer.allocate(8).order(byteOrder);
		this.isRunning = isRunning;
	}


	public long getMinimalPeriod()
	{
		return minimalPeriod;
	}


	public void start()
	{
		counter = 0;
		schedulerThread = Executors.newScheduledThreadPool(1, Utilities.threadFactory("scheduler", false));
		schedulerThread.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				if(isRunning.get())
				{
					buffer.clear();
					buffer.putLong(0,counter);

					inRing.push(buffer, null, handler);
					counter++;
				}
			}
		}, 0, minimalPeriod, TimeUnit.MILLISECONDS);
	}


	public void stop()
	{
		if(schedulerThread != null)
		{
			schedulerThread.shutdown();
			try
			{
				schedulerThread.awaitTermination(minimalPeriod * 2, TimeUnit.SECONDS);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
