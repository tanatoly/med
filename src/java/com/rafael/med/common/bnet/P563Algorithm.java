package com.rafael.med.common.bnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.StringUtils;

public class P563Algorithm 
{
	private static final String P_563 = "p563";

	public static P563Algorithm INSTANCE = new P563Algorithm();

	private Object mutex = new Object();
	static
	{
		System.loadLibrary(P_563);
		//System.out.println( P_563 + ".dll loaded succesfully");
	}

	private final ByteBuffer inDirectBuffer 	= ByteBuffer.allocateDirect(60 * 1024 * 1024).order(ByteOrder.LITTLE_ENDIAN);
	private final ByteBuffer outDirectBuffer 	= ByteBuffer.allocateDirect(1024).order(ByteOrder.LITTLE_ENDIAN);
	
	private static int rangeFrom 	= 4;
	private static int rangeTo 		= 120;

	native void p563(ByteBuffer acquired, ByteBuffer result); //POLQA

	public static void setRangeInSeconds(int from, int to)
	{
		rangeFrom 	= from;
		rangeTo		= to;
	}
	
	
	boolean isNot = true;
	
	public P563Result runAlgorithm(byte[] audioData)
	{
		P563Result result = null;
		synchronized (mutex) 
		{
			int length = audioData.length;	
			if(length > 8000 * 2 * rangeFrom && length < 8000 * 2 * rangeTo) // between 4 - 120 seconds
			{
		
				inDirectBuffer.clear();
				outDirectBuffer.clear();
			
				inDirectBuffer.putInt(length/2);
				inDirectBuffer.put(audioData);
			
				p563(inDirectBuffer, outDirectBuffer);
				result = P563Result.fromBuffer(outDirectBuffer);
			}
		}
		return result;
	}
	
	public static final class P563Result
	{
		//	mutes_struct			tMutes;
		public double fMuteLength;
		public double fSpeechInterruptions;
		public double fSharpDeclines;
		public double fUnnaturalSilenceMean;
		
		//	noise_analysis_struct	tNoise;
		public double fEstBGNoise;
	    public double fEstSegSNR;
		public double fSpecLevelDev;
		public double fSpecLevelRange;
		public double fRelNoiseFloor;
		public double fNoiseLevel;
		public double fSnr;
		public double fHiFreqVar;
		public double fSpectralClarity;
		public double fLocalBGNoise;
		public double fLocalBGNoiseMean;
		public double fLocalBGNoiseLog;
		public double fLocalMeanDistSamp; 
		public double fGlobalBGNoise;	
		
		//	unnatural_struct		tUnnatural;
		public double fConsistentArtTracker;
		public double fVtpMaxTubeSection;
		public double fFinalVtpAverage;
		public double fVtpPeakTracker;
		public double fArtAverage;
		public double fVtpVadOverlap;
		public double fPitchCrossCorrelOffset;
		public double fPitchCrossPower;
		public double fFrameRepeats;
		public double fFrameRepeatsMean;     
		public double fUBeeps;	
		public double fUBeepsMean;		
		public double fUnBeepsMeanDistSamp;  
		public double fRobotisation;
		public double fCepADev;			   
		public double fCepSkew;			           
		public double fCepCurt;			           
		public double fLPCCurt;			           
		public double fLPCSkew;			           
		public double fLPCSkewAbs;	
		
		//	basic_desc_struct		tBasicDesc;
		public double fSpeechLevel;
		public double fPitchAverage;
		public double fSpeechSectionLevelVar;
		
		//	speech_extract_struct	tSpeechExtract;
		public double fBasicVoiceQualityAsym;
		public double fBasicVoiceQuality;

		public int lPartition;
		public double fPredictedMos;
		public double fIeValue;
		
		public static P563Result fromBuffer(ByteBuffer buffer)
		{
			P563Result result = new P563Result();
			
//			mutes_struct			tMutes; 4 
			result.fMuteLength 				= buffer.getDouble();
			result.fSpeechInterruptions 	= buffer.getDouble();
			result.fSharpDeclines 			= buffer.getDouble();
			result.fUnnaturalSilenceMean 	= buffer.getDouble();
			
			//	noise_analysis_struct	tNoise; 14
			result.fEstBGNoise 				= buffer.getDouble();
		    result.fEstSegSNR 				= buffer.getDouble();
			result.fSpecLevelDev 			= buffer.getDouble();
			result.fSpecLevelRange 			= buffer.getDouble();
			result.fRelNoiseFloor 			= buffer.getDouble();
			result.fNoiseLevel 				= buffer.getDouble();
			result.fSnr 					= buffer.getDouble();
			result.fHiFreqVar 				= buffer.getDouble();
			result.fSpectralClarity 		= buffer.getDouble();
			result.fLocalBGNoise 			= buffer.getDouble();
			result.fLocalBGNoiseMean 		= buffer.getDouble();
			result.fLocalBGNoiseLog 		= buffer.getDouble();
			result.fLocalMeanDistSamp 		= buffer.getDouble(); 
			result.fGlobalBGNoise 			= buffer.getDouble();	
			
			//	unnatural_struct		tUnnatural;20
			result.fConsistentArtTracker 	= buffer.getDouble();
			result.fVtpMaxTubeSection 		= buffer.getDouble();
			result.fFinalVtpAverage 		= buffer.getDouble();
			result.fVtpPeakTracker 			= buffer.getDouble();
			result.fArtAverage 				= buffer.getDouble();
			result.fVtpVadOverlap 			= buffer.getDouble();
			result.fPitchCrossCorrelOffset 	= buffer.getDouble();
			result.fPitchCrossPower 		= buffer.getDouble();
			result.fFrameRepeats 			= buffer.getDouble();
			result.fFrameRepeatsMean 		= buffer.getDouble();     
			result.fUBeeps 					= buffer.getDouble();	
			result.fUBeepsMean 				= buffer.getDouble();		
			result.fUnBeepsMeanDistSamp 	= buffer.getDouble();  
			result.fRobotisation 			= buffer.getDouble();
			result.fCepADev 				= buffer.getDouble();			   
			result.fCepSkew 				= buffer.getDouble();			           
			result.fCepCurt 				= buffer.getDouble();			           
			result.fLPCCurt 				= buffer.getDouble();			           
			result.fLPCSkew 				= buffer.getDouble();			           
			result.fLPCSkewAbs 				= buffer.getDouble();	
			
			//	basic_desc_struct		tBasicDesc; 3
			result.fSpeechLevel 			= buffer.getDouble();
			result.fPitchAverage 			= buffer.getDouble();
			result.fSpeechSectionLevelVar 	= buffer.getDouble();
			
			//	speech_extract_struct	tSpeechExtract; 2
			result.fBasicVoiceQualityAsym 	= buffer.getDouble();
			result.fBasicVoiceQuality 		= buffer.getDouble();

			
			
			
			result.lPartition  				= buffer.getInt();
			result.fPredictedMos 			= buffer.getDouble();
			result.fIeValue 				= buffer.getDouble();
			
			//System.out.println(String.format("java -> fMuteLength=%f , fEstBGNoise=%f , fGlobalBGNoise=%f, fConsistentArtTracker=%f, fSpeechLevel=%f, fBasicVoiceQuality=%f, lPartition=%d, fPredictedMos=%f, fIeValue=%f\n", result.fMuteLength , result.fEstBGNoise , result.fGlobalBGNoise , result.fConsistentArtTracker , result.fSpeechLevel, result.fBasicVoiceQuality,result.lPartition, result.fPredictedMos, result.fIeValue));
			
			return result;
		}

		public String toToottip()
		{
			StringBuilder builder = new StringBuilder();
			
			
			builder.append("MuteLength				=").append(String.format("%.2f",fMuteLength)).append(StringUtils.LF);
			builder.append("SpeechInterruptions		=").append(String.format("%.2f",fSpeechInterruptions)).append(StringUtils.LF);
			builder.append("SharpDeclines				=").append(String.format("%.2f",fSharpDeclines)).append(StringUtils.LF);
			builder.append("UnnaturalSilenceMean		=").append(String.format("%.2f",fUnnaturalSilenceMean)).append(StringUtils.LF);
			
			//	noise_analysis_struct	tNoise; 14
			builder.append("EstBGNoise				=").append(String.format("%.2f",fEstBGNoise)).append(StringUtils.LF);
		    builder.append("EstSegSNR				=").append(String.format("%.2f",fEstSegSNR)).append(StringUtils.LF);
			builder.append("SpecLevelDev				=").append(String.format("%.2f",fSpecLevelDev)).append(StringUtils.LF);
			builder.append("SpecLevelRange			=").append(String.format("%.2f",fSpecLevelDev)).append(StringUtils.LF);
			builder.append("RelNoiseFloor				=").append(String.format("%.2f",fRelNoiseFloor)).append(StringUtils.LF);
			builder.append("NoiseLevel				=").append(String.format("%.2f",fNoiseLevel)).append(StringUtils.LF);
			builder.append("Snr						=").append(String.format("%.2f",fSnr)).append(StringUtils.LF);
			builder.append("HiFreqVar					=").append(String.format("%.2f",fHiFreqVar)).append(StringUtils.LF);
			builder.append("SpectralClarity				=").append(String.format("%.2f",fSpectralClarity)).append(StringUtils.LF);
			builder.append("LocalBGNoise				=").append(String.format("%.2f",fLocalBGNoise)).append(StringUtils.LF);
			builder.append("LocalBGNoiseMean			=").append(String.format("%.2f",fLocalBGNoiseMean)).append(StringUtils.LF);
			builder.append("LocalBGNoiseLog			=").append(String.format("%.2f",fLocalBGNoiseLog)).append(StringUtils.LF);
			builder.append("LocalMeanDistSamp		=").append(String.format("%.2f",fLocalMeanDistSamp)).append(StringUtils.LF); 
			builder.append("GlobalBGNoise			=").append(String.format("%.2f",fGlobalBGNoise)).append(StringUtils.LF);	
			
			//	unnatural_struct		tUnnatural;20
			builder.append("ConsistentArtTracker		=").append(String.format("%.2f",fConsistentArtTracker)).append(StringUtils.LF);
			builder.append("VtpMaxTubeSection		=").append(String.format("%.2f",fVtpMaxTubeSection)).append(StringUtils.LF);
			builder.append("FinalVtpAverage			=").append(String.format("%.2f",fFinalVtpAverage)).append(StringUtils.LF);
			builder.append("VtpPeakTracker			=").append(String.format("%.2f",fVtpPeakTracker)).append(StringUtils.LF);
			builder.append("ArtAverage 				=").append(String.format("%.2f",fArtAverage)).append(StringUtils.LF);
			builder.append("VtpVadOverlap			=").append(String.format("%.2f",fVtpVadOverlap)).append(StringUtils.LF);
			builder.append("PitchCrossCorrelOffset 		=").append(String.format("%.2f",fPitchCrossCorrelOffset)).append(StringUtils.LF);
			builder.append("PitchCrossPower			=").append(String.format("%.2f",fPitchCrossPower)).append(StringUtils.LF);
			builder.append("FrameRepeats				=").append(String.format("%.2f",fFrameRepeats)).append(StringUtils.LF);
			builder.append("FrameRepeatsMean			=").append(String.format("%.2f",fFrameRepeatsMean)).append(StringUtils.LF);     
			builder.append("UBeeps					=").append(String.format("%.2f",fUBeeps)).append(StringUtils.LF);	
			builder.append("UBeepsMean				=").append(String.format("%.2f",fUBeepsMean)).append(StringUtils.LF);
			
			builder.append("UnBeepsMeanDistSamp		=").append(String.format("%.2f",fUnBeepsMeanDistSamp)).append(StringUtils.LF);  
			builder.append("Robotisation				=").append(String.format("%.2f",fRobotisation)).append(StringUtils.LF);
			builder.append("CepADev					=").append(String.format("%.2f",fCepADev)).append(StringUtils.LF);			   
			builder.append("CepSkew					=").append(String.format("%.2f",fCepSkew)).append(StringUtils.LF);			           
			builder.append("CepCurt					=").append(String.format("%.2f",fCepCurt)).append(StringUtils.LF);			           
			builder.append("LPCCurt					=").append(String.format("%.2f",fLPCCurt)).append(StringUtils.LF);			           
			builder.append("LPCSkew					=").append(String.format("%.2f",fLPCSkew)).append(StringUtils.LF);			           
			builder.append("LPCSkewAb				s=").append(String.format("%.2f",fLPCSkewAbs)).append(StringUtils.LF);	
			
			//	basic_desc_struct		tBasicDesc; 3
			builder.append("SpeechLevel				=").append(String.format("%.2f",fSpeechLevel)).append(StringUtils.LF);
			builder.append("PitchAverage				=").append(String.format("%.2f",fPitchAverage)).append(StringUtils.LF);
			builder.append("SpeechSectionLevelVar		=").append(String.format("%.2f",fSpeechSectionLevelVar)).append(StringUtils.LF);
			
			//	speech_extract_struct	tSpeechExtract; 2
			builder.append("BasicVoiceQualityAsym		=").append(String.format("%.2f",fBasicVoiceQualityAsym)).append(StringUtils.LF);
			builder.append("BasicVoiceQuality			=").append(String.format("%.2f",fBasicVoiceQuality)).append(StringUtils.LF);

			
			
			builder.append("lPartition					=").append(String.valueOf(lPartition)).append(StringUtils.LF);
			builder.append("IeValue					=").append(String.format("%.2f",fIeValue));
			
			return builder.toString();
		}

		
	}
}
