package com.rafael.med;

import java.nio.ByteBuffer;

public class ParamRange extends DeviceParam
{
	public final int paramId;
	public final boolean isMin;
	
	public double value;
	
	
	public ParamRange(String id, String paramId, String isMin)
	{
		super(Integer.parseInt(id));
		this.paramId 		= Integer.parseInt(paramId);
		this.isMin			= Boolean.parseBoolean(isMin);
	}
	
	public ParamRange(ParamRange prototype)
	{
		super(prototype.id);
		this.paramId 		= prototype.paramId;
		this.isMin			= prototype.isMin;
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
			throw new Exception("WRONG TYPE FOR VALUE TYPE OF DYNAMIC RANGE " + valueType);
		}
		
		Param param = device.params.get(paramId);
		if(param == null)
		{
			throw new Exception("NOT FOUND PARAM ID =  " + paramId + " FOR DYNAMIC RANGE WITH ID = " + id);
		}
		
		if(isMin)
		{
			param.minValue = value;
			param.isMinSet.set(true);
		}
		else
		{
			param.maxValue = value;
			param.isMaxSet.set(true);
		}
		
	}
}
