package com.rafael.med.wave;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.BnetData;
import com.rafael.med.common.bnet.VoiceChannel;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BnetPane extends TitledPane
{
	public final BnetData bnetData;
	public final UnitPane unitPane;
	public final ObservableList<Node> channels;
	
	private VBox content;
	
	public BnetPane(UnitPane unitPane,BnetData bnetData)
	{
		this.unitPane = unitPane;
		this.bnetData = bnetData;
		
		GridPane title = new GridPane();
		title.setPadding(new Insets(0, 16, 0, 40));
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(20);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(60);
		title.getRowConstraints().add(r);
		title.getColumnConstraints().addAll(c1,c1,c2);
		
		
		Text bnetLabel = new Text(bnetData.name);
		bnetLabel.setFill(Color.GHOSTWHITE);
		
		Text datesLabel = new Text(bnetData.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss) + " - " + bnetData.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		datesLabel.setFill(Color.ALICEBLUE);
		
		GridPane.setConstraints(bnetLabel, 	0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(datesLabel, 2, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		

		title.getChildren().addAll(bnetLabel, datesLabel);
		setGraphic(title);	
		
		content = new VBox(5);
		channels = content.getChildren();
		
		for (VoiceChannel voiceChannel : bnetData.voiceData.channels.values())
		{
			if(voiceChannel != null && voiceChannel.isExists())
			{
				ChannelPane voiceChannelPane = new ChannelPane(this,voiceChannel);
				content.getChildren().add(voiceChannelPane);
			}
		}
		setContent(content);
	}
}
