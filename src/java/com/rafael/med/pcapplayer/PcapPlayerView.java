package com.rafael.med.pcapplayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.AppManager;
import com.rafael.med.common.AudioUtils;
import com.rafael.med.common.CapturedPacket;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ILBC;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.common.ILBC.Mode;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

public class PcapPlayerView extends BorderPane
{
	private static final Logger log = LogManager.getLogger();
	
	
	public static final AudioFormat 	AUDIO_FORMAT		= new AudioFormat(8000,16,1,true,false);
	
	private final File wavFile 			= new File("current.wav");
	private final ByteBuffer pcmBuffer	= ByteBuffer.allocate(15_000_000).order(ByteOrder.LITTLE_ENDIAN);
	
	private XYChart.Data<String, Number>[] seriesData;
	private MediaPlayer mediaPlayer;
	
	private Button backButton;
	private Button stopButton;
	private Button playButton;
	private Button pauseButton;
	private Button forwardButton;
	private Slider timeSlider;
	private Label playTime;
	private Duration duration;
	
	private  List<File> selectedFilesList 		= new ArrayList<>();
	private Map<File, FileItem> filesMap 		= new LinkedHashMap<>();
	private TreeSet<CapturedPacket> packetsSet 	= new TreeSet<>();
	
	private ListView<FileItem> files;


	
	private final class FileItem
	{
		private BooleanProperty isSelected = new SimpleBooleanProperty(false);
		public boolean isSelectedFile = false;
		private File file;
		
		public FileItem(File file)
		{
			this.file = file;
			isSelected.addListener(new ChangeListener<Boolean>()
			{
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
				{
					if(newValue != null)
					{
						reset();
						isSelectedFile = newValue.booleanValue();
						if(isSelectedFile)
						{
							selectedFilesList.add(file);
						}
						else
						{
							selectedFilesList.remove(file);
						}
						
						boolean isEmpty = selectedFilesList.isEmpty();
						
						timeSlider.setDisable(isEmpty);
						playButton.setDisable(isEmpty);
						stopButton.setDisable(isEmpty);
						pauseButton.setDisable(isEmpty);
						forwardButton.setDisable(isEmpty);
						backButton.setDisable(isEmpty);
					}
				}
			});
		}
		@Override
		public String toString()
		{
			return file.toString();
		}
	}
	
	public PcapPlayerView(AppPcapPlayer appPcapPlayer) 
	{
		

		Label label = new Label("PCAP PLAYER");
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-font-size: 16;-fx-font-weight: BOLD;-fx-text-fill: white;");
		HBox top = new HBox(label,ViewUtils.hspace());
		top.setPadding(new Insets(4, 10, 4, 10));
		
		Pane filePane = createFilesView();
		filePane.setMaxWidth(400);
		filePane.setMinWidth(400);
		
		setRight(filePane);
		setTop(top);
				
		BarChart<String, Number> barChart	= createAudioBarChart();
		Pane buttons 		= createButtonView();
		Pane controls		= createControlView();
		
		VBox center = new VBox(20, ViewUtils.vspace(100),  barChart,ViewUtils.vspace(), buttons, controls,ViewUtils.vspace(40));
		setCenter(center);
		
		setStyle("-fx-background-color : -color-50;");

		timeSlider.setDisable(true);
		playButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		forwardButton.setDisable(true);
		backButton.setDisable(true);	
		
		playButton.setOnAction(e ->
		{
			try
			{
				if(preparePlaying())
				{
					mediaPlayer.play();
				}
				else
				{
					throw new Exception("NO RTP PACKETS IN FILE");
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppPcapPlayer.class,log,"ON ACTION FAILED : ",ex);
			}
			
		});
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
        barchart.setCategoryGap(1);
        barchart.setVerticalGridLinesVisible(false);
        barchart.setTitle("Live Audio Spectrum Data");
        xAxis.setLabel("Frequency bands");
        yAxis.setLabel("Magnitudes");
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "dB"));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        seriesData = new XYChart.Data[128];
       
        for (int i = 0; i < seriesData.length; i++) 
        {
            seriesData[i] = new XYChart.Data<>(Integer.toString(i + 1), 50);
            series.getData().add(seriesData[i]);
        }
        barchart.getData().add(series);
        barchart.setMaxHeight(Double.MAX_VALUE);
        
        return barchart;
	}
	
	
	
	private Pane createButtonView() 
	{
		backButton = new Button("Back");
		backButton.setId("back-button");
		stopButton = new Button("Stop");
		stopButton.setId("stop-button");
		playButton = new Button("Play");
		playButton.setId("play-button");
		pauseButton = new Button("Pause");
		pauseButton.setId("pause-button");
		forwardButton = new Button("Forward");
		forwardButton.setId("forward-button");

		HBox pane = new HBox(backButton, stopButton, playButton, pauseButton, forwardButton);
		pane.setAlignment(Pos.CENTER);
		return pane;
	}
	private Pane createControlView()
	{
		BorderPane pane = new BorderPane();

		HBox seekBar = new HBox(20);
		seekBar.setAlignment(Pos.CENTER);

        Label timeLabel = new Label("Time");
        timeLabel.setMinWidth(Control.USE_PREF_SIZE);
        timeLabel.setTextFill(Color.WHITE);
        seekBar.getChildren().add(timeLabel);

        timeSlider = new Slider();
        timeSlider.setId("media-slider");
    
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener((Observable ov) -> 
        {
            if (timeSlider.isValueChanging()) 
            {
                if (duration != null) 
                {
                	mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
                updateValues();
            }
        });
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        seekBar.getChildren().add(timeSlider);
		
        playTime = new Label("00:00");
        playTime.setMinWidth(Control.USE_PREF_SIZE);
        playTime.setTextFill(Color.WHITE);
        seekBar.getChildren().add(playTime);
        
		pane.setCenter(seekBar);
		BorderPane.setMargin(seekBar, 	 new Insets(10, 20, 5, 20));
		return pane;
	}
	
	private Pane createFilesView()
	{
		BorderPane pane = new BorderPane();	
		JFXButton add	 = new JFXButton("ADD");
		add.prefWidthProperty().bind(pane.widthProperty().subtract(30));
		
		add.setFocusTraversable(false);
		add.setStyle(" -fx-font-size: 15px;-jfx-button-type: RAISED;-fx-background-color: -color-20;-fx-text-fill: white;");
		
		JFXButton remove = new JFXButton("REMOVE");
		remove.setFocusTraversable(false);
		remove.setStyle(" -fx-font-size: 15px;-jfx-button-type: RAISED;-fx-background-color: -color-20;-fx-text-fill: white;");
		remove.prefWidthProperty().bind(pane.widthProperty().subtract(30));
		
		HBox bottom = new HBox(10,add,remove);
		pane.setBottom(bottom);
		this.files = new ListView<>();
		files.setCellFactory(CheckBoxListCell.forListView(new Callback<FileItem, ObservableValue<Boolean>>()
		{
			@Override
			public ObservableValue<Boolean> call(FileItem param)
			{
				return param.isSelected;
			}
		}));
		
		pane.setCenter(files);
		
		BorderPane.setMargin(bottom, 	new Insets(10, 10, 10, 10));
		BorderPane.setMargin(files,		new Insets(10, 10, 0, 10));
	
		add.setOnAction(e ->
		{
			
			files.getSelectionModel().clearSelection();
			add.setDisable(true);
			List<File> selectedFiles = AppManager.INSTANCE.showOpenMultiple(AppPcapPlayer.class);
			if(selectedFiles != null)
			{
				
				for (File file : selectedFiles)
				{
					if(!filesMap.containsKey(file))
					{
						FileItem fileItem = new FileItem(file);
						filesMap.put(file, fileItem);
						files.getItems().add(fileItem);
					}
				}
			}
			add.setDisable(false);
		});
		
		remove.setOnAction(e ->
		{
			FileItem selectedFileItem = files.getSelectionModel().getSelectedItem();
			if(selectedFileItem != null)
			{
				reset();
				filesMap.remove(selectedFileItem.file);
				if(files.getItems().remove(selectedFileItem))
				{
					if(mediaPlayer != null)
					{
						mediaPlayer.stop();
					}
				}
				files.getSelectionModel().clearSelection();
			}
		});
		remove.disableProperty().bind(files.getSelectionModel().selectedItemProperty().isNull());	
		return pane;
	}

	private boolean preparePlaying() throws Exception
	{
		
		if(convertToPCMData(selectedFilesList, pcmBuffer))
		{
			if(mediaPlayer != null)
			{
				mediaPlayer.stop();
				mediaPlayer.dispose();
			}

			Media media 				= new Media(wavFile.toURI().toString());
			mediaPlayer 				= new MediaPlayer(media);

			mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) ->
			{
				updateValues();
			});

			mediaPlayer.setOnStopped(() -> 
			{
				reset();
			});

			mediaPlayer.setOnEndOfMedia(() -> 
			{
				reset();
			});

			mediaPlayer.setOnReady(() -> 
			{
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
				for (int i = 0; i < seriesData.length; i++)
				{
					seriesData[i].setYValue(0);
				}
			});

			mediaPlayer.setOnPlaying(() -> 
			{
				mediaPlayer.setStartTime(Duration.ZERO);
				mediaPlayer.setStopTime(mediaPlayer.getTotalDuration());
				files.setDisable(true);
			});

			mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener()
			{
				@Override
				public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases)
				{
					for (int i = 0; i < seriesData.length; i++) 
					{
						seriesData[i].setYValue(magnitudes[i] + 60);
					}
				}
			});

			stopButton.setOnAction(e ->
			{
				mediaPlayer.stop();
			});

			pauseButton.setOnAction(e ->
			{
				mediaPlayer.pause();
			});

			backButton.setOnAction(e ->
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				mediaPlayer.seek(Duration.seconds(currentTime.toSeconds() - 5.0));
			});

			forwardButton.setOnAction(e ->
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				mediaPlayer.seek(Duration.seconds(currentTime.toSeconds() + 5.0));
			});
			return true;
		}
		return false;
	}

	
	public void reset()
	{
		if(mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.seek(Duration.ZERO);
		}
		files.setDisable(false);
		for (int i = 0; i < seriesData.length; i++)
		{
			seriesData[i].setYValue(0);
		}
	}
	
	
	protected void updateValues()
	{
		if (playTime != null && timeSlider != null && duration != null)
		{
			Platform.runLater(() ->
			{
				Duration currentTime = mediaPlayer.getCurrentTime();
				playTime.setText(AudioUtils.formatTime(currentTime, duration));
				if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
				{
					timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
				}
			});
		}
	}
	
	
	private static ILBC ilbc							= new ILBC(Mode.MODE_20);
	private static ByteBuffer ilbcIn 					= ByteBuffer.allocate(ILBC.Mode.MODE_20.bytes).order(ByteOrder.LITTLE_ENDIAN);
	private static ByteBuffer ilbcOut 					= ByteBuffer.allocate(ILBC.Mode.MODE_20.size * 2).order(ByteOrder.LITTLE_ENDIAN);
	private static int ilbcCombineLen 					= ILBC.Mode.MODE_20.bytes * 2;
	
	public  boolean convertToPCMData(List<File> selectedFilesList, ByteBuffer pcmBuffer) throws Exception
	{	
		packetsSet.clear();
		for (File currentFile : selectedFilesList)
		{
			if(currentFile.getName().contains(".pcap") && currentFile.length() > 0)
			{
				ByteBuffer pcapFileBuffer = Utilities.readFile(currentFile,ByteOrder.LITTLE_ENDIAN);
				pcapFileBuffer.flip();
				pcapFileBuffer.position(Constants.PCAP_GLOBAL_HEADER_LENGTH);
				while(pcapFileBuffer.hasRemaining())
				{
					int remaining = pcapFileBuffer.remaining();
					if (remaining >= Constants.PCAP_PACKET_HEADER_LENGTH)
					{
						CapturedPacket packet = CapturedPacket.createFromBuffer(pcapFileBuffer);
						if(packet != null)
						{
							packetsSet.add(packet);
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}	
				}
			}
		}
		ilbc.decoder.reset();
		pcmBuffer.clear();

		for (CapturedPacket packet : packetsSet)
		{
			if(packet.rtpPayloadType == Constants.ILBC_20_PAYLOD_TYPE && packet.rtpPayloadlen == ilbcCombineLen) // ilbc combine 2 packets
			{
				ilbcCombineDecode(ilbc, packet.rtpBuffer, ilbcIn, ilbcOut, pcmBuffer);
				ilbcCombineDecode(ilbc, packet.rtpBuffer, ilbcIn, ilbcOut, pcmBuffer);

			}
			else if(packet.rtpPayloadType == Constants.PCM_PAYLOAD_TYPE && packet.rtpPayloadlen == 120)
			{
				for (int i = 0; i < 120; i++)
				{
					pcmBuffer.put(packet.rtpBuffer.get());
				}
			}
		}

		if(!packetsSet.isEmpty())
		{

			pcmBuffer.flip();

			byte[] data = new byte[pcmBuffer.limit()];
			pcmBuffer.get(data, 0, data.length);
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			AudioInputStream audioOutputStream = new AudioInputStream(in, AUDIO_FORMAT, data.length/AUDIO_FORMAT.getFrameSize());
			AudioSystem.write(audioOutputStream, AudioFileFormat.Type.WAVE, wavFile);

			in.close();
			audioOutputStream.close();
		}
		return !packetsSet.isEmpty();
	}
	
	
	private static void ilbcCombineDecode(ILBC ilbc, ByteBuffer rtp, ByteBuffer in, ByteBuffer out, ByteBuffer pcm)
	{
		in.clear();
		out.clear();
		
		for (int i = 0; i < ILBC.Mode.MODE_20.bytes; i++)
		{
			in.put(rtp.get());
		}
		
		in.flip();
		ilbc.decode(in, out);
		out.flip();
		pcm.put(out);
	}
}


