package com.rafael.med.common.bnet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BnetData 
{
	private static final Logger log = LogManager.getLogger();
	
	public final UnitData unitData;
	public final String name;
	public final VoiceData 		voiceData  		= new VoiceData(this);
	public final TelemetryData 	telemetryData  	= new TelemetryData(this);
	public LocalDateTime fromTime				= LocalDateTime.MAX;
	public LocalDateTime toTime					= LocalDateTime.MIN;	


	public BnetData(UnitData unitData, Path bnetPath) throws IOException
	{
		this.unitData = unitData;
		this.name = bnetPath.getFileName().toString();
		
		if (Files.exists(bnetPath) && Files.isDirectory(bnetPath))
		{
			Files.walkFileTree(bnetPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
				{
					if(file.toFile().length() > 0)
					{
						voiceData.checkAndAdd(file.toFile());
						telemetryData.checkAndAdd(file.toFile());
					}
					return super.visitFile(file, attrs);
				}
			});
		}
		
		voiceData.complete();
		telemetryData.complete();
		
		this.fromTime = (voiceData.fromTime.isBefore(telemetryData.fromTime)) ? voiceData.fromTime : telemetryData.fromTime;
		this.toTime   = (voiceData.toTime.isAfter(telemetryData.toTime)) ? voiceData.toTime : telemetryData.toTime;
		
		log.debug("{} created",this);
	}

	
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("BnetData [name=");
		builder.append(name);
		builder.append(", voiceData=");
		builder.append(voiceData);
		builder.append(", telemetryData=");
		builder.append(telemetryData);
		builder.append(", fromTime=");
		builder.append(fromTime);
		builder.append(", toTime=");
		builder.append(toTime);
		builder.append("]");
		return builder.toString();
	}
}
