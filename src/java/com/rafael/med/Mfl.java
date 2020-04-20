package com.rafael.med;

import java.nio.ByteBuffer;

public class Mfl extends DeviceParam
{
	public int value;
	public String valueString;
	public final String name;
	
	public Mfl(String id, String name,String isError)
	{
		super(Integer.parseInt(id));
		this.name = name;
	}
	
	public Mfl(Mfl prototype)
	{
		super(prototype.id);
		this.name = prototype.name;
	}
	public void handleMessage(long timestamp, ByteBuffer buffer) throws Exception

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
