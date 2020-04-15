package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
//github.com/tanatoly/med.git
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

	
	public AtomicBoolean isMinSet = new AtomicBoolean(false);
	public AtomicBoolean isMaxSet = new AtomicBoolean(false);
	
	public final Map<Long, Object> records = new HashMap<>(1000);
	
	

	
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
		if(isMinSet.get() && isMaxSet.get())
		{
			return "[" + df.format(minValue) + " : " + df.format(maxValue) + "]";
		}
		else if (isMinSet.get())
		{
			return "[" + df.format(minValue) + " : ]";
		}
		else if (isMaxSet.get())
		{
			return "[ : " + df.format(maxValue) + "]";
		}
		else
		{
			return StringUtils.EMPTY;
		}
	}	

	public void handleMessage(long timestamp, ByteBuffer buffer) 
	{
		int valueType = buffer.get();

		boolean isNowWarning = false;
		if(valueType == TYPE_FLOAT)
		{
			float v = buffer.getFloat();
			valueFloat = v;
			isNowWarning = isNowWarming(valueFloat);
			records.put(timestamp, v);
		}
		else if(valueType == TYPE_INT)
		{
			int v = buffer.getInt();
			valueInt = v;
			isNowWarning = isNowWarming(valueInt);
			records.put(timestamp, v);
		}
		else if(valueType == TYPE_STRING)
		{
			int stringLength 	= buffer.get();
			byte[] bytes 		= new byte[stringLength];
			buffer.get(bytes);
			String v = new String(bytes);
			valueString = v;
			records.put(timestamp, v);
		}
		isWarning.compareAndSet(!isNowWarning, isNowWarning);
	}

	
	private boolean isNowWarming(double value)
	{
		if(isMinSet.get() && isMaxSet.get())
		{
			return value < minValue || value > maxValue;
		}
		else if (isMinSet.get())
		{
			return value < minValue;
		}
		else if (isMaxSet.get())
		{
			return value > maxValue;
		}
		else
		{
			return value < minValue || value > maxValue;
		}
	}

}