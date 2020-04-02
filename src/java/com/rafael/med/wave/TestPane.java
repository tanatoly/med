package com.rafael.med.wave;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.TestData;
import com.rafael.med.common.bnet.UnitData;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TestPane extends BorderPane
{
	private boolean isExpanded 		= false;
	public ObservableList<Node> 	units;
	private AppWave 				app;

	public TestPane(AppWave app)
	{
		this.app = app;
		
		VBox main = new VBox();
		BorderPane.setMargin(main, new Insets(4));
		units = main.getChildren();
		
		ScrollPane scrollPane = new ScrollPane(main);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		setCenter(scrollPane);
	}
	
	public void reset(TestData testData) throws Exception
	{
		units.clear();
		
		List<UnitPane> unitPanes = new ArrayList<>();
		for (UnitData unitData : testData.units)
		{	
			UnitPane unitPane = new UnitPane(this,unitData);
			unitPane.setExpanded(isExpanded);
			units.add(unitPane);
			unitPanes.add(unitPane);
		}
		this.setUserData(unitPanes);
		app.testNameLabel.setText(testData.name + "   ( " + testData.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss) + " - " + testData.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss) + " )");
	}
	

	public void onExpandedAction(JFXButton expandedButton, Text expandedTrue, Text expandedFalse)
	{
		isExpanded = !isExpanded;
		if(isExpanded)
		{
			expandedButton.setGraphic(expandedTrue);
		}
		else
		{
			expandedButton.setGraphic(expandedFalse);
		}
		
		for (Node node : units)
		{
			if (node instanceof UnitPane)
			{
				UnitPane singleRow = (UnitPane) node;
				singleRow.setExpanded(isExpanded);
			}
		}
	}
	
}
