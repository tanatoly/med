package com.rafael.med.debrief;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleNode;
import com.rafael.med.AppManager;
import com.rafael.med.common.AudioUtils;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.common.bnet.ChannelType;
import com.rafael.med.debrief.TelemetryData.Atile;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PlayerView extends BorderPane
{
	private static final Logger log = LogManager.getLogger();
	
	private static final int _60 						= 60;
	
	private static final String CURRENT_WAV 			= "current-%s.wav";
	private static final double PIXEL_PER_SECOND 		= 10;
	public static final double PIXEL_PER_MILLISECOND   	= PIXEL_PER_SECOND / 1000;
	private static final int CANVAS_HEIGHT 				= 80;
	public static final double TILE_WIDTH  				= 104;
	public static final double TILE_HEIGHT 				= 80;
	
	
	public MediaPlayer mediaPlayer;
	public JFXCheckBox audioEnable;
	private JFXCheckBox muteDisable;
	
	private final File		wavFile;
	private final String	wavFileUri;
	private ScrollPane		canvasScroll;
	private Canvas			canvas;
	private GraphicsContext gc;
	private ScrollBar graphScrollBar;
	private Timeline animation;
	private XYChart.Data<String,Number>[] series1Data;
	private double canvasW;
	private double canvasH;
	
	private int durationInSeconds;
	private HBox tiles;

	private RowData rowData;
	
	private AtomicBoolean 	isCreated	  = new AtomicBoolean(false);

	private RowResult rowResult;
	private DebriefView debrief;

	private JFXToggleNode cutStart;

	private JFXToggleNode cutStop;

	private LocalDateTime startTime;

	private LocalDateTime cutStartTime;
	private LocalDateTime cutStopTime;

	private AppDebrief appDebrief;
	
		
	public PlayerView(DebriefView debrief,AppDebrief appDebrief, File dir,RowData rowData, LocalDateTime startTime, int durationInSeconds) 
	{
		this.appDebrief					= appDebrief;
		this.debrief					= debrief;
		this.startTime					= startTime;
		this.durationInSeconds 			= durationInSeconds;
		this.canvasW					= durationInSeconds * PIXEL_PER_SECOND;
		this.canvasH					= CANVAS_HEIGHT;
		this.rowData					= rowData;
		wavFile 						= new File(String.format(CURRENT_WAV, dir.getName()));
		wavFileUri						= wavFile.toURI().toString();
		canvas							= new Canvas(canvasW, canvasH);
		canvasScroll					= new ScrollPane(canvas);
		gc 								= canvas.getGraphicsContext2D();
	
		tiles = new HBox(6);
		tiles.setAlignment(Pos.CENTER);
		
		BarChart<String,Number> audioChart	= createAudioBarChart();
		audioChart.setMinWidth(300);
		
		BorderPane audioPane = new BorderPane();
		
		cutStart 	= ViewUtils.jfxtogglebutton(null, FontAwesomeIcon.CARET_SQUARE_ALT_RIGHT, 30, 30, Color.BLACK, Color.WHITE, Color.GREEN, null,2);
		cutStart.setOnAction(e->
		{
			cutStop.setDisable(false);
			if(mediaPlayer != null)
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				cutStartTime = startTime.plusSeconds((long) currentTime.toSeconds());
				String cutStartTimeStr = cutStartTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss);
				cutStart.setTooltip(new Tooltip(cutStartTimeStr));
			}
		});
		
		cutStop 	= ViewUtils.jfxtogglebutton(null, FontAwesomeIcon.SAVE, 30, 30, Color.BLACK, Color.WHITE, Color.GREEN, null,2);
		cutStop.setOnAction(e->
		{
			if(mediaPlayer != null)
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				cutStopTime = startTime.plusSeconds((long) currentTime.toSeconds());
				String cutStopTimeStr = cutStopTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss);
				cutStop.setTooltip(new Tooltip(cutStopTimeStr));
				
				createCut(cutStartTime, cutStopTime);
			}
			
			cutStop.setSelected(false);
			cutStop.setDisable(true);
			cutStart.setSelected(false);
			
			
		});
		
		cutStart.setDisable(true);
		cutStop.setDisable(true);
		
		VBox cutBox = new VBox(ViewUtils.vspace(), cutStart,ViewUtils.vspace(), cutStop ,ViewUtils.vspace());
		cutBox.setPadding(new Insets(4));
		
		
		audioPane.setLeft(cutBox);
		audioPane.setCenter(canvasScroll);
		audioPane.setRight(audioChart);
		audioPane.setMaxHeight(CANVAS_HEIGHT + 10);
				
		ScrollPane tilesScroll 			= new ScrollPane(tiles);
		
		canvasScroll.setMinHeight(CANVAS_HEIGHT + 20);
		canvasScroll.setMaxHeight(CANVAS_HEIGHT + 20);
	
		tilesScroll.setMinHeight(TILE_HEIGHT + 20);
		tilesScroll.setMaxHeight(TILE_HEIGHT + 20);
		
		
		setMinHeight(canvasScroll.getMinHeight() + tilesScroll.getMinHeight() + 50);
		setMaxHeight(canvasScroll.getMaxHeight() + tilesScroll.getMaxHeight() + 50);
		
			
		VBox center = new VBox();
		center.setPadding(new Insets(2, 6, 2, 6));
		center.getChildren().addAll(audioPane,  tilesScroll);	
		setCenter(center);
		
		VBox left = createLeft(dir);
		left.setMinWidth(200);
		left.setMaxWidth(200);
		setLeft(left);		
		
		setStyle("-fx-border-color:-color-90; -fx-border-width: 1 1 1 1;");
	}

	
	private void createCut(LocalDateTime cutStartTime, LocalDateTime cutStopTime) 
	{
		long fromStartToStartSeconds 	= java.time.Duration.between(startTime, cutStartTime).toSeconds();
		long fromStartToStopSeconds 	= java.time.Duration.between(startTime, cutStopTime).toSeconds();
		
		int indexFrom = (int) (rowResult.bytesPerSecond * fromStartToStartSeconds);
		int indexTo   = (int) (rowResult.bytesPerSecond * fromStartToStopSeconds);
		
		int size = indexTo - indexFrom;
		byte[] cutArray = new byte[size];
		
		System.arraycopy(rowResult.mixerBuffer.array(), indexFrom, cutArray, 0, size);
		
		try 
		{
			saveWavFile(cutArray, new File((cutStartTime.format(Utilities.DATE_TIME_PATTERN_1) + "---" + cutStopTime.format(Utilities.DATE_TIME_PATTERN_1))+ ".wav"));
		}
		catch (IOException e1) 
		{
			AppManager.INSTANCE.showError(AppDebrief.class, log, "FAILED SAVE CUT WAV FILE : ", e1);
		}
		
	}


	private VBox createLeft(File dir)
	{
		VBox vbox 	= new VBox();
		Button removeButton 	= ViewUtils.jfxbutton(null, FontAwesomeIcon.REMOVE, 30, 30, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "",2);
		removeButton.setOnAction(e->
		{
			appDebrief.removeDirectory(dir);
		});
		
		Label label = new Label(dir.getName());
		label.setAlignment(Pos.CENTER_LEFT);
		label.setStyle("-fx-font-size: 16;-fx-font-weight: BOLD;-fx-text-fill: white;");
		vbox.getChildren().add(new HBox(label,ViewUtils.hspace(),removeButton));
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(ViewUtils.vspace());
		
		audioEnable 	= new JFXCheckBox();
		audioEnable.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				isCreated.set(false);
			}
		});
		
		
		
		Label l1 = new Label("audio");
		l1.setStyle("-fx-font-size: 14; -fx-font-weight: BOLD;-fx-text-fill: white;");
		HBox audioBox = new HBox(10,l1,ViewUtils.hspace(),audioEnable);
		audioBox.setMaxWidth(Double.MAX_VALUE);
		audioBox.setAlignment(Pos.CENTER_LEFT);
		
		
		muteDisable 	= new JFXCheckBox();
		muteDisable.selectedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				if(mediaPlayer != null)
				{
					mediaPlayer.setMute(newValue);
				}
			}
		});
		
		
		
		Label l2 = new Label("mute");
		l2.setStyle("-fx-font-size: 14; -fx-font-weight: BOLD;-fx-text-fill: white;");
		HBox muteBox = new HBox(10,l2,ViewUtils.hspace(),muteDisable);
		muteBox.setMaxWidth(Double.MAX_VALUE);
		muteBox.setAlignment(Pos.CENTER_LEFT);
		
		vbox.getChildren().addAll(audioBox,muteBox);
		
		for (ChannelType channelType : ChannelType.values())
		{
			HBox hbox = new HBox(10);
			hbox.setMaxWidth(Double.MAX_VALUE);
			hbox.setAlignment(Pos.CENTER_LEFT);
			
			Label l = new Label(channelType.name());
			l.setStyle("-fx-font-size: 10; -fx-font-weight: BOLD;-fx-text-fill: white;");
			
			Rectangle g = new Rectangle(100,4,channelType.color);
			hbox.getChildren().addAll(l,ViewUtils.hspace(),g);
			
			vbox.getChildren().add(hbox);
			vbox.getChildren().add(ViewUtils.vspace());
		}	
		vbox.setPadding(new Insets(10,6,10,6));
		vbox.setStyle("-fx-background-color : -color-30;-fx-border-color: -color-90;-fx-border-width: 0 1 0 0;");
		return vbox;
	}
	
	

	@SuppressWarnings("unchecked")
	private BarChart<String, Number> createAudioBarChart()
	{
        final CategoryAxis xAxis 	= new CategoryAxis();
        final NumberAxis yAxis 		= new NumberAxis(0, 50, 10);        
        final BarChart<String, Number> barchart = new BarChart<>(xAxis, yAxis);
        barchart.setLegendVisible(false);
        barchart.setAnimated(false);
        barchart.setBarGap(0);
        barchart.setCategoryGap(0);
        barchart.setHorizontalGridLinesVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1Data = new XYChart.Data[128];
       
        for (int i = 0; i < series1Data.length; i++) 
        {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 1), 50);
            series1.getData().add(series1Data[i]);
        }
        barchart.getData().add(series1);
        return barchart;
	}
	
	public void reset(LocalDateTime startTime, LocalDateTime stopTime) throws Exception
	{
		this.rowResult 		= rowData.mixerChannels(startTime, stopTime);	
		for (ChannelResult channelResult : rowResult.channelResults)
		{		
			if(channelResult != null)
			{
				channelResult.lines = AudioUtils.generateAudioGraphLines(channelResult.audioByteArray, AppDebrief.AUDIO_FORMAT, canvasW, canvasH);
			}
		}
		
		drawGraph();
		
		rowResult.telemetry.resetTiles();
		
		tiles.getChildren().clear();
		//tiles.getChildren().add(ViewUtils.hspace());
		
		
		for (Atile tile : rowResult.telemetry.tilesMap.values())
		{
			tiles.getChildren().add(tile);
		}
		
		
//		for (int i = 0; i < 10; i++)
//		{
//			tiles.getChildren().add(new Atile());
//		}
		
		for (int i = 0; i < series1Data.length; i++)
		{
			series1Data[i].setYValue(0);
		}
		
		if(mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.dispose();
			mediaPlayer = null;
		}
		if(animation != null)
		{
			animation.stop();
		}
		isCreated.set(false);
		audioEnable.setSelected(false);
		
		cutStart.setSelected(false);
		cutStop.setSelected(false);
		cutStart.setDisable(true);
		cutStop.setDisable(true);
	}
	
	public void saveWavFile(byte[] array, File wavFile) throws IOException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(array);
		AudioInputStream audioOutputStream = new AudioInputStream(in, AppDebrief.AUDIO_FORMAT, array.length/AppDebrief.AUDIO_FORMAT.getFrameSize());
		AudioSystem.write(audioOutputStream, AudioFileFormat.Type.WAVE, wavFile);
		in.close();
		audioOutputStream.close();
	}
	
	private void drawGraph()
	{	
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (ChannelResult channelResult : rowResult.channelResults)
		{		
			if(channelResult != null)
			{
				gc.setStroke(channelResult.type.color);
				if(channelResult.lines != null && !channelResult.lines.isEmpty())
				{
					for (Line line : channelResult.lines)
					{
						gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
					}
				}
			}
		}
		gc.setStroke(Color.WHITE);
		for (int i = 0; i < canvasW / _60 /PIXEL_PER_SECOND; i ++)
		{
			double x = i  * _60 * PIXEL_PER_SECOND; 
			gc.strokeLine(x ,0, x, canvasH);
		}
		gc.setFill(Color.color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 0.5));
	}

	public void preparePlaying() throws Exception 
	{
		audioEnable.setDisable(true);
		if(isCreated.compareAndSet(false, true))
		{
			graphScrollBar = ViewUtils.getNodeScrollBar(canvasScroll, Orientation.HORIZONTAL);
			graphScrollBar.setVisible(false);
			animation = new Timeline(new KeyFrame(Duration.seconds(durationInSeconds), new KeyValue(graphScrollBar.valueProperty(), 1)));
			mediaPlayer = null;
			if(audioEnable.isSelected())
			{
				saveWavFile(rowResult.mixerBuffer.array(),wavFile);
				createMediaPlayer();
			}
		}
		if(graphScrollBar !=null)
		{
			graphScrollBar.setValue(0.0);
			graphScrollBar.setVisible(false);
		}
	}

	private void createMediaPlayer()
	{
		mediaPlayer 	= new MediaPlayer(new Media(wavFileUri));
		
		mediaPlayer.setOnEndOfMedia(() -> 
		{
			debrief.onMediaEnd(PlayerView.this,mediaPlayer);
		});
		mediaPlayer.setOnReady(() -> 
		{
			debrief.onMediaReady(PlayerView.this,mediaPlayer);
		});
		mediaPlayer.setOnPlaying(() -> 
		{
			mediaPlayer.setStartTime(Duration.ZERO);
			mediaPlayer.setStopTime(mediaPlayer.getTotalDuration());
			debrief.onMediaPlaying(PlayerView.this,mediaPlayer);
		});
		mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener()
		{
			@Override
			public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases)
			{
				
				if(!muteDisable.isSelected())
				{
					for (int i = 0; i < series1Data.length; i++)
					{
						series1Data[i].setYValue(magnitudes[i] + _60);
					}
				}
			}
		});
		
		mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) ->
		{
			debrief.onMediaCurrentTime(PlayerView.this,mediaPlayer,oldValue,newValue);
		});
	}

	
	
	public void onStopOrEnd()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.seek(Duration.ZERO);
			mediaPlayer.stop();
		}
		if(animation != null)
		{
			animation.jumpTo(Duration.ZERO);
			animation.stop();
		}
		if(graphScrollBar != null)
		{
			graphScrollBar.setValue(0.0);
		}
		audioEnable.setDisable(false);
		
		cutStart.setSelected(false);
		cutStop.setSelected(false);
		cutStart.setDisable(true);
		cutStop.setDisable(true);
	}
	
	public void stop()
	{
		onStopOrEnd();
	}

	public void play()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.play();
		}
		if(animation != null)
		{
			animation.play();
		}
		
		cutStart.setDisable(false);
	}

	public void pause()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.pause();
		}
		if(animation != null)
		{
			animation.pause();
		}
	}

	public void goToTime(Duration toTime)
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.seek(toTime);
		}
		if(animation != null)
		{
			animation.jumpTo(toTime);
		}
	}
	
	public void onTickTime(long currentTimeInSeconds,double x_new)
	{
		drawGraph();
		gc.fillRect(0, 0, x_new, canvas.getHeight());
		rowResult.telemetry.setTime(currentTimeInSeconds);
	}
}