package com.rafael.med.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.TreeSet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.logging.log4j.Logger;

import com.rafael.med.common.bnet.ChannelType;

public class ChannelUtils 
{	
	public static void saveWavFile(byte[] array, File wavFile, AudioFormat audioFormat) throws IOException
	{
		wavFile.getParentFile().mkdirs();
		wavFile.createNewFile();
		
		
		ByteArrayInputStream in = new ByteArrayInputStream(array);
		AudioInputStream audioOutputStream = new AudioInputStream(in, audioFormat, array.length/audioFormat.getFrameSize());
		AudioSystem.write(audioOutputStream, AudioFileFormat.Type.WAVE, wavFile);
		in.close();
		audioOutputStream.close();
	}
	
	
	
	public static boolean[] cutToRange(Logger log, LocalDateTime fromTime, LocalDateTime toTime, TreeSet<CapturedPacket> sourceSet, TreeSet<CapturedPacket> targetSet, boolean[] isExists, long timeStepInSeconds)
	{
		log.debug("cut to range from = {} , to = {} , timeStepInSeconds = {}",fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss),toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss),timeStepInSeconds );
		
		targetSet.clear();
		toTime = toTime.plusSeconds(timeStepInSeconds);
				
		for (int i = 0; i < isExists.length; i++)
		{
			isExists[i] = false;
		}
		
		for (CapturedPacket packet : sourceSet)
		{
			if(packet.localDateTime.isAfter(fromTime)  && packet.localDateTime.isBefore(toTime))
			{
				LocalDateTime current = fromTime;
				int index = 0;
				while (current.isBefore(toTime))
				{
					LocalDateTime next = current.plusSeconds(timeStepInSeconds);					
					isExists[index] = isExists[index] || (packet.localDateTime.isAfter(current)  && packet.localDateTime.isBefore(next));
					if(isExists[index])
					{
						targetSet.add(packet);
					}
					current = next;
					index++;
				}
			}
		}
		return isExists;
	}
	
	public static ChannelType filterRTPPacketToChannelType(CapturedPacket packet)
	{
		for (ChannelType type : ChannelType.values())
		{
			if(packet.dstPort == type.dstPort && ((type.dstPort == ChannelType.LEGACY_TX_RTP.dstPort ) || (type.dstPort == ChannelType.LEGACY_RX_RTP.dstPort  ) || (type.dstPort == ChannelType.GUARD_TX_RTP.dstPort ) || (type.dstPort == ChannelType.GUARD_RX_RTP.dstPort  ))) // legacy Rx,TX, Guard Rx,Tx
			{
				 return type;
			}
			else if(packet.dstPort == type.dstPort && ((type.dstPort == ChannelType.MANET_RX_RTP.dstPort ) || (type.dstPort == ChannelType.SATCOM_RX_RTP.dstPort  ))) // Manet ,Satcom
			{
				if(type.isRx && type.sourceIp.equals(packet.srcIp)) //RX
				{
					return type;
				}
				else if(!type.isRx && !type.sourceIp.equals(packet.srcIp)) //TX
				{
					return type;
				}
			}
		}
		return null;
	}
	
	
//	public ChannelResult createAudioData(ChannelType channelType, TreeSet<CapturedPacket> secondsPackets , LocalDateTime fromTime, LocalDateTime toTime, AudioFormat  audioFormat)
//	{		
//		final Duration duration 					= Duration.between(fromTime, toTime);
//		final long durationInSeconds 				= duration.getSeconds();
//		final int sampleRate						= (int) audioFormat.getSampleRate();
//		final int sampleSizeInBytes					= audioFormat.getSampleSizeInBits()/8;
//		final int channelsNumber					= audioFormat.getChannels();
//		final int timeForSampleInMicroseconds 		= 1_000_000 / sampleRate;
//		final int durationPerByteInMicroseconds		= timeForSampleInMicroseconds / sampleSizeInBytes;
//		final int bytesPerSecond					= sampleRate * sampleSizeInBytes;
//		final int audioByteArrayLength 				= (int) (durationInSeconds * sampleRate * sampleSizeInBytes * channelsNumber);
//				
//		
//		ChannelResult result 	= null;
//		
//		int rtpPayloadPcmLength 	= 0;
//		int jitterInMicroseconds 	= 0;
//		
//		if(!secondsPackets.isEmpty())
//		{
//			result 	= new ChannelResult(channelType);
//			result.startTime 		= fromTime;
//			
//			byte [] audioByteArray 			= new byte[audioByteArrayLength];
//			result.audioByteArray			= audioByteArray;
//			
//			
//			LocalDateTime prevPacketTime 	= null;
//			int index = 0;
//			for (CapturedPacket packet : secondsPackets)
//			{
//				LocalDateTime currentTime 				= packet.localDateTime;
//				
//				Duration durationFromStart 				= Duration.between(fromTime, currentTime);
//				long durationFromStartInMicroseconds 	= durationFromStart.toNanos() / 1_000;
//				if(prevPacketTime == null)
//				{
//					rtpPayloadPcmLength 	= decodeRtpPayloadLengthAndInit(packet,bytesPerSecond);
//					jitterInMicroseconds 	= (rtpPayloadPcmLength * 1_000_000 / bytesPerSecond) * 3;
//					index = (int) (durationFromStartInMicroseconds / durationPerByteInMicroseconds);
//					
//					if(index % 2 != 0)
//					{
//						index = index + 1;
//					}
//					
//					result.startIndex  	= index;
//					result.startTime 	= currentTime;
//				}
//				else
//				{
//					Duration durationfromPrev  				= Duration.between(prevPacketTime, currentTime);
//					long durationFromPrevInMicroseconds  	= durationfromPrev.toNanos() / 1_000;
//					if(durationFromPrevInMicroseconds < jitterInMicroseconds)
//					{
//						index = index + rtpPayloadPcmLength;
//					}
//					else
//					{
//						index = (int) (durationFromStartInMicroseconds / durationPerByteInMicroseconds);
//					}
//					
//					if(index % 2 != 0)
//					{
//						index = index + 1;
//					}
//				}
//				prevPacketTime = currentTime;
//				
//				if(packet.rtpPayload == null) // firstTime to this packet
//				{
//					if(packet.rtpPayloadType == PCM_PAYLOAD_TYPE && packet.rtpPayloadlen == PCM_PAYLOAD_LEN)
//					{
//						packet.rtpPayload = decodePCM(packet);
//					}
//					else if(packet.rtpPayloadType == ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == ILBC_30_PAYLOD_TYPE)
//					{
//						packet.rtpPayload = decodeILBC(packet, ilbc, ilbcIn, ilbcOut);
//					}
//					
//					packet.rtpPayload = decodeRtpPayload(packet);
//				}
//				copyPacketToStream(audioByteArray, packet.rtpPayload, index);	
//			}
//			result.endTime 	= prevPacketTime.plusSeconds(rtpPayloadPcmLength / bytesPerSecond);
//			result.endIndex = index + rtpPayloadPcmLength;
//			
//		}
//		return result;
//	}
	
	
	
//	private int decodeRtpPayloadLengthAndInit(CapturedPacket packet, int bytesPerSecond)
//	{
//		if(packet.rtpPayloadType == PCM_PAYLOAD_TYPE)
//		{
//			return PCM_PAYLOAD_LEN;
//		}
//		else if(packet.rtpPayloadType == ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == ILBC_30_PAYLOD_TYPE)
//		{
//			if(ilbc == null)
//			{
//				if(packet.rtpPayloadType == ILBC_20_PAYLOD_TYPE)
//				{
//					initILBC(ILBC.Mode.MODE_20);
//				}
//				else
//				{
//					initILBC(ILBC.Mode.MODE_30);
//				}
//			}
//			
//			int size = packet.rtpPayloadlen / ilbc.mode.bytes;
//			int i = ilbc.mode.size * 2 * size;
//			System.out.println("--------------------------- " + i);
//			return i;
//		}
//		throw new IllegalStateException("PACKET = " + packet + " HAS NOT CORRECT RTP PAYLOAD TYPE");
//	}
//	
	
	
	public static void copyPacketToStream(byte[] audioByteArray, byte[] rtpPCMPayload, int index)
	{
		int copyLength = rtpPCMPayload.length;
		int remaining = audioByteArray.length - index;
		if(remaining > 0)
		{
			if(remaining < copyLength)
			{
				copyLength = remaining;
			}
			try
			{
				System.arraycopy(rtpPCMPayload, 0, audioByteArray, index, copyLength);
			}
			catch (Exception e)
			{
				System.out.println("---------------------- INDEX = " + index + " REMAING = " + remaining);
			}
		}
	}
	
	
	public static byte[] decodePCM(CapturedPacket packet)
	{
		byte[]  result = new byte[Constants.PCM_PAYLOAD_LEN];
		packet.rtpBuffer.get(result);
		return result;
	}
	
	public static byte[] decodeILBC(CapturedPacket packet, ILBC ilbc, ByteBuffer ilbcIn, ByteBuffer ilbcOut)
	{
		int outSizeInBytes = ilbc.mode.size * 2;
		int size = packet.rtpPayloadlen / ilbc.mode.bytes;
		byte [] result = new byte[outSizeInBytes * size];
		for (int i = 0; i < size; i++)
		{
			ilbcIn.clear();
			ilbcOut.clear();
			
			for (int j = 0; j < ilbc.mode.bytes; j++)
			{
				ilbcIn.put(packet.rtpBuffer.get());
			}
			
			ilbcIn.flip();
			ilbc.decode(ilbcIn, ilbcOut);
			ilbcOut.flip();
			ilbcOut.get(result, i * outSizeInBytes, outSizeInBytes);
		}
		return result;
	}	
}
