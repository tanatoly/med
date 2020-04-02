package com.rafael.med.wave;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.rafael.med.AppManager;
import com.rafael.med.common.bnet.P862Algorithm;
import com.rafael.med.common.bnet.TestData;
import com.rafael.med.common.bnet.VoiceSegment;
import com.rafael.med.common.bnet.P862Algorithm.P862Result;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class WaveManager 
{
	private static final Logger log = LogManager.getLogger();
	
	public static final WaveManager INSTANCE = new WaveManager();
	
	private TestPane testPane;
	private TestData testData;
	private Button mosButton;
	private MosTablePane mosTablePane;

	private VoiceSegment currentSelectedSegment;

	private List<VoiceSegment> currentSimilarVoiceSegments;

	private JFXDrawersStack cardPane;

	private JFXButton chooseButton;

	private JFXButton expandedButton;

	private JFXButton closeButton;
	
	
	public void reset(File selectedDir, TestPane testPane, Button mosButton, MosTablePane mosTablePane, JFXDrawersStack cardPane, JFXButton chooseButton, JFXButton expandedButton, JFXButton closeButton) throws Exception
	{
		this.testData = new TestData(selectedDir.toPath());
		this.testPane = testPane;
		this.testPane.reset(testData);
		this.mosButton = mosButton;
		this.mosTablePane = mosTablePane;
		this.cardPane		= cardPane;
		this.chooseButton = chooseButton;
		this.expandedButton = expandedButton;
		this.closeButton = closeButton;
	}
	
	private void onFindSimilarSegments(VoiceSegment sourceSegment)
	{
		currentSimilarVoiceSegments = testData.findSimilarVoiceSegments(sourceSegment);
		for (VoiceSegment voiceSegment : currentSimilarVoiceSegments)
		{
			for (Node unitNode : testPane.units)
			{
				UnitPane unitPane = (UnitPane) unitNode;
				if(voiceSegment.voiceChannel.voiceData.bnetData.unitData == unitPane.unitData)
				{
					//unitPane.setExpanded(true);
					
					for (Node bnetNode : unitPane.bnets)
					{
						BnetPane bnetPane = (BnetPane) bnetNode;
						if(voiceSegment.voiceChannel.voiceData.bnetData == bnetPane.bnetData)
						{
							//bnetPane.setExpanded(true);
							
							for (Node channelNode : bnetPane.channels)
							{
								ChannelPane channelPane = (ChannelPane) channelNode;
								
								if(voiceSegment.voiceChannel == channelPane.voiceChannel)
								{
									for (Node segmentNode : channelPane.segmentPanes)
									{
										SegmentPane segmentPane = (SegmentPane) segmentNode;
										if(voiceSegment == segmentPane.segment)
										{
											if(voiceSegment == sourceSegment)
											{
												segmentPane.setSelected(true, true);
											}
											else
											{
												segmentPane.setSelected(true, false);
												channelPane.showChartAndScrollToSegment(segmentPane);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean onSelectChartSegment(boolean isSelected, SegmentPane selectedSegmentPane)
	{
		clearChartSelected();
		mosButton.setDisable(!isSelected);
		if(isSelected)
		{
			currentSelectedSegment = selectedSegmentPane.segment;
			onFindSimilarSegments(selectedSegmentPane.segment);
		}
		else
		{
			currentSelectedSegment = null;
		}
		return currentSimilarVoiceSegments.size() == 1;
	}
	
	
	
	private void clearChartSelected()
	{
		for (Node unitNode : testPane.units)
		{
			UnitPane unitPane = (UnitPane) unitNode;
			for (Node bnetNode : unitPane.bnets)
			{
				BnetPane bnetPane = (BnetPane) bnetNode;
				for (Node channelNode : bnetPane.channels)
				{
					ChannelPane channelPane = (ChannelPane) channelNode;
					for (Node segmentNode : channelPane.segmentPanes)
					{
						SegmentPane segmentPane = (SegmentPane) segmentNode;
						segmentPane.setSelected(false, false);
					}		
				}
			}
		}
	}

	public void mosAction() 
	{
		
		try
		{
			List<P862Result> p862Results = new ArrayList<>(currentSimilarVoiceSegments.size());
			for (VoiceSegment segment : currentSimilarVoiceSegments)
			{
				if(segment != currentSelectedSegment)
				{
					currentSelectedSegment.checkAndCreate();
					segment.checkAndCreate();
					P862Result p862Result = P862Algorithm.INSTANCE.runAlgorithm(currentSelectedSegment.waveFile.getAbsolutePath(), segment.waveFile.getAbsolutePath());
					p862Result.setReferenceSegment(currentSelectedSegment);
					p862Result.setDegradedSegment(segment);
					p862Results.add(p862Result);
				}
				segment.checkAndCreate();
			}
			
			mosTablePane.reset(p862Results, currentSelectedSegment);
			((JFXDrawersStack)cardPane).toggle((JFXDrawer) cardPane.getUserData(),true);
			
			
			
			chooseButton.setDisable(true);
			expandedButton.setDisable(true);
			closeButton.setVisible(true);
		}
		catch (Exception ex)
		{
			AppManager.INSTANCE.showError(AppWave.class, log, "ON ACTION ERROR : ", ex);
		}
	}
	
	
	
	private static final Background BACKGROUND_AQUAMARINE 	= new Background(new BackgroundFill(Color.AQUAMARINE,  null, null));
	private static final Background BACKGROUND_YELLOW 		= new Background(new BackgroundFill(Color.YELLOW,  null, null));
	private static final Background BACKGROUND_ORANGERED 	= new Background(new BackgroundFill(Color.ORANGERED,  null, null));
	private static final Background BACKGROUND_GREENYELLOW 	= new Background(new BackgroundFill(Color.GREENYELLOW,  null, null));
	
	public static void mosLabel(Labeled labeled, double mos)
	{
		labeled.setText(String.format(" %.2f ",mos));
		labeled.setTextFill(Color.BLACK);
		
		if(mos >= 4.0)
		{
			labeled.setBackground(BACKGROUND_GREENYELLOW);
		}
		else if(mos >= 3.0)
		{
			labeled.setBackground(BACKGROUND_AQUAMARINE);
		}
		else if(mos >= 2.0)
		{
			labeled.setBackground(BACKGROUND_YELLOW);
		} 
		else if(mos >= 1.0)
		{
			labeled.setBackground(BACKGROUND_ORANGERED);
		} 
	}
}
