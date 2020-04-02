package com.rafael.med.common.bnet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.sound.sampled.AudioFormat;

import com.rafael.med.common.CapturedPacket;
import com.rafael.med.common.ChannelUtils;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ILBC;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.P563Algorithm.P563Result;

public class VoiceSegment
{
	
	public static final AudioFormat AUDIO_FORMAT				= new AudioFormat(8000,16,1,true,false);
	private static final int SAMPLE_RATE						= (int) AUDIO_FORMAT.getSampleRate();
	private static final int SAMPLE_SIZE_IN_BYTES				= AUDIO_FORMAT.getSampleSizeInBits()/8;
	private static final int CHANNELS_NUMBER					= AUDIO_FORMAT.getChannels();
	private static final int SAMPLE_DURATION_IN_MICROSECONDS 	= 1_000_000 / SAMPLE_RATE;
	private static final int DURATION_PER_BYTE_IN_MICROSECONDS	= SAMPLE_DURATION_IN_MICROSECONDS / SAMPLE_SIZE_IN_BYTES;
	private static final int BYTES_PER_SECOND					= SAMPLE_RATE * SAMPLE_SIZE_IN_BYTES;
	

	private static final double NOT_DEFINED = -100.0;
	
	public final int index;
	public final LocalDateTime fromTime;
	public final VoiceChannel voiceChannel;
	public LocalDateTime toTime;
	public Duration duration;
	public long durationInMillis;
	public File waveFile;
	private int audioByteArrayLength;

	/****** lasy initialization *****/
	private ILBC 	 	ilbc;
	private ByteBuffer 	ilbcIn;
	private ByteBuffer 	ilbcOut;
	private byte[] 		audioByteArray;
	


	private double p563 = NOT_DEFINED;
	private P563Result p563Result;
	public int currentRelated;
	public String tooltipResult;
	
	public VoiceSegment(VoiceChannel voiceChannel, int index, LocalDateTime fromTime) 
	{
		this.voiceChannel 	= voiceChannel;
		this.index 			= index;
		this.fromTime 		= fromTime;
	}

	public void setToTime(LocalDateTime toTime)
	{
		this.toTime 					= toTime.plusSeconds(1);
		this.duration 					= Duration.between(fromTime, toTime);
		this.durationInMillis 			= duration.toMillis();
		this.audioByteArrayLength		= (int) (durationInMillis * (SAMPLE_RATE/1000) * SAMPLE_SIZE_IN_BYTES * CHANNELS_NUMBER);
		String fileName					= fromTime.format(Utilities.DATE_TIME_PATTERN_1) + "_" + toTime.format(Utilities.DATE_TIME_PATTERN_1)+ ".wav";
		this.waveFile					= Paths.get(voiceChannel.voiceData.bnetData.unitData.testData.root.toString(), voiceChannel.voiceData.bnetData.unitData.name, voiceChannel.voiceData.bnetData.name, voiceChannel.channelType.name(), fileName ).toFile();

	}
	
	private byte[] createAudioArray()
	{
		byte [] audioByteArray 		= null;
		int rtpPayloadPcmLength 	= 0;
		int jitterInMicroseconds 	= 0;
		
		if (voiceChannel instanceof VoiceRTPChannel)
		{
			VoiceRTPChannel voiceRTPChannel = (VoiceRTPChannel) voiceChannel;			
			if(!voiceRTPChannel.rtpPackets.isEmpty())
			{
				audioByteArray 			= new byte[audioByteArrayLength];
				LocalDateTime prevPacketTime 	= null;
				int index = 0;
				for (CapturedPacket packet : voiceRTPChannel.rtpPackets)
				{
					LocalDateTime currentTime 				= packet.localDateTime;
					
					if( (currentTime.isAfter(fromTime) || currentTime.isEqual(fromTime)) && (currentTime.isBefore(toTime) || currentTime.isEqual(toTime)) )
					{
						Duration durationFromStart 				= Duration.between(fromTime, currentTime);
						long durationFromStartInMicroseconds 	= durationFromStart.toNanos() / 1_000;
						if(prevPacketTime == null)
						{
							rtpPayloadPcmLength 	= decodeRtpPayloadLengthAndInit(packet);
							jitterInMicroseconds 	= (rtpPayloadPcmLength * 1_000_000 / BYTES_PER_SECOND) * 3;
							index = (int) (durationFromStartInMicroseconds / DURATION_PER_BYTE_IN_MICROSECONDS);

							if(index % 2 != 0)
							{
								index = index + 1;
							}
						}
						else
						{
							Duration durationfromPrev  				= Duration.between(prevPacketTime, currentTime);
							long durationFromPrevInMicroseconds  	= durationfromPrev.toNanos() / 1_000;
							if(durationFromPrevInMicroseconds < jitterInMicroseconds)
							{
								index = index + rtpPayloadPcmLength;
							}
							else
							{
								index = (int) (durationFromStartInMicroseconds / DURATION_PER_BYTE_IN_MICROSECONDS);
							}

							if(index % 2 != 0)
							{
								index = index + 1;
							}
						}
						prevPacketTime = currentTime;

						if(packet.rtpPayload == null) // firstTime to this packet
						{
							if(packet.rtpPayloadType == Constants.PCM_PAYLOAD_TYPE && packet.rtpPayloadlen == Constants.PCM_PAYLOAD_LEN)
							{
								packet.rtpPayload = ChannelUtils.decodePCM(packet);
							}
							else if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == Constants.ILBC_30_PAYLOD_TYPE)
							{
								packet.rtpPayload = ChannelUtils.decodeILBC(packet, ilbc, ilbcIn, ilbcOut);
							}
						}
						ChannelUtils.copyPacketToStream(audioByteArray, packet.rtpPayload, index);	
					}
				}
			}
		}
		return audioByteArray;
	}
	
	private int decodeRtpPayloadLengthAndInit(CapturedPacket packet)
	{
		if(packet.rtpPayloadType == Constants.PCM_PAYLOAD_TYPE)
		{
			return Constants.PCM_PAYLOAD_LEN;
		}
		else if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == Constants.ILBC_30_PAYLOD_TYPE)
		{
			if(ilbc == null)
			{
				if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE)
				{
					initILBC(ILBC.Mode.MODE_20);
				}
				else
				{
					initILBC(ILBC.Mode.MODE_30);
				}
			}
			
			int size = packet.rtpPayloadlen / ilbc.mode.bytes;
			int i = ilbc.mode.size * 2 * size;
			return i;
		}
		throw new IllegalStateException("PACKET = " + packet + " HAS NOT CORRECT RTP PAYLOAD TYPE");
	}
	
	private void initILBC(ILBC.Mode mode)
	{	
		this.ilbc 		= new ILBC(mode);
		this.ilbcIn 	= ByteBuffer.allocate(ilbc.mode.bytes).order(ByteOrder.LITTLE_ENDIAN);
		this.ilbcOut 	= ByteBuffer.allocate(ilbc.mode.size * 2).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	
	public byte[] getAudioByteArray() 
	{
		if(audioByteArray == null)
		{
			audioByteArray = createAudioArray();
		}
		return audioByteArray;
	}
	
	public void checkAndCreate() throws IOException 
	{
		getAudioByteArray();
		if(!waveFile.exists())
		{
			ChannelUtils.saveWavFile(audioByteArray, waveFile, AUDIO_FORMAT);
		}
	}

	
	public double algorithmP563()
	{
		if(p563 == NOT_DEFINED)
		{
			getAudioByteArray();
			p563Result = P563Algorithm.INSTANCE.runAlgorithm(audioByteArray);
			if(p563Result != null)
			{
				p563 = p563Result.fPredictedMos;
				tooltipResult = p563Result.toToottip();
			}
			else
			{
				p563 = 0;
			}
		}
		return p563;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoiceSegment [index=");
		builder.append(index);
		builder.append(", fromTime=");
		builder.append(fromTime);
		builder.append(", voiceChannel=");
		builder.append(voiceChannel.channelType.name());
		builder.append(", toTime=");
		builder.append(toTime);
		builder.append(", durationInMillis=");
		builder.append(durationInMillis);
		builder.append("]");
		return builder.toString();
	}
	
	
}