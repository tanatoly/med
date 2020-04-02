package com.rafael.med.common.bnet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnitData 
{
	private static final Logger log = LogManager.getLogger();
	
	public final TestData testData;
	public final String name;
	public final List<BnetData> bnets  		= new ArrayList<>();
	public LocalDateTime fromTime			= LocalDateTime.MAX;
	public LocalDateTime toTime				= LocalDateTime.MIN;	

	
	public UnitData(TestData testData, Path unitPath) throws IOException 
	{
		this.testData = testData;
		this.name = unitPath.getFileName().toString();

		if (Files.exists(unitPath) && Files.isDirectory(unitPath))
		{
			Files.walkFileTree(unitPath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					if(dir.getParent().equals(unitPath)) // bnet level
					{
						BnetData bnetData = new BnetData(UnitData.this, dir);
						
						LocalDateTime currentFromTime 	= bnetData.fromTime;
						LocalDateTime currentToTime 	= bnetData.toTime;
						
						if(currentFromTime.isBefore(fromTime))
						{
							fromTime = currentFromTime;
						}
						
						if(currentToTime.isAfter(toTime))
						{
							toTime = currentToTime;
						}
						bnets.add(bnetData);
					} 
					return super.preVisitDirectory(dir, attrs);
				}
			});
		}
		log.debug("{} created",this);
	}

	
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("UnitData [name=");
		builder.append(name);
		builder.append(", bnets=");
		builder.append(bnets);
		builder.append(", fromTime=");
		builder.append(fromTime);
		builder.append(", toTime=");
		builder.append(toTime);
		builder.append("]");
		return builder.toString();
	}
}

