package com.rafael.med;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;

import org.apache.commons.lang3.StringUtils;

public class Chart extends DeviceParam
{
	private static final String S_S_S_STEP_S_MESSAGE_S_MS = "%s [%s : %s], step %s, message %03d ms";
	public double maxX 	= Double.MAX_VALUE;
	public double minX 	= Double.MAX_VALUE;
	public double maxY 	= Double.MAX_VALUE;
	public double minY 	= Double.MAX_VALUE;
	public double stepX = Double.MAX_VALUE;
	public double stepY = Double.MAX_VALUE;
	
	
	private String name;
	public boolean isChartReady = false;
	public List<Double> data = new ArrayList<>();
	public String labelX;
	public String labelY;
	
	public int modulus = 1;
		
	private long lastTimeUpdate = 0;
	private long delta = 0;
	
	public Chart(int id, String name, String labelX, String labelY) 
	{
		super(id);
		this.name = name;
		this.labelX = labelX;
		this.labelY = labelY;
	}
	
	public void setNextValue(double value)
	{
		if(isChartReady )
		{
			if(lastTimeUpdate > 0)
			{
				delta = System.currentTimeMillis() - lastTimeUpdate;
			}
			data.add(value);
			lastTimeUpdate = System.currentTimeMillis();
		}
	}
	
	public String getFullName()
	{
		
		return String.format(S_S_S_STEP_S_MESSAGE_S_MS , name, minX , maxX, stepX, delta);
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	@Override
	public String toString()
	{
		return String.format("Chart [id= %s,maxX=%s, minX=%s, maxY=%s, minY=%s, stepX=%s, stepY=%s, name=%s]", id, maxX, minX, maxY, minY, stepX, stepY, name);
	}



	public static abstract class ChartFeature extends DeviceParam implements Cloneable
	{
		public final int chartId;
		public final AtomicLong lastTimestamp = new AtomicLong(0);
		protected String feature;

		public ChartFeature(int id, int chartId)
		{
			super(id);
			this.chartId = chartId;
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
				throw new Exception("WRONG TYPE = " + valueType + "(MUST BE ONLY " + TYPE_FLOAT + " or " + TYPE_INT + ") FOR CHART " + feature.toUpperCase());
			}

			Chart chart = device.charts.get(chartId);
			if(chart == null)
			{
				throw new Exception("NOT FOUND CHART ID =  " + chartId + " FOR CHART " + feature.toUpperCase() + " WITH ID = " + id);
			}
			lastTimestamp.set(timestamp);
			handleValue(chart, value);
			
			if(!chart.isChartReady)
			{
				chart.isChartReady = (chart.maxX < Double.MAX_VALUE && chart.minX < Double.MAX_VALUE && chart.maxX < Double.MAX_VALUE && chart.minY < Double.MAX_VALUE && chart.stepX < Double.MAX_VALUE && chart.stepX < Double.MAX_VALUE);
			}
			
		}

		abstract void handleValue(Chart chart, double value);
	}
	
	
	public static final class ChartRange extends ChartFeature
	{
		
		public final boolean isMin;
		public final boolean isAxisX;
		
		public ChartRange(int id, int chartId, boolean isMin, boolean isAxisX)
		{
			super(id,chartId);
			this.isMin 		= isMin;
			this.isAxisX 	= isAxisX;
			this.feature	= this.getClass().getName();
		}
		
		@Override
		protected ChartRange clone()
		{
			return new ChartRange(this.id, this.chartId, this.isMin, this.isAxisX);
		}

		@Override
		public void handleValue(Chart chart, double value)
		{
			if(isAxisX)
			{
				if(isMin)
				{
					chart.minX = value;
				}
				else
				{
					chart.maxX = value;
				}
			}
			else
			{
				if(isMin)
				{
					chart.minY = value;
				}
				else
				{
					chart.maxY = value;
				}
			}
		}
	}
	
	public static final class ChartStep extends ChartFeature
	{
		public final boolean isAxisX;
		
		public ChartStep(int id, int chartId, boolean isAxisX)
		{
			super(id,chartId);
			this.isAxisX 	= isAxisX;
		}
		
		@Override
		protected ChartStep clone()
		{
			return new ChartStep(this.id, this.chartId, this.isAxisX);
		}

		@Override
		public void handleValue(Chart chart, double value)
		{
			if(isAxisX)
			{
				chart.stepX = value;
			}
			else
			{
				chart.stepY = value;
			}
		}
	}
	
	public static final class ChartValue extends ChartFeature
	{
		
		public ChartValue(int id, int chartId)
		{
			super(id,chartId);
		}
		
		@Override
		protected ChartValue clone()
		{
			return new ChartValue(this.id, this.chartId);
		}

		@Override
		public void handleValue(Chart chart, double value)
		{	
			chart.setNextValue(value);
		}
	}

	

	
}


	
