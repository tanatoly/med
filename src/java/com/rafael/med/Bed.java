package com.rafael.med;

import java.nio.ByteBuffer;
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
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bed other = (Bed) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public String toString()
	{
		return String.format("Bed [id=%s]", id);
	}
}