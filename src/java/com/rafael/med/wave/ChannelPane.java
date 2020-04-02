package com.rafael.med.wave;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.AppManager;
import com.rafael.med.common.Constants;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.common.bnet.VoiceChannel;
import com.rafael.med.common.bnet.VoiceSegment;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ChannelPane extends GridPane
{
	
	private static final Logger log = LogManager.getLogger();
	
	
	private static final String SECONDS = " seconds";


	public final VoiceChannel voiceChannel;
	
		
	public final BnetPane bnetPane;
	private Button chartRangeButton;
	private Pane rangePane;
	private Node chartPane;
	private Text rangeText;
	
	public  ObservableList<Node> segmentPanes;
		



	private final PlayerPane playerPane;


	private ScrollPane scroll;


	protected LocalDateTime lowCurrent;


	protected LocalDateTime highCurrent;


	protected long currentDurationInSeconds;


	private Label fromTimeLabel;


	private Label toTimeLabel;


	private Text durationLabel;


	private JFXButton createAudioButton;


	private Text chartText;


	private JFXButton folderButton;


	public JFXButton mosButton;


	private Text p563Text;


	private Text p862Text;
	
	public boolean isP563RunOnce = false;
	
	public ChannelPane(BnetPane bnetPane, VoiceChannel voiceChannel)
	{
		this.bnetPane 				= bnetPane;
		this.voiceChannel 			= voiceChannel;
		
		
		playerPane 					= new PlayerPane();
		
		setMaxHeight(144);
		setMinHeight(144);
		
		String colorStr = StringUtils.replaceOnce(voiceChannel.channelType.color.toString(), "0x", "#");
		setStyle("-fx-background-color :  -color-70;-fx-border-color: " + colorStr + ";-fx-border-width: 2 2 2 2;");
		maxWidthProperty().bind(bnetPane.widthProperty().subtract(20));
		
		p563Text 				= ViewUtils.glyphIcon(FontAwesomeIcon.VINE, String.valueOf(50 * 0.7),Color.WHITE);
		p862Text	 			= ViewUtils.glyphIcon(FontAwesomeIcon.STRIKETHROUGH, String.valueOf(50 * 0.7),Color.WHITE);
		mosButton 				= ViewUtils.jfxbutton(50,50,Color.GRAY,Color.AQUA, null, 2);
		mosButton.setGraphic(p563Text);
		
		rangeText 				= ViewUtils.glyphIcon(FontAwesomeIcon.RANDOM, String.valueOf(50 * 0.7),Color.WHITE);
		chartText	 			= ViewUtils.glyphIcon(FontAwesomeIcon.LINE_CHART, String.valueOf(50 * 0.7),Color.WHITE);
		chartRangeButton 		= ViewUtils.jfxbutton(50,50,Color.GRAY,Color.AQUA, null, 2);
		chartRangeButton.setGraphic(chartText);
		createAudioButton 		= ViewUtils.jfxbutton(null, FontAwesomeIcon.HEADPHONES, 50, 50, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "open",2);
		folderButton 			= ViewUtils.jfxbutton(null, FontAwesomeIcon.FOLDER_OPEN, 50, 50, Color.GRAY, Color.GHOSTWHITE, Color.AQUA, "open",2);
		folderButton.setDisable(true);
		
		
		
		
		
		rangePane	= createRangePane(voiceChannel);
		chartPane  	= createChartPane(voiceChannel);
		StackPane stackPane = new StackPane(rangePane,chartPane);
		StackPane.setMargin(chartPane, new Insets(4));
		StackPane.setMargin(rangePane, new Insets(4));
		
		stackPane.setStyle("-fx-background-color :  -color-60;");
		playerPane.setStyle("-fx-background-color :  -color-60;");
		
	
		Label labelName = new Label(voiceChannel.channelType.type);
		labelName.setFont(new Font(18));
		Label l = ViewUtils.jfxLabel(null, voiceChannel.channelType.rxtxLabel, 50, 50, Color.TRANSPARENT, Color.GHOSTWHITE);
		Label l1 = ViewUtils.jfxLabel(null, voiceChannel.channelType.point.label, 50, 50, Color.TRANSPARENT, Color.GHOSTWHITE);
		
		Text t = new Text(voiceChannel.channelType.rxtxString);
		t.setFill(Color.GHOSTWHITE);
		Text t1 =  new Text(voiceChannel.channelType.point.name());
		t1.setFill(Color.GHOSTWHITE);
		
		GridPane fisrt = new GridPane();
		fisrt.setStyle("-fx-background-color :  " + colorStr + ";");
		
		RowConstraints r10 = new RowConstraints();
		r10.setPercentHeight(50);
		RowConstraints r20 = new RowConstraints();
		r20.setPercentHeight(25);
		fisrt.getRowConstraints().addAll(r10,r20, r20);
		ColumnConstraints c10 = new ColumnConstraints();
		c10.setPercentWidth(50);
		fisrt.getColumnConstraints().addAll(c10,c10);
		
		GridPane.setConstraints(labelName, 			0, 2 , 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(l, 					0, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(l1, 				1, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(t, 					0, 1 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(t1, 				1, 1 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		fisrt.getChildren().addAll(labelName, l, l1,t,t1);
		
		
		
		//mosButton.setDisable(true);
		
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(50);
		getRowConstraints().addAll(r1,r1);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(5);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(40);
		getColumnConstraints().addAll(c1,c1,c1,c2,c1,c2);
		
		GridPane.setConstraints(fisrt, 			0, 0 , 2, 2, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(chartRangeButton, 		2, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(mosButton, 		2, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(stackPane, 			3, 0, 1, 2, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(folderButton, 		4, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(createAudioButton, 	4, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(playerPane, 		5, 0, 1, 2, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		getChildren().addAll(fisrt, chartRangeButton,mosButton, stackPane, createAudioButton, folderButton, playerPane);
		
		folderButton.setOnAction(e ->
		{
			if(playerPane.waveFile != null)
			{
				try
				{
					Runtime.getRuntime().exec("explorer " + playerPane.waveFile.getParent().toString());
				} 
				catch (IOException e1)
				{
					AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", e1);
				}
			}
		});
		
		chartRangeButton.setOnAction( e-> 
		{
			showStack(chartRangeButton.getGraphic() == chartText);			
		});
		
		chartPane.visibleProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
		{
			createAudioButton.setDisable(true);
			if(newValue)
			{
				for (Node node : segmentPanes) 
				{
					SegmentPane segmentPane = (SegmentPane) node;
					if(segmentPane.isSelected)
					{
						createAudioButton.setDisable(false);
						break;
					}
				}
			}
			
			mosButton.setDisable(!newValue || isP563RunOnce);
			playerPane.setDisable(true);
			
		});
		
		chartPane.setVisible(false);
		createAudioButton.setOnAction(e -> 
		{
			VoiceSegment currentVoiceSegment = null;
			if(rangePane.isVisible())
			{
				currentVoiceSegment = new VoiceSegment(voiceChannel, 0, lowCurrent);
				currentVoiceSegment.setToTime(highCurrent);
			}
			else
			{
				for (Node node : segmentPanes) 
				{
					SegmentPane segmentPane = (SegmentPane) node;
					if(segmentPane.isSelected)
					{
						currentVoiceSegment = segmentPane.segment;
						segmentPane.setUsed();
						break;
					}
				}
			}
			
			if(currentVoiceSegment != null)
			{
				try
				{
					currentVoiceSegment.checkAndCreate();
					folderButton.setDisable(false);
					playerPane.reset(currentVoiceSegment.waveFile, currentVoiceSegment.durationInMillis, currentVoiceSegment.getAudioByteArray(), currentVoiceSegment.fromTime, currentVoiceSegment.toTime, 0);
					createAudioButton.setDisable(true);
					playerPane.setDisable(false);
				} 
				catch (Exception e1) 
				{
					AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", e1);
				}
			}
		});
		
		Task<Void> p563Task = new Task<Void>() 
		{

			@Override
			protected Void call() throws Exception 
			{
				final AtomicInteger count = new AtomicInteger(segmentPanes.size());
				
				Node graphic = mosButton.getGraphic();
				
				for (Node node : segmentPanes) 
				{
					SegmentPane segmentPane = (SegmentPane) node;
					double result = segmentPane.segment.algorithmP563();
					Platform.runLater(() -> 
					{
						WaveManager.mosLabel(segmentPane.e1, result);
						Text text = new Text(String.valueOf(count.decrementAndGet()));
						text.setFill(Color.WHITE);
						mosButton.setGraphic(text);
						segmentPane.initTooltip();
					});
				}
				Platform.runLater(() -> 
				{
					mosButton.setGraphic(graphic);
				});
				
				return null;
			}
		};
		
		
		mosButton.setOnAction( e ->
		{
			Node graphic = mosButton.getGraphic();
			if(graphic == p563Text)
			{
				isP563RunOnce = true;
				mosButton.setDisable(isP563RunOnce);
				Executors.newSingleThreadExecutor().execute(p563Task);
			}
			else
			{
				WaveManager.INSTANCE.mosAction();
				for (Node node : segmentPanes) 
				{
					SegmentPane segmentPane = (SegmentPane) node;
					if(segmentPane.isSelected)
					{
						segmentPane.setUsed();
					}
				}
				
			}
		});
		
		
		
	}
	
	private ScrollPane createChartPane(VoiceChannel voiceChannel) 
	{
		HBox pane = new HBox(10);
		segmentPanes = pane.getChildren();
		scroll = new ScrollPane(pane);
		
		scroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		
		List<VoiceSegment> segments = voiceChannel.getSegments();
		for (VoiceSegment segment : segments) 
		{	
			segmentPanes.add(new SegmentPane(this, segment));
		}
		return scroll;
	}

	private GridPane createRangePane(VoiceChannel voiceChannel)
	{
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(0,5,0,5));
		RowConstraints r1 = new RowConstraints();
		r1.setPercentHeight(30);
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(40);
		pane.getRowConstraints().addAll(r1,r1,r2);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(50);
		pane.getColumnConstraints().addAll(c1,c1);
		
		
		fromTimeLabel 	= new Label(voiceChannel.getFromTime().format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		toTimeLabel 	= new Label(voiceChannel.getToTime().format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		durationLabel 	= new Text(Duration.between(voiceChannel.getFromTime(), voiceChannel.getToTime()).getSeconds() + SECONDS);
		durationLabel.setFill(Color.WHITE);
		
		long lowSecond 	= voiceChannel.getFromTime().toEpochSecond(Constants.ZONE_OFFSET);
		long highSecond = voiceChannel.getToTime().toEpochSecond(Constants.ZONE_OFFSET);
		RangeSlider timeSlider = new RangeSlider(lowSecond, highSecond, lowSecond, highSecond);
		
		
		lowCurrent 	= voiceChannel.getFromTime();
		highCurrent = voiceChannel.getToTime();
		
		timeSlider.lowValueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				onRangeChange(newValue, null);
			}
		});
		
		timeSlider.highValueProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				onRangeChange(null, newValue);
			}
		});
		
		GridPane.setConstraints(fromTimeLabel, 		0, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(toTimeLabel, 		1, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(durationLabel, 		0, 1 , 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(timeSlider, 		0, 2 , 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		
		pane.getChildren().addAll(fromTimeLabel, toTimeLabel, timeSlider, durationLabel);
		
		return pane;
	}
	
	private void onRangeChange(Number newLowValue, Number newHighValue)
	{
		if(newLowValue != null)
		{
			lowCurrent = LocalDateTime.ofEpochSecond(newLowValue.longValue(), 0, Constants.ZONE_OFFSET);
			fromTimeLabel.setText(lowCurrent.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		}
		if(newHighValue != null)
		{
			highCurrent = LocalDateTime.ofEpochSecond(newHighValue.longValue(), 0, Constants.ZONE_OFFSET);
			toTimeLabel.setText(highCurrent.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss));
		}
		currentDurationInSeconds = Duration.between(lowCurrent, highCurrent).getSeconds();
		if(currentDurationInSeconds < 0 )
		{
			currentDurationInSeconds = 0;
		}
		durationLabel.setText(currentDurationInSeconds + SECONDS);
		if(currentDurationInSeconds > 1800)
		{
			durationLabel.setFill(Color.RED);
			createAudioButton.setDisable(true);
		}
		else
		{
			durationLabel.setFill(Color.LAWNGREEN);
			createAudioButton.setDisable(false);
		}
		playerPane.setDisable(true);
	}
	

	private void showStack(boolean isChartShow)
	{
		if(isChartShow)
		{
			chartRangeButton.setGraphic(rangeText);
		}
		else
		{
			chartRangeButton.setGraphic(chartText);
		}
		rangePane.setVisible(!isChartShow);
		chartPane.setVisible(isChartShow);
	}
		
	
	public void onSegmentSelected(boolean isSelected, boolean isSource, SegmentPane segmentPane)
	{
		if(isSelected && isSource)
		{
			mosButton.setGraphic(p862Text);
		}
		else
		{
			mosButton.setGraphic(p563Text);
		}
		
		if(chartPane != null && chartPane.isVisible())
		{
			createAudioButton.setDisable(!isSelected);
			if(isSelected && isSource)
			{
				mosButton.setDisable(false);
			}
			else
			{
				mosButton.setDisable(isP563RunOnce);
			}
		}
		
		playerPane.setDisable(true);
		
	}
	
	
	public void showChartAndScrollToSegment(SegmentPane selectedSegmentPane) 
	{
		showStack(true);
				
		double w = scroll.getContent().getBoundsInLocal().getWidth();
		
		
	//	double xMax = selectedSegmentPane.getBoundsInParent().getMaxX();
		double xMin = selectedSegmentPane.getBoundsInParent().getMinX();
		double xCenter = xMin + selectedSegmentPane.getBoundsInParent().getWidth() / 2.0;
		
		double scrollWidth = scroll.getViewportBounds().getWidth();
		double scrollMax = scroll.getHmax();
		double hscroll = scrollMax / (w - (scrollWidth/2))  * xCenter;
		
		//double value = scroll.getHmax() * ((x - 0.5 * v) / (w - v));
		scroll.setHvalue(hscroll);
		
		
		
//		System.out.println(scrollWidth);
//		
//		System.out.println("w = " + w +", xMax = " + xMax + ", xMin = " + xMin + ", xCenter = " + xCenter + " , hscroll = " + hscroll);
				
	}

	
}
