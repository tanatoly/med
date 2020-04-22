package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rafael.med.Chart.ChartRange;
import com.rafael.med.Chart.ChartStep;
import com.rafael.med.Chart.ChartValue;
import com.rafael.med.ParamFeature.ParamAlarm;
import com.rafael.med.ParamFeature.ParamDefault;
import com.rafael.med.ParamFeature.ParamRange;

public class Device 
{
	public final int type;
	public final String name;
	public String serial;
	
	public long lastMessageTime = 0;
	
	public final Map<Integer, Param> 	params 			= new LinkedHashMap<>();
	public final Map<Integer, Mfl> 		mfls 			= new LinkedHashMap<>();
	public final Map<Integer, Chart> 	charts 			= new LinkedHashMap<>();
	
	public final Map<Integer, DeviceParam> all			= new HashMap<>();
	
	
	
	public final List<Long> timestamps = new ArrayList<>(1000);
	
	
	
	public Device(String type, String name) // prototype device
	{
		this.type 	= Integer.parseInt(type);
		this.name 	= name;
	}

	public Device(Device prototype, String serial)  // existing device
	{
		this.type 	= prototype.type;
		this.name 	= prototype.name;
		this.serial = serial;
		

		for (Map.Entry<Integer, DeviceParam> entry : prototype.all.entrySet()) 
		{
			Integer key 		= entry.getKey();
			DeviceParam value 	= entry.getValue();
			all.put(key, value);
			
			if (value instanceof Param) 
			{
				Param param = (Param) value;
				params.put(key, param);
			}
			else if (value instanceof Mfl) 
			{
				Mfl mfl = (Mfl) value;
				mfls.put(key, mfl);
			}
			else if (value instanceof Chart) 
			{
				Chart chart = (Chart) value;
				charts.put(key, chart);
			}
		}
	}
	
	

	public void handleMessage(ByteBuffer buffer) throws Exception
	{
		long timestamp = System.currentTimeMillis();
		lastMessageTime = timestamp;
		timestamps.add(timestamp);
		int paramsSize = buffer.get();
		for (int i = 0; i < paramsSize; i++)
		{
			int paramKey = buffer.getShort();
			DeviceParam deviceParam = all.get(paramKey);
			if(deviceParam == null)
			{
				throw new Exception("NOT FOUND PARAM FOR KEY = " + paramKey);
			}
			
			if (deviceParam instanceof Param)
			{
				Param param = (Param) deviceParam;
				param.handleMessage(timestamp,buffer);
			}
			else if (deviceParam instanceof ParamRange)
			{
				ParamRange range = (ParamRange) deviceParam;
				range.handleMessage(this, timestamp, buffer);
			}
			else if (deviceParam instanceof ParamDefault)
			{
				ParamDefault paramDefault = (ParamDefault) deviceParam;
				paramDefault.handleMessage(this,timestamp,buffer);
			}
			else if (deviceParam instanceof ParamAlarm) 
			{
				ParamAlarm paramAlarm = (ParamAlarm) deviceParam;
				paramAlarm.handleMessage(this,timestamp,buffer);
			}
			else if (deviceParam instanceof Mfl) 
			{
				Mfl mfl = (Mfl) deviceParam;
				mfl.handleMessage(timestamp,buffer);
			}
			
			else if (deviceParam instanceof ChartRange)
			{
				ChartRange chartRange = (ChartRange) deviceParam;
				chartRange.handleMessage(this, timestamp, buffer);
			}
			else if (deviceParam instanceof ChartStep)
			{
				ChartStep chartStep = (ChartStep) deviceParam;
				chartStep.handleMessage(this,timestamp,buffer);
			}
			else if (deviceParam instanceof ChartValue) 
			{
				ChartValue chartValue = (ChartValue) deviceParam;
				chartValue.handleMessage(this,timestamp,buffer);
			}
		}
	}


	
	
	
	
	public void addParam(String paramId, String paramName, String paramType, String presision, String units, String min,String max, String regular) 
	{
		Param param = new Param(paramId, paramName, paramType, presision, units, min, max , regular);
		params.put(param.id, param);
		all.put(param.id, param);
	}

	public void addParamDefault(String defaultId, String paramId)
	{
		ParamDefault paramDefault = new ParamDefault(Integer.parseInt(defaultId), Integer.parseInt(paramId));
		all.put(paramDefault.id, paramDefault);
	}
	
	public void addParamRange(String rangeId, String paramId, String isMin)
	{
		ParamRange range = new ParamRange(Integer.parseInt(rangeId), Integer.parseInt(paramId), Boolean.parseBoolean(isMin));
		all.put(range.id, range);
	}
	public void addParamAlarm(String alarmId, String paramId) 
	{
		ParamAlarm alarm = new ParamAlarm(Integer.parseInt(alarmId), Integer.parseInt(paramId));
		all.put(alarm.id, alarm);
	}
	
	public void addMfl(String mflId, String name, String isError)
	{
		Mfl mfl = new Mfl(mflId, name, isError);
		mfls.put(mfl.id, mfl);
		all.put(mfl.id, mfl);
	}
	
	

	public void addChart(String chartId, String chartName, String chartLabelX, String chartLabelY)
	{
		Chart chart = new Chart(Integer.parseInt(chartId), chartName,chartLabelX , chartLabelY);
		charts.put(chart.id, chart);
		all.put(chart.id, chart);
	}

	public void addChartRange(String id, String chartId, String isAxisX, String isMin)
	{
		ChartRange chartRange = new ChartRange(Integer.parseInt(id), Integer.parseInt(chartId), Boolean.parseBoolean(isMin), Boolean.parseBoolean(isAxisX));
		all.put(chartRange.id, chartRange);
		
	}

	public void adChartStep(String id, String chartId, String isAxisX)
	{
		ChartStep chartStep = new ChartStep(Integer.parseInt(id), Integer.parseInt(chartId), Boolean.parseBoolean(isAxisX));
		all.put(chartStep.id, chartStep);
	}

	public void addChartValue(String id, String chartId)
	{
		ChartValue chartValue = new ChartValue(Integer.parseInt(id), Integer.parseInt(chartId));
		all.put(chartValue.id, chartValue);
	}

	public void clearRecording() 
	{
		timestamps.clear();
		for (Param param : params.values())
		{
			if(param != null)
			{
				param.records.clear();
			}
		}
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((serial == null) ? 0 : serial.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serial == null) {
			if (other.serial != null)
				return false;
		} else if (!serial.equals(other.serial))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return String.format("Device [type=%s, name=%s, serial=%s]", type, name, serial);
	}
}