package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Device 
{
	public final int type;
	public final String name;
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
		
		for (Map.Entry<Integer, Param> entry : prototype.params.entrySet()) 
		{
			Integer key = entry.getKey();
			Param value = entry.getValue();
			
			params.put(key, new Param(value));
			
			
		}
		
		
	}
	

	public void handleMessage(ByteBuffer buffer) throws Exception
	{
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((serial == null) ? 0 : serial.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serial == null) {
			if (other.serial != null)
				return false;
		} else if (!serial.equals(other.serial))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Device [type=%s, name=%s, serial=%s]", type, name, serial);
	}

	public void addParam(Param param)
	{
		params.put(param.id, param);
		
	}
}