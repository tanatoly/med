package com.rafael.med.deploy;

import com.jfoenix.controls.JFXCheckBox;
import com.rafael.med.common.entity.Bnet;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BnetRow extends TitledPane
{
	public JFXCheckBox checkBox;
	private ProgressBar progressBar;

	public BnetRow(Bnet bnet)
	{
		HBox content = new HBox(10);
//		content.setMinHeight(80);
		content.setMaxHeight(100);
		
		content.setPadding(new Insets(4, 4, 4, 4));
		
		content.setAlignment(Pos.CENTER_LEFT);
		
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		
		textArea.setFocusTraversable(false);
		
		HBox.setHgrow(textArea, Priority.ALWAYS);
		
		
		content.getChildren().addAll(createInfoPane(),createVersionPane(),createThroughPane(), textArea);
		
//		String prefix = "";
//		
//		
//		
//		if(bnetId < 10)
//		{
//			prefix = "bnet   " + bnetId;
//		}
//		else if(bnetId < 100)
//		{
//			
//		}
//		else
//		{
//			prefix = "bnet " + bnetId;
//		}
//		
//		setText(prefix);
		
		Text label = new Text("bnet " + bnet.getId());
		label.setFill(Color.WHITE);
		
		progressBar = new ProgressBar(0.0);
		progressBar.setMinHeight(16);
		progressBar.setDisable(true);
		
		progressBar.setFocusTraversable(false);
		
		checkBox = new JFXCheckBox();
		checkBox.setCheckedColor(Color.BLUE);
		checkBox.setUnCheckedColor(Color.WHITE);
		
		checkBox.setScaleX(1.5);
		checkBox.setAlignment(Pos.TOP_CENTER);
		
		
		GridPane pane = new GridPane();
		pane.minWidthProperty().bind(widthProperty().subtract(16 * 2));
		
		progressBar.prefWidthProperty().bind(pane.widthProperty().divide(2));
		
		//pane.setStyle("-fx-background-color :  red;");
		pane.setPadding(new Insets(0, 16, 0, 16));
		
		//pane.setGridLinesVisible(true);
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(20);
		pane.getRowConstraints().add(r);
		pane.getColumnConstraints().addAll(c,c,c,c,c);
		
		GridPane.setConstraints(label, 			0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(progressBar, 	1, 0, 3, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(checkBox, 		4, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

		
		//HBox titleBox = new HBox();
		pane.getChildren().addAll(label, progressBar,  checkBox);
		
		setGraphic(pane);
		//setContentDisplay(ContentDisplay.RIGHT);
		
		setContent(content);
	}
	
	private Pane createInfoPane()
	{
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(0, 6, 0, 6));
		
		//pane.setGridLinesVisible(true);
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100/7);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(50);
		
		pane.getRowConstraints().add(r);
		pane.getColumnConstraints().add(c);
		
		pane.setStyle("-fx-background-color :  -color-50;");
		Label versions = new Label("Bnet info");
		
		
		Text field1 = new Text("serial id : ");
		Text field2 	= new Text("role : ");
		Text rf1 	= new Text("platfrom : ");
		Text rf2 	= new Text("aircraft  : ");
		Text cpld   = new Text("config : ");
		Text image  = new Text("control ip : ");
		
		Text field1Value = new Text("-------");
		Text field2Value 	= new Text("-------");
		Text rf1Value 	= new Text("-------");
		Text rf2Value 	= new Text("-------");
		Text cpldValue   = new Text("-------");
		Text imageValue  = new Text("-------");
		
		
		
		GridPane.setConstraints(versions, 	0, 0, 2, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field1, 	0, 1, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field1Value, 1, 1, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field2, 		0, 2, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field2Value, 	1, 2, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf1, 		0, 3, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf1Value, 	1, 3, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf2, 		0, 4, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf2Value, 	1, 4, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(cpld, 		0, 5, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(cpldValue, 	1, 5, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(image, 		0, 6, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(imageValue, 1, 6, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		ObservableList<Node> children = pane.getChildren();
		children.addAll(versions,field1,field1Value,field2,field2Value, rf1,rf1Value, rf2, rf2Value, cpld,cpldValue,image,imageValue);
		
		for (Node node : children)
		{
			if (node instanceof Text)
			{
				Text text = (Text) node;
				text.setFill(Color.WHITE);
				text.setFont(Font.font(15));
			}
		}
		
		
		pane.setMinWidth(260);
		pane.setMaxWidth(260);
		pane.setMaxHeight(Double.MAX_VALUE);
		
		return pane;
	}
	
	private Pane createVersionPane()
	{
		
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(0, 6, 0, 6));
		
		//pane.setGridLinesVisible(true);
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100/7);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(50);
		
		pane.getRowConstraints().add(r);
		pane.getColumnConstraints().add(c);
		
		pane.setStyle("-fx-background-color :  -color-50;");
		Label versions = new Label("Versions");
		
		
		Text field1 		= new Text("master : ");
		Text field2 			= new Text("dpu : ");
		Text rf1 			= new Text("rf1 : ");
		Text rf2 			= new Text("rf2 : ");
		Text cpld   		= new Text("cpld : ");
		Text image  		= new Text("image : ");
		
		Text field1Value 	= new Text("-------");
		Text field2Value 		= new Text("-------");
		Text rf1Value 		= new Text("-------");
		Text rf2Value 		= new Text("-------");
		Text cpldValue   	= new Text("-------");
		Text imageValue  	= new Text("-------");
		
		
		
		GridPane.setConstraints(versions, 	0, 0, 2, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field1, 	0, 1, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field1Value, 1, 1, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field2, 		0, 2, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field2Value, 	1, 2, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf1, 		0, 3, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf1Value, 	1, 3, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf2, 		0, 4, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf2Value, 	1, 4, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(cpld, 		0, 5, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(cpldValue, 	1, 5, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(image, 		0, 6, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(imageValue, 1, 6, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		ObservableList<Node> children = pane.getChildren();
		children.addAll(versions,field1,field1Value,field2,field2Value, rf1,rf1Value, rf2, rf2Value, cpld,cpldValue,image,imageValue);
		
		for (Node node : children)
		{
			if (node instanceof Text)
			{
				Text text = (Text) node;
				text.setFill(Color.WHITE);
				text.setFont(Font.font(15));
			}
		}
		
		
		pane.setMinWidth(260);
		pane.setMaxWidth(260);
		pane.setMaxHeight(Double.MAX_VALUE);
		
		return pane;
	}
	
	private Pane createThroughPane()
	{
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(0, 6, 0, 6));
		
		//pane.setGridLinesVisible(true);
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100/5);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(50);
		
		pane.getRowConstraints().add(r);
		pane.getColumnConstraints().add(c);
		
		pane.setStyle("-fx-background-color :  -color-50;");
		Label versions = new Label("App info");
		
		
		Text field1 = new Text("to bnet ip: ");
		Text field2 	= new Text("to lan ip : ");
		Text rf1 	= new Text("is remote : ");
		Text rf2 	= new Text("version  : ");
//		Text cpld   = new Text("in progress : ");
//		Text image  = new Text("control ip : ");
		
		Text field1Value = new Text("-------");
		Text field2Value 	= new Text("-------");
		Text rf1Value 	= new Text("-------");
		Text rf2Value 	= new Text("-------");
//		Text cpldValue   = new Text("-------");
//		Text imageValue  = new Text("-------");
		
		
		
		GridPane.setConstraints(versions, 	0, 0, 2, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field1, 	0, 1, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field1Value, 1, 1, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(field2, 		0, 2, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(field2Value, 	1, 2, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf1, 		0, 3, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf1Value, 	1, 3, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(rf2, 		0, 4, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(rf2Value, 	1, 4, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
//		GridPane.setConstraints(cpld, 		0, 5, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//		GridPane.setConstraints(cpldValue, 	1, 5, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//		
//		GridPane.setConstraints(image, 		0, 6, 1, 1, HPos.LEFT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//		GridPane.setConstraints(imageValue, 1, 6, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		ObservableList<Node> children = pane.getChildren();
		children.addAll(versions,field1,field1Value,field2,field2Value, rf1,rf1Value, rf2, rf2Value);
		
		for (Node node : children)
		{
			if (node instanceof Text)
			{
				Text text = (Text) node;
				text.setFill(Color.WHITE);
				text.setFont(Font.font(15));
			}
		}
		
		
		pane.setMinWidth(260);
		pane.setMaxWidth(260);
		pane.setMaxHeight(Double.MAX_VALUE);
		
		return pane;
	}

	public void setChecked(boolean selected) 
	{
		checkBox.setSelected(selected);
		
	}

	public void setProgress(double x)
	{
		progressBar.setProgress(x);
		
	}

	public Bnet getBnet() {
		return null;
		
	}

	
	
}
