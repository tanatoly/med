package com.rafael.med.debrief;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class RowResult
{
	public ByteBuffer mixerBuffer;
	public ChannelResult [] channelResults;
	public TelemetryData telemetry;
	public File dir;
	public LocalDateTime fromTime;
	public LocalDateTime toTime;
	public long durationInSeconds;
	public long bytesPerSecond;
	
	public RowResult(int channels)
	{
		channelResults = new ChannelResult[channels];	
	}

}
