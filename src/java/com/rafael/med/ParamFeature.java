package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ParamFeature extends DeviceParam implements Cloneable
{
	
	
	
	
	public final int paramId;
	public final AtomicLong lastTimestamp = new AtomicLong(0);
	protected String feature;
	
	public ParamFeature(int id, int paramId)
	{
		super(id);
		this.paramId = paramId;
	}

	
	public void handleMessage(Device device, long timestamp, ByteBuffer buffer) throws Exception
	{
		int valueType = buffer.get();
		double value = 0;

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
			throw new Exception("WRONG TYPE = " + valueType + "(MUST BE ONLY " + TYPE_FLOAT + " or " + TYPE_INT + ") FOR PARAM " + feature.toUpperCase());
		}

		Param param = device.params.get(paramId);
		if(param == null)
		{
			throw new Exception("NOT FOUND PARAM ID =  " + paramId + " FOR PARAM " + feature.toUpperCase() + " WITH ID = " + id);
		}
		lastTimestamp.set(timestamp);
		handleValue(param, value);
	}
	
	abstract void handleValue(Param param, double value);
	
	public static final class ParamAlarm extends ParamFeature 
	{
	
		public ParamAlarm(int id, int paramId)
		{
			super(id,paramId);
			this.feature	= this.getClass().getName();
		}
			
		@Override
		protected ParamAlarm clone()
		{
			return new ParamAlarm(this.id, this.paramId);
		}
		
		@Override
		public void handleValue(Param param, double value)
		{
			param.isAlarm.set((value == 0) ? false : true);
		}
	}
	
	public static final class ParamDefault extends ParamFeature 
	{
		
		public ParamDefault(int id, int paramId)
		{
			super(id,paramId);
			this.feature	= this.getClass().getName();
		}
		
		@Override
		protected ParamDefault clone() 
		{
			return new ParamDefault(this.id, this.paramId);
		}
		
		@Override
		public void handleValue(Param param, double value)
		{
			param.defaultValue = value;
			param.isDefaultSet.set(true);
		}
	}
	
	public static final class ParamRange extends ParamFeature
	{
		
		public final boolean isMin;
		
		public ParamRange(int id, int paramId, boolean isMin)
		{
			super(id,paramId);
			this.isMin = isMin;
			this.feature	= this.getClass().getName();
		}
		
		@Override
		protected ParamRange clone()
		{
			return new ParamRange(this.id, this.paramId, this.isMin);
		}

		@Override
		public void handleValue(Param param, double value)
		{
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
}
