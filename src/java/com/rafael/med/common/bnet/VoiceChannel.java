package com.rafael.med.common.bnet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public  abstract class VoiceChannel 
{
	public final ChannelType channelType;
	public final VoiceData	voiceData;
	private final AtomicBoolean isExists				= new AtomicBoolean(false);
	public final List<VoiceSegment> segments			= new ArrayList<>();
	
	
	public VoiceChannel(VoiceData voiceData, ChannelType channelType)
	{
		this.voiceData		= voiceData;
		this.channelType 	= channelType;
	}
	
	public void addVoicePacket(VoicePacket packet)
	{
		isExists.compareAndSet(false, true);
		addPacket(packet);
	}
	
	public boolean isExists()
	{
		return isExists.get();
	}
	
	public List<VoiceSegment> getSegments()
	{
		return segments;
	}
	

	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("VoiceChannel [channelType=");
		builder.append(channelType);
		builder.append(", isExists=");
		builder.append(isExists);
		builder.append(", segments=");
		builder.append(segments);
		builder.append("]");
		return builder.toString();
	}
	
	public abstract LocalDateTime getFromTime();
	public abstract LocalDateTime getToTime();
	public abstract void addPacket(VoicePacket packet);
	public abstract void complete();
	
}
