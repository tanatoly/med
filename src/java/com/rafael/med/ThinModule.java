package com.rafael.med;

import com.rafael.med.MedData.Bed;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ThinModule extends GridPane
{
	private Bed bed;

	public ThinModule()
	{
		setGridLinesVisible(true);
		
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(15);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(5);
		RowConstraints r3 = new RowConstraints();
		r3.setPercentHeight(10);
		getRowConstraints().addAll(r1,r2, r3,r3,r3,r3,r3,r3,r3,r3);
		
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(25);
		getColumnConstraints().addAll(c,c,c,c);
		
		Text fullView = ViewUtils.glyphIcon(FontAwesomeIcon.ID_CARD, 30, Color.WHITE);
		fullView.setOnMouseClicked(e ->
		{

			MedManager.INSTANCE.showDetails(null);
		});
		Text bedNumber = new Text("007");
		bedNumber.setFill(Color.AQUA);
		bedNumber.setFont(Font.font(24));
		Text alarm = ViewUtils.glyphIcon(FontAwesomeIcon.WARNING, 30, Color.RED);
		alarm.setOnMouseClicked(e ->
		{
			
		});
		
		GridPane.setConstraints(bedNumber, 				0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(alarm, 					2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(fullView, 				3, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		
		getChildren().addAll(bedNumber, fullView, alarm);
	}
	
	public void setBed(Bed bed)
	{
		this.bed = bed;
	}
	
	public void onTimeClick()
	{
		
	}
}
