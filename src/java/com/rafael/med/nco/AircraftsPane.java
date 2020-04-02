package com.rafael.med.nco;

import org.controlsfx.tools.Borders;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AircraftsPane extends ScrollPane
{
	private int row 			= 0;
	private int column 			= 0;
	
	private int count 			= 0;
	private GridPane grid;
	private double width;
	private double height;
	
	public AircraftsPane()
	{
		
		Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
		width  = visualBounds.getWidth();
		height = visualBounds.getHeight();
		
		
		grid = new GridPane();
		
		setVbarPolicy(ScrollBarPolicy.ALWAYS);
		setHbarPolicy(ScrollBarPolicy.NEVER);
		setContent(grid);
		setFitToWidth(true);
		setFitToHeight(true);
		
		int columns	= 2;
		for (int j = 0; j < columns; j++)
		{
			ColumnConstraints columnConstraints = new ColumnConstraints(width/2 - 14);
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			grid.getColumnConstraints().add(columnConstraints);
		}
	}
	
	
	public void addAircraft(final Aircraft aircraft)
	{
		double h = height/2 - 65;
		aircraft.view.setMinHeight(h);
		aircraft.view.setMaxHeight(h);

		GridPane.setMargin(aircraft.view, new Insets(6));

		if(count %2 == 0)
		{
			column = 0;
			row++;
		}
		else
		{
			column++;
		}
		Node borderNode = Borders.wrap(aircraft.view).lineBorder().color(Color.DARKGREY.darker()).outerPadding(4).innerPadding(6,6,6,2).radius(6).buildAll();
		grid.add(borderNode,column,row);
		count++;
	}
}
