package com.rafael.med.wave;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.BnetData;
import com.rafael.med.common.bnet.UnitData;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class UnitPane extends TitledPane
{
	public final UnitData unitData;
	public final TestPane testPane;
	public final ObservableList<Node> bnets;
	
	private VBox content;
	public UnitPane(TestPane testPane,UnitData unitData)
	{
		this.testPane = testPane;
		this.unitData = unitData;
		
		setStyle("-fx-background-color : -color-90;");	
		Text unitNameLabel = new Text(unitData.name);
		unitNameLabel.setFill(Color.LIGHTYELLOW);
		content = new VBox(10);
		bnets = content.getChildren();
		
		content.setPadding(new Insets(4, 4, 4, 4));
		content.setAlignment(Pos.CENTER_LEFT);
		
		StringBuilder bnetsText = new StringBuilder("( ");
		
		int i = 0;
		
		
		for (BnetData bnetData : unitData.bnets)
		{
			bnetsText.append(bnetData.name);
			if(i < unitData.bnets.size() - 1)
			{
				bnetsText.append(", ");
			}
			BnetPane bnetPane = new BnetPane(this,bnetData);
			bnets.add(bnetPane);
			i++;
		}
		
		bnetsText.append(" )");
		
		Text bnetsLabel = new Text(bnetsText.toString());
		bnetsLabel.setFill(Color.DARKSEAGREEN);
		
		Text datesLabel = new Text(unitData.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss) + " - " + unitData.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		datesLabel.setFill(Color.WHITESMOKE);
		
		GridPane title = new GridPane();
		title.minWidthProperty().bind(widthProperty().subtract(16 * 2));
		title.setPadding(new Insets(0, 16, 0, 16));
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(20);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(60);
		title.getRowConstraints().add(r);
		title.getColumnConstraints().addAll(c1,c1,c2);
		
		GridPane.setConstraints(unitNameLabel, 	0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(bnetsLabel, 	1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(datesLabel, 	2, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		title.getChildren().addAll(unitNameLabel, bnetsLabel, datesLabel);
		setGraphic(title);		
		setContent(content);
		
	}
}
