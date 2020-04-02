package com.rafael.med.debrief;

import java.time.LocalDateTime;
import java.util.List;

import com.rafael.med.common.bnet.ChannelType;

import javafx.scene.shape.Line;

public class ChannelResult
{

	public byte[] 					audioByteArray;
	public int 						startIndex;
	public int 						endIndex;
	public LocalDateTime			startTime;
	public LocalDateTime			endTime;
	public List<Line>				lines;
	public ChannelType 			type;
	
	public ChannelResult(ChannelType type)
	{
		this.type = type;
	}

	@Override
	public String toString() {
		return "ChannelResult [audioByteArray l =" + audioByteArray.length + ", startIndex=" + startIndex
				+ ", endIndex=" + endIndex + ", startTime=" + startTime + ", endTime=" + endTime + ", lines=" + lines
				+ ", type=" + type + "]";
	}

	
	
}
