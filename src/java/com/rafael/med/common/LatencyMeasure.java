package com.rafael.med.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LatencyMeasure
{
	private final long [] 		times;
	private final AtomicInteger	counter;
	private final String		title;
	private final int 			maxCapacity;
	private Logger log;

	public LatencyMeasure(Logger log, String title, int maxCapacity)
	{
		this.log			= log;
		this.title			= title;
		this.maxCapacity 	= maxCapacity;
		if(maxCapacity < 10_000)
		{
			throw new IllegalArgumentException("must be >= 10_000");
		}

		this.times 			= new long[maxCapacity];
		this.counter		= new AtomicInteger(0);
		for (int i = 0; i < times.length; i++)
		{
			times[i] = 0;
		}
	}

	public void measure(long beginMeasure)
	{
		if(counter.compareAndSet(maxCapacity - 1, 0))
		{
			if(log != null && log.isDebugEnabled())
			{
				log.debug(stats(title,times));
			}
			else
			{
				System.out.println(stats(title,times));
			}
		}
		times[counter.getAndIncrement()] = System.nanoTime() - beginMeasure;
	}

	public void measureDelta(long delta)
	{
		if(counter.compareAndSet(maxCapacity - 1, 0))
		{
			if(log != null && log.isDebugEnabled())
			{
				log.debug(stats(title,times));
			}
			else
			{
				System.out.println(stats(title,times));
			}
			for (int i = 0; i < times.length; i++)
			{
				times[i] = 0;
			}
		}
		times[counter.getAndIncrement()] = delta;
	}


	public static String stats(String title,long [] times)
	{
		Arrays.sort(times);
		double [] stats = new double[6];
		stats[0] = times[times.length / 2 ] / 1e3;						// 50 percentile
		stats[1] = times[times.length * 9 /10 ] / 1e3;					// 90 percentile
		stats[2] = times[times.length - times.length /100 	] / 1e3;	// 99 percentile
		stats[3] = times[times.length - times.length /1000 	] / 1e3;	// 99.9 percentile
		stats[4] = times[times.length - times.length /10000 ] / 1e3;	// 99.99 percentile
		stats[5] = times[times.length  - 1 ] / 1e3; 					// worst percentile

		return String.format("%s latency measured : \n %.2f us for 50 percentile\n %.2f us for 90 percentile\n %.2f us for 99 percentile\n %.2f us for 99.9 percentile\n %.2f us for 99.99 percentile\n %.2f us for worst percentile",title, stats[0],stats[1],stats[2],stats[3],stats[4],stats[5]);
	}


	public static double [] stats(long [] times)
	{
		Arrays.sort(times);
		double [] stats = new double[6];
		stats[0] = times[times.length / 2 ] / 1e3;						// 50 percentile
		stats[1] = times[times.length * 9 /10 ] / 1e3;					// 90 percentile
		stats[2] = times[times.length - times.length /100 	] / 1e3;	// 99 percentile
		stats[3] = times[times.length - times.length /1000 	] / 1e3;	// 99.9 percentile
		stats[4] = times[times.length - times.length /10000 ] / 1e3;	// 99.99 percentile
		stats[5] = times[times.length  - 1 ] / 1e3; 					// worst percentile
		return stats;
	}


	public static String toStatsString(String headline ,double [] stats)
	{
		return String.format("%s latency measured : \n %.2f us for 50 percentile\n %.2f us for 90 percentile\n %.2f us for 99 percentile\n %.2f us for 99.9 percentile\n %.2f us for 99.99 percentile\n %.2f us for worst percentile",headline, stats[0],stats[1],stats[2],stats[3],stats[4],stats[5]);

	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception
	{
		Logger log = LogManager.getLogger();

		int maxCapacity = 1000;

		Map<Long , Object> m1 = new ConcurrentHashMap<>(maxCapacity);
		Map<Long , Object> m2 = new HashMap<>(maxCapacity);


		long [] keys = new long[maxCapacity];


		for (int i = 0; i < keys.length; i++)
		{
			keys[i] = System.nanoTime();
			Thread.sleep(1);
		}



		int COUNT = 100000;
		LatencyMeasure measure = new LatencyMeasure(null, "M1 - put", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				m1.put(keys[k], new Object());
			}
			measure.measure(begin);
		}

		measure = new LatencyMeasure(log, "M2 - put ", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				m2.put(keys[k], new Object());
			}
			measure.measure(begin);
		}

		measure = new LatencyMeasure(null, "M1 - put - update", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				m1.put(keys[k], new Object());
			}
			measure.measure(begin);
		}

		measure = new LatencyMeasure(log, "M2 - put - update ", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				m2.put(keys[k], new Object());
			}
			measure.measure(begin);
		}


		measure = new LatencyMeasure(null, "M1 - get", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				Object object = m1.get(keys[k]);
			}
			measure.measure(begin);
		}


		measure = new LatencyMeasure(log, "M2 - get ", COUNT);

		for (int i = 0; i < COUNT; i++)
		{
			long begin = System.nanoTime();
			for (int k = 0; k < keys.length; k++)
			{
				Object object = m2.get(keys[k]);
			}
			measure.measure(begin);
		}
	}
}
