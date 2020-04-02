package com.rafael.med.ans;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.rafael.med.ans.ChannelData.SendType;
import com.rafael.med.ans.ChannelData.Type;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class ChannelDataView extends GridPane
{
	private TextField 							nameField 					= new JFXTextField();
	private TextField 							inCapacityField				= new JFXTextField("1024");
	private ComboBox<ByteOrder> 				byteOrderField				= new JFXComboBox<>(FXCollections.observableArrayList(ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN));
	private ComboBox<NetworkInterface> 			networkInterfaceField		= new JFXComboBox<>();
	private ComboBox<InetAddress> 				localHostField				= new JFXComboBox<>();
	private TextField 							localPortField				= new JFXTextField("20000");
	private TextField 							multicastField				= new JFXTextField("243.0.0.1");
	private TextField 							remoteHostField				= new JFXTextField("127.0.0.1");
	private TextField 							remotePortField				= new JFXTextField("20000");

	private TextField 							dataAmountField				= new JFXTextField("160");
	private TextField 							dataBytesField				= new JFXTextField();
	private ComboBox<ChannelData.SendType> 		sendTypeField				= new JFXComboBox<>();
	private TextField 							sendPeriodInmsField			= new JFXTextField("200");
	private ComboBox<ChannelData.Type> 			typeField					= new JFXComboBox<>(FXCollections.observableArrayList(Type.values()));

	private ChannelData currentData;



	public ChannelDataView()
	{
		Map<NetworkInterface, Set<InetAddress>> map = new LinkedHashMap<>();
		ObservableList<NetworkInterface> networkInterfaces = FXCollections.observableArrayList();

		try {
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
			{
				NetworkInterface networkInterface = e.nextElement();
				if(networkInterface.isUp())
				{
					networkInterfaces.add(networkInterface);
					Set<InetAddress> addresses = new LinkedHashSet<>();
					map.put(networkInterface, addresses);

					for (InterfaceAddress currentIntAdress : networkInterface.getInterfaceAddresses())
					{
						InetAddress address = currentIntAdress.getAddress();
						addresses.add(address);
					}
				}
			}
		} catch (SocketException e) 
		{
			throw new RuntimeException(e);
		}

		networkInterfaceField.setItems(networkInterfaces);
		byteOrderField.setItems(FXCollections.observableArrayList(ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN));


		sendTypeField.setItems(FXCollections.observableArrayList(SendType.values()));

		nameField.setPromptText("Name");
		nameField.setMinWidth(300);
		nameField.setMaxWidth(300);


		networkInterfaceField.setPromptText("Interface");
		networkInterfaceField.setMinWidth(300);
		networkInterfaceField.setMaxWidth(300);


		localHostField.setPromptText("Local host");
		localHostField.setDisable(true);
		localHostField.setMinWidth(200);
		localHostField.setMaxWidth(200);


		localPortField.setPromptText("Local port");
		localPortField.setMinWidth(160);
		localPortField.setMaxWidth(160);


		byteOrderField.setPromptText("Byte order");
		byteOrderField.setMinWidth(160);
		byteOrderField.setMaxWidth(160);


		typeField.setMinWidth(300);
		typeField.setMaxWidth(300);


		inCapacityField.setPromptText("Input capacity (bytes)");
		inCapacityField.setMinWidth(160);
		inCapacityField.setMaxWidth(160);
		inCapacityField.setDisable(true);

		remoteHostField.setPromptText("Remote host");
		remoteHostField.setMinWidth(200);
		remoteHostField.setMaxWidth(200);
		remoteHostField.setDisable(true);

		remotePortField.setPromptText("Remote port");
		remotePortField.setMinWidth(160);
		remotePortField.setMaxWidth(160);
		remotePortField.setDisable(true);


		multicastField.setPromptText("Join to multicast");
		multicastField.setMinWidth(160);
		multicastField.setMaxWidth(160);
		multicastField.setDisable(true);

		sendTypeField.setPromptText("Send Type");
		sendTypeField.setMinWidth(300);
		sendTypeField.setMaxWidth(300);
		sendTypeField.setDisable(true);

		sendPeriodInmsField.setPromptText("Period in ms");
		sendPeriodInmsField.setMinWidth(300);
		sendPeriodInmsField.setMaxWidth(300);
		sendPeriodInmsField.setDisable(true);

		dataAmountField.setPromptText("Amount");
		dataAmountField.setMinWidth(100);
		dataAmountField.setMaxWidth(100);
		dataAmountField.setDisable(true);


		dataBytesField.setPromptText("Data in format -> index of byte : byte value (5:89, 6:0, 7:0 ,8:0, 9:12)");
		dataBytesField.setMinWidth(500);
		dataBytesField.setMaxWidth(500);
		dataBytesField.setDisable(true);



		RowConstraints r 	= new RowConstraints(70);
    	ColumnConstraints c = new ColumnConstraints();
    	c.setPercentWidth(25);
    	getRowConstraints().addAll(r, r, r, r, r ,r,r);
    	getColumnConstraints().addAll(c,c,c,c);

    	GridPane.setConstraints(nameField, 					1, 0, 2, 1, HPos.CENTER,VPos.CENTER);

    	GridPane.setConstraints(networkInterfaceField, 		0, 1, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(localHostField, 			1, 1, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(localPortField, 			2, 1, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(byteOrderField, 			3, 1, 1, 1, HPos.CENTER,VPos.CENTER);

    	GridPane.setConstraints(typeField, 					1, 2, 2, 1, HPos.CENTER,VPos.CENTER);


    	GridPane.setConstraints(inCapacityField, 			0, 3, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(remoteHostField, 			1, 3, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(remotePortField, 			2, 3, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(multicastField, 			3, 3, 1, 1, HPos.CENTER,VPos.CENTER);

    	GridPane.setConstraints(sendTypeField, 				0, 4, 2, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(sendPeriodInmsField, 		2, 4, 2, 1, HPos.CENTER,VPos.CENTER);

    	GridPane.setConstraints(dataAmountField, 			0, 5, 1, 1, HPos.CENTER,VPos.CENTER);
    	GridPane.setConstraints(dataBytesField, 			1, 5, 3, 1, HPos.CENTER,VPos.CENTER);



    	getChildren().addAll(nameField,typeField, inCapacityField,byteOrderField, localHostField,networkInterfaceField, localPortField,multicastField,remoteHostField,remotePortField,dataAmountField,dataBytesField, sendTypeField,sendPeriodInmsField);



    	networkInterfaceField.getSelectionModel().selectedItemProperty().addListener((ChangeListener<NetworkInterface>) (observable, oldValue, newValue) ->
		{
			localHostField.getSelectionModel().clearSelection();
			if(newValue != null)
			{
				localHostField.getItems().setAll(map.get(newValue));
				localHostField.setDisable(false);
			}
		});

    	typeField.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Type>) (observable, oldValue, newValue) ->
		{

			if(newValue != null)
			{
				localPortField.setDisable(false);
				if(newValue == Type.RECEIVING_ONLY)
				{
					inCapacityField.setDisable(false);
					remoteHostField.setDisable(true);
					remotePortField.setDisable(true);
					multicastField.setDisable(false);
					sendTypeField.setDisable(true);
					dataAmountField.setDisable(true);
					dataBytesField.setDisable(true);
					inCapacityField.setDisable(false);
				}
				else if(newValue == Type.SENDING_ONLY)
				{
					inCapacityField.setDisable(true);
					remoteHostField.setDisable(false);
					remotePortField.setDisable(false);
					multicastField.setDisable(true);
					sendTypeField.setDisable(false);
					dataAmountField.setDisable(false);
					dataBytesField.setDisable(false);
					inCapacityField.setDisable(false);
				}
				else if(newValue == Type.RECEIVEING_AND_SENDING)
				{
					inCapacityField.setDisable(false);
					remoteHostField.setDisable(false);
					remotePortField.setDisable(false);
					multicastField.setDisable(false);
					sendTypeField.setDisable(false);
					dataAmountField.setDisable(false);
					dataBytesField.setDisable(false);
					inCapacityField.setDisable(false);
				}
			}
			else
			{
				inCapacityField.setDisable(true);
				remoteHostField.setDisable(true);
				remotePortField.setDisable(true);
				multicastField.setDisable(true);
				sendTypeField.setDisable(true);
				sendPeriodInmsField.setDisable(true);
				dataAmountField.setDisable(true);
				dataBytesField.setDisable(true);
			}
		});

    	sendTypeField.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SendType>) (observable, oldValue, newValue) ->
		{
			if(newValue != null)
			{
				sendPeriodInmsField.setDisable(newValue == SendType.BY_CLICK);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public void setData(ChannelData channelData)
	{
		this.currentData = channelData;
		if(channelData != null)
		{
			nameField.setText(channelData.name);
			inCapacityField.setText(channelData.inCapacity);
			byteOrderField.setValue(channelData.byteOrder);
			networkInterfaceField.setValue(channelData.networkInterface);
			localHostField.setValue(channelData.localHost);
			localPortField.setText(channelData.localPort);
			multicastField.setText(channelData.multicast);
			remoteHostField.setText(channelData.remoteHost);
			remotePortField.setText(channelData.remotePort);
			dataAmountField.setText(channelData.dataAmount);
			dataBytesField.setText(channelData.dataBytes);
			sendTypeField.setValue(channelData.sendType);
			sendPeriodInmsField.setText(channelData.sendPeriodInms);
			typeField.setValue(channelData.type);
		}
		else
		{
			ObservableList<Node> children = getChildren();
			for (Node node : children)
			{
				if (node instanceof TextField)
				{
					TextField textField = (TextField) node;
					textField.setText(null);
				}
				else if (node instanceof ComboBox)
				{
					ComboBox comboBox = (ComboBox) node;
					comboBox.getSelectionModel().clearSelection();
				}
			}

			byteOrderField.getSelectionModel().select(ByteOrder.BIG_ENDIAN);
		}
	}


	public ChannelData getData()
	{
		if(currentData != null)
		{
			currentData.name 				= nameField.getText();
			currentData.inCapacity 			= inCapacityField.getText();
			currentData.byteOrder 			= byteOrderField.getValue();
			currentData.networkInterface 	= networkInterfaceField.getValue();
			currentData.localHost 			= localHostField.getValue();
			currentData.localPort 			= localPortField.getText();
			currentData.multicast 			= multicastField.getText();
			currentData.remoteHost 			= remoteHostField.getText();
			currentData.remotePort 			= remotePortField.getText();
			currentData.type				= typeField.getValue();
			currentData.dataAmount 			= dataAmountField.getText();
			currentData.dataBytes 			= dataBytesField.getText();
			currentData.sendType 			= sendTypeField.getValue();
			currentData.sendPeriodInms 		= sendPeriodInmsField.getText();
		}
		else
		{
			currentData = new ChannelData(nameField.getText(), inCapacityField.getText(), byteOrderField.getValue(), networkInterfaceField.getValue(), localHostField.getValue(), localPortField.getText(), typeField.getValue(),multicastField.getText(), remoteHostField.getText(), remotePortField.getText(), dataAmountField.getText(), dataBytesField.getText(), sendTypeField.getValue(), sendPeriodInmsField.getText(), null, null,null);
		}

		currentData.init();
		return currentData;
	}

	public String validate()
	{
		StringBuilder builder = new StringBuilder();
		if(StringUtils.isBlank(nameField.getText()))
		{
			builder.append("nameField is bLank\n");
		}
		if(networkInterfaceField.getValue() == null)
		{
			builder.append("networkInterfaceField is bLank\n");
		}
		if(localHostField.getValue() == null)
		{
			builder.append("localHostField is bLank\n");
		}
		if(!StringUtils.isNumeric(localPortField.getText()))
		{
			builder.append("localPortField is bLank or is not numeric\n");
		}
		if(byteOrderField.getValue() == null)
		{
			builder.append("byteOrderField is bLank\n");
		}

		if(typeField.getValue() == null)
		{
			builder.append("typeField is bLank\n");
		}
		else
		{

			if(typeField.getValue() == Type.RECEIVING_ONLY)
			{
				if(!StringUtils.isNumeric(inCapacityField.getText()))
				{
					builder.append("inCapacityField is bLank or is not numeric\n");
				}
			}
			else if(typeField.getValue() == Type.SENDING_ONLY)
			{
				if(StringUtils.isBlank(remoteHostField.getText()))
				{
					builder.append("remoteHostField is bLank\n");
				}
				if(!StringUtils.isNumeric(remotePortField.getText()))
				{
					builder.append("remotePortField is bLank or is not numeric\n");
				}
				if(sendTypeField.getValue() == null)
				{
					builder.append("sendTypeField is bLank\n");
				}
				else
				{
					if(sendTypeField.getValue() == SendType.PERIODIC && !StringUtils.isNumeric(sendPeriodInmsField.getText()))
					{
						builder.append("sendPeriodInmsField is bLank or is not numeric\n");
					}
				}
				if(!StringUtils.isNumeric(dataAmountField.getText()))
				{
					builder.append("dataAmountField is bLank or is not numeric\n");
				}


			}
			else if(typeField.getValue() == Type.RECEIVEING_AND_SENDING)
			{
				if(!StringUtils.isNumeric(inCapacityField.getText()))
				{
					builder.append("inCapacityField is bLank or is not numeric\n");
				}
				if(StringUtils.isBlank(remoteHostField.getText()))
				{
					builder.append("remoteHostField is bLank\n");
				}
				if(!StringUtils.isNumeric(remotePortField.getText()))
				{
					builder.append("remotePortField is bLank or is not numeric\n");
				}
				if(sendTypeField.getValue() == null)
				{
					builder.append("sendTypeField is bLank\n");
				}
				else
				{
					if(sendTypeField.getValue() == SendType.PERIODIC && !StringUtils.isNumeric(sendPeriodInmsField.getText()))
					{
						builder.append("sendPeriodInmsField is bLank or is not numeric\n");
					}
				}
				if(!StringUtils.isNumeric(dataAmountField.getText()))
				{
					builder.append("dataAmountField is bLank or is not numeric\n");
				}
			}
		}


		if(builder.length() > 0 )
		{
			return builder.toString();
		}
		return null;
	}
}
