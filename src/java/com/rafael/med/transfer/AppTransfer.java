package com.rafael.med.transfer;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.rafael.med.AppBase;
import com.rafael.med.AppManager;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AppTransfer extends AppBase 
{
	
	private static final Logger log = LogManager.getLogger();
	public enum TYPE
	{
		TRANSMITTER, RECEIVER;
	}
	private Canvas canvas;
	private GraphicsContext g;


	private TextField portField;
	private ComboBox<InetAddress> localHostField;
	private ComboBox<NetworkInterface> networkInterfaceField;
	private ComboBox<TYPE> typeField;
	private TYPE type;
	private TextField remoteHostField;
	JFXButton addButton;
	JFXButton startButton;
	private JFXButton resetButton;

	
	private ImageReceiver imageReceiver;
	private ImageTransmitter imageTransmitter;
	
	
	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args)
	{
		BorderPane main = new BorderPane();
		main.setStyle("-fx-background-color :  -color-60;");
		
		Label label = new Label();
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-font-size: 16;-fx-font-weight: BOLD;-fx-text-fill: white;");
		
		Map<NetworkInterface,Set<InetAddress>> nisMap = new HashMap<>();
		try
		{
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
			{
				NetworkInterface localni = e.nextElement();
				if(localni.isUp())
				{
					Set<InetAddress> list = new HashSet<>();
					nisMap.put(localni, list);
					for (InterfaceAddress currentIntAdress : localni.getInterfaceAddresses())
					{
						InetAddress address = currentIntAdress.getAddress();
						if (address instanceof Inet4Address)
						{
							list.add(address);
						}
						
					}
				}
			}
		}
		catch (Exception ex)
		{
			AppManager.INSTANCE.showError(AppTransfer.class,log,"ON ACTION FAILED : ",ex);
		}
		
		typeField 				= new ComboBox<>(FXCollections.observableArrayList(TYPE.values()));
		typeField.setMinWidth(160);
		typeField.setMaxWidth(160);
		
		networkInterfaceField 		= new ComboBox<>(FXCollections.observableArrayList(nisMap.keySet()));
		networkInterfaceField.setMinWidth(480);
		networkInterfaceField.setMaxWidth(480);
	
		
		localHostField 	= new ComboBox<>();
		localHostField.setMinWidth(170);
		localHostField.setMaxWidth(170);
		
		portField = new TextField("24004");
		portField.setStyle("-fx-background-color :  -color-30;-fx-font-size: 16;-fx-text-fill: white;");
		portField.setPromptText("Port");
		portField.setId("textfeild");
		portField.setMinWidth(80);
		portField.setMaxWidth(80);
		
		remoteHostField = new TextField();
		remoteHostField.setStyle("-fx-background-color :  -color-30;-fx-font-size: 16;-fx-text-fill: white;");
		remoteHostField.setPromptText("Target Host");
		remoteHostField.setId("textfeild");
		remoteHostField.setMinWidth(160);
		remoteHostField.setMaxWidth(160);
		
		addButton 	= ViewUtils.jfxbutton("OPEN IMAGE", null, 100, 40, Color.BLACK, Color.WHITE, Color.AQUA, "add",2);
		startButton 	= ViewUtils.jfxbutton("START", null, 100, 40, Color.BLACK, Color.WHITE, Color.AQUA, "add",2);
		resetButton 	= ViewUtils.jfxbutton(null, FontAwesomeIcon.RECYCLE, 40, 40, Color.BLACK, Color.WHITE, Color.AQUA, "add",2);
		
		HBox top = new HBox(10,label, ViewUtils.hspace(),typeField, networkInterfaceField, localHostField,portField,remoteHostField, ViewUtils.hspace(), addButton,  startButton, resetButton,ViewUtils.hspace());
		top.setPadding(new Insets(4, 10, 4, 10));
		
		canvas = new Canvas();
		g = canvas.getGraphicsContext2D(); 
		BorderPane center = new BorderPane(canvas);
		canvas.widthProperty().bind(center.widthProperty());
		canvas.heightProperty().bind(center.heightProperty());
		main.setCenter(center);
		main.setTop(top);
			
		networkInterfaceField.getSelectionModel().selectedItemProperty().addListener((ChangeListener<NetworkInterface>) (observable, oldValue, newValue) ->
		{
			localHostField.getSelectionModel().clearSelection();
			if(newValue != null)
			{
				localHostField.getItems().setAll(nisMap.get(newValue));
				localHostField.setDisable(false);
			}
		});
		NetworkInterface first = nisMap.keySet().iterator().next();
		networkInterfaceField.setValue(first);
		localHostField.setValue(nisMap.get(first).iterator().next());
		
		typeField.getSelectionModel().selectedItemProperty().addListener((ChangeListener<TYPE>) (observable, oldValue, newValue) ->
		{
			if(newValue != null)
			{
				type = newValue;
				networkInterfaceField.setDisable(false);
				localHostField.setDisable(false);
				portField.setDisable(false);
				
				if(TYPE.TRANSMITTER == newValue)
				{
					remoteHostField.setDisable(false);
					addButton.setDisable(false);
					startButton.setDisable(true);
					
					imageTransmitter = new ImageTransmitter(this,configuration, g, canvas.getWidth(), canvas.getHeight());
				}
				else if(TYPE.RECEIVER == newValue)
				{
					remoteHostField.setText(null);
					remoteHostField.setDisable(true);
					addButton.setDisable(true);
					startButton.setDisable(false);
					imageReceiver = new ImageReceiver(this, configuration, g, canvas.getWidth(), canvas.getHeight(),canvas);
				}
			}
		});
		
		
		addButton.setOnAction(e ->
		{
			addButton.setDisable(true);
			File selectedFile = AppManager.INSTANCE.showOpenFile(AppTransfer.class);
			if(selectedFile != null)
			{
				try
				{
					imageTransmitter.onNewFile(selectedFile);
					addButton.setDisable(true);
					startButton.setDisable(false);
				}
				catch (Exception ex)
				{
					AppManager.INSTANCE.showError(AppTransfer.class,log,"ON ACTION FAILED : ",ex);
				}
			}
		});
		
		startButton.setOnAction(e ->
		{
			try
			{
				
				NetworkInterface networkInterface 		= networkInterfaceField.getValue();
				InetAddress localAddress 				= localHostField.getValue();
				int port 								= Integer.parseInt(portField.getText().trim());
				String targetHost						= remoteHostField.getText();
				
				if(type == TYPE.RECEIVER)
				{
					imageReceiver.start(networkInterface, localAddress, port);
					startButton.setDisable(true);
				}
				else
				{
					imageTransmitter.start(networkInterface, localAddress, port, targetHost);
					startButton.setDisable(true);
					addButton.setDisable(true);
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppTransfer.class,log,"ON ACTION FAILED : ",ex);
			}
		});
		
		resetButton.setOnAction(e ->
		{
			reset();
		});
		reset();
		
		return main;
	}
	
	public void reset()
	{
		typeField.setValue(null);
		typeField.setDisable(false);
		
		networkInterfaceField.setDisable(true);
		localHostField.setDisable(true);
		portField.setDisable(true);
		remoteHostField.setDisable(true);
		addButton.setDisable(true);
		startButton.setDisable(true);
		canvas.setVisible(true);
	}
}
