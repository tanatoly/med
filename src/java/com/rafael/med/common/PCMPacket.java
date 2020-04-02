package com.rafael.med.common;

import java.time.LocalDateTime;

import com.rafael.med.common.bnet.VoicePacket;

public final class PCMPacket implements Comparable<PCMPacket>,VoicePacket
{

	public LocalDateTime localDateTime;

	@Override
	public int compareTo(PCMPacket o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
