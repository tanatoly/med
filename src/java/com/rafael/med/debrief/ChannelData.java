package com.rafael.med.debrief;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.CapturedPacket;
import com.rafael.med.common.ChannelUtils;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ILBC;
import com.rafael.med.common.bnet.ChannelType;

public class ChannelData
{
	private static final Logger log = LogManager.getLogger();
	
	
	
	public TreeSet<CapturedPacket> datesPackets				= new TreeSet<>();
	public TreeSet<CapturedPacket> hoursPackets				= new TreeSet<>();
	public TreeSet<CapturedPacket> minutesPackets			= new TreeSet<>();
	public TreeSet<CapturedPacket> secondsPackets			= new TreeSet<>();
	
	
	private boolean[] isDateExists 		= new boolean[AppDebrief.TABLE_DAYS];
	private boolean[] isHourExists 		= new boolean[AppDebrief.TABLE_HOURS];
	private boolean[] isMinuteExists 	= new boolean[AppDebrief.TABLE_MINUTES];
	private boolean[] isSecondsExists 	= new boolean[AppDebrief.TABLE_SECONDS];
	
	
	/****** lasy initialization *****/
	private ILBC 	 	ilbc;
	private ByteBuffer 	ilbcIn;
	private ByteBuffer 	ilbcOut;


	public ChannelType 	type;
	public Set<LocalDate>	dateSet = new HashSet<>();
		
	public ChannelData(ChannelType type)
	{
		this.type 	= type;
	}

	
	public boolean addPacket(CapturedPacket packet)
	{
		dateSet.add(packet.localDate);
		return datesPackets.add(packet);
	}
	
//	private boolean[] cutToRange(LocalDateTime fromTime, LocalDateTime toTime, TreeSet<CapturedPacket> sourceSet, TreeSet<CapturedPacket> targetSet, boolean[] isExists, long plusInSeconds)
//	{
//		log.debug("channel : {} to range from ={} , to = {} , plusInSeconds = {}",type.name(),fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss),toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss),plusInSeconds );
//		
//		
//		targetSet.clear();
//		toTime = toTime.plusSeconds(plusInSeconds);
//		for (int i = 0; i < isExists.length; i++)
//		{
//			isExists[i] = false;
//		}
//		
//		for (CapturedPacket packet : sourceSet)
//		{
//			if(packet.localDateTime.isAfter(fromTime)  && packet.localDateTime.isBefore(toTime))
//			{
//				LocalDateTime current = fromTime;
//				int index = 0;
//				while (current.isBefore(toTime))
//				{
//					LocalDateTime next = current.plusSeconds(plusInSeconds);					
//					isExists[index] = isExists[index] || (packet.localDateTime.isAfter(current)  && packet.localDateTime.isBefore(next));
//					if(isExists[index])
//					{
//						targetSet.add(packet);
//					}
//					current = next;
//					index++;
//				}
//			}
//		}
//		return isExists;
//	}
	
	
	public boolean[] setHoursRange(LocalDateTime fromHour, 		LocalDateTime toHour)
	{
		return ChannelUtils.cutToRange(log,fromHour, toHour, datesPackets, hoursPackets, isHourExists, 60 * 60 );
	}
	
	public boolean[] setMinutesRange(LocalDateTime fromMinute, 	LocalDateTime toMinute)
	{
		return ChannelUtils.cutToRange(log,fromMinute, toMinute, hoursPackets, minutesPackets, isMinuteExists, 60 );
	}
	
	public boolean[] setSecondsRange(LocalDateTime fromSecond, 	LocalDateTime toSecond)
	{
		return ChannelUtils.cutToRange(log,fromSecond, toSecond, minutesPackets, secondsPackets, isSecondsExists, 1 );
	}
	
	public ChannelResult createAudioData(LocalDateTime fromTime, int audioByteArrayLength, int audioSamplesArrayLength, int durationPerByteInMicroseconds, int bytesPerSecond)
	{		
		ChannelResult result 	= null;
		
		int rtpPayloadPcmLength 	= 0;
		int jitterInMicroseconds 	= 0;
		
		if(!secondsPackets.isEmpty())
		{
			result 	= new ChannelResult(type);
			result.startTime 		= fromTime;
			
			byte [] audioByteArray 			= new byte[audioByteArrayLength];
			result.audioByteArray			= audioByteArray;
			
			
			LocalDateTime prevPacketTime 	= null;
			int index = 0;
			for (CapturedPacket packet : secondsPackets)
			{
				LocalDateTime currentTime 				= packet.localDateTime;
				
				Duration durationFromStart 				= Duration.between(fromTime, currentTime);
				long durationFromStartInMicroseconds 	= durationFromStart.toNanos() / 1_000;
				if(prevPacketTime == null)
				{
					rtpPayloadPcmLength 	= decodeRtpPayloadLengthAndInit(packet);
					jitterInMicroseconds 	= (rtpPayloadPcmLength * 1_000_000 / bytesPerSecond) * 3;
					index = (int) (durationFromStartInMicroseconds / durationPerByteInMicroseconds);
					
					if(index % 2 != 0)
					{
						index = index + 1;
					}
					
					result.startIndex  	= index;
					result.startTime 	= currentTime;
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
						index = (int) (durationFromStartInMicroseconds / durationPerByteInMicroseconds);
					}
					
					if(index % 2 != 0)
					{
						index = index + 1;
					}
				}
				prevPacketTime = currentTime;
				
				if(packet.rtpPayload == null) // firstTime to this packet
				{
					//packet.rtpPayload = decodeRtpPayload(packet);
					
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
			result.endTime 	= prevPacketTime.plusSeconds(rtpPayloadPcmLength / bytesPerSecond);
			result.endIndex = index + rtpPayloadPcmLength;
			
		}
		return result;
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
			System.out.println("--------------------------- " + i);
			return i;
		}
		throw new IllegalStateException("PACKET = " + packet + " HAS NOT CORRECT RTP PAYLOAD TYPE");
	}
	
	
	
//	private byte[] decodeRtpPayload(CapturedPacket packet)
//	{
//		byte[] result = null;
//		if(packet.rtpPayloadType == AppBase.PCM_PAYLOAD_TYPE && packet.rtpPayloadlen == AppBase.PCM_PAYLOAD_LEN)
//		{
//			result = new byte[AppBase.PCM_PAYLOAD_LEN];
//			packet.rtpBuffer.get(result);
//		}
//		else if(packet.rtpPayloadType == AppBase.ILBC_20_PAYLOD_TYPE || packet.rtpPayloadType == AppBase.ILBC_30_PAYLOD_TYPE)
//		{	
//			int outSizeInBytes = ilbc.mode.size * 2;
//			int size = packet.rtpPayloadlen / ilbc.mode.bytes;
//			result = new byte[outSizeInBytes * size];
//			for (int i = 0; i < size; i++)
//			{
//				ilbcIn.clear();
//				ilbcOut.clear();
//				
//				for (int j = 0; j < ilbc.mode.bytes; j++)
//				{
//					ilbcIn.put(packet.rtpBuffer.get());
//				}
//				
//				ilbcIn.flip();
//				ilbc.decode(ilbcIn, ilbcOut);
//				ilbcOut.flip();
//				ilbcOut.get(result, i * outSizeInBytes, outSizeInBytes);
//			}
//		}
//		return result;
//	}

//	private void copyPacketToStream(byte[] audioByteArray, byte[] rtpPCMPayload, int index)
//	{
//		int copyLength = rtpPCMPayload.length;
//		int remaining = audioByteArray.length - index;
//		
//		if(remaining > 0)
//		{
//		if(remaining < copyLength)
//		{
//			copyLength = remaining;
//		}
//		try
//		{
//			System.arraycopy(rtpPCMPayload, 0, audioByteArray, index, copyLength);
//		}
//		catch (Exception e)
//		{
//			System.out.println("---------------------- INDEX = " + index + " REMAING = " + remaining);
//		}
//		}
//	}
	
	
	private void initILBC(ILBC.Mode mode)
	{	
		this.ilbc 		= new ILBC(mode);
		this.ilbcIn 	= ByteBuffer.allocate(ilbc.mode.bytes).order(ByteOrder.LITTLE_ENDIAN);
		this.ilbcOut 	= ByteBuffer.allocate(ilbc.mode.size * 2).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	
	public Boolean getDay0() {return isDateExists[0];}
	public Boolean getDay1() {return isDateExists[1];}
	public Boolean getDay2() {return isDateExists[2];}
	public Boolean getDay3() {return isDateExists[3];}
	public Boolean getDay4() {return isDateExists[4];}
	public Boolean getDay5() {return isDateExists[5];}
	public Boolean getDay6() {return isDateExists[6];}
	public Boolean getDay7() {return isDateExists[7];}
	public Boolean getDay8() {return isDateExists[8];}
	public Boolean getDay9() {return isDateExists[9];}
	public Boolean getDay10() {return isDateExists[10];}
	public Boolean getDay11() {return isDateExists[11];}
	public Boolean getDay12() {return isDateExists[12];}
	public Boolean getDay13() {return isDateExists[13];}
	public Boolean getDay14() {return isDateExists[14];}
	public Boolean getDay15() {return isDateExists[15];}
	public Boolean getDay16() {return isDateExists[16];}
	public Boolean getDay17() {return isDateExists[17];}
	public Boolean getDay18() {return isDateExists[18];}
	public Boolean getDay19() {return isDateExists[19];}
	public Boolean getDay20() {return isDateExists[20];}
	public Boolean getDay21() {return isDateExists[21];}
	public Boolean getDay22() {return isDateExists[22];}
	public Boolean getDay23() {return isDateExists[23];}
	public Boolean getDay24() {return isDateExists[24];}
	public Boolean getDay25() {return isDateExists[25];}
	public Boolean getDay26() {return isDateExists[26];}
	public Boolean getDay27() {return isDateExists[27];}
	public Boolean getDay28() {return isDateExists[28];}
	public Boolean getDay29() {return isDateExists[29];}
	public Boolean getDay30() {return isDateExists[30];}
	public Boolean getDay31() {return isDateExists[31];}
	public Boolean getDay32() {return isDateExists[32];}
	public Boolean getDay33() {return isDateExists[33];}
	public Boolean getDay34() {return isDateExists[34];}
	public Boolean getDay35() {return isDateExists[35];}
	public Boolean getDay36() {return isDateExists[36];}
	public Boolean getDay37() {return isDateExists[37];}
	public Boolean getDay38() {return isDateExists[38];}
	public Boolean getDay39() {return isDateExists[39];}
	public Boolean getDay40() {return isDateExists[40];}
	public Boolean getDay41() {return isDateExists[41];}
	public Boolean getDay42() {return isDateExists[42];}
	public Boolean getDay43() {return isDateExists[43];}
	public Boolean getDay44() {return isDateExists[44];}
	public Boolean getDay45() {return isDateExists[45];}
	public Boolean getDay46() {return isDateExists[46];}
	public Boolean getDay47() {return isDateExists[47];}
	public Boolean getDay48() {return isDateExists[48];}
	public Boolean getDay49() {return isDateExists[49];}
	public Boolean getDay50() {return isDateExists[50];}
	public Boolean getDay51() {return isDateExists[51];}
	public Boolean getDay52() {return isDateExists[52];}
	public Boolean getDay53() {return isDateExists[53];}
	public Boolean getDay54() {return isDateExists[54];}
	public Boolean getDay55() {return isDateExists[55];}
	public Boolean getDay56() {return isDateExists[56];}
	public Boolean getDay57() {return isDateExists[57];}
	public Boolean getDay58() {return isDateExists[58];}
	public Boolean getDay59() {return isDateExists[59];}
	public Boolean getDay60() {return isDateExists[60];}
	public Boolean getDay61() {return isDateExists[61];}
	public Boolean getDay62() {return isDateExists[62];}
	public Boolean getDay63() {return isDateExists[63];}
	public Boolean getDay64() {return isDateExists[64];}
	public Boolean getDay65() {return isDateExists[65];}
	public Boolean getDay66() {return isDateExists[66];}
	public Boolean getDay67() {return isDateExists[67];}
	public Boolean getDay68() {return isDateExists[68];}
	public Boolean getDay69() {return isDateExists[69];}
	public Boolean getDay70() {return isDateExists[70];}
	public Boolean getDay71() {return isDateExists[71];}
	public Boolean getDay72() {return isDateExists[72];}
	public Boolean getDay73() {return isDateExists[73];}
	public Boolean getDay74() {return isDateExists[74];}
	public Boolean getDay75() {return isDateExists[75];}
	public Boolean getDay76() {return isDateExists[76];}
	public Boolean getDay77() {return isDateExists[77];}
	public Boolean getDay78() {return isDateExists[78];}
	public Boolean getDay79() {return isDateExists[79];}
	public Boolean getDay80() {return isDateExists[80];}
	public Boolean getDay81() {return isDateExists[81];}
	public Boolean getDay82() {return isDateExists[82];}
	public Boolean getDay83() {return isDateExists[83];}
	public Boolean getDay84() {return isDateExists[84];}
	public Boolean getDay85() {return isDateExists[85];}
	public Boolean getDay86() {return isDateExists[86];}
	public Boolean getDay87() {return isDateExists[87];}
	public Boolean getDay88() {return isDateExists[88];}
	public Boolean getDay89() {return isDateExists[89];}
	public Boolean getDay90() {return isDateExists[90];}
	public Boolean getDay91() {return isDateExists[91];}
	public Boolean getDay92() {return isDateExists[92];}
	public Boolean getDay93() {return isDateExists[93];}
	public Boolean getDay94() {return isDateExists[94];}
	public Boolean getDay95() {return isDateExists[95];}
	public Boolean getDay96() {return isDateExists[96];}
	public Boolean getDay97() {return isDateExists[97];}
	public Boolean getDay98() {return isDateExists[98];}
	public Boolean getDay99() {return isDateExists[99];}
	
	public Boolean getHour0() 	{return isHourExists[0];}
	public Boolean getHour1() 	{return isHourExists[1];}
	public Boolean getHour2() 	{return isHourExists[2];}
	public Boolean getHour3() 	{return isHourExists[3];}
	public Boolean getHour4() 	{return isHourExists[4];}
	public Boolean getHour5() 	{return isHourExists[5];}
	public Boolean getHour6() 	{return isHourExists[6];}
	public Boolean getHour7() 	{return isHourExists[7];}
	public Boolean getHour8() 	{return isHourExists[8];}
	public Boolean getHour9() 	{return isHourExists[9];}
	public Boolean getHour10() {return isHourExists[10];}
	public Boolean getHour11() {return isHourExists[11];}
	public Boolean getHour12() {return isHourExists[12];}
	public Boolean getHour13() {return isHourExists[13];}
	public Boolean getHour14() {return isHourExists[14];}
	public Boolean getHour15() {return isHourExists[15];}
	public Boolean getHour16() {return isHourExists[16];}
	public Boolean getHour17() {return isHourExists[17];}
	public Boolean getHour18() {return isHourExists[18];}
	public Boolean getHour19() {return isHourExists[19];}
	public Boolean getHour20() {return isHourExists[20];}
	public Boolean getHour21() {return isHourExists[21];}
	public Boolean getHour22() {return isHourExists[22];}
	public Boolean getHour23() {return isHourExists[23];}
	public Boolean getHour24() {return isHourExists[24];}
	public Boolean getHour25() {return isHourExists[25];}
	
	public Boolean getMinute0() {return isMinuteExists[0];}
	public Boolean getMinute1() {return isMinuteExists[1];}
	public Boolean getMinute2() {return isMinuteExists[2];}
	public Boolean getMinute3() {return isMinuteExists[3];}
	public Boolean getMinute4() {return isMinuteExists[4];}
	public Boolean getMinute5() {return isMinuteExists[5];}
	public Boolean getMinute6() {return isMinuteExists[6];}
	public Boolean getMinute7() {return isMinuteExists[7];}
	public Boolean getMinute8() {return isMinuteExists[8];}
	public Boolean getMinute9() {return isMinuteExists[9];}
	public Boolean getMinute10() {return isMinuteExists[10];}
	public Boolean getMinute11() {return isMinuteExists[11];}
	public Boolean getMinute12() {return isMinuteExists[12];}
	public Boolean getMinute13() {return isMinuteExists[13];}
	public Boolean getMinute14() {return isMinuteExists[14];}
	public Boolean getMinute15() {return isMinuteExists[15];}
	public Boolean getMinute16() {return isMinuteExists[16];}
	public Boolean getMinute17() {return isMinuteExists[17];}
	public Boolean getMinute18() {return isMinuteExists[18];}
	public Boolean getMinute19() {return isMinuteExists[19];}
	public Boolean getMinute20() {return isMinuteExists[20];}
	public Boolean getMinute21() {return isMinuteExists[21];}
	public Boolean getMinute22() {return isMinuteExists[22];}
	public Boolean getMinute23() {return isMinuteExists[23];}
	public Boolean getMinute24() {return isMinuteExists[24];}
	public Boolean getMinute25() {return isMinuteExists[25];}
	public Boolean getMinute26() {return isMinuteExists[26];}
	public Boolean getMinute27() {return isMinuteExists[27];}
	public Boolean getMinute28() {return isMinuteExists[28];}
	public Boolean getMinute29() {return isMinuteExists[29];}
	public Boolean getMinute30() {return isMinuteExists[30];}
	public Boolean getMinute31() {return isMinuteExists[31];}
	public Boolean getMinute32() {return isMinuteExists[32];}
	public Boolean getMinute33() {return isMinuteExists[33];}
	public Boolean getMinute34() {return isMinuteExists[34];}
	public Boolean getMinute35() {return isMinuteExists[35];}
	public Boolean getMinute36() {return isMinuteExists[36];}
	public Boolean getMinute37() {return isMinuteExists[37];}
	public Boolean getMinute38() {return isMinuteExists[38];}
	public Boolean getMinute39() {return isMinuteExists[39];}
	public Boolean getMinute40() {return isMinuteExists[40];}
	public Boolean getMinute41() {return isMinuteExists[41];}
	public Boolean getMinute42() {return isMinuteExists[42];}
	public Boolean getMinute43() {return isMinuteExists[43];}
	public Boolean getMinute44() {return isMinuteExists[44];}
	public Boolean getMinute45() {return isMinuteExists[45];}
	public Boolean getMinute46() {return isMinuteExists[46];}
	public Boolean getMinute47() {return isMinuteExists[47];}
	public Boolean getMinute48() {return isMinuteExists[48];}
	public Boolean getMinute49() {return isMinuteExists[49];}
	public Boolean getMinute50() {return isMinuteExists[50];}
	public Boolean getMinute51() {return isMinuteExists[51];}
	public Boolean getMinute52() {return isMinuteExists[52];}
	public Boolean getMinute53() {return isMinuteExists[53];}
	public Boolean getMinute54() {return isMinuteExists[54];}
	public Boolean getMinute55() {return isMinuteExists[55];}
	public Boolean getMinute56() {return isMinuteExists[56];}
	public Boolean getMinute57() {return isMinuteExists[57];}
	public Boolean getMinute58() {return isMinuteExists[58];}
	public Boolean getMinute59() {return isMinuteExists[59];}
	public Boolean getMinute60() {return isMinuteExists[60];}
	public Boolean getMinute61() {return isMinuteExists[61];}
	public Boolean getMinute62() {return isMinuteExists[62];}
	public Boolean getMinute63() {return isMinuteExists[63];}
	public Boolean getMinute64() {return isMinuteExists[64];}
	public Boolean getMinute65() {return isMinuteExists[65];}
	public Boolean getMinute66() {return isMinuteExists[66];}
	public Boolean getMinute67() {return isMinuteExists[67];}
	public Boolean getMinute68() {return isMinuteExists[68];}
	public Boolean getMinute69() {return isMinuteExists[69];}
	
	public Boolean getSecond0() {return isSecondsExists[0];}
	public Boolean getSecond1() {return isSecondsExists[1];}
	public Boolean getSecond2() {return isSecondsExists[2];}
	public Boolean getSecond3() {return isSecondsExists[3];}
	public Boolean getSecond4() {return isSecondsExists[4];}
	public Boolean getSecond5() {return isSecondsExists[5];}
	public Boolean getSecond6() {return isSecondsExists[6];}
	public Boolean getSecond7() {return isSecondsExists[7];}
	public Boolean getSecond8() {return isSecondsExists[8];}
	public Boolean getSecond9() {return isSecondsExists[9];}
	public Boolean getSecond10() {return isSecondsExists[10];}
	public Boolean getSecond11() {return isSecondsExists[11];}
	public Boolean getSecond12() {return isSecondsExists[12];}
	public Boolean getSecond13() {return isSecondsExists[13];}
	public Boolean getSecond14() {return isSecondsExists[14];}
	public Boolean getSecond15() {return isSecondsExists[15];}
	public Boolean getSecond16() {return isSecondsExists[16];}
	public Boolean getSecond17() {return isSecondsExists[17];}
	public Boolean getSecond18() {return isSecondsExists[18];}
	public Boolean getSecond19() {return isSecondsExists[19];}
	public Boolean getSecond20() {return isSecondsExists[20];}
	public Boolean getSecond21() {return isSecondsExists[21];}
	public Boolean getSecond22() {return isSecondsExists[22];}
	public Boolean getSecond23() {return isSecondsExists[23];}
	public Boolean getSecond24() {return isSecondsExists[24];}
	public Boolean getSecond25() {return isSecondsExists[25];}
	public Boolean getSecond26() {return isSecondsExists[26];}
	public Boolean getSecond27() {return isSecondsExists[27];}
	public Boolean getSecond28() {return isSecondsExists[28];}
	public Boolean getSecond29() {return isSecondsExists[29];}
	public Boolean getSecond30() {return isSecondsExists[30];}
	public Boolean getSecond31() {return isSecondsExists[31];}
	public Boolean getSecond32() {return isSecondsExists[32];}
	public Boolean getSecond33() {return isSecondsExists[33];}
	public Boolean getSecond34() {return isSecondsExists[34];}
	public Boolean getSecond35() {return isSecondsExists[35];}
	public Boolean getSecond36() {return isSecondsExists[36];}
	public Boolean getSecond37() {return isSecondsExists[37];}
	public Boolean getSecond38() {return isSecondsExists[38];}
	public Boolean getSecond39() {return isSecondsExists[39];}
	public Boolean getSecond40() {return isSecondsExists[40];}
	public Boolean getSecond41() {return isSecondsExists[41];}
	public Boolean getSecond42() {return isSecondsExists[42];}
	public Boolean getSecond43() {return isSecondsExists[43];}
	public Boolean getSecond44() {return isSecondsExists[44];}
	public Boolean getSecond45() {return isSecondsExists[45];}
	public Boolean getSecond46() {return isSecondsExists[46];}
	public Boolean getSecond47() {return isSecondsExists[47];}
	public Boolean getSecond48() {return isSecondsExists[48];}
	public Boolean getSecond49() {return isSecondsExists[49];}
	public Boolean getSecond50() {return isSecondsExists[50];}
	public Boolean getSecond51() {return isSecondsExists[51];}
	public Boolean getSecond52() {return isSecondsExists[52];}
	public Boolean getSecond53() {return isSecondsExists[53];}
	public Boolean getSecond54() {return isSecondsExists[54];}
	public Boolean getSecond55() {return isSecondsExists[55];}
	public Boolean getSecond56() {return isSecondsExists[56];}
	public Boolean getSecond57() {return isSecondsExists[57];}
	public Boolean getSecond58() {return isSecondsExists[58];}
	public Boolean getSecond59() {return isSecondsExists[59];}
	public Boolean getSecond60() {return isSecondsExists[60];}
	public Boolean getSecond61() {return isSecondsExists[61];}
	public Boolean getSecond62() {return isSecondsExists[62];}
	public Boolean getSecond63() {return isSecondsExists[63];}
	public Boolean getSecond64() {return isSecondsExists[64];}
	public Boolean getSecond65() {return isSecondsExists[65];}
	public Boolean getSecond66() {return isSecondsExists[66];}
	public Boolean getSecond67() {return isSecondsExists[67];}
	public Boolean getSecond68() {return isSecondsExists[68];}
	public Boolean getSecond69() {return isSecondsExists[69];}
	public Boolean getSecond70() {return isSecondsExists[70];}
	public Boolean getSecond71() {return isSecondsExists[71];}
	public Boolean getSecond72() {return isSecondsExists[72];}
	public Boolean getSecond73() {return isSecondsExists[73];}
	public Boolean getSecond74() {return isSecondsExists[74];}
	public Boolean getSecond75() {return isSecondsExists[75];}
	public Boolean getSecond76() {return isSecondsExists[76];}
	public Boolean getSecond77() {return isSecondsExists[77];}
	public Boolean getSecond78() {return isSecondsExists[78];}
	public Boolean getSecond79() {return isSecondsExists[79];}
	public Boolean getSecond80() {return isSecondsExists[80];}
	public Boolean getSecond81() {return isSecondsExists[81];}
	public Boolean getSecond82() {return isSecondsExists[82];}
	public Boolean getSecond83() {return isSecondsExists[83];}
	public Boolean getSecond84() {return isSecondsExists[84];}
	public Boolean getSecond85() {return isSecondsExists[85];}
	public Boolean getSecond86() {return isSecondsExists[86];}
	public Boolean getSecond87() {return isSecondsExists[87];}
	public Boolean getSecond88() {return isSecondsExists[88];}
	public Boolean getSecond89() {return isSecondsExists[89];}
	public Boolean getSecond90() {return isSecondsExists[90];}
	public Boolean getSecond91() {return isSecondsExists[91];}
	public Boolean getSecond92() {return isSecondsExists[92];}
	public Boolean getSecond93() {return isSecondsExists[93];}
	public Boolean getSecond94() {return isSecondsExists[94];}
	public Boolean getSecond95() {return isSecondsExists[95];}
	public Boolean getSecond96() {return isSecondsExists[96];}
	public Boolean getSecond97() {return isSecondsExists[97];}
	public Boolean getSecond98() {return isSecondsExists[98];}
	public Boolean getSecond99() {return isSecondsExists[99];}
	public Boolean getSecond100() {return isSecondsExists[100];}
	public Boolean getSecond101() {return isSecondsExists[101];}
	public Boolean getSecond102() {return isSecondsExists[102];}
	public Boolean getSecond103() {return isSecondsExists[103];}
	public Boolean getSecond104() {return isSecondsExists[104];}
	public Boolean getSecond105() {return isSecondsExists[105];}
	public Boolean getSecond106() {return isSecondsExists[106];}
	public Boolean getSecond107() {return isSecondsExists[107];}
	public Boolean getSecond108() {return isSecondsExists[108];}
	public Boolean getSecond109() {return isSecondsExists[109];}
	public Boolean getSecond110() {return isSecondsExists[110];}
	public Boolean getSecond111() {return isSecondsExists[111];}
	public Boolean getSecond112() {return isSecondsExists[112];}
	public Boolean getSecond113() {return isSecondsExists[113];}
	public Boolean getSecond114() {return isSecondsExists[114];}
	public Boolean getSecond115() {return isSecondsExists[115];}
	public Boolean getSecond116() {return isSecondsExists[116];}
	public Boolean getSecond117() {return isSecondsExists[117];}
	public Boolean getSecond118() {return isSecondsExists[118];}
	public Boolean getSecond119() {return isSecondsExists[119];}
	public Boolean getSecond120() {return isSecondsExists[120];}
	public Boolean getSecond121() {return isSecondsExists[121];}
	public Boolean getSecond122() {return isSecondsExists[122];}
	public Boolean getSecond123() {return isSecondsExists[123];}
	public Boolean getSecond124() {return isSecondsExists[124];}
	public Boolean getSecond125() {return isSecondsExists[125];}
	public Boolean getSecond126() {return isSecondsExists[126];}
	public Boolean getSecond127() {return isSecondsExists[127];}
	public Boolean getSecond128() {return isSecondsExists[128];}
	public Boolean getSecond129() {return isSecondsExists[129];}
	public Boolean getSecond130() {return isSecondsExists[130];}
	public Boolean getSecond131() {return isSecondsExists[131];}
	public Boolean getSecond132() {return isSecondsExists[132];}
	public Boolean getSecond133() {return isSecondsExists[133];}
	public Boolean getSecond134() {return isSecondsExists[134];}
	public Boolean getSecond135() {return isSecondsExists[135];}
	public Boolean getSecond136() {return isSecondsExists[136];}
	public Boolean getSecond137() {return isSecondsExists[137];}
	public Boolean getSecond138() {return isSecondsExists[138];}
	public Boolean getSecond139() {return isSecondsExists[139];}
	public Boolean getSecond140() {return isSecondsExists[140];}
	public Boolean getSecond141() {return isSecondsExists[141];}
	public Boolean getSecond142() {return isSecondsExists[142];}
	public Boolean getSecond143() {return isSecondsExists[143];}
	public Boolean getSecond144() {return isSecondsExists[144];}
	public Boolean getSecond145() {return isSecondsExists[145];}
	public Boolean getSecond146() {return isSecondsExists[146];}
	public Boolean getSecond147() {return isSecondsExists[147];}
	public Boolean getSecond148() {return isSecondsExists[148];}
	public Boolean getSecond149() {return isSecondsExists[149];}
	public Boolean getSecond150() {return isSecondsExists[150];}
	public Boolean getSecond151() {return isSecondsExists[151];}
	public Boolean getSecond152() {return isSecondsExists[152];}
	public Boolean getSecond153() {return isSecondsExists[153];}
	public Boolean getSecond154() {return isSecondsExists[154];}
	public Boolean getSecond155() {return isSecondsExists[155];}
	public Boolean getSecond156() {return isSecondsExists[156];}
	public Boolean getSecond157() {return isSecondsExists[157];}
	public Boolean getSecond158() {return isSecondsExists[158];}
	public Boolean getSecond159() {return isSecondsExists[159];}
	public Boolean getSecond160() {return isSecondsExists[160];}
	public Boolean getSecond161() {return isSecondsExists[161];}
	public Boolean getSecond162() {return isSecondsExists[162];}
	public Boolean getSecond163() {return isSecondsExists[163];}
	public Boolean getSecond164() {return isSecondsExists[164];}
	public Boolean getSecond165() {return isSecondsExists[165];}
	public Boolean getSecond166() {return isSecondsExists[166];}
	public Boolean getSecond167() {return isSecondsExists[167];}
	public Boolean getSecond168() {return isSecondsExists[168];}
	public Boolean getSecond169() {return isSecondsExists[169];}
	public Boolean getSecond170() {return isSecondsExists[170];}
	public Boolean getSecond171() {return isSecondsExists[171];}
	public Boolean getSecond172() {return isSecondsExists[172];}
	public Boolean getSecond173() {return isSecondsExists[173];}
	public Boolean getSecond174() {return isSecondsExists[174];}
	public Boolean getSecond175() {return isSecondsExists[175];}
	public Boolean getSecond176() {return isSecondsExists[176];}
	public Boolean getSecond177() {return isSecondsExists[177];}
	public Boolean getSecond178() {return isSecondsExists[178];}
	public Boolean getSecond179() {return isSecondsExists[179];}
	public Boolean getSecond180() {return isSecondsExists[180];}
	public Boolean getSecond181() {return isSecondsExists[181];}
	public Boolean getSecond182() {return isSecondsExists[182];}
	public Boolean getSecond183() {return isSecondsExists[183];}
	public Boolean getSecond184() {return isSecondsExists[184];}
	public Boolean getSecond185() {return isSecondsExists[185];}
	public Boolean getSecond186() {return isSecondsExists[186];}
	public Boolean getSecond187() {return isSecondsExists[187];}
	public Boolean getSecond188() {return isSecondsExists[188];}
	public Boolean getSecond189() {return isSecondsExists[189];}
	public Boolean getSecond190() {return isSecondsExists[190];}
	public Boolean getSecond191() {return isSecondsExists[191];}
	public Boolean getSecond192() {return isSecondsExists[192];}
	public Boolean getSecond193() {return isSecondsExists[193];}
	public Boolean getSecond194() {return isSecondsExists[194];}
	public Boolean getSecond195() {return isSecondsExists[195];}
	public Boolean getSecond196() {return isSecondsExists[196];}
	public Boolean getSecond197() {return isSecondsExists[197];}
	public Boolean getSecond198() {return isSecondsExists[198];}
	public Boolean getSecond199() {return isSecondsExists[199];}
	public Boolean getSecond200() {return isSecondsExists[200];}
	public Boolean getSecond201() {return isSecondsExists[201];}
	public Boolean getSecond202() {return isSecondsExists[202];}
	public Boolean getSecond203() {return isSecondsExists[203];}
	public Boolean getSecond204() {return isSecondsExists[204];}
	public Boolean getSecond205() {return isSecondsExists[205];}
	public Boolean getSecond206() {return isSecondsExists[206];}
	public Boolean getSecond207() {return isSecondsExists[207];}
	public Boolean getSecond208() {return isSecondsExists[208];}
	public Boolean getSecond209() {return isSecondsExists[209];}
	public Boolean getSecond210() {return isSecondsExists[210];}
	public Boolean getSecond211() {return isSecondsExists[211];}
	public Boolean getSecond212() {return isSecondsExists[212];}
	public Boolean getSecond213() {return isSecondsExists[213];}
	public Boolean getSecond214() {return isSecondsExists[214];}
	public Boolean getSecond215() {return isSecondsExists[215];}
	public Boolean getSecond216() {return isSecondsExists[216];}
	public Boolean getSecond217() {return isSecondsExists[217];}
	public Boolean getSecond218() {return isSecondsExists[218];}
	public Boolean getSecond219() {return isSecondsExists[219];}
	public Boolean getSecond220() {return isSecondsExists[220];}
	public Boolean getSecond221() {return isSecondsExists[221];}
	public Boolean getSecond222() {return isSecondsExists[222];}
	public Boolean getSecond223() {return isSecondsExists[223];}
	public Boolean getSecond224() {return isSecondsExists[224];}
	public Boolean getSecond225() {return isSecondsExists[225];}
	public Boolean getSecond226() {return isSecondsExists[226];}
	public Boolean getSecond227() {return isSecondsExists[227];}
	public Boolean getSecond228() {return isSecondsExists[228];}
	public Boolean getSecond229() {return isSecondsExists[229];}
	public Boolean getSecond230() {return isSecondsExists[230];}
	public Boolean getSecond231() {return isSecondsExists[231];}
	public Boolean getSecond232() {return isSecondsExists[232];}
	public Boolean getSecond233() {return isSecondsExists[233];}
	public Boolean getSecond234() {return isSecondsExists[234];}
	public Boolean getSecond235() {return isSecondsExists[235];}
	public Boolean getSecond236() {return isSecondsExists[236];}
	public Boolean getSecond237() {return isSecondsExists[237];}
	public Boolean getSecond238() {return isSecondsExists[238];}
	public Boolean getSecond239() {return isSecondsExists[239];}
	public Boolean getSecond240() {return isSecondsExists[240];}
	public Boolean getSecond241() {return isSecondsExists[241];}
	public Boolean getSecond242() {return isSecondsExists[242];}
	public Boolean getSecond243() {return isSecondsExists[243];}
	public Boolean getSecond244() {return isSecondsExists[244];}
	public Boolean getSecond245() {return isSecondsExists[245];}
	public Boolean getSecond246() {return isSecondsExists[246];}
	public Boolean getSecond247() {return isSecondsExists[247];}
	public Boolean getSecond248() {return isSecondsExists[248];}
	public Boolean getSecond249() {return isSecondsExists[249];}
	public Boolean getSecond250() {return isSecondsExists[250];}
	public Boolean getSecond251() {return isSecondsExists[251];}
	public Boolean getSecond252() {return isSecondsExists[252];}
	public Boolean getSecond253() {return isSecondsExists[253];}
	public Boolean getSecond254() {return isSecondsExists[254];}
	public Boolean getSecond255() {return isSecondsExists[255];}
	public Boolean getSecond256() {return isSecondsExists[256];}
	public Boolean getSecond257() {return isSecondsExists[257];}
	public Boolean getSecond258() {return isSecondsExists[258];}
	public Boolean getSecond259() {return isSecondsExists[259];}
	public Boolean getSecond260() {return isSecondsExists[260];}
	public Boolean getSecond261() {return isSecondsExists[261];}
	public Boolean getSecond262() {return isSecondsExists[262];}
	public Boolean getSecond263() {return isSecondsExists[263];}
	public Boolean getSecond264() {return isSecondsExists[264];}
	public Boolean getSecond265() {return isSecondsExists[265];}
	public Boolean getSecond266() {return isSecondsExists[266];}
	public Boolean getSecond267() {return isSecondsExists[267];}
	public Boolean getSecond268() {return isSecondsExists[268];}
	public Boolean getSecond269() {return isSecondsExists[269];}
	public Boolean getSecond270() {return isSecondsExists[270];}
	public Boolean getSecond271() {return isSecondsExists[271];}
	public Boolean getSecond272() {return isSecondsExists[272];}
	public Boolean getSecond273() {return isSecondsExists[273];}
	public Boolean getSecond274() {return isSecondsExists[274];}
	public Boolean getSecond275() {return isSecondsExists[275];}
	public Boolean getSecond276() {return isSecondsExists[276];}
	public Boolean getSecond277() {return isSecondsExists[277];}
	public Boolean getSecond278() {return isSecondsExists[278];}
	public Boolean getSecond279() {return isSecondsExists[279];}
	public Boolean getSecond280() {return isSecondsExists[280];}
	public Boolean getSecond281() {return isSecondsExists[281];}
	public Boolean getSecond282() {return isSecondsExists[282];}
	public Boolean getSecond283() {return isSecondsExists[283];}
	public Boolean getSecond284() {return isSecondsExists[284];}
	public Boolean getSecond285() {return isSecondsExists[285];}
	public Boolean getSecond286() {return isSecondsExists[286];}
	public Boolean getSecond287() {return isSecondsExists[287];}
	public Boolean getSecond288() {return isSecondsExists[288];}
	public Boolean getSecond289() {return isSecondsExists[289];}
	public Boolean getSecond290() {return isSecondsExists[290];}
	public Boolean getSecond291() {return isSecondsExists[291];}
	public Boolean getSecond292() {return isSecondsExists[292];}
	public Boolean getSecond293() {return isSecondsExists[293];}
	public Boolean getSecond294() {return isSecondsExists[294];}
	public Boolean getSecond295() {return isSecondsExists[295];}
	public Boolean getSecond296() {return isSecondsExists[296];}
	public Boolean getSecond297() {return isSecondsExists[297];}
	public Boolean getSecond298() {return isSecondsExists[298];}
	public Boolean getSecond299() {return isSecondsExists[299];}
	public Boolean getSecond300() {return isSecondsExists[300];}
	public Boolean getSecond301() {return isSecondsExists[301];}
	public Boolean getSecond302() {return isSecondsExists[302];}
	public Boolean getSecond303() {return isSecondsExists[303];}
	public Boolean getSecond304() {return isSecondsExists[304];}
	public Boolean getSecond305() {return isSecondsExists[305];}
	public Boolean getSecond306() {return isSecondsExists[306];}
	public Boolean getSecond307() {return isSecondsExists[307];}
	public Boolean getSecond308() {return isSecondsExists[308];}
	public Boolean getSecond309() {return isSecondsExists[309];}
	public Boolean getSecond310() {return isSecondsExists[310];}
	public Boolean getSecond311() {return isSecondsExists[311];}
	public Boolean getSecond312() {return isSecondsExists[312];}
	public Boolean getSecond313() {return isSecondsExists[313];}
	public Boolean getSecond314() {return isSecondsExists[314];}
	public Boolean getSecond315() {return isSecondsExists[315];}
	public Boolean getSecond316() {return isSecondsExists[316];}
	public Boolean getSecond317() {return isSecondsExists[317];}
	public Boolean getSecond318() {return isSecondsExists[318];}
	public Boolean getSecond319() {return isSecondsExists[319];}
	public Boolean getSecond320() {return isSecondsExists[320];}
	public Boolean getSecond321() {return isSecondsExists[321];}
	public Boolean getSecond322() {return isSecondsExists[322];}
	public Boolean getSecond323() {return isSecondsExists[323];}
	public Boolean getSecond324() {return isSecondsExists[324];}
	public Boolean getSecond325() {return isSecondsExists[325];}
	public Boolean getSecond326() {return isSecondsExists[326];}
	public Boolean getSecond327() {return isSecondsExists[327];}
	public Boolean getSecond328() {return isSecondsExists[328];}
	public Boolean getSecond329() {return isSecondsExists[329];}
	public Boolean getSecond330() {return isSecondsExists[330];}
	public Boolean getSecond331() {return isSecondsExists[331];}
	public Boolean getSecond332() {return isSecondsExists[332];}
	public Boolean getSecond333() {return isSecondsExists[333];}
	public Boolean getSecond334() {return isSecondsExists[334];}
	public Boolean getSecond335() {return isSecondsExists[335];}
	public Boolean getSecond336() {return isSecondsExists[336];}
	public Boolean getSecond337() {return isSecondsExists[337];}
	public Boolean getSecond338() {return isSecondsExists[338];}
	public Boolean getSecond339() {return isSecondsExists[339];}
	public Boolean getSecond340() {return isSecondsExists[340];}
	public Boolean getSecond341() {return isSecondsExists[341];}
	public Boolean getSecond342() {return isSecondsExists[342];}
	public Boolean getSecond343() {return isSecondsExists[343];}
	public Boolean getSecond344() {return isSecondsExists[344];}
	public Boolean getSecond345() {return isSecondsExists[345];}
	public Boolean getSecond346() {return isSecondsExists[346];}
	public Boolean getSecond347() {return isSecondsExists[347];}
	public Boolean getSecond348() {return isSecondsExists[348];}
	public Boolean getSecond349() {return isSecondsExists[349];}
	public Boolean getSecond350() {return isSecondsExists[350];}
	public Boolean getSecond351() {return isSecondsExists[351];}
	public Boolean getSecond352() {return isSecondsExists[352];}
	public Boolean getSecond353() {return isSecondsExists[353];}
	public Boolean getSecond354() {return isSecondsExists[354];}
	public Boolean getSecond355() {return isSecondsExists[355];}
	public Boolean getSecond356() {return isSecondsExists[356];}
	public Boolean getSecond357() {return isSecondsExists[357];}
	public Boolean getSecond358() {return isSecondsExists[358];}
	public Boolean getSecond359() {return isSecondsExists[359];}

	@Override
	public String toString()
	{
		return String.format("ChannelData [%s]", type.toString());
	}
}
