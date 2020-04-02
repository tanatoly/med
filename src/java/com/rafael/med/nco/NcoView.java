package com.rafael.med.nco;

import java.net.NetworkInterface;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import com.rafael.med.AppManager;
import com.rafael.med.common.Constants;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.nco.NcoManager.Mode;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class NcoView extends BorderPane
{
	
	
private static final Logger log = LogManager.getLogger();
	
	private static final String BNET_REMOTE_ADDRESS 				= "50.50.50.7";
	private static final String BNET_REMOTE_PORT 					= "17018";
	private static final String BNET_LOCAL_PORT 					= "17017";
	private static final String BACKBONE_LOCAL_PORT 				= "44444";
	private static final String BACKBONE_MULTICAST_ADDRESS 			= "234.0.0.1";
	
	
	public JFXDrawersStack cardPane;
	private JFXDrawer drawer;

	public JFXButton closeButton;

	public ToggleGroup 		toggleGroup;
	public ToggleButton 	masterButton;
	public ToggleButton 	slaveButton;
	public ToggleButton 	standaloneButton;
	
	
	public JFXButton startStopScenarioButton;
	public Text stopScenarioText;
	public Text startScenarioText;

	public AircraftsPane aircraftPane;

	private HBox bnetBox;

	private HBox backboneBox;

	public JFXButton startStopButton;
	

	public NcoView(Stage stage)
	{
		setBackground(Constants.BACKGOUND_70);
		
		StackPane center			= new StackPane();	
		
		StackPane multiFunctionPane = new StackPane();
		multiFunctionPane.setPadding(new Insets(4));
		multiFunctionPane.setBackground(Constants.BACKGOUND_30);
		
		aircraftPane = new AircraftsPane();
	
		
	//	aircraftPane.setBackground(Constants.COLOR_25);
		StackPane.setAlignment(aircraftPane, Pos.CENTER);
		
		StackPane drawerPane 		= new StackPane(multiFunctionPane);
		cardPane = new JFXDrawersStack();
		((JFXDrawersStack)cardPane).setContent(aircraftPane);
		drawer = new JFXDrawer();
		drawer.setDirection(DrawerDirection.RIGHT);
		drawer.setSidePane(drawerPane);
		drawer.setOverLayVisible(false);
		drawer.setResizableOnDrag(false);
		cardPane.setUserData(drawer);
		((JFXDrawersStack)cardPane).toggle(drawer, false);
	    
	    center.getChildren().addAll(cardPane);	
		StackPane.setAlignment(aircraftPane, Pos.CENTER);

		
		Pane top = createTop();
		
		
		BorderPane.setMargin(center, new Insets(3));
		BorderPane.setMargin(top, new Insets(3));
		
		center.setBackground(Constants.BACKGOUND_20);
		top.setBackground(Constants.BACKGOUND_45);
		
		setCenter(center);
		setTop(top);
		
		
		
		stage.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> 
		{
			drawer.setDefaultDrawerSize((double) newValue);
		});
	}
	
	private Pane createTop()
	{
		// bnet fields
		Text t1 = new Text("BNET");
		t1.setFont(Constants.FONT_16);
		t1.setFill(Color.LIGHTGREY);
		
		JFXComboBox<String> bnetLocalAddressCombo = new JFXComboBox<String>();
		bnetLocalAddressCombo.setPrefWidth(170);
		bnetLocalAddressCombo.setPromptText("Select address");
		
		
		
		
		Text t11 = new Text(":");
		t11.setFont(Constants.FONT_14);
		t11.setFill(Color.WHITE);
		
		TextField bnetLocalPortField 		= new JFXTextField(BNET_LOCAL_PORT);
		bnetLocalPortField.setPrefWidth(50);
		
		Text t12 = new Text("-");
		t12.setFont(Constants.FONT_14);
		t12.setFill(Color.WHITE);
		
		JFXTextField bnetRemoteAddressField = new JFXTextField(BNET_REMOTE_ADDRESS);
		bnetRemoteAddressField.setPrefWidth(120);
		
		Text t13 = new Text(":");
		t13.setFont(Constants.FONT_14);
		t13.setFill(Color.WHITE);
		
		TextField bnetRemotePortField 		= new JFXTextField(BNET_REMOTE_PORT);
		bnetRemotePortField.setPrefWidth(50);
		
		
		bnetBox = new HBox(5,t1, ViewUtils.hspace(20), bnetLocalAddressCombo, t11, bnetLocalPortField, t12, bnetRemoteAddressField, t13, bnetRemotePortField);
		bnetBox.setAlignment(Pos.CENTER);
		bnetBox.setBorder(new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
		bnetBox.setPadding(new Insets(1, 10, 1, 10));
		
		
		for (Node node : bnetBox.getChildren())
		{
			node.setFocusTraversable(false);
		}
		
		// backbone fields
		Text t2 = new Text("BACKBONE");
		t2.setFont(Constants.FONT_16);
		t2.setFill(Color.LIGHTGRAY);
		
		
		JFXComboBox<String> backboneLocalAddressCombo = new JFXComboBox<String>();
		backboneLocalAddressCombo.setPrefWidth(170);
		backboneLocalAddressCombo.setPromptText("Select address");
		
		
		Text t21 = new Text(":");
		t21.setFont(Constants.FONT_14);
		t21.setFill(Color.WHITE);
		
		TextField backboneLocalPortField 	= new JFXTextField(BACKBONE_LOCAL_PORT);
		backboneLocalPortField.setPrefWidth(50);
		
		Text t22 = new Text("-");
		t22.setFont(Constants.FONT_14);
		t22.setFill(Color.WHITE);
		
		
		JFXTextField backboneRemoteAddressField = new JFXTextField(BACKBONE_MULTICAST_ADDRESS);
		backboneRemoteAddressField.setPrefWidth(120);
		backboneLocalPortField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			// TODO Auto-generated method stub
			
		});
		
		
		Text t23 = new Text(":");
		t23.setFont(Constants.FONT_14);
		t23.setFill(Color.WHITE);
		
		Label backboneRemoteAddressPort = new Label();
		backboneRemoteAddressPort.setFont(Constants.FONT_14);
		backboneRemoteAddressPort.setTextFill(Color.WHITE);
		backboneRemoteAddressPort.textProperty().bind(backboneLocalPortField.textProperty());
		
		backboneRemoteAddressPort.setMinWidth(50);
		backboneRemoteAddressPort.setMaxWidth(50);
		
		
		backboneBox = new HBox(5,t2, ViewUtils.hspace(20), backboneLocalAddressCombo, t21, backboneLocalPortField, t22, backboneRemoteAddressField, t23, backboneRemoteAddressPort);
		backboneBox.setAlignment(Pos.CENTER);
		backboneBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(2.0), BorderWidths.DEFAULT)));
		backboneBox.setPadding(new Insets(1, 10, 1, 10));
		
		
		for (Node node : backboneBox.getChildren())
		{
			node.setFocusTraversable(false);
		}
		
		Map<String, NetworkInterface> addressNetworkMap = Utilities.getAddressNetworkMap();
		for (String hostname : addressNetworkMap.keySet())
		{
			bnetLocalAddressCombo.getItems().add(hostname);
			backboneLocalAddressCombo.getItems().add(hostname);
		}
		
		
		
		
		
		
		toggleGroup 		= new ToggleGroup();
		masterButton		= createButton(toggleGroup, Mode.MASTER);
		slaveButton			= createButton(toggleGroup, Mode.SLAVE);
		standaloneButton	= createButton(toggleGroup, Mode.STANDALONE);
		
		
		
	
		
		HBox buttonPane = new HBox(10,masterButton,slaveButton/*, standaloneButton*/);
		buttonPane.setAlignment(Pos.CENTER);
		
		
		Text startText 				= ViewUtils.glyphIcon(FontAwesomeIcon.CHAIN_BROKEN, String.valueOf(40 * 0.7),Color.WHITE);
		Text stopText	 			= ViewUtils.glyphIcon(FontAwesomeIcon.CHAIN, String.valueOf(40 * 0.7),Color.WHITE);
		startStopButton 	= ViewUtils.jfxbutton(60,40,Color.BLACK,Color.AQUA, null, 2);
		startStopButton.setGraphic(startText);	
		startStopButton.setDisable(true);
		
		closeButton 		= ViewUtils.jfxbutton(null, FontAwesomeIcon.CLOSE, 60, 40, Color.BLACK,Color.WHITE, Color.AQUA,null,2);
		closeButton.setDisable(true);
	
		
		Button startStopScenarioButton 	= createStartStopScenarioButton();
		
		
		closeButton.setOnAction(e -> 
		{
			try
			{
				
				((JFXDrawersStack)cardPane).toggle((JFXDrawer) cardPane.getUserData(),false);
				closeButton.setDisable(true);
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppNco.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		startStopButton.setOnAction( e-> 
		{
			try
			{
				Text text = (Text) startStopButton.getGraphic();
				
				boolean isStart = text == startText;
				setTopDisable(isStart || NcoManager.INSTANCE.isOnceStarted.get(), isStart);
				
				
				if(isStart) 
				{
					String bnetLocalHost 		= bnetLocalAddressCombo.getValue();
					String bnetLocalPort 		= bnetLocalPortField.getText();
					String backboneLocalHost	= backboneLocalAddressCombo.getValue();
					String backboneLocalPort	= backboneLocalPortField.getText();
					String bnetRemoteHost 		= bnetRemoteAddressField.getText();
					String bnetRemotePort 		= bnetRemotePortField.getText();
					String backboneMulticast 	= backboneRemoteAddressField.getText();
					
					NcoManager.INSTANCE.start(bnetLocalHost, bnetLocalPort, bnetRemoteHost, bnetRemotePort, backboneLocalHost, backboneLocalPort, backboneMulticast);
					startStopButton.setGraphic(stopText);
				}
				else
				{
					NcoManager.INSTANCE.stop();
					startStopButton.setGraphic(startText);
				}
				Toggle selectedToggle = toggleGroup.getSelectedToggle();
				startStopScenarioButton.setDisable( !(selectedToggle != null && selectedToggle != slaveButton && NcoManager.INSTANCE.isRunning.get()));
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppNco.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		
		
		
			
		
		bnetLocalAddressCombo.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) ->
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
								&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);
			
			startStopButton.setDisable(!isEnable);					
		});
		
		bnetLocalPortField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			startStopButton.setDisable(!isEnable);
		});
		
		bnetRemoteAddressField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			startStopButton.setDisable(!isEnable);					
		});
		
		bnetRemotePortField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			startStopButton.setDisable(!isEnable);					
		});
		
		backboneLocalAddressCombo.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) ->
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			startStopButton.setDisable(!isEnable);					
		});
		
		backboneLocalPortField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			startStopButton.setDisable(!isEnable);					
		});
		
		backboneRemoteAddressField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			
			
			startStopButton.setDisable(!isEnable);					
		});
		
		
		toggleGroup.selectedToggleProperty().addListener((ChangeListener<Toggle>) (observable, oldValue, newValue) -> 
		{
			boolean isEnable = StringUtils.isNotBlank(bnetLocalAddressCombo.getValue()) && StringUtils.isNumeric(bnetLocalPortField.getText()) && StringUtils.isNotBlank(bnetRemoteAddressField.getText()) && StringUtils.isNumeric(bnetRemotePortField.getText())
					&& StringUtils.isNotBlank(backboneLocalAddressCombo.getValue()) && StringUtils.isNumeric(backboneLocalPortField.getText()) && StringUtils.isNotBlank(backboneRemoteAddressField.getText()) && (toggleGroup.getSelectedToggle() != null);

			
			
			
			startStopButton.setDisable(!isEnable);
			
		});
		
		
		
		
		
		HBox top = new HBox(10);
		top.setAlignment(Pos.CENTER_LEFT);
	
		
		top.getChildren().addAll(bnetBox, backboneBox, ViewUtils.hspace(), buttonPane, ViewUtils.hspace(), startStopButton, startStopScenarioButton, closeButton);

		top.setMaxHeight(50);
		top.setMinHeight(50);
		
		return top;
	}
	
	private void setTopDisable(boolean isDisable, boolean isTogggleDisable)
	{
		masterButton.setDisable(isTogggleDisable);
		slaveButton.setDisable(isTogggleDisable);
		standaloneButton.setDisable(isTogggleDisable);
		
		for (Node node : bnetBox.getChildren())
		{
			node.setDisable(isDisable);
		}
		
		for (Node node : backboneBox.getChildren())
		{
			node.setDisable(isDisable);
		}
	}
	
	private ToggleButton createButton(ToggleGroup toggleGroup, Mode mode)
    {
    	ToggleButton toggleButton = new ToggleButton(mode.name())
    	{
    		@Override
    		public void fire()
    		{
    			if (getToggleGroup() == null || !isSelected()) { super.fire();} // we don't toggle from selected to not selected if part of a group
    		}
    	};
    	
    	toggleButton.setAlignment(Pos.CENTER);
    	toggleButton.setTextAlignment(TextAlignment.CENTER);
//    	toggleButton.setMinSize(120, 40);
//    	toggleButton.setMaxSize(120, 40);
    	
    	toggleButton.setMinSize(140, 40);
    	toggleButton.setMaxSize(140, 40);
    	
    	toggleButton.setId("toggle-button");
    	toggleGroup.getToggles().add(toggleButton);
    	toggleButton.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) ->
    	{
    		if(newValue)
    		{
    			NcoManager.INSTANCE.selectedModeBeforeMessage = mode;
    		}
		});
    	return toggleButton;
    }
	
	Button createStartStopScenarioButton()
	{
		startScenarioText 				= ViewUtils.glyphIcon(FontAwesomeIcon.PLAY_CIRCLE, String.valueOf(40 * 0.7),Color.WHITE);
		stopScenarioText	 			= ViewUtils.glyphIcon(FontAwesomeIcon.STOP_CIRCLE, String.valueOf(40 * 0.7),Color.WHITE);
		startStopScenarioButton 		= ViewUtils.jfxbutton(60,40,Color.BLACK,Color.AQUA, null, 2);
		startStopScenarioButton.setGraphic(startScenarioText);	
		startStopScenarioButton.setDisable(true);
		
		startStopScenarioButton.setOnAction( e-> 
		{
			try
			{
				Text text = (Text) startStopScenarioButton.getGraphic();
				
				boolean isStartScenario = text == startScenarioText;
				if(isStartScenario) 
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.START_SCENARIO, null);
				}
				else
				{
					NcoManager.INSTANCE.backboneController.toOutgoingMessage(BackboneMessageBuilder.STOP_SCENARIO, null);
				}
			}
			catch (Exception ex)
			{
				AppManager.INSTANCE.showError(AppNco.class, log, "ON ACTION ERROR : ", ex);
			}
		});
		return startStopScenarioButton;
	}
}
