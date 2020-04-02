package com.rafael.med.common.bnet;

import java.time.LocalDateTime;
import java.util.TreeSet;

import com.rafael.med.common.PCMPacket;

public class VoicePCMChannel extends VoiceChannel
{

	public TreeSet<PCMPacket> 		pcmPackets	= new TreeSet<>();
	
	public VoicePCMChannel(VoiceData voiceData, ChannelType channelType) 
	{
		super(voiceData,channelType);
	}

	
	@Override
	public LocalDateTime getFromTime() 
	{
		if(isExists())
		{
			return pcmPackets.first().localDateTime;
		}
		return null;
	}

	@Override
	public LocalDateTime getToTime()
	{
		if(isExists())
		{
			return pcmPackets.last().localDateTime;
		}
		return null;
	}
	
	@Override
	public void addPacket(VoicePacket packet) 
	{
		if (packet instanceof PCMPacket) 
		{
			PCMPacket  pcmPacket = (PCMPacket) packet;
			pcmPackets.add(pcmPacket);
		}
	}


	@Override
	public void complete() 
	{
		
		
	}

}
