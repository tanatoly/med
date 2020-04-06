package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class Bed
{
	
	public final Map<String,Device> devices = new HashMap<>();
	
	String number;
	public String patientName;
	public String patientId;
	public final int id;
	private Map<Integer, Device> allDevices;

	public String room;
	public final AtomicLong firstTime = new AtomicLong(0);
	
	public Bed(String id, Map<Integer, Device> allDevices)
	{
		this.id = Integer.parseInt(id);
		this.allDevices = allDevices;
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
			Device prototype = allDevices.get(deviceType);
			if(prototype == null)
			{
				throw new Exception("NOT FOUND DEVICE WITH TYPE NUMBER = " + deviceType);
			}
			device = new Device(prototype, serial);
			devices.put(serial, device);
		}
		device.handleMessage(deviceType,buffer);
	}
	
	public String getName()
	{
		return "חדר " + room + "  מיטה " + number;
	}
	
	public String getFirstTime()
	{
		return "לפני 24 דקות";
	}
}