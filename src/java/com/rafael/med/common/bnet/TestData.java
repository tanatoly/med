package com.rafael.med.common.bnet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestData 
{
	private static final Logger log = LogManager.getLogger();
	
	public final Path root;
	public final String name;
	public final List<UnitData> units  		= new ArrayList<>();
	public LocalDateTime fromTime			= LocalDateTime.MAX;
	public LocalDateTime toTime				= LocalDateTime.MIN;	
	
	public TestData(Path selectedPath) throws IOException 
	{
		this.root = selectedPath;
		this.name = selectedPath.getFileName().toString();

		if (Files.exists(selectedPath) && Files.isDirectory(selectedPath))
		{
			Files.walkFileTree(selectedPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					if(dir.getParent().equals(selectedPath)) // unit level
					{
						UnitData unitData = new UnitData(TestData.this, dir);
						
						LocalDateTime currentFromTime 	= unitData.fromTime;
						LocalDateTime currentToTime 	= unitData.toTime;
						
						if(currentFromTime.isBefore(fromTime))
						{
							fromTime = currentFromTime;
						}
						
						if(currentToTime.isAfter(toTime))
						{
							toTime = currentToTime;
						}
						units.add(unitData);
					} 
					return super.preVisitDirectory(dir, attrs);
				}
			});
		}
		log.debug("{} created",this);
	}
	
	
	
	public List<VoiceSegment> findSimilarVoiceSegments(VoiceSegment sourceSegment)
	{
		LocalDateTime BEGIN 	= sourceSegment.fromTime.minusNanos(50_000_000);
		LocalDateTime END		= sourceSegment.toTime.plusNanos(50_000_000);
		
		Duration source			= Duration.between(BEGIN, END);
		
		List<VoiceSegment> segments = new ArrayList<>();
		segments.add(sourceSegment);
		for (UnitData unitData : units)
		{
			for (BnetData bnetData : unitData.bnets)
			{
				for (VoiceChannel voiceChannel : bnetData.voiceData.channels.values())
				{
					for (VoiceSegment currentSegment : voiceChannel.segments)
					{
						
						if(currentSegment != sourceSegment)
						{
						
							Duration related = null;
							
							if(currentSegment.fromTime.isBefore(BEGIN) && currentSegment.toTime.isAfter(BEGIN) && currentSegment.toTime.isBefore(END))
							{
								related = Duration.between(BEGIN, currentSegment.toTime);
							}
							else if(currentSegment.fromTime.isAfter(BEGIN) && currentSegment.fromTime.isBefore(END) && currentSegment.toTime.isAfter(END))
							{
								related = Duration.between(currentSegment.fromTime, END);
							}
							else if(currentSegment.fromTime.isAfter(BEGIN) && currentSegment.toTime.isBefore(END))
							{
								related = Duration.between(currentSegment.fromTime, currentSegment.toTime);
							}
							else if(currentSegment.fromTime.isBefore(BEGIN) && currentSegment.toTime.isAfter(END))
							{
								related = Duration.between(currentSegment.fromTime, currentSegment.toTime);
							}
							
							if(related != null)
							{
								
								double x = ((double)related.toMillis()/(double)source.toMillis()) * 100.0;
								int z = (int) x;
								//System.out.println(z);
								if(z > 50)
								{
									currentSegment.currentRelated = z;
									segments.add(currentSegment);
								}
							}						
						}

					}
				}
			}
		}
		return Collections.unmodifiableList(segments);
	}
	
	
	

	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("TestData [name=");
		builder.append(name);
		builder.append(", units=");
		builder.append(units);
		builder.append(", fromTime=");
		builder.append(fromTime);
		builder.append(", toTime=");
		builder.append(toTime);
		builder.append("]");
		return builder.toString();
	}
}
