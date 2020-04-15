package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Device 
{
	public final int type;
	public final String name;
	public String serial;
	public String error;
	public boolean isWorking;
	public long lastMessageTime = 0;
	
	public final Map<Integer, Param> params 			= new LinkedHashMap<>();
	public final Map<Integer, ParamRange> ranges 		= new HashMap<>();
	public final Map<Integer, ParamDefault> defaults 	= new HashMap<>();
	public final Map<Integer, Mfl> mfls 				= new LinkedHashMap<>();

	public final Map<Integer, DeviceParam> all			= new HashMap<>();
	
	
	
	public final List<Long> timestamps = new ArrayList<>(1000);
	
	
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
			
			Param param = new Param(value);
			params.put(key, param);
			all.put(key, param);
		}
		
		for (Map.Entry<Integer, ParamRange> entry : prototype.ranges.entrySet()) 
		{
			Integer key = entry.getKey();
			ParamRange value = entry.getValue();
			
			ParamRange range = new ParamRange(value);
			ranges.put(key, range);
			all.put(key, range);
		}
		
		for (Map.Entry<Integer, Mfl> entry : prototype.mfls.entrySet()) 
		{
			Integer key = entry.getKey();
			Mfl value = entry.getValue();
			
			Mfl mfl = new Mfl(value);
			mfls .put(key, mfl);
			all .put(key, mfl);
		}
		
		for (Map.Entry<Integer, ParamDefault> entry : prototype.defaults.entrySet()) 
		{
			Integer key = entry.getKey();
			ParamDefault value = entry.getValue();
			
			ParamDefault paramDefault = new ParamDefault(value);
			defaults.put(key, paramDefault);
			all.put(key, paramDefault);
		}
		
	}
	
	

	public void handleMessage(ByteBuffer buffer) throws Exception
	{
		long timestamp = System.currentTimeMillis();
		lastMessageTime = timestamp;
		timestamps.add(timestamp);
		int paramsSize = buffer.get();
		for (int i = 0; i < paramsSize; i++)
		{
			int paramKey = buffer.getShort();
			DeviceParam deviceParam = all.get(paramKey);
			if(deviceParam == null)
			{
				throw new Exception("NOT FOUND PARAM FOR KEY = " + paramKey);
			}
			
			if (deviceParam instanceof Param)
			{
				Param param = (Param) deviceParam;
				param.handleMessage(timestamp,buffer);
			}
			else if (deviceParam instanceof ParamRange)
			{
				ParamRange range = (ParamRange) deviceParam;
				range.handleMessage(this,buffer);
			}
			else if (deviceParam instanceof ParamDefault)
			{
				ParamDefault paramDefault = (ParamDefault) deviceParam;
				paramDefault.handleMessage(this,buffer);
			}
			else if (deviceParam instanceof Mfl) 
			{
				Mfl mfl = (Mfl) deviceParam;
				mfl.handleMessage(timestamp,buffer);
			}
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
		all.put(param.id, param);
	}

	
	public void addDefault(String defaultId, String paramId)
	{
		ParamDefault paramDefault = new ParamDefault(defaultId, paramId);
		defaults.put(paramDefault.id, paramDefault);
		all.put(paramDefault.id, paramDefault);
	}
	
	public void addDinamicRange(String rangeId, String paramId, String isMin)
	{
		ParamRange range = new ParamRange(rangeId, paramId, isMin);
		ranges.put(range.id, range);
		all.put(range.id, range);
	}

	public void addMfl(String mflId, String name, String isError)
	{
		Mfl mfl = new Mfl(mflId, name, isError);
		mfls.put(mfl.id, mfl);
		all.put(mfl.id, mfl);
	}

	public void clearRecording() 
	{
		timestamps.clear();
		for (Param param : params.values())
		{
			if(param != null)
			{
				param.records.clear();
			}
		}
	}
	
}