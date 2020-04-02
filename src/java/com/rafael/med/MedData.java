package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MedData 
{
	public static class Param
	{
		private static final int TYPE_FLOAT 	= 1;
		private static final int TYPE_INT 		= 2;
		private static final int TYPE_STRING 	= 3;
		
		
		public int key;
		public String name;
		public int valueType;
		public float valueFloat;
		public int valueInt;
		public String valueString;
		public double minValue;
		public double maxValue;
		public String units;
		
		public String getValue()
		{
			return "value";
		}

		public void handleMessage(ByteBuffer buffer) 
		{
			int valueType = buffer.get();
			if(valueType == TYPE_FLOAT)
			{
				valueFloat = buffer.getFloat();
			}
			else if(valueType == TYPE_INT)
			{
				valueInt = buffer.getInt();
			}
			else if(valueType == TYPE_STRING)
			{
				int stringLength 	= buffer.getShort();
				byte[] bytes 		= new byte[stringLength];
				buffer.get(bytes);
				valueString = new String(bytes);
			}
		}
	}
	
	public class Device 
	{
		public int type;
		public String typeName;
		public String serial;
		public String error;
		public boolean isWorking;
		
		
		public final Map<Integer, Param> params = new LinkedHashMap<>();


		public void handleMessage(int deviceType, ByteBuffer buffer) throws Exception
		{
			if(type != deviceType)
			{
				throw new Exception("UNEXPECTED DEVICE TYPE " + deviceType + " (SERIAL  = " + serial + " , TYPE = " + type);
			}
			int paramsSize = buffer.get();
			for (int i = 0; i < paramsSize; i++)
			{
				int paramKey = buffer.getShort();
				Param param = params.get(paramKey);
				if(param == null)
				{
					throw new Exception("NOT FOUND PARAM FOR KEY = " + paramKey);
				}
				param.handleMessage(buffer);
			}
		}
	}
	
	public static final class Bed
	{
		public final String number;
		public final Map<String,Device> devices = new HashMap<>(8);
		
		public String patientName;
		public String patientId;
		public Bed(String number)
		{
			this.number = number;
		}
		public void handleMessage(ByteBuffer buffer) throws Exception
		{
			int deviceType 		= buffer.get();
			int serialLength	= buffer.get();
			byte[] serialBytes	= new byte[serialLength];
			buffer.get(serialBytes);
			String serial		= new String(serialBytes);
			
			Device device = devices.get(serial);
			if(device == null)
			{
				throw new Exception("NOT FOUND DEVICE FOR SERIAL = " + serial);
			}
			device.handleMessage(deviceType,buffer);
		}
	}
	
	public static final class Department
	{
		public final String name;
		public final List<Bed> beds = new ArrayList<>(21);
		public Department(String name)
		{
			this.name = name;
		}
	}
	
	
	public final Map<String, Department> 	departments 	= new TreeMap<>();
	public final Map<Integer, Bed>		 	allBeds			= new TreeMap<>();
	public final List<Bed> 					emergencyBeds 	= new ArrayList<>(42);
	
	
	
	public void handleMessage(ByteBuffer buffer, Integer bedNumber) throws Exception
	{
		Bed bed = allBeds.get(bedNumber);
		if(bed == null)
		{
			throw new Exception("NOT FOUND BED FOR NUMBER = " + bedNumber);
		}
		bed.handleMessage(buffer);
	}
}
