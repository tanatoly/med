package com.rafael.med.common.bnet;

import java.io.File;
import java.time.LocalDateTime;

public class TelemetryData 
{
	public final BnetData bnetData;
	public LocalDateTime fromTime				= LocalDateTime.MAX;
	public LocalDateTime toTime					= LocalDateTime.MIN;	
	
	public TelemetryData(BnetData bnetData) 
	{
		this.bnetData = bnetData;
	}

	public void checkAndAdd(File file)
	{
		
	}

	public void complete()
	{
		
	}

}
