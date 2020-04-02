package com.rafael.med.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import javafx.scene.shape.Line;
import javafx.util.Duration;

public class AudioUtils
{	
//	public static final class ILBCPack
//	{
//		public ILBC 	 	ilbc;
//		public ByteBuffer 	ilbcIn;
//		public ByteBuffer 	ilbcOut;
//		
//		public ILBCPack(ILBC.Mode mode) 
//		{
//			this.ilbc 		= new ILBC(mode);
//			this.ilbcIn 	= ByteBuffer.allocate(ilbc.mode.bytes).order(ByteOrder.LITTLE_ENDIAN);
//			this.ilbcOut 	= ByteBuffer.allocate(ilbc.mode.size * 2).order(ByteOrder.LITTLE_ENDIAN);
//		}
//	}
	
//	public static byte[] createAudioData(TreeSet<CapturedPacket> sortedSet, LocalDateTime fromTime, LocalDateTime toTime, AudioFormat audioFormat)
//	{		
//		
//		if(sortedSet == null || sortedSet.isEmpty() || sortedSet.first().localDateTime.isAfter(toTime) || sortedSet.last().localDateTime.isBefore(fromTime))
//		{
//			throw new IllegalArgumentException("sortedSet == null or sortedSet.isEmpty() or sortedSet.first().localDateTime.isAfter(toTime) or sortedSet.last().localDateTime.isBefore(fromTime)");
//		}
//		
//		
//		LocalDateTime startTime 				= (sortedSet.first().localDateTime.isBefore(fromTime) ? fromTime : sortedSet.first().localDateTime);
//		LocalDateTime endTime 					= (sortedSet.last().localDateTime.isAfter(toTime) ? toTime : sortedSet.last().localDateTime);
//		java.time.Duration duration 			= java.time.Duration.between(startTime, toTime);
//		long durationInSeconds 					= duration.getSeconds();
//		
//
//		int sampleRate							= (int) audioFormat.getSampleRate();
//		int sampleSizeInBytes					= audioFormat.getSampleSizeInBits()/8;
//		int channelsNumber						= audioFormat.getChannels();
//		int timeForSampleInMicroseconds 		= 1_000_000 / sampleRate;
//		int durationPerByteInMicroseconds		= timeForSampleInMicroseconds / sampleSizeInBytes;
//		int bytesPerSecond						= sampleRate * sampleSizeInBytes;
//		int audioByteArrayLength 				= (int) (durationInSeconds * sampleRate * sampleSizeInBytes * channelsNumber);
//		
//		byte [] audioByteArray 					= new byte[audioByteArrayLength];
//		
//		
//		int rtpPayloadPcmLength 				= 0;
//		int jitterInMicroseconds 				= 0;		
//		LocalDateTime prevPacketTime 			= null;
//		int index 								= 0;
//		
//		ILBCPack ilbcPack						= null;
//		
//		for (CapturedPacket packet : sortedSet)
//		{
//			if((packet.localDateTime.isAfter(startTime) || packet.localDateTime.isEqual(startTime)) && ( packet.localDateTime.isBefore(endTime) || packet.localDateTime.isEqual(endTime)) )
//			{
//				LocalDateTime currentTime 				= packet.localDateTime;
//				java.time.Duration durationFromStart 	= java.time.Duration.between(startTime, currentTime);
//				long durationFromStartInMicroseconds 	= durationFromStart.toNanos() / 1_000;
//				
//				if(prevPacketTime == null)
//				{
//					if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == Constants.ILBC_30_PAYLOD_TYPE)
//					{
//						if(ilbcPack == null)
//						{
//							if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE)
//							{
//								ilbcPack	 = new ILBCPack(ILBC.Mode.MODE_20);
//							}
//							else
//							{
//								ilbcPack	 = new ILBCPack(ILBC.Mode.MODE_30);
//							}
//						}
//					}
//					rtpPayloadPcmLength 	= decodeRtpPayloadLength(packet);
//					jitterInMicroseconds 	= (rtpPayloadPcmLength * 1_000_000 / bytesPerSecond) * 3;
//					index = (int) (durationFromStartInMicroseconds / durationPerByteInMicroseconds);
//					
//					if(index % 2 != 0)
//					{
//						index = index + 1;
//					}
//				}
//				else
//				{
//					java.time.Duration durationfromPrev  	= java.time.Duration.between(prevPacketTime, currentTime);
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
//					packet.rtpPayload = decodeRtpPayload(packet,ilbcPack);
//				}
//				
//				
//				int remaining 	= audioByteArray.length - index;
//				int copyLength 	= packet.rtpPayload.length;
//				
//				if(remaining > 0)
//				{
//					if(remaining < copyLength)
//					{
//						copyLength = remaining;
//					}
//					System.arraycopy(packet.rtpPayload, 0, audioByteArray, index, copyLength);
//				}
//
//			}			
//		}
//		return audioByteArray;
//	}
	
	
//	private static int decodeRtpPayloadLength(CapturedPacket packet)
//	{
//		if(packet.rtpPayloadType == Constants.PCM_PAYLOAD_TYPE)
//		{
//			return Constants.PCM_PAYLOAD_LEN;
//		}
//		else if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE)
//		{	
//			int size = packet.rtpPayloadlen / ILBC.Mode.MODE_20.bytes;
//			return ILBC.Mode.MODE_20.size * 2 * size;
//		}
//		else if(packet.rtpPayloadType == Constants.ILBC_30_PAYLOD_TYPE)
//		{
//			int size = packet.rtpPayloadlen / ILBC.Mode.MODE_30.bytes;
//			return ILBC.Mode.MODE_30.size * 2 * size;
//		}
//		throw new IllegalStateException("PACKET = " + packet + " HAS NOT CORRECT RTP PAYLOAD TYPE");
//	}
	
	
//	public static byte[] decodeRtpPayload(CapturedPacket packet, ILBCPack ilbcPack)
//	{
//		byte[] result = null;
//		if(packet.rtpPayloadType == Constants.PCM_PAYLOAD_TYPE && packet.rtpPayloadlen == Constants.PCM_PAYLOAD_LEN)
//		{
//			result = new byte[Constants.PCM_PAYLOAD_LEN];
//			packet.rtpBuffer.get(result);
//		}
//		else if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == Constants.ILBC_30_PAYLOD_TYPE)
//		{	
//			int outSizeInBytes = ilbcPack.ilbc.mode.size * 2;
//			int size = packet.rtpPayloadlen / ilbcPack.ilbc.mode.bytes;
//			result = new byte[outSizeInBytes * size];
//			for (int i = 0; i < size; i++)
//			{
//				ilbcPack.ilbcIn.clear();
//				ilbcPack.ilbcOut.clear();
//				
//				for (int j = 0; j < ilbcPack.ilbc.mode.bytes; j++)
//				{
//					ilbcPack.ilbcIn.put(packet.rtpBuffer.get());
//				}
//				
//				ilbcPack.ilbcIn.flip();
//				ilbcPack.ilbc.decode(ilbcPack.ilbcIn, ilbcPack.ilbcOut);
//				ilbcPack.ilbcOut.flip();
//				ilbcPack.ilbcOut.get(result, i * outSizeInBytes, outSizeInBytes);
//			}
//		}
//		return result;
//	}
	
	
	
//	private static final double WAVEFORM_HEIGHT_COEFFICIENT = 1.3;
//	public static int[] getWavAmplitudes(File file) throws UnsupportedAudioFileException , IOException
//	{
//		System.out.println("Calculting WAV amplitudes");
//		
//		//Get Audio input stream
//		try (AudioInputStream input = AudioSystem.getAudioInputStream(file))
//		{
//			AudioFormat baseFormat 	= input.getFormat();
//			Encoding encoding 		= AudioFormat.Encoding.PCM_UNSIGNED;
//			float sampleRate 		= baseFormat.getSampleRate();
//			int numChannels 		= baseFormat.getChannels();
//			
//			AudioFormat decodedFormat = new AudioFormat(encoding, sampleRate, 16, numChannels, numChannels * 2, sampleRate, false);
//			int available 				= input.available();
//			
//			//Get the PCM Decoded Audio Input Stream
//			try (AudioInputStream pcmDecodedInput = AudioSystem.getAudioInputStream(decodedFormat, input))
//			{
//				final int BUFFER_SIZE = 4096; //this is actually bytes
//				
//				//Create a buffer
//				byte[] buffer = new byte[BUFFER_SIZE];
//				
//				//Now get the average to a smaller array
//				int maximumArrayLength 	= 100_000;
//				int[] finalAmplitudes 	= new int[maximumArrayLength];
//				int samplesPerPixel 	= available / maximumArrayLength;
//				
//				//Variables to calculate finalAmplitudes array
//				int currentSampleCounter 	= 0;
//				int arrayCellPosition 		= 0;
//				float currentCellValue 		= 0.0f;
//				
//				//Variables for the loop
//				int arrayCellValue = 0;
//				
//				//Read all the available data on chunks
//				while (pcmDecodedInput.readNBytes(buffer, 0, BUFFER_SIZE) > 0)
//				{
//					for (int i = 0; i < buffer.length - 1; i += 2)
//					{
//						
//						//Calculate the value
//						arrayCellValue = (int) ( ( ( ( ( buffer[i + 1] << 8 ) | buffer[i] & 0xff ) << 16 ) / 32767 ) * WAVEFORM_HEIGHT_COEFFICIENT );
//						
//						//Tricker
//						if (currentSampleCounter != samplesPerPixel) 
//						{
//							++currentSampleCounter;
//							currentCellValue += Math.abs(arrayCellValue);
//						} 
//						else 
//						{
//							//Avoid ArrayIndexOutOfBoundsException
//							if (arrayCellPosition != maximumArrayLength)
//							{
//								finalAmplitudes[arrayCellPosition] = finalAmplitudes[arrayCellPosition + 1] = (int) currentCellValue / samplesPerPixel;
//							}
//							
//							//Fix the variables
//							currentSampleCounter = 0;
//							currentCellValue = 0;
//							arrayCellPosition += 2;
//						}
//					}
//				}
//				
//				return finalAmplitudes;
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			
//		}
//		return new int[1];
//	}
//	
//	public static float[] processAmplitudes(int[] sourcePcmData, int width) 
//	{
//		System.out.println("Processing WAV amplitudes");
//		
//
//		float[] waveData = new float[width];
//		int samplesPerPixel = sourcePcmData.length / width;
//		
//		System.out.println("Processing WAV amplitudes width = " + width + " samplesPerPixel = " + samplesPerPixel);
//		
//		//Calculate
//		float nValue;
//		for (int w = 0; w < width; w++)
//		{
//			
//			//For performance keep it here
//			int c = w * samplesPerPixel;
//			nValue = 0.0f;
//			
//			//Keep going
//			for (int s = 0; s < samplesPerPixel; s++) 
//			{
//				nValue += ( Math.abs(sourcePcmData[c + s]) / 65536.0f );
//			}
//			
//			//Set WaveData
//			waveData[w] = nValue / samplesPerPixel;
//		}
//		
//		System.out.println("Finished Processing amplitudes");
//		return waveData;
//	}
	
	
	public static AudioFormat getFormat(String path) throws Exception
	{
		File file = new File(path);
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		return format;
	}
	
	public static final ByteBuffer readAudioFile(String path) throws Exception
	{
		File file = new File(path);
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = audioInputStream.getFormat();
		int size = audioInputStream.available();
	//	System.out.println("File = " + file + " audio format = " + format + ", size = " + size);
		
		ByteOrder byteOrder = format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		ByteBuffer byteBuffer = ByteBuffer.allocate(size).order(byteOrder);
		
		byte[] buffer = new byte[4096];
		int c;
		while ((c = audioInputStream.read(buffer, 0, buffer.length)) != -1)
		{
			byteBuffer.put(buffer, 0, c);
		}
		audioInputStream.close();
		byteBuffer.flip();
	//	System.out.println("audio data in byteBuffer = " + byteBuffer);
		return byteBuffer;
	}
	
	public static byte[] readAudioDataAsByteArray(String path) throws Exception
	{
		ByteBuffer byteBuffer = readAudioFile(path);
		byte[] result = new byte[byteBuffer.limit()];
		byteBuffer.get(result, 0, result.length);
		return result;
	}
	
	public static short[] readAudioFileAsShortArray(String path) throws Exception
	{
		ByteBuffer byteBuffer = readAudioFile(path);
		ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
		short[] result = new short[shortBuffer.limit()];
		shortBuffer.get(result, 0, result.length);
		return result;
	}
	
	
	
	public static void writeAudioWave(ByteBuffer byteBuffer, AudioFormat audioFormat, File file) throws Exception
	{
		byte[] data = new byte[byteBuffer.limit()];
		int offset = 0;
		byteBuffer.position(offset);
		byteBuffer.get(data, 0, data.length - offset);
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		AudioInputStream audioOutputStream = new AudioInputStream(in, audioFormat, data.length);
		AudioSystem.write(audioOutputStream, AudioFileFormat.Type.WAVE, file);
	}
	
	
	
	public static void writeAudioWave(ByteBuffer byteBuffer, AudioFormat audioFormat, String path) throws Exception
	{
		byte[] destData = new byte[byteBuffer.limit()];
		int offset = 0;
		byteBuffer.position(offset);
		byteBuffer.get(destData, 0, destData.length - offset);
		writeAudioWave(destData, audioFormat, path);
	}
	
	public static void writeAudioWave(byte[] data, AudioFormat audioFormat, String path) throws Exception
	{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		AudioInputStream audioOutputStream = new AudioInputStream(in, audioFormat, data.length/2);
		File file = new File(path);
		AudioSystem.write(audioOutputStream, AudioFileFormat.Type.WAVE, file);
	}
	
	public static List<Line> generateAudioGraphLines(byte[] audioBytes, AudioFormat format, double canvasW, double canvasH)
	{
		int[] audioData = null;
		if (format.getSampleSizeInBits() == 16)
		{
			int nlengthInSamples = audioBytes.length / 2;
			audioData = new int[nlengthInSamples];
			if (format.isBigEndian())
			{
				for (int i = 0; i < nlengthInSamples; i++)
				{
					/* First byte is MSB (high order) */
					int MSB = (int) audioBytes[2*i];
					/* Second byte is LSB (low order) */
					int LSB = (int) audioBytes[2*i+1];

				}
			} 
			else
			{
				for (int i = 0; i < nlengthInSamples; i++)
				{
					/* First byte is LSB (low order) */
					int LSB = (int) audioBytes[2*i];
					/* Second byte is MSB (high order) */
					int MSB = (int) audioBytes[2*i+1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		} 
		else if (format.getSampleSizeInBits() == 8)
		{
			int nlengthInSamples = audioBytes.length;
			audioData = new int[nlengthInSamples];
			if (format.getEncoding() == Encoding.PCM_SIGNED)
			{
				for (int i = 0; i < audioBytes.length; i++)
				{
					audioData[i] = audioBytes[i];
				}
			} 
			else
			{
				for (int i = 0; i < audioBytes.length; i++)
				{
					audioData[i] = audioBytes[i] - 128;
				}
			}
		}

		int frames_per_pixel = (int) (audioBytes.length / format.getFrameSize()/canvasW);
		byte my_byte = 0;
		double y_last = 0;
		int numChannels = format.getChannels();

		List<Line> result = null;

		if(audioData != null)
		{
			result = new ArrayList<>((int)canvasW);
			for (double x = 0; x < canvasW; x++)
			{
				int idx = (int) (frames_per_pixel * numChannels * x);
				if (format.getSampleSizeInBits() == 8) 
				{
					my_byte = (byte) audioData[idx];
				} 
				else
				{
					my_byte = (byte) (128 * audioData[idx] / 32768 );
				}
				double y_new = (double) (canvasH * (128 - my_byte) / 256);
				result.add(new Line(x, y_last, x, y_new));
				y_last = y_new;
			}
		}
		return result;
	}
	
	

	
	public static SourceDataLine createSourceDataLine(AudioFormat audioFormat) throws Exception
	{
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		// TODO : Should getting the SourceDataLine go in start() In which case
		// its configurable to know the Formats beforehand.
		SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		sourceDataLine.open(audioFormat);
		sourceDataLine.start();
		
		return sourceDataLine;
	}
	
//	public static void toSoundCard(Frame frame, boolean isFirst, boolean isLast, SourceDataLine sourceDataLine) throws Exception
//	{
//		
//		// FIXME : write() will block till all bytes are written. Need async
//		// operation here.
//		
//		// short[] data = frame.getData();
//		
//		ByteBuffer source = frame.getDataByteBuffer();
//		byte[] outputData = new byte[source.limit()];
//		
//		for (int i = 0; i < outputData.length; i++)
//		{
//			outputData[i] = source.get();
//		}
//		
//		// for (int i = 0; i < outputData.length; i++)
//		// {
//		// outputData[i * 2] = (byte) data[i];
//		// outputData[i * 2 + 1] = (byte) (data[i] >> 8);
//		// }
//		
//		sourceDataLine.write(outputData, 0, outputData.length / 2);
//		
//	}
	
	private static final String _02D_02D 				= "%02d:%02d";
	private static final String D_02D_02D 				= "%d:%02d:%02d";
	private static final String _02D_02D_02D_02D 		= "%02d:%02d/%02d:%02d";
	private static final String D_02D_02D_D_02D_02D 	= "%d:%02d:%02d/%d:%02d:%02d";
	private static final int _60 						= 60;
	
	public static String formatTime(Duration elapsed, Duration duration)
	{
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (_60 * _60);
		if (elapsedHours > 0)
		{
			intElapsed -= elapsedHours * _60 * _60;
		}
		int elapsedMinutes = intElapsed / _60;
		int elapsedSeconds = intElapsed - elapsedHours * _60 * _60 - elapsedMinutes * _60;

		if (duration.greaterThan(Duration.ZERO))
		{
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (_60 * _60);
			if (durationHours > 0)
			{
				intDuration -= durationHours * _60 * _60;
			}
			int durationMinutes = intDuration / _60;
			int durationSeconds = intDuration - durationHours * _60 * _60 - durationMinutes * _60;

			if (durationHours > 0)
			{
				return String.format(D_02D_02D_D_02D_02D, elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
			}
			else
			{
				return String.format(_02D_02D_02D_02D, elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
			}
		}
		else
		{
			if (elapsedHours > 0)
			{
				return String.format(D_02D_02D, elapsedHours, elapsedMinutes, elapsedSeconds);
			}
			else
			{
				return String.format(_02D_02D, elapsedMinutes, elapsedSeconds);
			}
		}
	}
	
}
