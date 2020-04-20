package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ParamFeature extends DeviceParam implements Cloneable
{
	public final int paramId;
	public final AtomicLong lastTimestamp = new AtomicLong(0);
	
	public ParamFeature(int id, int paramId)
	{
		super(id);
		this.paramId = paramId;
	}

	
	abstract void handleMessage(Device device, long timestamp, ByteBuffer buffer) throws Exception;
	
	
	public static final class ParamAlarm extends ParamFeature 
	{
	
		public ParamAlarm(int id, int paramId)
		{
			super(id,paramId);
		}
			
		@Override
		protected ParamAlarm clone()
		{
			return new ParamAlarm(this.id, this.paramId);
		}
		
		@Override
		public void handleMessage(Device device, long timestamp, ByteBuffer buffer) throws Exception
		{
			int valueType = buffer.get();
			int value = 0;
			if(valueType == TYPE_INT)
			{
				value = buffer.getInt();
			}
			else
			{
				throw new Exception("WRONG TYPE FOR VALUE TYPE OF ALARM " + valueType);
			}
				
			Param param = device.params.get(paramId);
			if(param == null)
			{
				throw new Exception("NOT FOUND PARAM ID =  " + paramId + " FOR ALARM WITH ID = " + id);
			}
			param.isAlarm.set((value == 0) ? false : true);
			lastTimestamp.set(timestamp);
		}
	}
	
	public static final class ParamDefault extends ParamFeature 
	{
		
		public ParamDefault(int id, int paramId)
		{
			super(id,paramId);
		}
		
		@Override
		protected ParamDefault clone() 
		{
			return new ParamDefault(this.id, this.paramId);
		}
		
		@Override
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
				throw new Exception("WRONG TYPE FOR VALUE TYPE OF DEFAULT " + valueType);
			}
			
			Param param = device.params.get(paramId);
			if(param == null)
			{
				throw new Exception("NOT FOUND PARAM ID =  " + paramId + " FOR DEFAULT WITH ID = " + id);
			}
			
			param.defaultValue = value;
			param.isDefaultSet.set(true);
			lastTimestamp.set(timestamp);
		}
	}
	
	public static final class ParamRange extends ParamFeature
	{
		
		public final boolean isMin;
		
		public ParamRange(int id, int paramId, boolean isMin)
		{
			super(id,paramId);
			this.isMin = isMin;
		}
		
		@Override
		protected ParamRange clone()
		{
			return new ParamRange(this.id, this.paramId, this.isMin);
		}

		@Override
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
			lastTimestamp.set(timestamp);
		}
	}
}
