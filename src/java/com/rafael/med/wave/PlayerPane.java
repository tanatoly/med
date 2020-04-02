package com.rafael.med.wave;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.AppManager;
import com.rafael.med.common.AudioUtils;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.VoiceSegment;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class PlayerPane extends GridPane 
{
	private static final Logger log = LogManager.getLogger();
	
	private static final int CANVAS_HEIGHT 				= 60;
	private static final double SHIFT_IN_SECONDS 		= 2;

	

	
	
	private Canvas canvas;
	private GraphicsContext gc;
	private Slider timeSlider;	
	private ScrollPane canvasScroll;
	private MediaPlayer 		mediaPlayer;
	private Duration 			mediaDuration;
	
	
	private double x_prev = 0;
	
	private Label playTimeLabel;
	private Label fromTimeLabel;
	private Label toTimeLabel;
	

	private List<Line> graph;
	private double canvasW;
	private double prevCanvasW;
	private double canvasH = CANVAS_HEIGHT;
	private Button backButton;
	private Button stopButton;
	private Button playButton;
	private Button pauseButton;
	private Button forwardButton;

	private double pixelPerMillis 	= 0;
	public File waveFile;
	
	public PlayerPane()
	{	
		this.fromTimeLabel 	= new Label();
		this.toTimeLabel 	= new Label();
		this.playTimeLabel 	= new Label();
		
		
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(30);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(30);
		RowConstraints r3 = new RowConstraints();
		r3.setPercentHeight(40);
		getRowConstraints().add(r1);
		getRowConstraints().add(r2);
		getRowConstraints().add(r3);
		
		
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(30);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(40);
		getColumnConstraints().addAll(c1,c2,c1);
		
		backButton = new Button("Back");
		backButton.setId("back-button");
		backButton.setOnAction(e ->
		{
			Duration currentTime = mediaPlayer.getCurrentTime();;
			Duration seconds = Duration.seconds(currentTime.toSeconds() - SHIFT_IN_SECONDS);
			goToTime(seconds);
		});
		forwardButton = new Button("Forward");
		forwardButton.setId("forward-button");
		forwardButton.setOnAction(e ->
		{
			Duration currentTime = mediaPlayer.getCurrentTime();
			Duration seconds = Duration.seconds(currentTime.toSeconds() + SHIFT_IN_SECONDS);
			goToTime(seconds);
		});
		
		stopButton = new Button("Stop");
		stopButton.setId("stop-button");
		stopButton.setOnAction(e ->
		{
			onStopOrEnd();
		});
		playButton = new Button("Play");
		playButton.setId("play-button");
		playButton.setOnAction(e ->
		{
			if(mediaPlayer != null)
			{
				mediaPlayer.play();
				timeSlider.setDisable(false);
			}
		});
		
		
		pauseButton = new Button("Pause");
		pauseButton.setId("pause-button");
		pauseButton.setOnAction(e ->
		{
			if(mediaPlayer != null)
			{
				mediaPlayer.pause();
			}
		});
		
		
		

		HBox buttonPane = new HBox(backButton, stopButton, playButton, pauseButton, forwardButton);
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setMaxHeight(56);
		buttonPane.setMinHeight(56);
				
		timeSlider	= createSlider();

		canvasScroll = new ScrollPane();
		canvasScroll.setStyle("-fx-background-color :  black;");
		canvasScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		canvasScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		GridPane.setConstraints(buttonPane, 		1, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(fromTimeLabel, 		0, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(toTimeLabel, 		2, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(timeSlider, 		0, 0 , 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(playTimeLabel, 		2, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(canvasScroll, 			0, 1 , 3, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		
		getChildren().addAll(canvasScroll,fromTimeLabel,toTimeLabel,timeSlider ,playTimeLabel,buttonPane);
		
		disablePlayer(true);
	}
	
	public void disablePlayer(boolean isDisable)
	{	
		this.fromTimeLabel.setText(null);
		this.toTimeLabel.setText(null);
		this.playTimeLabel.setText(null);
		
		timeSlider.setDisable(isDisable);
		playButton.setDisable(isDisable);
		pauseButton.setDisable(isDisable);
		stopButton.setDisable(isDisable);
		forwardButton.setDisable(isDisable);
		backButton.setDisable(isDisable);
	}
	
	private Slider createSlider()
	{
		Slider timeSlider = new Slider();
		timeSlider.setId("media-slider");
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.prefWidthProperty().bind(this.widthProperty().divide(2));
		timeSlider.valueProperty().addListener((Observable ov) -> 
        {
            if (timeSlider.isValueChanging()) 
            {
                if (mediaDuration != null) 
                {
                	Duration multiply = mediaDuration.multiply(timeSlider.getValue() / 100.0);
                	goToTime(multiply);
                }
                updateValues();
            }
        });
        return timeSlider;
	}
	
	private void drawGraph()
	{	
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setStroke(Color.CYAN);
		if(graph != null && !graph.isEmpty())
		{
			for (Line line : graph)
			{
				gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
			}
		}
		gc.setStroke(Color.WHITE);
		gc.setFill(Color.color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 0.5));
	}
	
		
	
	public void reset(File waveFile, long durationInMillis, byte[] audioByteArray, LocalDateTime fromTime, LocalDateTime toTime,int canvasWidth)
	{
		try 
		{
			this.waveFile = waveFile;
			if(canvasWidth > 0)
			{
				this.canvasW = canvasWidth;
			}
			else
			{
				this.canvasW				= canvasScroll.getWidth() - 2;
			}
			this.pixelPerMillis			= canvasW/(durationInMillis);
			this.mediaPlayer 			= new MediaPlayer(new Media(waveFile.toURI().toString()));

			disablePlayer(false);
			
			this.fromTimeLabel.setText(fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			this.toTimeLabel.setText(toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
			this.playTimeLabel.setText(AudioUtils.formatTime(Duration.ZERO, Duration.seconds(durationInMillis/1000)));
			
			
			if(this.canvas == null || prevCanvasW != canvasW)
			{
				//System.out.println("-  -------------- canvasW = " +canvasW + " prev = " + prevCanvasW);
				this.canvas		= new Canvas(canvasW, canvasH);
				this.gc 			= canvas.getGraphicsContext2D();  
				this.canvasScroll.setContent(canvas);
				this.prevCanvasW = canvasW;
			}
			
			this.graph = AudioUtils.generateAudioGraphLines(audioByteArray, VoiceSegment.AUDIO_FORMAT, canvasW, canvasH);
			
			this.mediaPlayer.setOnEndOfMedia(() -> 
			{
				onStopOrEnd();
			});
			
			this.mediaPlayer.setOnReady(() -> 
			{
				mediaDuration = mediaPlayer.getMedia().getDuration();
				updateValues();
			});
			
			this.mediaPlayer.setOnPlaying(() -> 
			{
				mediaPlayer.setStartTime(Duration.ZERO);
				mediaPlayer.setStopTime(mediaPlayer.getTotalDuration());
			});
					
			this.mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) ->
			{
				updateValues();
				double x_new = pixelPerMillis * newValue.toMillis();
				double delta =  Math.abs(x_new - x_prev);
				if(delta >= 1)
				{
					drawGraph();
					gc.fillRect(0, 0, x_new, canvas.getHeight());
					x_prev = x_new;
				}
			});
			drawGraph();
		} 
		catch (Exception e) 
		{
			AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", e);
		}
	}
	
	private void updateValues()
	{
		if (mediaDuration != null)
		{
			Platform.runLater(() ->
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				playTimeLabel.setText(AudioUtils.formatTime(currentTime, mediaDuration));
				if (!timeSlider.isDisabled() && mediaDuration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
				{
					timeSlider.setValue(currentTime.divide(mediaDuration.toMillis()).toMillis() * 100.0);
				}
			});
		}
	}
	
	public void onStopOrEnd()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.seek(Duration.ZERO);
			mediaPlayer.stop();
			timeSlider.setDisable(true);
			timeSlider.setValue(0);
		}
	}
	
	public void goToTime(Duration toTime)
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.seek(toTime);
		}
	}
}
