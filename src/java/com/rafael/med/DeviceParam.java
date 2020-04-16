package com.rafael.med;

import java.text.DecimalFormat;

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
}
