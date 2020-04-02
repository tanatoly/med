package com.rafael.med.common.bnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;

import com.rafael.med.common.Utilities;

public class P862Algorithm 
{
	private static final String P_862 = "p862";

	public static P862Algorithm INSTANCE = new P862Algorithm();
	
	private Object mutex = new Object();

	
	static
	{
		System.loadLibrary(P_862);
		//System.out.println( P_862 + ".dll loaded succesfully");
	}

	private ByteBuffer referenceDirectBuffer 	= ByteBuffer.allocateDirect(512).order(ByteOrder.LITTLE_ENDIAN);
	private ByteBuffer degradedDirectBuffer 	= ByteBuffer.allocateDirect(512).order(ByteOrder.LITTLE_ENDIAN);
	private ByteBuffer outDirectBuffer 			= ByteBuffer.allocateDirect(1430).order(ByteOrder.LITTLE_ENDIAN);

	native void p862(ByteBuffer reference, ByteBuffer degraded, ByteBuffer result); //PESQ
	
	
	public P862Result runAlgorithm(String referncePath, String degradedPath)
	{
		synchronized (mutex)
		{
			zeroBuffer(referenceDirectBuffer);
			zeroBuffer(degradedDirectBuffer);
			zeroBuffer(outDirectBuffer);
			
			//System.out.println("referncePath = " + referncePath +", degradedPath = " +degradedPath);
					
			referenceDirectBuffer.put(referncePath.getBytes());
			degradedDirectBuffer.put(degradedPath.getBytes());
			
			p862(referenceDirectBuffer, degradedDirectBuffer, outDirectBuffer);
			P862Result p862Result = P862Result.fromBuffer(outDirectBuffer);
					
			return p862Result;
		}
	}
	
	private static final byte ZERO =0;
	
	private void zeroBuffer(ByteBuffer buffer)
	{
		buffer.clear();
		while(buffer.hasRemaining())
		{
			buffer.put(ZERO);
		}
		buffer.clear();
	}
	
	public static final class P862Result
	{
		public static final int MAXNUTTERANCES = 50;
		
		private VoiceSegment referenceSegment;
		private VoiceSegment degradedSegment;
		
		
	
		public int 			Nutterances;
		public int 			Largest_uttsize;
		public int 			Nsurf_samples;
		public int  		Crude_DelayEst;
		public float 		Crude_DelayConf;
		
		public int  		UttSearch_Start[] 	= new int[MAXNUTTERANCES]; 
		public int  		UttSearch_End[] 	= new int[MAXNUTTERANCES]; 
		public int  		Utt_DelayEst[] 		= new int[MAXNUTTERANCES]; 
		public int  		Utt_Delay[] 		= new int[MAXNUTTERANCES];
		public float 		Utt_DelayConf[] 	= new float[MAXNUTTERANCES];
		public int  		Utt_Start[] 		= new int[MAXNUTTERANCES];
		public int  		Utt_End[] 			= new int[MAXNUTTERANCES];
		
		public float 		pesq_mos;
		public float 		mapped_mos;
		public short 		mode;

		private int relatedPercent;

		private String allResult;
		private String fromTo;
		
			
		public static P862Result fromBuffer(ByteBuffer buffer)
		{
			P862Result result = new P862Result();
			
			result.Nutterances			= buffer.getInt();
			result.Largest_uttsize		= buffer.getInt();
			result.Nsurf_samples		= buffer.getInt();
			result.Crude_DelayEst		= buffer.getInt();
			result.Crude_DelayConf		= buffer.getFloat();
			for (int i = 0; i < result.UttSearch_Start.length; i++) 
			{
				result.UttSearch_Start[i] = buffer.getInt();
			}
			
			for (int i = 0; i < result.UttSearch_End.length; i++) 
			{
				result.UttSearch_End[i] = buffer.getInt();
			}
			
			for (int i = 0; i < result.Utt_DelayEst.length; i++) 
			{
				result.Utt_DelayEst[i] = buffer.getInt();
			}
			
			for (int i = 0; i < result.Utt_Delay.length; i++) 
			{
				result.Utt_Delay[i] = buffer.getInt();
			}
			
			
			for (int i = 0; i < result.Utt_DelayConf.length; i++) 
			{
				result.Utt_DelayConf[i] = buffer.getFloat();
			}
			for (int i = 0; i < result.Utt_Start.length; i++) 
			{
				result.Utt_Start[i] = buffer.getInt();
			}
			for (int i = 0; i < result.Utt_End.length; i++) 
			{
				result.Utt_End[i] = buffer.getInt();
			}
			
			
			result.pesq_mos				= buffer.getFloat();
			result.mapped_mos			= buffer.getFloat();
			result.mode					= buffer.getShort();
			
			//System.out.println(String.format("java        -> Nutterances = %d, Crude_DelayConf = %f, pesq_mos=%f, mapped_mos=%f\n", result.Nutterances, result.Crude_DelayConf,result.pesq_mos,result.mapped_mos));
			
			return result;
		}
		
		
		
		public VoiceSegment getReferenceSegment() 
		{
			return referenceSegment;
		}
		public void setReferenceSegment(VoiceSegment referenceSegment)
		{
			this.referenceSegment = referenceSegment;
		}
		
		public VoiceSegment getDegradedSegment()
		{
			return degradedSegment;
		}
		public void setDegradedSegment(VoiceSegment degradedSegment)
		{
			this.degradedSegment = degradedSegment;
			this.relatedPercent  = degradedSegment.currentRelated;
		}
		
		public String getName()
		{
			return degradedSegment.voiceChannel.voiceData.bnetData.unitData.name + "->" + degradedSegment.voiceChannel.voiceData.bnetData.name + "->" + degradedSegment.voiceChannel.channelType.name();
		}
		
		public String getFromTo()
		{
			if(fromTo == null)
			{
				StringBuilder builder = new StringBuilder();
				builder.append(degradedSegment.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS)).append(StringUtils.LF);
				builder.append(degradedSegment.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS)).append(StringUtils.LF).append(StringUtils.LF);
				builder.append("DURATION  ").append(Duration.between(degradedSegment.fromTime, degradedSegment.toTime).toMillis()).append(" millis").append(StringUtils.LF);
				fromTo = builder.toString();
			}
			return fromTo;
		}
		
		public String getTo()
		{
			return degradedSegment.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss);
		}
		
		public String getPesq()
		{
			if(allResult == null)
			{
				StringBuilder builder = new StringBuilder();
				builder.append("Nutterances		=").append(String.valueOf(Nutterances)).append(StringUtils.LF);
				builder.append("Largest_uttsize		=").append(String.valueOf(Largest_uttsize)).append(StringUtils.LF);
				builder.append("Nsurf_samples		=").append(String.valueOf(Nsurf_samples)).append(StringUtils.LF);
				builder.append("Crude_DelayEst	=").append(String.valueOf(Crude_DelayEst)).append(StringUtils.LF);
				builder.append("Crude_DelayConf	=").append(String.format("%.2f",Crude_DelayConf)).append(StringUtils.LF);
				builder.append("pesq_mos		=").append(String.format("%.2f",pesq_mos));
				allResult = builder.toString();
			}
			return allResult;
		}
		
		public float getMappedmos()
		{
			return mapped_mos;
		}
		
		public String getRelatedPercent()
		{
			return String.valueOf(relatedPercent);
		}
		
	}
}
