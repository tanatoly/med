package com.rafael.med;

import java.nio.ByteBuffer;

public class ParamDefault extends DeviceParam 
{
	public final int paramId;
	
	public double value;
	
	public ParamDefault(String id, String paramId)
	{
		super(Integer.parseInt(id));
		this.paramId 		= Integer.parseInt(paramId);
	}
	
	public ParamDefault(ParamDefault prototype)
	{
		super(prototype.id);
		this.paramId 		= prototype.paramId;
	}
	
	public void handleMessage(Device device, ByteBuffer buffer) throws Exception
	{
		int valueType = buffer.get();

		if(valueType == TYPE_FLOAT)
		{
			value = buffer.getFloat();
		}
		else if(valueType == TYPE_INT)
		{
			value = buffer.getInt();
		}
		else
		{
			throw new Exception("WRONG TYPE FOR VALUE TYPE OF DEFAULT " + valueType);
		}
		
		Param param = device.params.get(paramId);
		if(param == null)
		{
			throw new Exception("NOT FOUND PARAM ID =  " + paramId + " FOR DEFAULT WITH ID = " + id);
		}
		
		param.defaultValue = value;
		param.isDefaultSet.set(true);
	}
}
