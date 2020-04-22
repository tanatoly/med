package com.rafael.med;

import java.text.DecimalFormat;
import java.util.Objects;

public abstract class DeviceParam 
{
	public static final int TYPE_FLOAT 	= 1;
	public static final int TYPE_INT 		= 2;
	public static final int TYPE_STRING 	= 3;
	
	public DecimalFormat df = new DecimalFormat("#.00"); 
	
	
	public final int id;
	public DeviceParam(int id)
	{
		this.id = id;
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(id);
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
		DeviceParam other = (DeviceParam) obj;
		return id == other.id;
	}
}
