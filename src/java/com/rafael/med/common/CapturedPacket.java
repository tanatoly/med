package com.rafael.med.common;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.rafael.med.common.bnet.VoicePacket;

public final class CapturedPacket implements Comparable<CapturedPacket>, VoicePacket
{		
	public final Instant  		timestamp;
	public final LocalDateTime 	localDateTime;
	public final LocalDate 		localDate;

	public final String 		srcIp;
	public final String 		dstIp;
	public final int 			srcPort;
	public final int 			dstPort;

	public final ByteBuffer 	rtpBuffer;
	public final int 			rtpPayloadType;
	public final int 			rtpPayloadlen;
	public final int 			rtpHeaderlen;
	public final long 			rtpSsrc;
	public final int 			rtpSeqNumber;
	public final long 			rtpSampleTime;
	
	public byte [] 				rtpPayload;

	

	public CapturedPacket(long ts_sec, long ts_usec, long incl_len, long orig_len, byte[] srcIp, byte[] dstIp, int srcPort, int dstPort, byte[] rtpData) throws IOException
	{
		this.srcIp				= InetAddress.getByAddress(srcIp).getHostAddress();
		this.dstIp				= InetAddress.getByAddress(dstIp).getHostAddress();
		this.srcPort			= srcPort;
		this.dstPort			= dstPort;
		this.timestamp 			= Instant.ofEpochSecond(ts_sec, ts_usec * 1000);
		this.localDateTime 		= LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
		this.localDate			= localDateTime.toLocalDate();
		
		this.rtpBuffer 			= ByteBuffer.wrap(rtpData);
		this.rtpPayloadType 	= RTP.getPayloadType(rtpBuffer);
		this.rtpPayloadlen 		= RTP.getPayloadLength(rtpBuffer);
		this.rtpHeaderlen 		= RTP.getHeaderLength(rtpBuffer);
		this.rtpSsrc			= RTP.getSyncSource(rtpBuffer);
		this.rtpSeqNumber 		= RTP.getSeqNumber(rtpBuffer);
		this.rtpSampleTime		= RTP.getTimestamp(rtpBuffer);
		
		rtpBuffer.position(rtpHeaderlen);
	}

		
	@Override
	public int compareTo(CapturedPacket o)
	{
		return timestamp.compareTo(o.timestamp);
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapturedPacket other = (CapturedPacket) obj;
		if (timestamp == null)
		{
			if (other.timestamp != null)
				return false;
		}
		else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}


	@Override
	public String toString()
	{
		return String.format("Packet [time=%s, src=%s:%s, dst=%s:%s,rtType=%s, rtplen=%s, rtpSsrc=%s, rtpSeq=%s, rtpTime=%s]", localDateTime, srcIp, srcPort, dstIp, dstPort, rtpPayloadType,rtpPayloadlen, rtpSsrc, rtpSeqNumber, rtpSampleTime);
	}


	public static CapturedPacket createFromBuffer(ByteBuffer buffer) throws IOException
	{
		long ts_sec 			= buffer.getInt();
		long ts_usec 			= buffer.getInt();
		long incl_len 			= buffer.getInt();
		long orig_len 			= buffer.getInt();
		
		int remaining = buffer.remaining();
		
		if (remaining >= incl_len)
		{	
			buffer.position(buffer.position() + 14 + 12); // mac + ip without addresses
		
			
			ByteOrder currentOrder = buffer.order();
			
			buffer.order(ByteOrder.BIG_ENDIAN);
			byte[] srcIp			= new byte[4];
			buffer.get(srcIp);
		
			byte[] dstIp			= new byte[4];
			buffer.get(dstIp);
			
			int srcPort			= buffer.getShort() & 0xffff;
			int dstPort			= buffer.getShort() & 0xffff;
				
			buffer.position(buffer.position() + 4);
			
			buffer.order(currentOrder);
		
			int rtpLength = (int) (incl_len - Constants.PCAP_UDP_HEADER_LENGTH);	
			byte[] rtpData 	= new byte[rtpLength];
			buffer.get(rtpData);
			
			CapturedPacket packet 	= new CapturedPacket(ts_sec,ts_usec,incl_len,orig_len,srcIp,dstIp,srcPort,dstPort,  rtpData);
			return packet;
			
		}	
		return null;
	}
}