package com.rafael.med.common.bnet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

import com.rafael.med.common.CapturedPacket;

public class VoiceRTPChannel extends VoiceChannel
{
	public TreeSet<CapturedPacket> 	rtpPackets	= new TreeSet<>();
	
	public VoiceRTPChannel(VoiceData voiceData, ChannelType channelType)
	{
		super(voiceData, channelType);
	}

	@Override
	public LocalDateTime getFromTime() 
	{
		if(isExists())
		{
			return rtpPackets.first().localDateTime;
		}
		return null;
	}

	@Override
	public LocalDateTime getToTime()
	{
		if(isExists())
		{
			return rtpPackets.last().localDateTime;
		}
		return null;
	}

	@Override
	public void addPacket(VoicePacket packet) 
	{
		if (packet instanceof CapturedPacket) 
		{
			CapturedPacket rtpPacket = (CapturedPacket) packet;
			rtpPackets.add(rtpPacket);
		}
	}

	

	@Override
	public void complete() 
	{
		segments.clear();
		//System.out.println("********************************* " + this + " started");
		LocalDateTime previous = LocalDateTime.MIN;
		VoiceSegment currentSegment = null;
		int index = 1;
		for (CapturedPacket packet : rtpPackets) 
		{
			LocalDateTime current 	= packet.localDateTime;
			long seconds 			= Duration.between(previous, current).toSeconds();
			if(seconds > 2)
			{
				
				if(currentSegment == null)
				{
					currentSegment = new VoiceSegment(this,index,current);
				}
				else
				{
					currentSegment.setToTime(previous);
					segments.add(currentSegment);
					index++;
					currentSegment = new VoiceSegment(this,index,current);
				}
			}
			previous = current;
		}
		if(currentSegment != null)
		{
			currentSegment.setToTime(previous);
		}
		//System.out.println("********************************* " + this + " finished " + segments);
	}
}
