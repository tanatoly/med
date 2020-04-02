package com.rafael.med.debrief;

import java.io.File;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.AppBase;
import com.rafael.med.AppManager;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AppDebrief extends AppBase 
{
	private static final Logger log = LogManager.getLogger();
	
	
	public static final AudioFormat 	AUDIO_FORMAT	= new AudioFormat(8000,16,1,true,false);
	public static final ZoneOffset	ZONE_OFFSET			= OffsetDateTime.now().getOffset();
	
	public static final int 		TABLE_DAYS 			= 100;
	public static final int 		TABLE_HOURS 		= 26;
	public static final int 		TABLE_MINUTES 		= 70;
	public static  int 				TABLE_SECONDS 		= 0;
	

	public DebriefView 		debrifView;
	public TablesView 		tablesView;
	public Pane 			firstView;

	public JFXButton 		addButton;
	public JFXButton 		upButton;
	public JFXButton 		playButton;
	public JFXButton 		stopButton;
	public JFXButton 		backButton;
	public JFXButton 		forwardButton;
	public JFXButton 		pauseButton;


	
	private  Map<File, RowData>		rowsMap		= new LinkedHashMap<>();
	private  List<Button> debriefButtons 		= new ArrayList<>();
		
	private BorderPane main;
	
	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args) 
	{
		main = new BorderPane();

		main.setStyle("-fx-background-color :  -color-30;");
		
//		Pane top 				= buildTop();
//		top.setMinHeight(34);
//		top.setMaxHeight(34);
//		main.setTop(top);
		
		Pane bottom				= buildBottom();
		bottom.setMinHeight(34);
		bottom.setMaxHeight(34);
		BorderPane.setMargin(bottom, new Insets(4, 8, 8, 8));
		bottom.setStyle("-fx-background-color :  -color-60;");
		//setBottom(bottom);
		
		Pane left				= buildLeft();
		left.setMinWidth(80);
		left.setMaxWidth(80);
		BorderPane.setMargin(left, new Insets(4, 4, 4, 8));
		left.setStyle("-fx-background-color :  -color-60;");
		main.setLeft(left);
		
		StackPane center		= new StackPane();		
		BorderPane.setMargin(center, new Insets(4, 8, 4, 4));
		center.setStyle("-fx-background-color :  -color-60;");
		main.setCenter(center);

		int durationInSeconds		= configuration.getInteger("durationInSeconds", 360);
		TABLE_SECONDS 				= durationInSeconds + 1;
		
		debrifView					= new DebriefView(this, durationInSeconds ,rowsMap);
	    tablesView 					= new TablesView(this, durationInSeconds,  rowsMap);
	    firstView					= createFirstView();
		
	    center.setPadding(new Insets(6));	    
		center.getChildren().addAll(firstView,debrifView,tablesView);
		center.setStyle("-fx-background-color :  -color-40;");
		
		StackPane.setAlignment(debrifView, Pos.CENTER);
		StackPane.setAlignment(tablesView, Pos.CENTER);
		
		
		
		
		debrifView.visibleProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				addButton.setDisable(newValue);
			}
		});
				
		debrifView.setVisible(false);
		tablesView.setVisible(false);
		
		firstView.setVisible(true);
		upButton.setDisable(true);	
		
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		backButton.setDisable(true);
		forwardButton.setDisable(true);
		playButton.setDisable(true);
		
		return main;
	}
	
	private Pane createFirstView()
	{
		Label label = new Label("Add folder with unit data");
		label.setStyle("-fx-font-size: 60;-fx-text-fill: white;");
		BorderPane pane = new BorderPane(label);
		return pane;
	}

	
	private Pane buildBottom()
	{
		HBox pane = new HBox();
		pane.setAlignment(Pos.CENTER_RIGHT);
		return pane;
	}
	
	private Pane buildLeft()
	{
		VBox pane = new VBox(10);
		pane.setAlignment(Pos.TOP_CENTER);
		pane.getChildren().add(ViewUtils.vspace(0));
		pane.getChildren().add(addButton = ViewUtils.jfxbutton(null, FontAwesomeIcon.FOLDER_OPEN, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "add",2));
		pane.getChildren().add(upButton  = ViewUtils.jfxbutton(null, FontAwesomeIcon.ARROW_UP, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "uo",2));
		pane.getChildren().add(ViewUtils.vspace());
		
		pane.getChildren().add(playButton 	= ViewUtils.jfxbutton(null, FontAwesomeIcon.PLAY, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "play",2));
		pane.getChildren().add(stopButton  	= ViewUtils.jfxbutton(null, FontAwesomeIcon.STOP, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "stop",2));
		pane.getChildren().add(pauseButton  = ViewUtils.jfxbutton(null, FontAwesomeIcon.PAUSE, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "pause",2));
		
		pane.getChildren().add(backButton 	= ViewUtils.jfxbutton(null, FontAwesomeIcon.FAST_BACKWARD, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "back",2));
		pane.getChildren().add(forwardButton  = ViewUtils.jfxbutton(null, FontAwesomeIcon.FAST_FORWARD, 60, 60, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "forward",2));
		
		pane.getChildren().add(ViewUtils.vspace());
		
		//pane.getChildren().add(ViewUtils.vspace(0));
		
		
		debriefButtons.add(addButton);
		debriefButtons.add(upButton);
		debriefButtons.add(playButton);
		debriefButtons.add(pauseButton);
		debriefButtons.add(stopButton);
		debriefButtons.add(backButton);
		debriefButtons.add(forwardButton);
		debriefButtons.add(backButton);
		debriefButtons.add(forwardButton);
		
		addButton.setOnAction(e -> 
		{
			
			try
			{
				if(rowsMap.size() == 6)
				{
					throw new Exception("You have reached maximum units");
				}
				
				File selectedir = AppManager.INSTANCE.showDirectoryChooser(AppDebrief.class);
				if(selectedir != null && selectedir.isDirectory() && selectedir.exists())
				{
					if(!rowsMap.containsKey(selectedir))
					{
						RowData data = new RowData(selectedir);
						rowsMap.put(selectedir, data);
						firstView.setVisible(false);
						tablesView.reset();
						tablesView.setVisible(true);
					}
					else
					{
						throw new Exception("Directory '"+ selectedir.getName() + "' already exists in the system");
					}
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		upButton.setOnAction(e -> 
		{
			try
			{
				if(tablesView.isVisible())
				{
					tablesView.actionUp();
				}
				else if(debrifView.isVisible())
				{
					debrifView.stopAll();
					playButton.setDisable(true);
					tablesView.setVisible(true);
					debrifView.setVisible(false);
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		playButton.setOnAction(e -> 
		{
			try
			{
				if(debrifView.isVisible())
				{
					debrifView.playAll();
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		pauseButton.setOnAction(e -> 
		{
			try
			{
				if(debrifView.isVisible())
				{
					debrifView.pauseAll();
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		stopButton.setOnAction(e -> 
		{
			try
			{
				if(debrifView.isVisible())
				{
					debrifView.stopAll();
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		backButton.setOnAction(e -> 
		{
			try
			{
				if(debrifView.isVisible())
				{
					debrifView.backAll();
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		forwardButton.setOnAction(e -> 
		{
			try
			{
				if(debrifView.isVisible())
				{
					debrifView.forwardAll();
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppDebrief.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		return pane;
	}
	
	
	void debriefButton(boolean isDisable, boolean isVisible)
	{
		for (Button button : debriefButtons)
		{
			button.setDisable(isDisable);
			button.setVisible(isVisible);
		}
	}
	
	void removeDirectory(File dir)
	{
		if(debrifView.isVisible())
		{
			debrifView.stopAll();
		}
		playButton.setDisable(true);
		debrifView.setVisible(false);
		rowsMap.remove(dir);
		tablesView.reset();
		if(rowsMap.size() > 0)
		{
			firstView.setVisible(false);
			tablesView.setVisible(true);
		}
		else
		{
			firstView.setVisible(true);
			tablesView.setVisible(false);
		}
	}
	
	void showDebrief(LocalDateTime startTime) throws Exception
	{	
		debrifView.onShow(startTime);
		tablesView.setVisible(false);
		debrifView.setVisible(true);		
	}
}
