package com.rafael.med;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class Bed
{
	
	public final Map<String,Device> devices = new HashMap<>();
	
	public String number;
	public String patientName;
	public String patientId;
	public final int id;
	private Map<Integer, Device> prototypeDevices;

	public String room;
	public final AtomicLong firstTime = new AtomicLong(0);
	
	public Bed(String id, Map<Integer, Device> prototypeDevices)
	{
		this.id 				= Integer.parseInt(id);
		this.prototypeDevices 	= prototypeDevices;
	}
	public void handleMessage(ByteBuffer buffer) throws Exception
	{
		int deviceType 		= buffer.get();
		int serialLength	= buffer.get();
		byte[] serialBytes	= new byte[serialLength];
		buffer.get(serialBytes);
		String serial		= new String(serialBytes);
		
		Device device = devices.get(serial);
		if(device == null)
		{
			Device prototype = prototypeDevices.get(deviceType);
			if(prototype == null)
			{
				throw new Exception("NOT FOUND DEVICE WITH TYPE NUMBER = " + deviceType);
			}
			device = new Device(prototype, serial);
			devices.put(serial, device);
		}
		device.handleMessage(buffer);
	}
	
	public String getName()
	{
		return "חדר " + room + "  מיטה " + number;
	}
	
	public String getFirstTime()
	{
		long firstTime = Long.MAX_VALUE;
		
		for (Device device : devices.values())
		{
			for (Param param : device.params.values())
			{
				if(param.isAlarm)
				{
					if(param.firstTimeWarning  < firstTime)
					{
						firstTime = param.firstTimeWarning;
					}
				}
			}
		}
		if(firstTime != Long.MAX_VALUE && firstTime != 0)
		{
		
			Instant firstInstant 	= Instant.ofEpochMilli(firstTime);
		    Instant nowInstant 		= Instant.now(); 

		    Duration between = Duration.between(nowInstant, firstInstant);

		    System.out.println(between);
		    
		    long seconds = between.getSeconds();
			
			return " לפני" + seconds + " שניות";			
		}
		return "זזמן שגוי";
	}
}