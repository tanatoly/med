package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

public class Param extends DeviceParam
{
	
	public final String name;
	public final int valueType;
	public float valueFloat;
	public int valueInt;
	public String valueString;
	public double minValue;
	public double maxValue;
	public final String units;
	public final int presision;
	public boolean isRegular;
	public boolean isAlarm;
	
	public AtomicBoolean isWarning = new AtomicBoolean(false);
	public double defaultValue;
	
	public AtomicBoolean isDefaultSet = new AtomicBoolean(false);
	public AtomicBoolean isRangeSet = new AtomicBoolean(false);
	
	public Param(String id, String name, String valueType, String presision, String units, String min, String max, String regular, String alarm)
	{
		super(Integer.parseInt(id));
		this.name 			= name;
		this.valueType		= Integer.parseInt(valueType);
		this.presision		= Integer.parseInt(presision);
		this.units			= units;
		if(StringUtils.isNotBlank(min))
		{
			minValue = Double.parseDouble(min);
		}
		if(StringUtils.isNotBlank(max))
		{
			maxValue = Double.parseDouble(max);
		}
		
		if(StringUtils.isNotBlank(regular))
		{
			isRegular = Boolean.parseBoolean(regular);
		}
		if(StringUtils.isNotBlank(alarm))
		{
			isAlarm = Boolean.parseBoolean(alarm);
		}
	}

	public Param(Param prototype) 
	{
		super(prototype.id);
		this.name 			= prototype.name;
		this.valueType		= prototype.valueType;
		this.presision		= prototype.presision;
		this.units			= prototype.units;
		this.minValue 		= prototype.minValue;
		this.maxValue 		= prototype.maxValue;
		this.isRegular 		= prototype.isRegular;
		this.isAlarm 		= prototype.isAlarm;
	}

	public String getValue()
	{
		if(valueType == TYPE_FLOAT)
		{
			return df.format(valueFloat);
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
	
	public String getDefaultValue()
	{
		if(isDefaultSet.get())
		{
			return "(" + df.format(defaultValue) + ")";
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}
	
	public String getRange()
	{
		if(isRangeSet.get())
		{
			return "[" + df.format(minValue) + " : " + df.format(maxValue) + "]";
		}
		else
		{
			return StringUtils.EMPTY;
		}
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
			isNowWarning = valueInt < minValue || valueInt > maxValue;
		}
		else if(valueType == TYPE_STRING)
		{
			int stringLength 	= buffer.get();
			byte[] bytes 		= new byte[stringLength];
			buffer.get(bytes);
			valueString = new String(bytes);
		}
		isWarning.compareAndSet(!isNowWarning, isNowWarning);
	}

	
}