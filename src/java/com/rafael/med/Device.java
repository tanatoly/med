package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Device 
{
	public int type;
	public String name;
	public String serial;
	public String error;
	public boolean isWorking;
	
	
	public final Map<Integer, Param> params = new LinkedHashMap<>();


	public Device(String type, String name) // prototype device
	{
		this.type 	= Integer.parseInt(type);
		this.name 	= name;
	}

	public Device(Device prototype, String serial) // existing device
	{
		this.type 	= prototype.type;
		this.name 	= prototype.name;
		this.serial = serial;
	}
	

	public void handleMessage(int deviceType, ByteBuffer buffer) throws Exception
	{
		if(type != deviceType)
		{
			throw new Exception("UNEXPECTED DEVICE TYPE " + deviceType + " (SERIAL  = " + serial + " , TYPE = " + type);
		}
		int paramsSize = buffer.get();
		for (int i = 0; i < paramsSize; i++)
		{
			int paramKey = buffer.getShort();
			Param param = params.get(paramKey);
			if(param == null)
			{
				throw new Exception("NOT FOUND PARAM FOR KEY = " + paramKey);
			}
			param.handleMessage(buffer);
		}
	}


	public void addParam(Param param)
	{
		params.put(param.id, param);
		
	}
}