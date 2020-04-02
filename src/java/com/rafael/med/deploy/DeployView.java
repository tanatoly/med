package com.rafael.med.deploy;

import java.io.File;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.AppManager;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.common.entity.Bnet;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DeployView extends BorderPane
{

	private boolean isExpanded 		= true;
	private boolean isAllChecked 	= false;
	private ObservableList<Node> bnetRows;

	public DeployView()
	{
		setStyle("-fx-background-color :  -color-60;");
		VBox main = new VBox();
		BorderPane.setMargin(main, new Insets(4));
		this.bnetRows = main.getChildren();
		
		ScrollPane scrollPane = new ScrollPane(main);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		setCenter(scrollPane);
		
		GridPane top = new GridPane();
		BorderPane.setMargin(top, new Insets(4));
		top.setPadding(new Insets(4, 0, 0, 0));
		
		Text expandedTrue 			= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_DOWN, String.valueOf(60 * 0.7),Color.WHITE);
		Text expandedFalse	 		= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_RIGHT, String.valueOf(60 * 0.7),Color.WHITE);
		JFXButton expandedButton 	= ViewUtils.jfxbutton(60,60,Color.BLACK,Color.AQUA, null, 2);
		Text expandedCurrent		= (isExpanded) ? expandedTrue: expandedFalse;
		expandedButton.setGraphic(expandedCurrent);	
		expandedButton.setOnAction( e-> 
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
			
			for (Node node : bnetRows)
			{
				if (node instanceof TitledPane)
				{
					if (node instanceof BnetRow)
					{
						BnetRow singleRow = (BnetRow) node;
						singleRow.setExpanded(isExpanded);
					}
				}
			}
		});
		
		
		Text allCheckedTrue 		= ViewUtils.glyphIcon(FontAwesomeIcon.CHECK, String.valueOf(60 * 0.7),Color.DEEPSKYBLUE);
		Text allCheckedFalse	 	= ViewUtils.glyphIcon(FontAwesomeIcon.CHECK, String.valueOf(60 * 0.7),Color.WHITE);
		JFXButton allCheckedButton  = ViewUtils.jfxbutton(60,60,Color.BLACK,Color.AQUA, null, 2);
		Text allCheckedCurrent		= (isAllChecked) ? allCheckedTrue: allCheckedFalse;
		allCheckedButton.setGraphic(allCheckedCurrent);	
		allCheckedButton.setOnAction( e-> 
		{
			
			isAllChecked = !isAllChecked;
			if(isAllChecked )
			{
				allCheckedButton.setGraphic(allCheckedTrue);
			}
			else
			{
				allCheckedButton.setGraphic(allCheckedFalse);
			}
			
			for (Node node : bnetRows)
			{
				if (node instanceof BnetRow)
				{
					BnetRow singleRow = (BnetRow) node;
					singleRow.setChecked(isAllChecked);
				}
			}
		});
		
		
		
		Label  versionLabel = new Label();
		versionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60));
		GridPane.setHgrow(versionLabel, Priority.ALWAYS);
		
		JFXButton chooseButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.FOLDER_OPEN_ALT, 100, 60, Color.BLACK,Color.WHITE, Color.AQUA,null,2);		
		chooseButton.setOnAction(e -> 
		{
	    	File file = AppManager.INSTANCE.showOpenFile(AppDeploy.class);
	    	if(file != null)
	    	{
	    		versionLabel.setText(file.getName());
	    		
	    	}
		});
		
		JFXButton deployButton =  ViewUtils.jfxbutton(null, FontAwesomeIcon.GAVEL, 100, 60, Color.BLACK,Color.WHITE, Color.AQUA,null,2);
		deployButton.setOnAction(e -> 
		{
			Executors.newSingleThreadExecutor().execute(new Runnable() 
	    	{
				@Override
				public void run()
				{
					for (double i = 0; i < 100; i++) 
			    	{
			    		final double x = i/100;
			    		Platform.runLater(new Runnable()
			    		{
							@Override
							public void run()
							{
								
								for (Node node : bnetRows)
								{
									if (node instanceof BnetRow)
									{
										BnetRow singleRow = (BnetRow) node;
										singleRow.setProgress(x);
									}
								}
							}
						});
						try {Thread.sleep(100);} catch (InterruptedException e1) {}
					}
				}
			});
		});
		

		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100);
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(10);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(60);
		top.getRowConstraints().add(r);
		top.getColumnConstraints().addAll(c,c,c1,c,c);
		GridPane.setConstraints(expandedButton, 	0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(chooseButton, 		1, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(versionLabel, 		2, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(deployButton, 		3, 0, 1, 1, HPos.CENTER,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(allCheckedButton, 	4, 0, 1, 1, HPos.RIGHT,  VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		top.getChildren().addAll(expandedButton, chooseButton,  versionLabel, deployButton, allCheckedButton);
		setTop(top);
	}
	
	public void addBnet(Bnet bnet)
	{
		BnetRow bnetRow = new BnetRow(bnet);
		bnetRow.setExpanded(isExpanded);
		bnetRows.add(bnetRow);
	}
	
	public void removeBnet(Bnet bnet)
	{
		for (Node node : bnetRows)
		{
			if (node instanceof BnetRow)
			{
				BnetRow bnetRow = (BnetRow) node;
				if(bnetRow.getBnet().equals(bnet))
				{
					bnetRows.remove(bnetRow);
				}
			}
		}
	}
}
