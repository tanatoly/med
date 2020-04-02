package com.rafael.med.spect;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SpectsPane extends BorderPane
{
	private boolean isExpanded 		= false;
	public ObservableList<Node> 	spects;
	

	public SpectsPane()
	{		
		VBox main = new VBox();
		BorderPane.setMargin(main, new Insets(2));
		spects = main.getChildren();
		
		ScrollPane scrollPane = new ScrollPane(main);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		setCenter(scrollPane);
	}
	
	public void addSpect(int nodeId, ManetType manetType) throws Exception
	{
		
		SpectPane spectPane = new SpectPane(this, nodeId, manetType.name, manetType.opcode, manetType.channelSize);
		spectPane.setExpanded(isExpanded);
		spects.add(spectPane);	
		SpectManager.INSTANCE.addSpect(spectPane);
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
		
		for (Node node : spects)
		{
			if (node instanceof SpectPane)
			{
				SpectPane singleRow = (SpectPane) node;
				singleRow.setExpanded(isExpanded);
			}
		}
	}
}
