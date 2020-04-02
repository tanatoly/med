package com.rafael.med.spect;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SpectSegment extends StackPane 
{
	private static final Background TRANSPARENT_BG = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));	
	private static final Background WHITE_BG = new Background(new BackgroundFill(Color.WHITE, null, null));	
	private static final Background YELLOW_BG = new Background(new BackgroundFill(Color.YELLOW, null, null));	
	public static final Background[] BACKGROUNDS = new Background[100];	
	
	static
	{
		for (int i = 0; i < BACKGROUNDS.length; i++)
		{
			double d = i/100.0;
			Color color = Color.BLUE.interpolate(Color.YELLOW, d);
			BACKGROUNDS[i] = new Background(new BackgroundFill(color, null, null));	
		}
	}
	
	public final int channelNumber;
	public final SpectPane spectPane;
	private Tooltip tooltip;
	public static final String TOOLTIP = "channel = %d, percent = %d";
	
	public int percent;
	
	public SpectSegment(SpectPane spectPane, int channelNumber, int row )
	{
		this.spectPane = spectPane;
		this.channelNumber = channelNumber;
		tooltip = new Tooltip("channel = " + channelNumber);
		Tooltip.install(this, tooltip);
		setBackground(TRANSPARENT_BG);
		setUserData(tooltip);
	}
	
	public void setSegmentValue()
	{
		SpectChannel spectChannel = spectPane.channels[channelNumber - 1];
		percent = spectChannel.getValue();
		
		if(percent >= 0 && percent < 100)
		{
			setBackground(BACKGROUNDS[percent]);
			//setT(this, percent);
		}
		else if(percent == 100)
		{
			setBackground(YELLOW_BG);
		}
		else
		{
			setBackground(WHITE_BG);
		}
	}
	
//	public static void setT(SpectSegment segment, int percent)
//	{
//		Tooltip tooltip = (Tooltip) segment.getUserData();
//		tooltip.setText(String.format(TOOLTIP, segment.channelNumber, percent));
//	}
}
