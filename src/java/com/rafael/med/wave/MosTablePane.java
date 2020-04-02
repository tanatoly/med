package com.rafael.med.wave;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rafael.med.common.Utilities;
import com.rafael.med.common.bnet.P862Algorithm;
import com.rafael.med.common.bnet.VoiceSegment;
import com.rafael.med.common.bnet.P862Algorithm.P862Result;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MosTablePane extends BorderPane
{
	
	private static final int _640 = 640;
	private static final Font MOS_FONT = new Font(20);


	public static final class AudioPlayerTableCell extends TableCell<P862Result, VoiceSegment>
	{
		private PlayerPane audioPlayerPane;
		
		protected AudioPlayerTableCell(int i)
		{
			super();
			audioPlayerPane = new PlayerPane();
			audioPlayerPane.setMaxHeight(144);
			audioPlayerPane.setMinHeight(144);
		}
		
		@Override
		protected void updateItem(VoiceSegment item, boolean empty)
		{
			super.updateItem(item, empty);
			if(empty || item == null)
			{
				setGraphic(null);
			}
			else
			{
				
				setGraphic(audioPlayerPane);
				audioPlayerPane.reset(item.waveFile, item.durationInMillis, item.getAudioByteArray(), item.fromTime, item.toTime, _640);
			}
		}
	}
	
	
	private GridPane top;
	private TableView<P862Result> table;
	private Text topLabelUp;
	private Text topLabelDown;
	private PlayerPane refernceAudioPlayerPane;
	private Text topLabelM;

	public MosTablePane()
	{
		setStyle("-fx-background-color :  -color-60;");
		
		top 	= createTop();
		
		BorderPane.setMargin(top, new Insets(8));
		table	= createTable();
		BorderPane.setMargin(table, new Insets(8));
		setTop(top);
	    setCenter(table);
	}
	
	
	private TableView<P862Result> createTable()
	{
		TableView<P862Result> table = new TableView<>();
		table.setSelectionModel(null);
		
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		table.setFixedCellSize(160);
		table.setFocusTraversable(false);
		
	    TableColumn<P862Result, VoiceSegment> nameColumn = new TableColumn<>("Name");
	    nameColumn.setCellValueFactory(new PropertyValueFactory<>("degradedSegment"));
	    nameColumn.setCellFactory(new Callback<TableColumn<P862Result,VoiceSegment>, TableCell<P862Result,VoiceSegment>>()
		{
			@Override
			public TableCell<P862Result, VoiceSegment> call(TableColumn<P862Result, VoiceSegment> param)
			{
				Label label = new Label();
				label.setAlignment(Pos.CENTER);
				label.setFont(MOS_FONT);
				label.setFocusTraversable(false);
				label.setMaxHeight(Double.MAX_VALUE);
				label.setMaxWidth(Double.MAX_VALUE);
				
				return new TableCell<P862Algorithm.P862Result, VoiceSegment>()
				{
					@Override
					protected void updateItem(VoiceSegment segment, boolean empty)
					{
						super.updateItem(segment, empty);
						if(empty || segment == null)
						{
							setGraphic(null);
						}
						else
						{
							String colorStr = StringUtils.replaceOnce(segment.voiceChannel.channelType.color.toString(), "0x", "#");
							label.setStyle("-fx-background-color : "+ colorStr +";");
							StringBuilder builder = new StringBuilder();
							builder.append(segment.voiceChannel.voiceData.bnetData.unitData.name).append("		").append(segment.voiceChannel.voiceData.bnetData.name).append(StringUtils.LF).append(StringUtils.LF);
							builder.append(segment.voiceChannel.channelType.type).append("		").append(segment.voiceChannel.channelType.rxtxString).append("		").append(segment.voiceChannel.channelType.point.name()).append(StringUtils.LF);
							label.setText(builder.toString());
							setGraphic(label);
						}
					}
				};
			}
		});
	    nameColumn.setStyle("-fx-alignment : CENTER;-fx-font-weight: BOLD;");
	    nameColumn.setSortable(false);

	    TableColumn<P862Result, String> fromColumn = new TableColumn<>("FROM-TO");
	    fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromTo"));
	    fromColumn.setMinWidth(230);
	    fromColumn.setMaxWidth(230);
	    fromColumn.setSortable(false);
	    fromColumn.setStyle("-fx-alignment : CENTER;");

	    TableColumn<P862Result, String> relatedPercentColumn = new TableColumn<>("%");
	    relatedPercentColumn.setCellValueFactory(new PropertyValueFactory<>("relatedPercent"));
	    relatedPercentColumn.setMinWidth(80);
	    relatedPercentColumn.setMaxWidth(80);
	    relatedPercentColumn.setSortable(false);
	    relatedPercentColumn.setStyle("-fx-alignment : CENTER;-fx-font-weight: BOLD;");
	    
	    
	    TableColumn<P862Result, String> pesqmosColumn = new TableColumn<>("PESQ");
	    pesqmosColumn.setCellValueFactory(new PropertyValueFactory<>("pesq"));
	    pesqmosColumn.setMinWidth(200);
	    pesqmosColumn.setMaxWidth(200);
	    pesqmosColumn.setSortable(false);
	    pesqmosColumn.setStyle("-fx-alignment : CENTER");

	    TableColumn<P862Result, Float> mappedmosColumn = new TableColumn<>("MOS");
	    mappedmosColumn.setCellValueFactory(new PropertyValueFactory<>("mappedmos"));
	    
	    mappedmosColumn.setCellFactory(new Callback<TableColumn<P862Result,Float>, TableCell<P862Result,Float>>()
		{
			@Override
			public TableCell<P862Result, Float> call(TableColumn<P862Result, Float> param)
			{
				
				Label label = new Label();
				label.setAlignment(Pos.CENTER);
				label.setFont(MOS_FONT);
				label.setFocusTraversable(false);
				
				return new TableCell<P862Algorithm.P862Result, Float>()
				{
					@Override
					protected void updateItem(Float item, boolean empty)
					{
						super.updateItem(item, empty);
						if(empty || item == null)
						{
							setGraphic(null);
						}
						else
						{
							WaveManager.mosLabel(label, item);
							setGraphic(label);
						}
					}
				};
			}
		});
	    mappedmosColumn.setMinWidth(120);
	    mappedmosColumn.setMaxWidth(120);
	    mappedmosColumn.setSortable(false);
	    mappedmosColumn.setStyle("-fx-alignment : CENTER");
	    
	    TableColumn<P862Result, VoiceSegment> playerColumn = new TableColumn<>("PLAYER");
	    playerColumn.setCellValueFactory(new PropertyValueFactory<>("degradedSegment"));
	    playerColumn.setCellFactory(new Callback<TableColumn<P862Result,VoiceSegment>, TableCell<P862Result,VoiceSegment>>()
		{
			@Override
			public TableCell<P862Result, VoiceSegment> call(TableColumn<P862Result, VoiceSegment> param)
			{
				return new AudioPlayerTableCell(640);
			}
		});
	    
	    playerColumn.setMinWidth(_640);
	    playerColumn.setMaxWidth(_640);
	    playerColumn.setSortable(false);

	    table.getColumns().addAll(nameColumn, fromColumn, relatedPercentColumn, pesqmosColumn,mappedmosColumn,playerColumn);
		return table;
	}


	private GridPane createTop()
	{
		
		
		
		
//		Font f = new Font(30);
//		Font f1 = new Font(25);
//		
//		topUnit 		= new Text();
//		topUnit.setFill(Color.WHITE);
//		topUnit.setFont(f);
//		topBnet 		= new Text();
//		topBnet.setFill(Color.WHITE);
//		topBnet.setFont(f);
//		topDuration		= new Text();
//		topDuration.setFill(Color.WHITE);
//		topTxRx			= new Text();
//		topTxRx.setFill(Color.WHITE);
//		topTxRx.setFont(f1);
//		topPoint		= new Text();
//		topPoint.setFill(Color.WHITE);
//		topPoint.setFont(f1);
//		topType			= new Text();
//		topType.setFill(Color.WHITE);
//		topType.setFont(f1);
//		
//		refernceAudioPlayerPane = new PlayerPane();
//		//refernceAudioPlayerPane.setStyle("-fx-background-color : -color-40;");
//		
//		GridPane pane = new GridPane();
//		RowConstraints r1 = new RowConstraints();
//		r1.setPercentHeight(100/3);
//		pane.getRowConstraints().addAll(r1,r1, r1);
//		ColumnConstraints c1 = new ColumnConstraints();
//		c1.setPercentWidth(25);
//		ColumnConstraints c3 = new ColumnConstraints();
//		c3.setPercentWidth(50);
//		pane.getColumnConstraints().addAll(c1,c1,c3);
//		
//		GridPane.setConstraints(topUnit, 			0, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		GridPane.setConstraints(topBnet, 			1, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		GridPane.setConstraints(topType, 			0, 1 , 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		GridPane.setConstraints(topTxRx, 			0, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		GridPane.setConstraints(topPoint, 			1, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		
//		//GridPane.setConstraints(topDuration, 		2, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
//		
//		GridPane.setConstraints(refernceAudioPlayerPane, 		3, 0 , 1, 3, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//		
//		pane.getChildren().addAll(topUnit, topBnet,topType,topTxRx,topPoint,refernceAudioPlayerPane);
		
		
		
		
		GridPane pane = new GridPane();
		RowConstraints r = new RowConstraints();
		r.setPercentHeight(100/3);	
		pane.getRowConstraints().addAll(r,r,r);
	
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(50);
		pane.getColumnConstraints().addAll(c,c);
		
		Font f1 = new Font(25);
		
		topLabelUp = new Text();
		topLabelUp.setFill(Color.WHITE);
		topLabelUp.setFont(f1);
		
		topLabelM = new Text();
		topLabelM.setFill(Color.WHITE);
		topLabelM.setFont(f1);
		
		topLabelDown = new Text();
		topLabelDown.setFill(Color.WHITE);
		topLabelUp.setFont(f1);
		
		
		refernceAudioPlayerPane = new PlayerPane();
		
		GridPane.setConstraints(topLabelUp, 					0, 0 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(topLabelM, 						0, 1 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(topLabelDown, 					0, 2 , 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(refernceAudioPlayerPane, 		1, 0 , 1, 3, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		pane.getChildren().addAll(topLabelUp,topLabelDown,topLabelM,refernceAudioPlayerPane);
		
		pane.setMaxHeight(144);
		pane.setMinHeight(144);
		pane.setStyle("-fx-background-color : -color-40;");
		
		return pane;
	}


	public void reset(List<P862Result> results, VoiceSegment sourceSegment) throws Exception
	{
		table.getItems().clear();
		
		for (P862Result p862Result : results) 
		{
			table.getItems().add(p862Result);
		}
				
		String colorStr = StringUtils.replaceOnce(sourceSegment.voiceChannel.channelType.color.toString(), "0x", "#");
		top.setStyle("-fx-background-color : "+ colorStr +";");
				
		topLabelUp.setText(sourceSegment.voiceChannel.voiceData.bnetData.unitData.name + "	" + sourceSegment.voiceChannel.voiceData.bnetData.name);
		topLabelM.setText(sourceSegment.voiceChannel.channelType.type + "		" + sourceSegment.voiceChannel.channelType.rxtxString + "		" + sourceSegment.voiceChannel.channelType.point.name());
		topLabelDown.setText(sourceSegment.fromTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS) + "		" + sourceSegment.toTime.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS) + "	, DURATION - " + String.valueOf(sourceSegment.duration.toMillis()) + " milllis");
		
		refernceAudioPlayerPane.reset(sourceSegment.waveFile, sourceSegment.durationInMillis, sourceSegment.getAudioByteArray(), sourceSegment.fromTime, sourceSegment.toTime, 0);

		
	}
}
