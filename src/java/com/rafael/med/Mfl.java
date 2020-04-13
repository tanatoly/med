package com.rafael.med;

import java.nio.ByteBuffer;

public class Mfl extends DeviceParam
{
	public final boolean isError;
	public int value;
	public String valueString;
	public final String name;
	
	public Mfl(String id, String name,String isError)
	{
		super(Integer.parseInt(id));
		this.isError = Boolean.parseBoolean(isError);
		this.name = name;
	}
	
	public Mfl(Mfl prototype)
	{
		super(prototype.id);
		this.isError 	= prototype.isError;
		this.name = prototype.name;
	}

	public void handleMessage(ByteBuffer buffer) throws Exception
	{
		int valueType = buffer.get();

		if(valueType == TYPE_INT)
		{
			value = buffer.getInt();
		}
		else
		{
			throw new Exception("WRONG TYPE FOR VALUE TYPE OF MFL " + valueType);
		}
	}
}
