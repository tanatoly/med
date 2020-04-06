package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

public class Param
{
	private static final int TYPE_FLOAT 	= 1;
	private static final int TYPE_INT 		= 2;
	private static final int TYPE_STRING 	= 3;
	
	
	public int id;
	public String name;
	public int valueType;
	public float valueFloat;
	public int valueInt;
	public String valueString;
	public double minValue;
	public double maxValue;
	public String units;
	public int presision;
	public boolean isInRegularModule;
	public boolean isInEmergencyModule;
	
	public AtomicBoolean isWarning = new AtomicBoolean(false);
	public long firstTimeWarning;
	
	public Param(String id, String name, String valueType, String presision, String units, String min, String max, String regular, String alarm)
	{
		this.id 			= Integer.parseInt(id);
		this.name 			= name;
		this.valueType		= Integer.parseInt(valueType);
		this.presision		= Integer.parseInt(presision);
		this.units			= units;
		if(StringUtils.isNumeric(min))
		{
			minValue = Double.parseDouble(min);
		}
		if(StringUtils.isNumeric(max))
		{
			maxValue = Double.parseDouble(max);
		}
		
		if(StringUtils.isNotBlank(regular))
		{
			isInRegularModule = Boolean.parseBoolean(regular);
		}
		if(StringUtils.isNotBlank(alarm))
		{
			isInEmergencyModule = Boolean.parseBoolean(alarm);
		}
	}

	public String getValue()
	{
		if(valueType == TYPE_FLOAT)
		{
			return String.valueOf(valueFloat);
		}
		else if(valueType == TYPE_INT)
		{
			return String.valueOf(valueInt);
		}
		else if(valueType == TYPE_STRING)
		{
			return valueString;
		}
		return null;
	}

	public void handleMessage(ByteBuffer buffer) 
	{
		int valueType = buffer.get();
		boolean isNowWarning = false;
		if(valueType == TYPE_FLOAT)
		{
			valueFloat = buffer.getFloat();
			isNowWarning = valueFloat < minValue || valueFloat > maxValue;
			
		}
		else if(valueType == TYPE_INT)
		{
			valueInt = buffer.getInt();
			isNowWarning = valueFloat < minValue || valueFloat > maxValue;
		}
		else if(valueType == TYPE_STRING)
		{
			int stringLength 	= buffer.get();
			byte[] bytes 		= new byte[stringLength];
			buffer.get(bytes);
			valueString = new String(bytes);
		}
		
		if(isWarning.compareAndSet(!isNowWarning, isNowWarning))
		{
			long time = (isNowWarning) ? System.currentTimeMillis() : 0;
			firstTimeWarning = time;
		}
	}
}