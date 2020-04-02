package com.rafael.med.debrief;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class TelemetryData
{
	public static final class Atile extends BorderPane
	{
		private Label title;
		private Label center;
		
		public Atile(String name)
		{
			title = new Label(name);
			title.setAlignment(Pos.CENTER);
			BorderPane.setMargin(title, new Insets(2,4,2,4));
			BorderPane.setAlignment(title, Pos.TOP_CENTER);
			title.setStyle("-fx-font-size: 11;-fx-font-weight: BOLD;-fx-text-fill: white;");
			setTop(title);
			
			center = new Label();
			center.setAlignment(Pos.CENTER);
			//BorderPane.setMargin(title, new Insets(2,4,2,4));
			BorderPane.setAlignment(center, Pos.BOTTOM_CENTER);
			center.setStyle("-fx-font-size: 14;-fx-font-weight: BOLD;-fx-text-fill: white;");
			setCenter(center);
			
			setStyle("-fx-background-color : -color-10;-fx-border-color:-color-90; -fx-border-width: 1 1 1 1;");
			setMaxSize(PlayerView.TILE_WIDTH, PlayerView.TILE_HEIGHT);
			setMinSize(PlayerView.TILE_WIDTH, PlayerView.TILE_HEIGHT);
		}
		
		public void setValue(Object value)
		{
			if (value instanceof Label	) 
			{
				Label label = (Label) value;
				BorderPane.setAlignment(label, Pos.BOTTOM_CENTER);
				setCenter(label);
			}
			else
			{
				if(value == null)
				{
					center.setText(null);
				}
				else
				{
					center.setText(value.toString());
				}
				
			}
		}
		
	}
		
	public static final String TOD 						= "TOD";
//	public static final String GUARD_AVG_RSSI 			= "Guard AVG RSSI";
//	public static final String GUARD_ACTIVE_RX			= "Guard Active RX";
//	public static final String GUARD_ACTIVE_TX			= "Guard Active TX";
//	public static final String GUARD_FREQUENCY			= "Guard Frequency";
//	
//	public static final String LEGACY_AVG_RSSI 			= "Legacy AVG RSSI";
//	public static final String LEGACY_ACTIVE_RX			= "Legacy Active RX";
//	public static final String LEGACY_ACTIVE_TX			= "Legacy Active TX";
//	public static final String LEGACY_FREQUENCY			= "Legacy Frequency";
//	
//	public static final String WF_1						= "WF 1";
//	public static final String WF_2						= "WF 2";
//	public static final String WF_3						= "WF 3";
	
	public static final String PTT						= "PTT";
		
	
    private Map<String,Map<Long, String>> map 			= new HashMap<>();	
    public Map<String,Atile> tilesMap 					= new HashMap<>();
    
    
	public TelemetryData(File directory) throws Exception
	{
		List<File> allFiles = Utilities.listFiles(directory.toPath());
		for (File currentFile : allFiles)
		{
			if(currentFile.getName().contains(".csv"))
			{
				Map<Integer, String> indexMap = new LinkedHashMap<>();
				try(Scanner scanner = new Scanner(currentFile))
				{
					String nextLine 		= scanner.nextLine();
					String[] firstLine 		= nextLine.split(",");
					
					int indexTod = 0;
					for (int i = 0; i < firstLine.length; i++)
					{
						String currentType = firstLine[i];
						int lastIndexOf = currentType.lastIndexOf(":");
						if(lastIndexOf > 0)
						{
							currentType = currentType.substring(0, lastIndexOf);
						}
						currentType.trim();
						if( !(currentType.equalsIgnoreCase("Time")) )
						{
							indexMap.put(i, currentType);
						}
						if(currentType.equalsIgnoreCase(TOD))
						{
							indexTod = i;
						}
					}

					while(scanner.hasNextLine())
					{
						nextLine 		= scanner.nextLine();
						String[] split 	= nextLine.split(",");

						long timeInseconds = Long.parseLong(split[indexTod]);

						for (int i = 0; i < split.length; i++)
						{
							String value = split[i].trim();
							String type  = indexMap.get(i);
							if(type != null &&  !type.equalsIgnoreCase(TOD))
							{
								add(type,timeInseconds,value);
							}
						}
					}
				}
			}
		}
	}

	
	private void add(String type,long timeInseconds, String value)
	{
		Map<Long, String> valuePerTod = map.get(type);
		if(valuePerTod == null)
		{
			valuePerTod = new HashMap<>();
			map.put(type, valuePerTod);
			tilesMap.put(type, new Atile(type));
		}
		valuePerTod.put(timeInseconds, value);
	}
	
	public void setTime(long currentTimeInSeconds)
	{
		//System.out.println("--- " + currentTimeInSeconds);
		
		for (Map.Entry<String, Atile> entry : tilesMap.entrySet())
		{
			String type = entry.getKey();
			Atile tile	= entry.getValue();
			Map<Long, String> valuePerTod = map.get(type);
			String value = valuePerTod.get(currentTimeInSeconds);
			setValueToTile(type, tile, value);
		}
	}
	
	
	private void setValueToTile(String type, Atile tile, String value)
	{
		
		//System.out.println("type = " +type + ", tile = " +tile + " , value = " +value);
		
		if(type.equals(PTT))
		{
			Label l = null;
			if("PTT".equals(value))
			{
				l = ViewUtils.jfxLabel(null, MaterialIcon.PHONE, 46, 46, Color.RED, Color.WHITE);
			}
			else
			{
				l = ViewUtils.jfxLabel(null, MaterialIcon.CALL_END, 46, 46, Color.TRANSPARENT, Color.WHITE);
			}
			
			
			tile.setValue(l);
		}
		else
		{
			
			tile.setValue(value);
		}
	}

	public void resetTiles()
	{
		for (Map.Entry<String, Atile> entry : tilesMap.entrySet())
		{
			String type = entry.getKey();
			Atile tile	= entry.getValue();
			setValueToTile(type, tile, null);
		}
	}
	
	
	
	
	public static void main(String[] args) throws Exception
	{
		TelemetryData telemetryData = new TelemetryData(new File("D:\\test5"));
	}
}
