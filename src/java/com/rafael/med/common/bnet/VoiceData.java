package com.rafael.med.common.bnet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import com.rafael.med.common.CapturedPacket;
import com.rafael.med.common.ChannelUtils;
import com.rafael.med.common.Constants;
import com.rafael.med.common.Utilities;

public class VoiceData 
{
	public final BnetData	bnetData;
	public LocalDateTime fromTime			= LocalDateTime.MAX;
	public LocalDateTime toTime				= LocalDateTime.MIN;		

	public Map<ChannelType, VoiceChannel> channels	= new LinkedHashMap<>(ChannelType.values().length);
	
	
	public VoiceData(BnetData bnetData)
	{
		this.bnetData = bnetData;
		
		TreeSet<ChannelType> treeset = ChannelType.getTreeset();
		
		for (ChannelType channelType : treeset)
		{
			VoiceChannel voiceChannel = channelType.point == ChannelPoint.RTP ? new VoiceRTPChannel(this, channelType) : new VoicePCMChannel(this,channelType);
			channels.put(channelType, voiceChannel);
		}
	}
	
	
	public void checkAndAdd(File file) throws IOException
	{
		if(file.getName().contains(".pcap"))
		{
			ByteBuffer pcapFileBuffer = Utilities.readFile(file,ByteOrder.LITTLE_ENDIAN);
			pcapFileBuffer.flip();
			pcapFileBuffer.position(Constants.PCAP_GLOBAL_HEADER_LENGTH);
			while(pcapFileBuffer.hasRemaining())
			{
				int remaining = pcapFileBuffer.remaining();
				if (remaining >= Constants.PCAP_PACKET_HEADER_LENGTH)
				{
					CapturedPacket packet = CapturedPacket.createFromBuffer(pcapFileBuffer);
					if(packet != null)
					{
						ChannelType channelType = ChannelUtils.filterRTPPacketToChannelType(packet);
						if(channelType != null)
						{
							VoiceChannel voiceChannel = channels.get(channelType);
							if(voiceChannel != null)
							{
								voiceChannel.addVoicePacket(packet);	
							}
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}	
			}
		}
		else if(file.getName().contains(".rdat"))
		{
			
		}
	}


	public void complete() 
	{
		for (VoiceChannel voiceChannel : channels.values())
		{
			if(voiceChannel != null && voiceChannel.isExists())
			{
				LocalDateTime currentFromTime 	= voiceChannel.getFromTime();
				LocalDateTime currentToTime 	= voiceChannel.getToTime();
				
				if(currentFromTime.isBefore(fromTime))
				{
					fromTime = currentFromTime;
				}
				
				if(currentToTime.isAfter(toTime))
				{
					toTime = currentToTime;
				}
				
				voiceChannel.complete();
			}
		}
	}
}
