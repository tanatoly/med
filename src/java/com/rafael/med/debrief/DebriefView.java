package com.rafael.med.debrief;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.rafael.med.AppManager;
import com.rafael.med.common.AudioUtils;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class DebriefView extends BorderPane
{
	private static final double SHIFT_IN_SECONDS 		= 10;
	
	private Duration 	mediaDuration;
	private int 					durationInSeconds;
	private Map<File, RowData> 		rowsMap;
	
	private Label 				fromTime;
	private Label 				toTime;
	private Label 				playTime;
	private Slider 				timeSlider;

	private Map<File, PlayerView> map = new HashMap<>();
	private VBox vbox;
	private MediaPlayer mainMediaPlayer;
	private double x_prev = 0;
	private LocalDateTime startTime;
	private AppDebrief appDebrief;
	
	public DebriefView(AppDebrief appDebrief, int durationInSeconds, Map<File, RowData> rowsMap)
	{
		this.appDebrief				= appDebrief;
		this.durationInSeconds 		= durationInSeconds;
		this.rowsMap				= rowsMap;
		
		HBox timeTop = createTop();
		timeTop.setMinHeight(30);
		timeTop.setMaxHeight(30);
		setTop(timeTop);
		this.vbox = new VBox(4);
		
		ScrollPane scrollPane = new ScrollPane(vbox);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		setCenter(scrollPane);
		scrollPane.setStyle("-fx-background-color :  -color-60;");

	}

	private HBox createTop()
	{
		fromTime 		= new Label();
		fromTime.setId("date-label");
		toTime			= new Label();
		toTime.setId("date-label");
		
		timeSlider	= createSlider();
		playTime	= createTimeLabel();
		HBox.setHgrow(timeSlider, Priority.ALWAYS);
		
		HBox result = new HBox(ViewUtils.hspace(20),fromTime,ViewUtils.hspace(),timeSlider,playTime,ViewUtils.hspace(),toTime,ViewUtils.hspace(20));
		result.setAlignment(Pos.CENTER);
		return result;
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
                	for (PlayerView playerView :  map.values())
            		{
            			playerView.goToTime(multiply);
            		}
                }
                updateValues();
            }
        });
        return timeSlider;
	}
	

	private Label createTimeLabel()
	{
		Label playTime = new Label("00:00");
        playTime.setMinWidth(Control.USE_PREF_SIZE);
        playTime.setTextFill(Color.WHITE);
        return playTime;
	}
	private void updateValues()
	{
		if (playTime != null && timeSlider != null && mediaDuration != null)
		{
			Platform.runLater(() ->
			{
				Duration currentTime = getCurrentTime();
				playTime.setText(AudioUtils.formatTime(currentTime, mediaDuration));
				if (!timeSlider.isDisabled() && mediaDuration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
				{
					timeSlider.setValue(currentTime.divide(mediaDuration.toMillis()).toMillis() * 100.0);
				}
			});
		}
	}
	
	public void onShow(LocalDateTime startTime) throws Exception
	{
		this.startTime = startTime;
		fromTime.setText(startTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		LocalDateTime stopTime = startTime.plusSeconds(durationInSeconds);
		toTime.setText(stopTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		
		map.clear();
		vbox.getChildren().clear();
		
		for (RowData rowData : rowsMap.values())
		{
			if(rowData != null)
			{
				File dir = rowData.directory;
				PlayerView playerView = new PlayerView(this,appDebrief, dir, rowData, startTime,durationInSeconds);
				map.put(dir, playerView);
				vbox.getChildren().add(playerView);
				playerView.reset(startTime,stopTime);
			}
		}
		timeSlider.setDisable(true);
		
		appDebrief.playButton.setDisable(false);
		appDebrief.stopButton.setDisable(true);
		appDebrief.pauseButton.setDisable(true);
		appDebrief.backButton.setDisable(true);
		appDebrief.forwardButton.setDisable(true);
	}
	
	private Duration getCurrentTime()
	{
		if(mainMediaPlayer == null)
		{
			throw new RuntimeException("UNEXPECTED ERROR : mainMediaPlayer == null");
		}
		return mainMediaPlayer.getCurrentTime();
	}
	
	
	public void stopAll()
	{
		timeSlider.setDisable(true);
		timeSlider.setValue(0);
		for (PlayerView playerView : map.values())
		{
			playerView.stop();
		}
		
		appDebrief.playButton.setDisable(false);
		appDebrief.stopButton.setDisable(true);
		appDebrief.pauseButton.setDisable(true);
		appDebrief.backButton.setDisable(true);
		appDebrief.forwardButton.setDisable(true);
	}

	public void playAll() throws Exception
	{
		boolean isOneAudioExist = false;
		
		this.mainMediaPlayer = null;
		for (PlayerView playerView :  map.values())
		{
			playerView.preparePlaying();
			isOneAudioExist = isOneAudioExist || playerView.audioEnable.isSelected();
			if(mainMediaPlayer == null && playerView.audioEnable.isSelected())
			{
				mainMediaPlayer = playerView.mediaPlayer;
			}
		}
		
		if(!isOneAudioExist)
		{
			for (PlayerView playerView :  map.values())
			{
				playerView.audioEnable.setDisable(false);
			}
			AppManager.INSTANCE.showInfo(AppDebrief.class,"SPECIFY AT LEAST ONE AUDIO");
			return;
		}
		timeSlider.setValue(0.0);
		for (PlayerView playerView :  map.values())
		{
			playerView.play();
		}
		timeSlider.setDisable(false);
		
		appDebrief.playButton.setDisable(true);
		appDebrief.stopButton.setDisable(false);
		appDebrief.pauseButton.setDisable(false);
		appDebrief.backButton.setDisable(false);
		appDebrief.forwardButton.setDisable(false);
	}

	public void pauseAll()
	{
		for (PlayerView playerView :  map.values())
		{
			playerView.pause();
		}
		timeSlider.setDisable(true);
		
		appDebrief.playButton.setDisable(false);
		appDebrief.stopButton.setDisable(false);
		appDebrief.pauseButton.setDisable(true);
		appDebrief.backButton.setDisable(true);
		appDebrief.forwardButton.setDisable(true);
	}

	public void backAll()
	{
		Duration currentTime = getCurrentTime();
		Duration seconds = Duration.seconds(currentTime.toSeconds() - SHIFT_IN_SECONDS);
		
		for (PlayerView playerView :  map.values())
		{
			playerView.goToTime(seconds);
		}
	}

	public void forwardAll()
	{
		Duration currentTime = getCurrentTime();
		Duration seconds = Duration.seconds(currentTime.toSeconds() + SHIFT_IN_SECONDS);
		
		for (PlayerView playerView :  map.values())
		{
			playerView.goToTime(seconds);
		}
	}
	
	public void onMediaEnd(PlayerView playerView, MediaPlayer mediaPlayer)
	{
		for (PlayerView currentPlayer :  map.values())
		{
			currentPlayer.onStopOrEnd();
		}
		timeSlider.setDisable(true);
		timeSlider.setValue(0);
	}

	public void onMediaReady(PlayerView playerView, MediaPlayer mediaPlayer)
	{
		if(mainMediaPlayer == mediaPlayer)
		{
			mediaDuration = mainMediaPlayer.getMedia().getDuration();
			updateValues();
		}
	}

	public void onMediaPlaying(PlayerView playerView, MediaPlayer mediaPlayer)
	{
	}

	public void onMediaCurrentTime(PlayerView playerView, MediaPlayer mediaPlayer, Duration oldValue, Duration newValue)
	{
		if(mainMediaPlayer == mediaPlayer)
		{
			updateValues();
			
			double x_new = PlayerView.PIXEL_PER_MILLISECOND * newValue.toMillis();
			double delta =  Math.abs(x_new - x_prev);
	
			if(delta >= 1)
			{
				LocalDateTime currentTime = startTime.plusSeconds((long) newValue.toSeconds());
				long currentTimeInSeconds = currentTime.toEpochSecond(AppDebrief.ZONE_OFFSET);
				for (PlayerView currentPlayer :  map.values())
				{
					currentPlayer.onTickTime(currentTimeInSeconds, x_new);
				}
				x_prev = x_new;
			}
		}
	}
}
