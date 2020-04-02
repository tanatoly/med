package com.rafael.med.debrief;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.CapturedPacket;
import com.rafael.med.common.ChannelUtils;
import com.rafael.med.common.Constants;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.ChannelType;


public class RowData
{
	private static final Logger log = LogManager.getLogger();
	
	
	private RowCell[] isDateExists 					= new RowCell[AppDebrief.TABLE_DAYS];
	private RowCell[] isHourExists 					= new RowCell[AppDebrief.TABLE_HOURS];
	private RowCell[] isMinuteExists 				= new RowCell[AppDebrief.TABLE_MINUTES];
	private RowCell[] isSecondsExists 				= new RowCell[AppDebrief.TABLE_SECONDS];
	
	private final int sampleRate					= (int) AppDebrief.AUDIO_FORMAT.getSampleRate();
	private final int sampleSizeInBytes				= AppDebrief.AUDIO_FORMAT.getSampleSizeInBits()/8;
	private final int channelsNumber				= AppDebrief.AUDIO_FORMAT.getChannels();
	private final int timeForSampleInMicroseconds 	= 1_000_000 / sampleRate;
	private final int timeForByteInMicroseconds		= timeForSampleInMicroseconds / sampleSizeInBytes;
	private final int bytesPerSecond				= sampleRate * sampleSizeInBytes;
	
	
	private TreeSet<LocalDate> dates 				= new TreeSet<>();
	private ChannelData [] channels					= new ChannelData[ChannelType.values().length];
	private Map<ChannelType, ChannelData> map		= new HashMap<>(channels.length);
	
	public File 			directory;
	private TelemetryData 	telemetryData;
		
	
	public RowData(File directory) throws Exception
	{
		this.directory  		= directory;
		this.telemetryData 		= new TelemetryData(directory);
		
		initRowCells(isDateExists);
		initRowCells(isHourExists);
		initRowCells(isMinuteExists);
		initRowCells(isSecondsExists);
		
		for (int i = 0; i < channels.length; i++)
		{
			ChannelType channelType = ChannelType.values()[i];
			channels[i] 			= new ChannelData(channelType);
			map.put(channelType, channels[i]);
		}
		
		List<File> allFiles = Utilities.listFiles(directory.toPath());
		for (File currentFile : allFiles)
		{
			if(currentFile.getName().contains(".pcap") && currentFile.length() > 0)
			{
				ByteBuffer pcapFileBuffer = Utilities.readFile(currentFile,ByteOrder.LITTLE_ENDIAN);
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
								ChannelData channelData = map.get(channelType);
								if(channelData != null)
								{
									channelData.addPacket(packet);
									dates.add(packet.localDate);
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
		}
	}

	private void initRowCells(RowCell [] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i] = new RowCell();
		}
	}
	
	private void clearRowCells(RowCell [] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			array[i].clear();
		}
	}
	
	public void setAllDates(TreeSet<LocalDate> allDates)
	{
		clearRowCells(isDateExists);
		int index = 0;
		for (LocalDate localDate : allDates)
		{
			if(dates.contains(localDate))
			{
				isDateExists[index].isLegacyRx = (map.get(ChannelType.LEGACY_RX_RTP).dateSet.contains(localDate));
				isDateExists[index].isLegacyTx = (map.get(ChannelType.LEGACY_TX_RTP).dateSet.contains(localDate));
				isDateExists[index].isGuardRx = (map.get(ChannelType.GUARD_RX_RTP).dateSet.contains(localDate));
				isDateExists[index].isGuardTx = (map.get(ChannelType.GUARD_TX_RTP).dateSet.contains(localDate));
				isDateExists[index].isManetRx = (map.get(ChannelType.MANET_RX_RTP).dateSet.contains(localDate));
				isDateExists[index].isManetTx = (map.get(ChannelType.MANET_TX_RTP).dateSet.contains(localDate));
				isDateExists[index].isSatcomRx = (map.get(ChannelType.SATCOM_RX_RTP).dateSet.contains(localDate));
				isDateExists[index].isSatcomTx = (map.get(ChannelType.SATCOM_TX_RTP).dateSet.contains(localDate));
			}
			index++;
		}
	}
	
	public void setHoursRange(LocalDateTime fromHour, LocalDateTime toHour)
	{
		clearRowCells(isHourExists);
		
		boolean[] isHoursChannelExists =  map.get(ChannelType.LEGACY_RX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isLegacyRx = isHourExists[j].isLegacyRx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.LEGACY_TX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isLegacyTx = isHourExists[j].isLegacyTx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.GUARD_RX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isGuardRx = isHourExists[j].isGuardRx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.GUARD_TX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isGuardTx = isHourExists[j].isGuardTx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.MANET_RX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isManetRx = isHourExists[j].isManetRx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.MANET_TX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isManetTx = isHourExists[j].isManetTx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.SATCOM_RX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isSatcomRx = isHourExists[j].isSatcomRx || isHoursChannelExists[j];
		}
		
		isHoursChannelExists =  map.get(ChannelType.SATCOM_TX_RTP).setHoursRange(fromHour, toHour);
		for (int j = 0; j < isHoursChannelExists.length; j++)
		{
			isHourExists[j].isSatcomTx = isHourExists[j].isSatcomTx || isHoursChannelExists[j];
		}
	}
	
	public void setMinutesRange(LocalDateTime fromMinute, LocalDateTime toMinute)
	{
		clearRowCells(isMinuteExists);
		
		boolean[] isMinutessChannelExists =  map.get(ChannelType.LEGACY_RX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isLegacyRx = isMinuteExists[j].isLegacyRx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.LEGACY_TX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isLegacyTx = isMinuteExists[j].isLegacyTx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.GUARD_RX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isGuardRx = isMinuteExists[j].isGuardRx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.GUARD_TX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isGuardTx = isMinuteExists[j].isGuardTx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.MANET_RX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isManetRx = isMinuteExists[j].isManetRx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.MANET_TX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isManetTx = isMinuteExists[j].isManetTx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.SATCOM_RX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isSatcomRx = isMinuteExists[j].isSatcomRx || isMinutessChannelExists[j];
		}
		
		isMinutessChannelExists =  map.get(ChannelType.SATCOM_TX_RTP).setMinutesRange(fromMinute, toMinute);
		for (int j = 0; j < isMinutessChannelExists.length; j++)
		{
			isMinuteExists[j].isSatcomTx = isMinuteExists[j].isSatcomTx || isMinutessChannelExists[j];
		}
	}
	
	public void setSecondsRange(LocalDateTime fromSecond, LocalDateTime toSecond)
	{
		clearRowCells(isSecondsExists);
		
		boolean[] isSecondsChannelExists =  map.get(ChannelType.LEGACY_RX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isLegacyRx = isSecondsExists[j].isLegacyRx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.LEGACY_TX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isLegacyTx = isSecondsExists[j].isLegacyTx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.GUARD_RX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isGuardRx = isSecondsExists[j].isGuardRx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.GUARD_TX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isGuardTx = isSecondsExists[j].isGuardTx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.MANET_RX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isManetRx = isSecondsExists[j].isManetRx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.MANET_TX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isManetTx = isSecondsExists[j].isManetTx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.SATCOM_RX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isSatcomRx = isSecondsExists[j].isSatcomRx || isSecondsChannelExists[j];
		}
		
		isSecondsChannelExists =  map.get(ChannelType.SATCOM_TX_RTP).setSecondsRange(fromSecond, toSecond);
		for (int j = 0; j < isSecondsChannelExists.length; j++)
		{
			isSecondsExists[j].isSatcomTx = isSecondsExists[j].isSatcomTx || isSecondsChannelExists[j];
		}

	}
	
	public LocalDateTime getFirstSecond()
	{
		boolean isExist = false;
		LocalDateTime firstSecond = LocalDateTime.MAX;
		for (int i = 0; i < channels.length; i++)
		{
			TreeSet<CapturedPacket> channelSeconds = channels[i].secondsPackets;
			if(!channelSeconds.isEmpty())
			{
				LocalDateTime channelFirst = channelSeconds.first().localDateTime;
				if(channelFirst.isBefore(firstSecond))
				{
					firstSecond = channelFirst;
					isExist = true;
				}
			}
		}
		if(isExist)
		{
			return firstSecond;
		}
		return null;
	}
	
	public LocalDateTime getLastSecond()
	{
		boolean isExist = false;
		LocalDateTime lastSecond = LocalDateTime.MIN;
		for (int i = 0; i < channels.length; i++)
		{
			TreeSet<CapturedPacket> channelSeconds = channels[i].secondsPackets;
			if(!channelSeconds.isEmpty())
			{
				LocalDateTime channelLast = channelSeconds.last().localDateTime;
				if(channelLast.isAfter(lastSecond))
				{
					lastSecond = channelLast;
					isExist = true;
				}
			}
		}
		if(isExist)
		{
			return lastSecond;
		}
		return null;	
	}
	
	public RowResult mixerChannels(LocalDateTime fromTime, LocalDateTime toTime)
	{
		setSecondsRange(fromTime, toTime);
		
		RowResult rowResult = new RowResult(channels.length);
		
		final Duration duration 						= Duration.between(fromTime, toTime);
		final long durationInSeconds 					= duration.getSeconds();
		
		
		int audioByteArrayLength 		= (int) (durationInSeconds * sampleRate * sampleSizeInBytes * channelsNumber);
		int audioSamplesArrayLength 	= audioByteArrayLength / sampleSizeInBytes;
		
		int[] totalArray			= new int[audioSamplesArrayLength];
		log.debug("ROW : durationInSeconds = {}, sampleRate = {}, sampleSizeInBytes = {}, channelsNumber = {} ,audioByteArrayLength = {}, audioSamplesArrayLength = {}",durationInSeconds,sampleRate,sampleSizeInBytes,channelsNumber, audioByteArrayLength,audioSamplesArrayLength);
		
		for (int i = 0; i < channels.length; i++)
		{
				ChannelResult result = channels[i].createAudioData(fromTime, audioByteArrayLength, audioSamplesArrayLength, timeForByteInMicroseconds, bytesPerSecond);
				log.debug("ChannelResult for channel = {} : {}",channels[i], result);
				
				rowResult.channelResults[i] = result;
				if(result != null)
				{
					byte[] channelAudioByteArray 	= (byte[]) result.audioByteArray;				
					addSingleChannel(totalArray, channelAudioByteArray);
				}
			
		}
		
		int gain = 1;
		
		rowResult.mixerBuffer 			= normalize(totalArray, gain);
		rowResult.telemetry 			= telemetryData;
		rowResult.dir					= directory;
		rowResult.fromTime				= fromTime;
		rowResult.toTime				= toTime;
		rowResult.durationInSeconds		= durationInSeconds;
		rowResult.bytesPerSecond		= rowResult.mixerBuffer.capacity() / durationInSeconds;
		
		return rowResult;
	}
	
	private void addSingleChannel(int[] totalArray,byte[] channelAudioByteArray)
	{
		ByteBuffer channelAudioByteBuffer = ByteBuffer.wrap(channelAudioByteArray).order(ByteOrder.LITTLE_ENDIAN);
		int index = 0;
		while(channelAudioByteBuffer.hasRemaining())
		{
			totalArray[index]  = totalArray[index] + channelAudioByteBuffer.getShort();
			index++;
		}
	}
	
	private ByteBuffer normalize(int [] totalArray, double gain)
	{
		int minValue = 0;
		int maxValue = 0;
		for (int i = 0; i < totalArray.length; i++)
		{
			if (totalArray[i] > maxValue)
			{
				maxValue = totalArray[i];
			}
			else if (totalArray[i] < minValue)
			{
				minValue = totalArray[i];
			}
		}
        
		if (minValue > 0)
		{
			minValue = 0 - minValue;
		}
		
		if (minValue > maxValue)
		{
			maxValue = minValue;
		}
        
		if (maxValue > Short.MAX_VALUE)
		{
			gain = (gain * (double) Short.MAX_VALUE) / (double) maxValue;
		}
      
		ByteBuffer result = ByteBuffer.allocate(totalArray.length * 2).order(ByteOrder.LITTLE_ENDIAN);
		
		for (int i = 0; i < totalArray.length; i++)
		{
			short res = (short) ((double) totalArray[i] * gain);
			result.putShort(res);
		}
		return result;
	}
	
	public TreeSet<LocalDate> getDates()
	{
		return dates;
	}


	public File getUnit()
	{
		return this.directory;
	}
	
	@Override
	public String toString()
	{
		return getUnit().getName();
	}
	
	public RowCell getDay0() {return isDateExists[0];}
	public RowCell getDay1() {return isDateExists[1];}
	public RowCell getDay2() {return isDateExists[2];}
	public RowCell getDay3() {return isDateExists[3];}
	public RowCell getDay4() {return isDateExists[4];}
	public RowCell getDay5() {return isDateExists[5];}
	public RowCell getDay6() {return isDateExists[6];}
	public RowCell getDay7() {return isDateExists[7];}
	public RowCell getDay8() {return isDateExists[8];}
	public RowCell getDay9() {return isDateExists[9];}
	public RowCell getDay10() {return isDateExists[10];}
	public RowCell getDay11() {return isDateExists[11];}
	public RowCell getDay12() {return isDateExists[12];}
	public RowCell getDay13() {return isDateExists[13];}
	public RowCell getDay14() {return isDateExists[14];}
	public RowCell getDay15() {return isDateExists[15];}
	public RowCell getDay16() {return isDateExists[16];}
	public RowCell getDay17() {return isDateExists[17];}
	public RowCell getDay18() {return isDateExists[18];}
	public RowCell getDay19() {return isDateExists[19];}
	public RowCell getDay20() {return isDateExists[20];}
	public RowCell getDay21() {return isDateExists[21];}
	public RowCell getDay22() {return isDateExists[22];}
	public RowCell getDay23() {return isDateExists[23];}
	public RowCell getDay24() {return isDateExists[24];}
	public RowCell getDay25() {return isDateExists[25];}
	public RowCell getDay26() {return isDateExists[26];}
	public RowCell getDay27() {return isDateExists[27];}
	public RowCell getDay28() {return isDateExists[28];}
	public RowCell getDay29() {return isDateExists[29];}
	public RowCell getDay30() {return isDateExists[30];}
	public RowCell getDay31() {return isDateExists[31];}
	public RowCell getDay32() {return isDateExists[32];}
	public RowCell getDay33() {return isDateExists[33];}
	public RowCell getDay34() {return isDateExists[34];}
	public RowCell getDay35() {return isDateExists[35];}
	public RowCell getDay36() {return isDateExists[36];}
	public RowCell getDay37() {return isDateExists[37];}
	public RowCell getDay38() {return isDateExists[38];}
	public RowCell getDay39() {return isDateExists[39];}
	public RowCell getDay40() {return isDateExists[40];}
	public RowCell getDay41() {return isDateExists[41];}
	public RowCell getDay42() {return isDateExists[42];}
	public RowCell getDay43() {return isDateExists[43];}
	public RowCell getDay44() {return isDateExists[44];}
	public RowCell getDay45() {return isDateExists[45];}
	public RowCell getDay46() {return isDateExists[46];}
	public RowCell getDay47() {return isDateExists[47];}
	public RowCell getDay48() {return isDateExists[48];}
	public RowCell getDay49() {return isDateExists[49];}
	public RowCell getDay50() {return isDateExists[50];}
	public RowCell getDay51() {return isDateExists[51];}
	public RowCell getDay52() {return isDateExists[52];}
	public RowCell getDay53() {return isDateExists[53];}
	public RowCell getDay54() {return isDateExists[54];}
	public RowCell getDay55() {return isDateExists[55];}
	public RowCell getDay56() {return isDateExists[56];}
	public RowCell getDay57() {return isDateExists[57];}
	public RowCell getDay58() {return isDateExists[58];}
	public RowCell getDay59() {return isDateExists[59];}
	public RowCell getDay60() {return isDateExists[60];}
	public RowCell getDay61() {return isDateExists[61];}
	public RowCell getDay62() {return isDateExists[62];}
	public RowCell getDay63() {return isDateExists[63];}
	public RowCell getDay64() {return isDateExists[64];}
	public RowCell getDay65() {return isDateExists[65];}
	public RowCell getDay66() {return isDateExists[66];}
	public RowCell getDay67() {return isDateExists[67];}
	public RowCell getDay68() {return isDateExists[68];}
	public RowCell getDay69() {return isDateExists[69];}
	public RowCell getDay70() {return isDateExists[70];}
	public RowCell getDay71() {return isDateExists[71];}
	public RowCell getDay72() {return isDateExists[72];}
	public RowCell getDay73() {return isDateExists[73];}
	public RowCell getDay74() {return isDateExists[74];}
	public RowCell getDay75() {return isDateExists[75];}
	public RowCell getDay76() {return isDateExists[76];}
	public RowCell getDay77() {return isDateExists[77];}
	public RowCell getDay78() {return isDateExists[78];}
	public RowCell getDay79() {return isDateExists[79];}
	public RowCell getDay80() {return isDateExists[80];}
	public RowCell getDay81() {return isDateExists[81];}
	public RowCell getDay82() {return isDateExists[82];}
	public RowCell getDay83() {return isDateExists[83];}
	public RowCell getDay84() {return isDateExists[84];}
	public RowCell getDay85() {return isDateExists[85];}
	public RowCell getDay86() {return isDateExists[86];}
	public RowCell getDay87() {return isDateExists[87];}
	public RowCell getDay88() {return isDateExists[88];}
	public RowCell getDay89() {return isDateExists[89];}
	public RowCell getDay90() {return isDateExists[90];}
	public RowCell getDay91() {return isDateExists[91];}
	public RowCell getDay92() {return isDateExists[92];}
	public RowCell getDay93() {return isDateExists[93];}
	public RowCell getDay94() {return isDateExists[94];}
	public RowCell getDay95() {return isDateExists[95];}
	public RowCell getDay96() {return isDateExists[96];}
	public RowCell getDay97() {return isDateExists[97];}
	public RowCell getDay98() {return isDateExists[98];}
	public RowCell getDay99() {return isDateExists[99];}
	
	public RowCell getHour0() 	{return isHourExists[0];}
	public RowCell getHour1() 	{return isHourExists[1];}
	public RowCell getHour2() 	{return isHourExists[2];}
	public RowCell getHour3() 	{return isHourExists[3];}
	public RowCell getHour4() 	{return isHourExists[4];}
	public RowCell getHour5() 	{return isHourExists[5];}
	public RowCell getHour6() 	{return isHourExists[6];}
	public RowCell getHour7() 	{return isHourExists[7];}
	public RowCell getHour8() 	{return isHourExists[8];}
	public RowCell getHour9() 	{return isHourExists[9];}
	public RowCell getHour10() {return isHourExists[10];}
	public RowCell getHour11() {return isHourExists[11];}
	public RowCell getHour12() {return isHourExists[12];}
	public RowCell getHour13() {return isHourExists[13];}
	public RowCell getHour14() {return isHourExists[14];}
	public RowCell getHour15() {return isHourExists[15];}
	public RowCell getHour16() {return isHourExists[16];}
	public RowCell getHour17() {return isHourExists[17];}
	public RowCell getHour18() {return isHourExists[18];}
	public RowCell getHour19() {return isHourExists[19];}
	public RowCell getHour20() {return isHourExists[20];}
	public RowCell getHour21() {return isHourExists[21];}
	public RowCell getHour22() {return isHourExists[22];}
	public RowCell getHour23() {return isHourExists[23];}
	public RowCell getHour24() {return isHourExists[24];}
	public RowCell getHour25() {return isHourExists[25];}
	
	public RowCell getMinute0() {return isMinuteExists[0];}
	public RowCell getMinute1() {return isMinuteExists[1];}
	public RowCell getMinute2() {return isMinuteExists[2];}
	public RowCell getMinute3() {return isMinuteExists[3];}
	public RowCell getMinute4() {return isMinuteExists[4];}
	public RowCell getMinute5() {return isMinuteExists[5];}
	public RowCell getMinute6() {return isMinuteExists[6];}
	public RowCell getMinute7() {return isMinuteExists[7];}
	public RowCell getMinute8() {return isMinuteExists[8];}
	public RowCell getMinute9() {return isMinuteExists[9];}
	public RowCell getMinute10() {return isMinuteExists[10];}
	public RowCell getMinute11() {return isMinuteExists[11];}
	public RowCell getMinute12() {return isMinuteExists[12];}
	public RowCell getMinute13() {return isMinuteExists[13];}
	public RowCell getMinute14() {return isMinuteExists[14];}
	public RowCell getMinute15() {return isMinuteExists[15];}
	public RowCell getMinute16() {return isMinuteExists[16];}
	public RowCell getMinute17() {return isMinuteExists[17];}
	public RowCell getMinute18() {return isMinuteExists[18];}
	public RowCell getMinute19() {return isMinuteExists[19];}
	public RowCell getMinute20() {return isMinuteExists[20];}
	public RowCell getMinute21() {return isMinuteExists[21];}
	public RowCell getMinute22() {return isMinuteExists[22];}
	public RowCell getMinute23() {return isMinuteExists[23];}
	public RowCell getMinute24() {return isMinuteExists[24];}
	public RowCell getMinute25() {return isMinuteExists[25];}
	public RowCell getMinute26() {return isMinuteExists[26];}
	public RowCell getMinute27() {return isMinuteExists[27];}
	public RowCell getMinute28() {return isMinuteExists[28];}
	public RowCell getMinute29() {return isMinuteExists[29];}
	public RowCell getMinute30() {return isMinuteExists[30];}
	public RowCell getMinute31() {return isMinuteExists[31];}
	public RowCell getMinute32() {return isMinuteExists[32];}
	public RowCell getMinute33() {return isMinuteExists[33];}
	public RowCell getMinute34() {return isMinuteExists[34];}
	public RowCell getMinute35() {return isMinuteExists[35];}
	public RowCell getMinute36() {return isMinuteExists[36];}
	public RowCell getMinute37() {return isMinuteExists[37];}
	public RowCell getMinute38() {return isMinuteExists[38];}
	public RowCell getMinute39() {return isMinuteExists[39];}
	public RowCell getMinute40() {return isMinuteExists[40];}
	public RowCell getMinute41() {return isMinuteExists[41];}
	public RowCell getMinute42() {return isMinuteExists[42];}
	public RowCell getMinute43() {return isMinuteExists[43];}
	public RowCell getMinute44() {return isMinuteExists[44];}
	public RowCell getMinute45() {return isMinuteExists[45];}
	public RowCell getMinute46() {return isMinuteExists[46];}
	public RowCell getMinute47() {return isMinuteExists[47];}
	public RowCell getMinute48() {return isMinuteExists[48];}
	public RowCell getMinute49() {return isMinuteExists[49];}
	public RowCell getMinute50() {return isMinuteExists[50];}
	public RowCell getMinute51() {return isMinuteExists[51];}
	public RowCell getMinute52() {return isMinuteExists[52];}
	public RowCell getMinute53() {return isMinuteExists[53];}
	public RowCell getMinute54() {return isMinuteExists[54];}
	public RowCell getMinute55() {return isMinuteExists[55];}
	public RowCell getMinute56() {return isMinuteExists[56];}
	public RowCell getMinute57() {return isMinuteExists[57];}
	public RowCell getMinute58() {return isMinuteExists[58];}
	public RowCell getMinute59() {return isMinuteExists[59];}
	public RowCell getMinute60() {return isMinuteExists[60];}
	public RowCell getMinute61() {return isMinuteExists[61];}
	public RowCell getMinute62() {return isMinuteExists[62];}
	public RowCell getMinute63() {return isMinuteExists[63];}
	public RowCell getMinute64() {return isMinuteExists[64];}
	public RowCell getMinute65() {return isMinuteExists[65];}
	public RowCell getMinute66() {return isMinuteExists[66];}
	public RowCell getMinute67() {return isMinuteExists[67];}
	public RowCell getMinute68() {return isMinuteExists[68];}
	public RowCell getMinute69() {return isMinuteExists[69];}
	
	public RowCell getSecond0() {return isSecondsExists[0];}
	public RowCell getSecond1() {return isSecondsExists[1];}
	public RowCell getSecond2() {return isSecondsExists[2];}
	public RowCell getSecond3() {return isSecondsExists[3];}
	public RowCell getSecond4() {return isSecondsExists[4];}
	public RowCell getSecond5() {return isSecondsExists[5];}
	public RowCell getSecond6() {return isSecondsExists[6];}
	public RowCell getSecond7() {return isSecondsExists[7];}
	public RowCell getSecond8() {return isSecondsExists[8];}
	public RowCell getSecond9() {return isSecondsExists[9];}
	public RowCell getSecond10() {return isSecondsExists[10];}
	public RowCell getSecond11() {return isSecondsExists[11];}
	public RowCell getSecond12() {return isSecondsExists[12];}
	public RowCell getSecond13() {return isSecondsExists[13];}
	public RowCell getSecond14() {return isSecondsExists[14];}
	public RowCell getSecond15() {return isSecondsExists[15];}
	public RowCell getSecond16() {return isSecondsExists[16];}
	public RowCell getSecond17() {return isSecondsExists[17];}
	public RowCell getSecond18() {return isSecondsExists[18];}
	public RowCell getSecond19() {return isSecondsExists[19];}
	public RowCell getSecond20() {return isSecondsExists[20];}
	public RowCell getSecond21() {return isSecondsExists[21];}
	public RowCell getSecond22() {return isSecondsExists[22];}
	public RowCell getSecond23() {return isSecondsExists[23];}
	public RowCell getSecond24() {return isSecondsExists[24];}
	public RowCell getSecond25() {return isSecondsExists[25];}
	public RowCell getSecond26() {return isSecondsExists[26];}
	public RowCell getSecond27() {return isSecondsExists[27];}
	public RowCell getSecond28() {return isSecondsExists[28];}
	public RowCell getSecond29() {return isSecondsExists[29];}
	public RowCell getSecond30() {return isSecondsExists[30];}
	public RowCell getSecond31() {return isSecondsExists[31];}
	public RowCell getSecond32() {return isSecondsExists[32];}
	public RowCell getSecond33() {return isSecondsExists[33];}
	public RowCell getSecond34() {return isSecondsExists[34];}
	public RowCell getSecond35() {return isSecondsExists[35];}
	public RowCell getSecond36() {return isSecondsExists[36];}
	public RowCell getSecond37() {return isSecondsExists[37];}
	public RowCell getSecond38() {return isSecondsExists[38];}
	public RowCell getSecond39() {return isSecondsExists[39];}
	public RowCell getSecond40() {return isSecondsExists[40];}
	public RowCell getSecond41() {return isSecondsExists[41];}
	public RowCell getSecond42() {return isSecondsExists[42];}
	public RowCell getSecond43() {return isSecondsExists[43];}
	public RowCell getSecond44() {return isSecondsExists[44];}
	public RowCell getSecond45() {return isSecondsExists[45];}
	public RowCell getSecond46() {return isSecondsExists[46];}
	public RowCell getSecond47() {return isSecondsExists[47];}
	public RowCell getSecond48() {return isSecondsExists[48];}
	public RowCell getSecond49() {return isSecondsExists[49];}
	public RowCell getSecond50() {return isSecondsExists[50];}
	public RowCell getSecond51() {return isSecondsExists[51];}
	public RowCell getSecond52() {return isSecondsExists[52];}
	public RowCell getSecond53() {return isSecondsExists[53];}
	public RowCell getSecond54() {return isSecondsExists[54];}
	public RowCell getSecond55() {return isSecondsExists[55];}
	public RowCell getSecond56() {return isSecondsExists[56];}
	public RowCell getSecond57() {return isSecondsExists[57];}
	public RowCell getSecond58() {return isSecondsExists[58];}
	public RowCell getSecond59() {return isSecondsExists[59];}
	public RowCell getSecond60() {return isSecondsExists[60];}
	public RowCell getSecond61() {return isSecondsExists[61];}
	public RowCell getSecond62() {return isSecondsExists[62];}
	public RowCell getSecond63() {return isSecondsExists[63];}
	public RowCell getSecond64() {return isSecondsExists[64];}
	public RowCell getSecond65() {return isSecondsExists[65];}
	public RowCell getSecond66() {return isSecondsExists[66];}
	public RowCell getSecond67() {return isSecondsExists[67];}
	public RowCell getSecond68() {return isSecondsExists[68];}
	public RowCell getSecond69() {return isSecondsExists[69];}
	public RowCell getSecond70() {return isSecondsExists[70];}
	public RowCell getSecond71() {return isSecondsExists[71];}
	public RowCell getSecond72() {return isSecondsExists[72];}
	public RowCell getSecond73() {return isSecondsExists[73];}
	public RowCell getSecond74() {return isSecondsExists[74];}
	public RowCell getSecond75() {return isSecondsExists[75];}
	public RowCell getSecond76() {return isSecondsExists[76];}
	public RowCell getSecond77() {return isSecondsExists[77];}
	public RowCell getSecond78() {return isSecondsExists[78];}
	public RowCell getSecond79() {return isSecondsExists[79];}
	public RowCell getSecond80() {return isSecondsExists[80];}
	public RowCell getSecond81() {return isSecondsExists[81];}
	public RowCell getSecond82() {return isSecondsExists[82];}
	public RowCell getSecond83() {return isSecondsExists[83];}
	public RowCell getSecond84() {return isSecondsExists[84];}
	public RowCell getSecond85() {return isSecondsExists[85];}
	public RowCell getSecond86() {return isSecondsExists[86];}
	public RowCell getSecond87() {return isSecondsExists[87];}
	public RowCell getSecond88() {return isSecondsExists[88];}
	public RowCell getSecond89() {return isSecondsExists[89];}
	public RowCell getSecond90() {return isSecondsExists[90];}
	public RowCell getSecond91() {return isSecondsExists[91];}
	public RowCell getSecond92() {return isSecondsExists[92];}
	public RowCell getSecond93() {return isSecondsExists[93];}
	public RowCell getSecond94() {return isSecondsExists[94];}
	public RowCell getSecond95() {return isSecondsExists[95];}
	public RowCell getSecond96() {return isSecondsExists[96];}
	public RowCell getSecond97() {return isSecondsExists[97];}
	public RowCell getSecond98() {return isSecondsExists[98];}
	public RowCell getSecond99() {return isSecondsExists[99];}
	public RowCell getSecond100() {return isSecondsExists[100];}
	public RowCell getSecond101() {return isSecondsExists[101];}
	public RowCell getSecond102() {return isSecondsExists[102];}
	public RowCell getSecond103() {return isSecondsExists[103];}
	public RowCell getSecond104() {return isSecondsExists[104];}
	public RowCell getSecond105() {return isSecondsExists[105];}
	public RowCell getSecond106() {return isSecondsExists[106];}
	public RowCell getSecond107() {return isSecondsExists[107];}
	public RowCell getSecond108() {return isSecondsExists[108];}
	public RowCell getSecond109() {return isSecondsExists[109];}
	public RowCell getSecond110() {return isSecondsExists[110];}
	public RowCell getSecond111() {return isSecondsExists[111];}
	public RowCell getSecond112() {return isSecondsExists[112];}
	public RowCell getSecond113() {return isSecondsExists[113];}
	public RowCell getSecond114() {return isSecondsExists[114];}
	public RowCell getSecond115() {return isSecondsExists[115];}
	public RowCell getSecond116() {return isSecondsExists[116];}
	public RowCell getSecond117() {return isSecondsExists[117];}
	public RowCell getSecond118() {return isSecondsExists[118];}
	public RowCell getSecond119() {return isSecondsExists[119];}
	public RowCell getSecond120() {return isSecondsExists[120];}
	public RowCell getSecond121() {return isSecondsExists[121];}
	public RowCell getSecond122() {return isSecondsExists[122];}
	public RowCell getSecond123() {return isSecondsExists[123];}
	public RowCell getSecond124() {return isSecondsExists[124];}
	public RowCell getSecond125() {return isSecondsExists[125];}
	public RowCell getSecond126() {return isSecondsExists[126];}
	public RowCell getSecond127() {return isSecondsExists[127];}
	public RowCell getSecond128() {return isSecondsExists[128];}
	public RowCell getSecond129() {return isSecondsExists[129];}
	public RowCell getSecond130() {return isSecondsExists[130];}
	public RowCell getSecond131() {return isSecondsExists[131];}
	public RowCell getSecond132() {return isSecondsExists[132];}
	public RowCell getSecond133() {return isSecondsExists[133];}
	public RowCell getSecond134() {return isSecondsExists[134];}
	public RowCell getSecond135() {return isSecondsExists[135];}
	public RowCell getSecond136() {return isSecondsExists[136];}
	public RowCell getSecond137() {return isSecondsExists[137];}
	public RowCell getSecond138() {return isSecondsExists[138];}
	public RowCell getSecond139() {return isSecondsExists[139];}
	public RowCell getSecond140() {return isSecondsExists[140];}
	public RowCell getSecond141() {return isSecondsExists[141];}
	public RowCell getSecond142() {return isSecondsExists[142];}
	public RowCell getSecond143() {return isSecondsExists[143];}
	public RowCell getSecond144() {return isSecondsExists[144];}
	public RowCell getSecond145() {return isSecondsExists[145];}
	public RowCell getSecond146() {return isSecondsExists[146];}
	public RowCell getSecond147() {return isSecondsExists[147];}
	public RowCell getSecond148() {return isSecondsExists[148];}
	public RowCell getSecond149() {return isSecondsExists[149];}
	public RowCell getSecond150() {return isSecondsExists[150];}
	public RowCell getSecond151() {return isSecondsExists[151];}
	public RowCell getSecond152() {return isSecondsExists[152];}
	public RowCell getSecond153() {return isSecondsExists[153];}
	public RowCell getSecond154() {return isSecondsExists[154];}
	public RowCell getSecond155() {return isSecondsExists[155];}
	public RowCell getSecond156() {return isSecondsExists[156];}
	public RowCell getSecond157() {return isSecondsExists[157];}
	public RowCell getSecond158() {return isSecondsExists[158];}
	public RowCell getSecond159() {return isSecondsExists[159];}
	public RowCell getSecond160() {return isSecondsExists[160];}
	public RowCell getSecond161() {return isSecondsExists[161];}
	public RowCell getSecond162() {return isSecondsExists[162];}
	public RowCell getSecond163() {return isSecondsExists[163];}
	public RowCell getSecond164() {return isSecondsExists[164];}
	public RowCell getSecond165() {return isSecondsExists[165];}
	public RowCell getSecond166() {return isSecondsExists[166];}
	public RowCell getSecond167() {return isSecondsExists[167];}
	public RowCell getSecond168() {return isSecondsExists[168];}
	public RowCell getSecond169() {return isSecondsExists[169];}
	public RowCell getSecond170() {return isSecondsExists[170];}
	public RowCell getSecond171() {return isSecondsExists[171];}
	public RowCell getSecond172() {return isSecondsExists[172];}
	public RowCell getSecond173() {return isSecondsExists[173];}
	public RowCell getSecond174() {return isSecondsExists[174];}
	public RowCell getSecond175() {return isSecondsExists[175];}
	public RowCell getSecond176() {return isSecondsExists[176];}
	public RowCell getSecond177() {return isSecondsExists[177];}
	public RowCell getSecond178() {return isSecondsExists[178];}
	public RowCell getSecond179() {return isSecondsExists[179];}
	public RowCell getSecond180() {return isSecondsExists[180];}
	public RowCell getSecond181() {return isSecondsExists[181];}
	public RowCell getSecond182() {return isSecondsExists[182];}
	public RowCell getSecond183() {return isSecondsExists[183];}
	public RowCell getSecond184() {return isSecondsExists[184];}
	public RowCell getSecond185() {return isSecondsExists[185];}
	public RowCell getSecond186() {return isSecondsExists[186];}
	public RowCell getSecond187() {return isSecondsExists[187];}
	public RowCell getSecond188() {return isSecondsExists[188];}
	public RowCell getSecond189() {return isSecondsExists[189];}
	public RowCell getSecond190() {return isSecondsExists[190];}
	public RowCell getSecond191() {return isSecondsExists[191];}
	public RowCell getSecond192() {return isSecondsExists[192];}
	public RowCell getSecond193() {return isSecondsExists[193];}
	public RowCell getSecond194() {return isSecondsExists[194];}
	public RowCell getSecond195() {return isSecondsExists[195];}
	public RowCell getSecond196() {return isSecondsExists[196];}
	public RowCell getSecond197() {return isSecondsExists[197];}
	public RowCell getSecond198() {return isSecondsExists[198];}
	public RowCell getSecond199() {return isSecondsExists[199];}
	public RowCell getSecond200() {return isSecondsExists[200];}
	public RowCell getSecond201() {return isSecondsExists[201];}
	public RowCell getSecond202() {return isSecondsExists[202];}
	public RowCell getSecond203() {return isSecondsExists[203];}
	public RowCell getSecond204() {return isSecondsExists[204];}
	public RowCell getSecond205() {return isSecondsExists[205];}
	public RowCell getSecond206() {return isSecondsExists[206];}
	public RowCell getSecond207() {return isSecondsExists[207];}
	public RowCell getSecond208() {return isSecondsExists[208];}
	public RowCell getSecond209() {return isSecondsExists[209];}
	public RowCell getSecond210() {return isSecondsExists[210];}
	public RowCell getSecond211() {return isSecondsExists[211];}
	public RowCell getSecond212() {return isSecondsExists[212];}
	public RowCell getSecond213() {return isSecondsExists[213];}
	public RowCell getSecond214() {return isSecondsExists[214];}
	public RowCell getSecond215() {return isSecondsExists[215];}
	public RowCell getSecond216() {return isSecondsExists[216];}
	public RowCell getSecond217() {return isSecondsExists[217];}
	public RowCell getSecond218() {return isSecondsExists[218];}
	public RowCell getSecond219() {return isSecondsExists[219];}
	public RowCell getSecond220() {return isSecondsExists[220];}
	public RowCell getSecond221() {return isSecondsExists[221];}
	public RowCell getSecond222() {return isSecondsExists[222];}
	public RowCell getSecond223() {return isSecondsExists[223];}
	public RowCell getSecond224() {return isSecondsExists[224];}
	public RowCell getSecond225() {return isSecondsExists[225];}
	public RowCell getSecond226() {return isSecondsExists[226];}
	public RowCell getSecond227() {return isSecondsExists[227];}
	public RowCell getSecond228() {return isSecondsExists[228];}
	public RowCell getSecond229() {return isSecondsExists[229];}
	public RowCell getSecond230() {return isSecondsExists[230];}
	public RowCell getSecond231() {return isSecondsExists[231];}
	public RowCell getSecond232() {return isSecondsExists[232];}
	public RowCell getSecond233() {return isSecondsExists[233];}
	public RowCell getSecond234() {return isSecondsExists[234];}
	public RowCell getSecond235() {return isSecondsExists[235];}
	public RowCell getSecond236() {return isSecondsExists[236];}
	public RowCell getSecond237() {return isSecondsExists[237];}
	public RowCell getSecond238() {return isSecondsExists[238];}
	public RowCell getSecond239() {return isSecondsExists[239];}
	public RowCell getSecond240() {return isSecondsExists[240];}
	public RowCell getSecond241() {return isSecondsExists[241];}
	public RowCell getSecond242() {return isSecondsExists[242];}
	public RowCell getSecond243() {return isSecondsExists[243];}
	public RowCell getSecond244() {return isSecondsExists[244];}
	public RowCell getSecond245() {return isSecondsExists[245];}
	public RowCell getSecond246() {return isSecondsExists[246];}
	public RowCell getSecond247() {return isSecondsExists[247];}
	public RowCell getSecond248() {return isSecondsExists[248];}
	public RowCell getSecond249() {return isSecondsExists[249];}
	public RowCell getSecond250() {return isSecondsExists[250];}
	public RowCell getSecond251() {return isSecondsExists[251];}
	public RowCell getSecond252() {return isSecondsExists[252];}
	public RowCell getSecond253() {return isSecondsExists[253];}
	public RowCell getSecond254() {return isSecondsExists[254];}
	public RowCell getSecond255() {return isSecondsExists[255];}
	public RowCell getSecond256() {return isSecondsExists[256];}
	public RowCell getSecond257() {return isSecondsExists[257];}
	public RowCell getSecond258() {return isSecondsExists[258];}
	public RowCell getSecond259() {return isSecondsExists[259];}
	public RowCell getSecond260() {return isSecondsExists[260];}
	public RowCell getSecond261() {return isSecondsExists[261];}
	public RowCell getSecond262() {return isSecondsExists[262];}
	public RowCell getSecond263() {return isSecondsExists[263];}
	public RowCell getSecond264() {return isSecondsExists[264];}
	public RowCell getSecond265() {return isSecondsExists[265];}
	public RowCell getSecond266() {return isSecondsExists[266];}
	public RowCell getSecond267() {return isSecondsExists[267];}
	public RowCell getSecond268() {return isSecondsExists[268];}
	public RowCell getSecond269() {return isSecondsExists[269];}
	public RowCell getSecond270() {return isSecondsExists[270];}
	public RowCell getSecond271() {return isSecondsExists[271];}
	public RowCell getSecond272() {return isSecondsExists[272];}
	public RowCell getSecond273() {return isSecondsExists[273];}
	public RowCell getSecond274() {return isSecondsExists[274];}
	public RowCell getSecond275() {return isSecondsExists[275];}
	public RowCell getSecond276() {return isSecondsExists[276];}
	public RowCell getSecond277() {return isSecondsExists[277];}
	public RowCell getSecond278() {return isSecondsExists[278];}
	public RowCell getSecond279() {return isSecondsExists[279];}
	public RowCell getSecond280() {return isSecondsExists[280];}
	public RowCell getSecond281() {return isSecondsExists[281];}
	public RowCell getSecond282() {return isSecondsExists[282];}
	public RowCell getSecond283() {return isSecondsExists[283];}
	public RowCell getSecond284() {return isSecondsExists[284];}
	public RowCell getSecond285() {return isSecondsExists[285];}
	public RowCell getSecond286() {return isSecondsExists[286];}
	public RowCell getSecond287() {return isSecondsExists[287];}
	public RowCell getSecond288() {return isSecondsExists[288];}
	public RowCell getSecond289() {return isSecondsExists[289];}
	public RowCell getSecond290() {return isSecondsExists[290];}
	public RowCell getSecond291() {return isSecondsExists[291];}
	public RowCell getSecond292() {return isSecondsExists[292];}
	public RowCell getSecond293() {return isSecondsExists[293];}
	public RowCell getSecond294() {return isSecondsExists[294];}
	public RowCell getSecond295() {return isSecondsExists[295];}
	public RowCell getSecond296() {return isSecondsExists[296];}
	public RowCell getSecond297() {return isSecondsExists[297];}
	public RowCell getSecond298() {return isSecondsExists[298];}
	public RowCell getSecond299() {return isSecondsExists[299];}
	public RowCell getSecond300() {return isSecondsExists[300];}
	public RowCell getSecond301() {return isSecondsExists[301];}
	public RowCell getSecond302() {return isSecondsExists[302];}
	public RowCell getSecond303() {return isSecondsExists[303];}
	public RowCell getSecond304() {return isSecondsExists[304];}
	public RowCell getSecond305() {return isSecondsExists[305];}
	public RowCell getSecond306() {return isSecondsExists[306];}
	public RowCell getSecond307() {return isSecondsExists[307];}
	public RowCell getSecond308() {return isSecondsExists[308];}
	public RowCell getSecond309() {return isSecondsExists[309];}
	public RowCell getSecond310() {return isSecondsExists[310];}
	public RowCell getSecond311() {return isSecondsExists[311];}
	public RowCell getSecond312() {return isSecondsExists[312];}
	public RowCell getSecond313() {return isSecondsExists[313];}
	public RowCell getSecond314() {return isSecondsExists[314];}
	public RowCell getSecond315() {return isSecondsExists[315];}
	public RowCell getSecond316() {return isSecondsExists[316];}
	public RowCell getSecond317() {return isSecondsExists[317];}
	public RowCell getSecond318() {return isSecondsExists[318];}
	public RowCell getSecond319() {return isSecondsExists[319];}
	public RowCell getSecond320() {return isSecondsExists[320];}
	public RowCell getSecond321() {return isSecondsExists[321];}
	public RowCell getSecond322() {return isSecondsExists[322];}
	public RowCell getSecond323() {return isSecondsExists[323];}
	public RowCell getSecond324() {return isSecondsExists[324];}
	public RowCell getSecond325() {return isSecondsExists[325];}
	public RowCell getSecond326() {return isSecondsExists[326];}
	public RowCell getSecond327() {return isSecondsExists[327];}
	public RowCell getSecond328() {return isSecondsExists[328];}
	public RowCell getSecond329() {return isSecondsExists[329];}
	public RowCell getSecond330() {return isSecondsExists[330];}
	public RowCell getSecond331() {return isSecondsExists[331];}
	public RowCell getSecond332() {return isSecondsExists[332];}
	public RowCell getSecond333() {return isSecondsExists[333];}
	public RowCell getSecond334() {return isSecondsExists[334];}
	public RowCell getSecond335() {return isSecondsExists[335];}
	public RowCell getSecond336() {return isSecondsExists[336];}
	public RowCell getSecond337() {return isSecondsExists[337];}
	public RowCell getSecond338() {return isSecondsExists[338];}
	public RowCell getSecond339() {return isSecondsExists[339];}
	public RowCell getSecond340() {return isSecondsExists[340];}
	public RowCell getSecond341() {return isSecondsExists[341];}
	public RowCell getSecond342() {return isSecondsExists[342];}
	public RowCell getSecond343() {return isSecondsExists[343];}
	public RowCell getSecond344() {return isSecondsExists[344];}
	public RowCell getSecond345() {return isSecondsExists[345];}
	public RowCell getSecond346() {return isSecondsExists[346];}
	public RowCell getSecond347() {return isSecondsExists[347];}
	public RowCell getSecond348() {return isSecondsExists[348];}
	public RowCell getSecond349() {return isSecondsExists[349];}
	public RowCell getSecond350() {return isSecondsExists[350];}
	public RowCell getSecond351() {return isSecondsExists[351];}
	public RowCell getSecond352() {return isSecondsExists[352];}
	public RowCell getSecond353() {return isSecondsExists[353];}
	public RowCell getSecond354() {return isSecondsExists[354];}
	public RowCell getSecond355() {return isSecondsExists[355];}
	public RowCell getSecond356() {return isSecondsExists[356];}
	public RowCell getSecond357() {return isSecondsExists[357];}
	public RowCell getSecond358() {return isSecondsExists[358];}
	public RowCell getSecond359() {return isSecondsExists[359];}
}
