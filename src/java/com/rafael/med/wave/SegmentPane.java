package com.rafael.med.wave;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.VoiceSegment;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SegmentPane extends VBox
{
	public final ChannelPane channelPane;
	public final VoiceSegment segment;

		
	public boolean isSelected = false;
	public final Label e1;
	private Tooltip p563Tooltip;
	
	
	private static final Border BORDER_WHITE = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	private static final Border BORDER_BLACK = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	

	public SegmentPane(ChannelPane channelPane, VoiceSegment segment)
	{
		this.channelPane 	= channelPane;
		this.segment 		= segment;
		
		setSpacing(3);
		setPadding(new Insets(4, 1, 4, 1));
		setAlignment(Pos.CENTER);
		setMaxWidth(60);
		setMinWidth(60);
		setFillWidth(true);
		
		e1 = new Label(String.valueOf(segment.index));
		e1.setTextFill(Color.BLACK);
		
		Text e2 = new Text(String.valueOf(segment.duration.getSeconds()) + " sec");
		Text e3 = new Text(segment.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy));
		Text e4 = new Text(segment.fromTime.format(Utilities.DATE_TIME_HH_mm_ss));
		Text e5 = new Text(segment.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy));
		Text e6 = new Text(segment.toTime.format(Utilities.DATE_TIME_HH_mm_ss));
		
		getChildren().addAll(e1,e2,e3,e4,e5,e6);
		setSelected(false, false);
		
		setOnMouseClicked(e ->
		{
			isSelected = !isSelected;
			boolean isNotFound = WaveManager.INSTANCE.onSelectChartSegment(isSelected, this);
		//	System.out.println("---------- isNotFound = " + isNotFound + " isSelected = " + isSelected);
			if(isSelected)
			{
				channelPane.mosButton.setDisable(isNotFound);
			}
			else
			{
				channelPane.mosButton.setDisable(channelPane.isP563RunOnce);
			}
		});
		
		p563Tooltip = new Tooltip();
		
		
		setBorder(BORDER_BLACK);
	}


	public void setSelected(boolean isSelected, boolean isSource)
	{
		this.isSelected = isSelected;
		
		channelPane.onSegmentSelected(isSelected,isSource, this);
		
		String background = null;
		Color foreground = null;
		
		if(isSelected)
		{
			if(isSource)
			{
				background = "aquamarine";
				foreground = Color.BLACK;
			}
			else
			{
				background = "yellow";
				foreground = Color.BLACK;
			}
		}
		else
		{
			background = "-color-40";
			foreground = Color.AQUA;
		}
		
		ObservableList<Node> children = getChildren();
		for (Node child : children) 
		{
			if (child instanceof Text)
			{
				Text text = (Text) child;
				text.setFill(foreground);
			}
		}
		setStyle("-fx-background-color : " +  background  + ";-fx-font-size: 10.0;");
	}


	public void setUsed()
	{
		setBorder(BORDER_WHITE);
	}


	public void initTooltip() 
	{
		p563Tooltip.setText(segment.tooltipResult);
		e1.setTooltip(p563Tooltip);
		
	}
}
