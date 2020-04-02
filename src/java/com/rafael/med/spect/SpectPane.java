package com.rafael.med.spect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.jfoenix.controls.JFXCheckBox;
import com.rafael.med.common.ViewUtils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SpectPane extends TitledPane
{	
	public final AtomicInteger refreshInSec 			= new AtomicInteger(1);
	public final AtomicBoolean isRunning 				= new AtomicBoolean(false);
	public final List<List<SpectSegment>> segments		= new ArrayList<>();
	
	
	private final AtomicBoolean isFirst 				= new AtomicBoolean(true);
	private final GridPane grid;
	
	public final SpectChannel [] channels;
	public final int nodeId;
	public final int opcode;
	
	public SpectPane(SpectsPane spectsPane, int nodeId, String name, int opcode, int channelsSize)
	{
		
		this.nodeId = nodeId;
		this.opcode = opcode;
		
		setStyle("-fx-background-color : -color-90;");	
		channels = new SpectChannel [channelsSize];
		for (int i = 0; i < channels.length; i++)
		{
			channels[i] = new SpectChannel(i + 1);
		}
		
		BorderPane content = new BorderPane();	
		content.setPadding(new Insets(2));
		
		Text spectNameLabel = new Text(name);
		spectNameLabel.setFill(Color.WHITESMOKE);
		Text spectNodeLabel = new Text("node = " + nodeId);
		spectNodeLabel.setFill(Color.WHITESMOKE);
		
		Text spectOpcodeLabel = new Text("opcode = " + opcode);
		spectOpcodeLabel.setFill(Color.WHITESMOKE);
		Text spectChannelsSizeLabel = new Text("channels size = " + channelsSize);
		spectChannelsSizeLabel.setFill(Color.WHITESMOKE);
		
		
		GridPane title = new GridPane();

		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(100/4);
		title.getRowConstraints().add(r);
		title.getColumnConstraints().addAll(c1,c1,c1,c1);
		
		GridPane.setConstraints(spectNameLabel, 			0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(spectNodeLabel, 			1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(spectOpcodeLabel, 			2, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(spectChannelsSizeLabel, 	3, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		title.getChildren().addAll(spectNameLabel,spectNodeLabel, spectOpcodeLabel, spectChannelsSizeLabel);
		setGraphic(title);		
		setContent(content);
		
		content.setTop(createTop(channelsSize));
		
		grid = new GridPane();
		grid.setStyle("-fx-background-color : black;");	
		grid.setMinHeight(600);
		grid.setMaxHeight(600);
		
		content.setCenter(grid);
	}
	
	
	


	private Node createTop(int channelsSize)
	{
		GridPane pane = new GridPane();
		
		pane.setStyle("-fx-background-color : -color-40;");	
		pane.setPadding(new Insets(0,4,0,0));
		
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(80);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(20/4);
		pane.getRowConstraints().add(r);
		pane.getColumnConstraints().addAll(c1,c2,c2,c2,c2);
		
		TilePane checkBoxPane = new TilePane(Orientation.HORIZONTAL	, 4, 2);
		
		for (int i = 0; i < channelsSize; i++) 
		{
			JFXCheckBox checkBox = new JFXCheckBox(String.format("%3d", i + 1));
			checkBoxPane.getChildren().add(checkBox);
			final int j = i;
			checkBox.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
			{
				channels[j].isSelected = newValue;
			});
			checkBox.setSelected(false);
		}
		
		TitledPane top = new TitledPane();
		top.setStyle("-fx-background-color : -color-40;");
		top.setExpanded(false);
		HBox box = new HBox();
		box.minWidthProperty().bind(pane.widthProperty().multiply(0.8).subtract(40));
		JFXCheckBox all = new JFXCheckBox("all select");
		
		
		
		Text text = new Text("Channels");
		text.setFill(Color.WHITE);
		box.getChildren().addAll(text, ViewUtils.hspace(),all);
		box.setPadding(new Insets(0, 6, 0, 6));
		top.setGraphic(box);
		top.setContent(checkBoxPane);
		
		Font font = new Font(14);
		
		Text refreshText = new Text("Refresh");
		refreshText.setFill(Color.WHITE);
		refreshText.setFont(font);
		
		Spinner<Integer> refreshSpinner = new Spinner<Integer>(1, 10, 1);
		refreshSpinner.setMaxSize(80, 30);
		refreshSpinner.setMinSize(80, 30);
		refreshSpinner.setFocusTraversable(false);
		
		
		Text rowsText = new Text("Rows");
		rowsText.setFill(Color.WHITE);
		rowsText.setFont(font);
		
		Spinner<Integer> rowsSpinner = new Spinner<Integer>(10, 60, 60, 10);
		rowsSpinner.setMaxSize(80, 30);
		rowsSpinner.setMinSize(80, 30);
		rowsSpinner.setFocusTraversable(false);
		
		GridPane.setConstraints(top, 				0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rowsText, 			1, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rowsSpinner, 		2, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(refreshText, 		3, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(refreshSpinner, 	4, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		pane.getChildren().addAll(top, rowsText, rowsSpinner,refreshText, refreshSpinner);
		
		top.expandedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
		{
			if(!newValue)
			{
				resetCenter(rowsSpinner.getValue());
			}
			else
			{
				isRunning.set(false);
			}
		});
		
		refreshSpinner.valueProperty().addListener((ChangeListener<Integer>) (observable, oldValue, newValue) ->
		{
			refreshInSec.set(newValue);
		});
		
		rowsSpinner.valueProperty().addListener((ChangeListener<Integer>) (observable, oldValue, newValue) ->
		{
			resetCenter(rowsSpinner.getValue());
		});
		
		all.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
		{
			ObservableList<Node> children = checkBoxPane.getChildren();
			for (Node node : children) 
			{
				if (node instanceof JFXCheckBox)
				{
					JFXCheckBox checkBox = (JFXCheckBox) node;
					checkBox.setSelected(newValue);
					
				}
			}
			if(newValue)
			{
				resetCenter(rowsSpinner.getValue());
			}
			else
			{
				isRunning.set(false);
			}
		});
		return pane;
	}
	
	private void resetCenter(int rows) 
	{		
		
		Platform.runLater(() -> 
		{
	
			isRunning.set(false);
			
			grid.getChildren().clear();
			grid.getRowConstraints().clear();
			grid.getColumnConstraints().clear();		
			grid.setGridLinesVisible(true);
			
			int columns = 0;
			for (int i = 0; i < channels.length; i++)
			{
				if(channels[i].isSelected)
				{
					columns++;
				}
			}
			
			double width =  grid.getWidth();
			if(isFirst.compareAndSet(true, false))
			{
				grid.setMaxWidth(width);
				grid.setMinWidth(width);
			}
			double columnW = width /columns;
	//		System.out.println("width = " + width +", columns = " +columns +", columnW = " +columnW);
			ColumnConstraints c 	= new ColumnConstraints();
			c.setPercentWidth(columnW);
			c.setFillWidth(true);
			
			for (int j = 0; j < columns; j++) 
			{
				grid.getColumnConstraints().add(c);
			}
			
			double height = 600;
			double rowh	= height/rows;
	//		System.out.println("height = " + height +", rows = " +rows +", rowh = " +rowh);
			RowConstraints r = new RowConstraints();
			r.setPercentHeight(rowh);
			r.setFillHeight(true);		
			for (int j = 0; j < rows; j++)
			{
				grid.getRowConstraints().add(r);
			}
			
			segments.clear();
			
			for (int i = rows - 1; i >= 0; i--) 
			{
				ArrayList<SpectSegment> rowSegments = new ArrayList<>(columns);
				segments.add(rowSegments);
				
				int columnCount = 0;
				for (int j = 0; j < channels.length; j++)
				{
					if(channels[j].isSelected)
					{
						SpectSegment segment = new SpectSegment(SpectPane.this, channels[j].number, rows-i);
						rowSegments.add(segment);
						GridPane.setConstraints(segment, 	columnCount, i, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
						grid.getChildren().add(segment);
						columnCount++;
					}
				}
			}
			isRunning.set(true);
		});
	}
}
