package com.rafael.med.ans;

import com.rafael.med.ans.ChannelData.SendType;
import com.rafael.med.ans.ChannelData.Type;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ChannelView extends TitledPane
{
	private static final String RECEIVING = "RECEIVING";
	private static final String SEND = "SEND ONCE";
	private static final String STOP = "STOP SEND";
	private static final String START = "START SEND";
	private Button sendStartButton;
	public ChannelData channelData;

	private Label name;
	private Text edit;
	private Text close;
	private Text clear;
	private Text rxIndicator;
	private Label rxCount;
	private Text txIndicator;
	private Label httpTimeAvr;
	private Label txCount;
	private Text multicastIndicator;

	private long txCounter = 0;
	private long rxCounter = 0;
	private long httpAvr = 0;

	private static final String COUNT = "(%d)";
	private static final String HTTP_AVG = "(%d ms)";

	public ChannelView(AppAns networkSimulator)
	{

		setId("titledPane");
		name				= new Label();
		edit				= ViewUtils.glyphIconButton(FontAwesomeIcon.EDIT, 36 ,Color.WHITE, () ->
		{
			networkSimulator.openChannelDataView(channelData);

		});
		close				= ViewUtils.glyphIconButton(FontAwesomeIcon.CLOSE, 36 ,Color.WHITE, () ->
		{
			networkSimulator.closeChannel(channelData);

		});
		clear				= ViewUtils.glyphIconButton(FontAwesomeIcon.CIRCLE_ALT, 36 ,Color.WHITE, () ->
		{
			channelData.txCount.set(0);
			channelData.rxCount.set(0);
			channelData.httpAvr.set(0);
			channelData.httpSum.set(0);

		});

		rxIndicator			= ViewUtils.glyphIcon(FontAwesomeIcon.CARET_DOWN, 40 ,Color.TRANSPARENT);
		rxCount 			= new Label();
		txIndicator			= ViewUtils.glyphIcon(FontAwesomeIcon.CARET_UP, 40 ,Color.TRANSPARENT);
		txCount 			= new Label();
		httpTimeAvr			= new Label();
		sendStartButton		= new Button();

		multicastIndicator	= ViewUtils.glyphIcon(FontAwesomeIcon.FORUMBEE, 40 ,Color.TRANSPARENT);

		sendStartButton.setMinWidth(140);
		sendStartButton.setMaxWidth(140);

		sendStartButton.setMaxHeight(30);

		txCount.setId("title");
		rxCount.setId("title");
		name.setId("title");
		httpTimeAvr.setId("title");

		txCount.setMinWidth(100);
		txCount.setMaxWidth(100);

		rxCount.setMinWidth(100);
		rxCount.setMaxWidth(100);


		HBox title = new HBox();
		title.prefWidthProperty().bind(networkSimulator.viewPane.widthProperty().subtract(50));
		title.getChildren().addAll(name,ViewUtils.hspace(),multicastIndicator,ViewUtils.hspace(10), httpTimeAvr,ViewUtils.hspace(10), rxIndicator,ViewUtils.hspace(10), rxCount,ViewUtils.hspace(10), txIndicator,ViewUtils.hspace(10), txCount, ViewUtils.hspace(20), sendStartButton,ViewUtils.hspace(10), clear,ViewUtils.hspace(10), edit, ViewUtils.hspace(10), close);
		title.setAlignment(Pos.CENTER);
		setGraphic(title);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

//		HBox tabPane = new HBox(20);
//		tabPane.setStyle("-fx-background-color:derive(#0d6476,80%);");
//		tabPane.prefWidthProperty().bind(widthProperty().subtract(2));
//		ScrollPane tabScrollPane = new ScrollPane(tabPane);
//		setContent(tabScrollPane);
//		tabScrollPane.setFitToHeight(true);
//		tabScrollPane.setFitToWidth(true);

		sendStartButton.setOnAction(e->
		{
			onStartChannel();
		});
	}


	public void onStartChannel()
	{
		if(sendStartButton.getText().equals(START))
		{
			sendStartButton.setText(STOP);
			channelData.startSend();
			edit.setDisable(true);
			close.setDisable(true);
		}
		else if(sendStartButton.getText().equals(STOP))
		{
			sendStartButton.setText(START);
			channelData.stopSend();
			edit.setDisable(false);
			close.setDisable(false);
		}
		else if(sendStartButton.getText().equals(SEND))
		{
			channelData.send();
			edit.setDisable(false);
			close.setDisable(false);
		}
	}


	public void updateData(ChannelData channelData)
	{
		this.channelData = channelData;
		name.setText(channelData.name);


		if(channelData.remoteAddress != null)
		{
			sendStartButton.setDisable(false);
			if(channelData.sendType == SendType.BY_CLICK)
			{
				sendStartButton.setText(SEND);
			}
			else if(channelData.sendType == SendType.PERIODIC)
			{
				sendStartButton.setText(START);
			}
		}
		else
		{
			sendStartButton.setText(RECEIVING);
			sendStartButton.setDisable(true);
		}

		if(channelData.multicastAddress != null)
		{
			multicastIndicator.setFill(Color.WHITE);
		}
		else
		{
			multicastIndicator.setFill(Color.TRANSPARENT);
		}

	}

	public void repaint()
	{
		if(txCounter == channelData.txCount.get())
		{
			txIndicator.setFill(Color.TRANSPARENT);
		}
		else
		{
			txIndicator.setFill(Color.ORANGERED);
		}


		if(rxCounter == channelData.rxCount.get())
		{
			rxIndicator.setFill(Color.TRANSPARENT);
		}
		else
		{
			rxIndicator.setFill(Color.LIME);
		}

		txCounter 	= channelData.txCount.get();
		rxCounter   = channelData.rxCount.get();
		httpAvr		= channelData.httpAvr.get();

		txCount.setText(String.format(COUNT, txCounter));
		rxCount.setText(String.format(COUNT, rxCounter));
		httpTimeAvr.setText(String.format(HTTP_AVG, httpAvr));



	}
}
