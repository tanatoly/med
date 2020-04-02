package com.rafael.med.spect;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AppSpect extends AppBase
{
	private static final Logger log = LogManager.getLogger();
	
	
	private Pane 			firstView;
	private BorderPane 		main;
	private SpectsPane		spectsPane;

	public Label 			testNameLabel;
	private String			currentNodeIdText;
	private ManetType		currentManetType;

	private AtomicBoolean isFirst = new AtomicBoolean(true);


private JFXButton addButton;
private Map<String, NetworkInterface> inMap;
	
	@Override
	public Parent init(Configuration configuration, Stage stage, List<String> args) 
	{
		main = new BorderPane();
		main.setStyle("-fx-background-color :  -color-30;");		
		StackPane center		= new StackPane();		
		BorderPane.setMargin(center, new Insets(0, 4, 4, 4));
		center.setStyle("-fx-background-color :  -color-40;");
		main.setCenter(center);
	    firstView					= createFirstView();
	    spectsPane					= new SpectsPane();
	   
	    center.setPadding(new Insets(6));	    
	    	    
	    center.getChildren().addAll(firstView,spectsPane);	
		
		firstView.setVisible(true);
		spectsPane.setVisible(false);
		
		Pane top = createTop();
		main.setTop(top);
		return main;
	}

	
	private Pane createTop() 
	{
		
		
		Text expandedTrue 			= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_DOWN, String.valueOf(40 * 0.7),Color.WHITE);
		Text expandedFalse	 		= ViewUtils.glyphIcon(FontAwesomeIcon.CHEVRON_RIGHT, String.valueOf(40 * 0.7),Color.WHITE);
		JFXButton expandedButton 	= ViewUtils.jfxbutton(60,40,Color.BLACK,Color.AQUA, null, 2);
		expandedButton.setGraphic(expandedFalse);	
		expandedButton.setVisible(false);
		expandedButton.setOnAction( e-> 
		{
			spectsPane.onExpandedAction(expandedButton, expandedTrue, expandedFalse);
		});
		
		inMap = new TreeMap<>();
		try 
		{
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
			{
				NetworkInterface localni = e.nextElement();
				if(!localni.isLoopback() && !localni.isVirtual() && localni.isUp())
				{
					for (InterfaceAddress currentIntAdress : localni.getInterfaceAddresses())
					{
						InetAddress address = currentIntAdress.getAddress();
						if (address instanceof Inet4Address) 
						{
							String hostname = address.getHostAddress();
							inMap.put(hostname, localni);
						}
					}
				}
			}
		} 
		catch (SocketException e1)
		{
			e1.printStackTrace();
		}
		
		
		ComboBox<String> ipField = new ComboBox<String>();
		for (String hostname : inMap.keySet())
		{
			ipField.getItems().add(hostname);
		}
		
		ipField.setValue(ipField.getItems().get(0));
		ipField.setFocusTraversable(false);
		
		
		TextField nodeIdField = new TextField();
		nodeIdField.setPromptText("NODE ID");
		nodeIdField.setFocusTraversable(false);
		
		Spinner<ManetType> manetTypeSpinner = new Spinner<ManetType>(FXCollections.observableArrayList(ManetType.values()));
		manetTypeSpinner.setFocusTraversable(false);
		manetTypeSpinner.setMinWidth(400);
		
		currentManetType = ManetType.values()[0];

		addButton 		= ViewUtils.jfxbutton(null, FontAwesomeIcon.PLUS_SQUARE, 60, 40, Color.BLACK,Color.WHITE, Color.AQUA,null,2);
		addButton.setDisable(true);
		addButton.setFocusTraversable(false);
		
		
		
		
		HBox colors = new HBox();
		colors.setAlignment(Pos.CENTER_RIGHT);
		for (int i = 0; i < SpectSegment.BACKGROUNDS.length; i++) 
		{
			Label label = new Label();
			label.setMaxSize(4, 30);
			label.setMinSize(4, 30);
			label.setBackground( SpectSegment.BACKGROUNDS[i]);
			label.setTooltip(new Tooltip(String.valueOf(i)));
			colors.getChildren().add(label);
		}
		
		
		nodeIdField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			currentNodeIdText = newValue;
			checkIfDisable();
			
		});
		
		manetTypeSpinner.valueProperty().addListener((ChangeListener<ManetType>) (observable, oldValue, newValue) -> 
		{
			currentManetType = newValue;
			checkIfDisable();
		});
		
		
		addButton.setOnAction(e -> 
		{
			try
			{
				if(isFirst.compareAndSet(true, false))
				{
					firstView.setVisible(false);
					spectsPane.setVisible(true);
					expandedButton.setVisible(true);
					String hostname = ipField.getValue();
					NetworkInterface networkInterface = inMap.get(hostname);
					SpectManager.INSTANCE.start(hostname, networkInterface);
					ipField.setDisable(true);
				}
				spectsPane.addSpect(Integer.parseInt(nodeIdField.getText()),manetTypeSpinner.getValue());	
				addButton.setDisable(true);
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppSpect.class,log,"ON ACTION FAILED : ",ex);
			}
		});
			
		HBox top = new HBox(20);
		top.setAlignment(Pos.CENTER_LEFT);
		BorderPane.setMargin(top, new Insets(4));
		top.setPadding(new Insets(4, 0, 0, 0));		
		top.getChildren().addAll(expandedButton, ViewUtils.hspace(), ipField, nodeIdField,manetTypeSpinner, addButton, ViewUtils.hspace(), colors);
		return top;
	}

	
	private void checkIfDisable()
	{
		if(StringUtils.isNumeric(currentNodeIdText))
		{
			int opcode = currentManetType.opcode;
			boolean isExists = SpectManager.INSTANCE.isExists(Integer.parseInt(currentNodeIdText), opcode);
			addButton.setDisable(isExists);
		}
		else
		{
			addButton.setDisable(true);
		}
		
	}
		
	private Pane createFirstView()
	{
		Label label = new Label("Add folder with test recording");
		label.setStyle("-fx-font-size: 60;-fx-text-fill: white;");
		BorderPane pane = new BorderPane(label);
		return pane;
	}
}
