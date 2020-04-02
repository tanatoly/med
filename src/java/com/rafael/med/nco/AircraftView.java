package com.rafael.med.nco;

import java.io.File;

import org.controlsfx.tools.Borders;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.rafael.med.AppManager;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;
import com.rafael.med.nco.Aircraft.AircraftMode;
import com.rafael.med.nco.Aircraft.SAInputSelf;
import com.rafael.med.nco.Aircraft.SAInputTarget;
import com.rafael.med.nco.Aircraft.SAMember;
import com.rafael.med.nco.Aircraft.SAObject;
import com.rafael.med.nco.Aircraft.SATrack;
import com.rafael.med.nco.NcoManager.Mode;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AircraftView extends BorderPane
{
	private static final String TEXT_NULL = "---";
	

	public static final class TextFields
	{
		
		
		
		public StringProperty tf1                            = new SimpleStringProperty("1");
		public StringProperty tf2                            = new SimpleStringProperty("2");
		public StringProperty tf3                            = new SimpleStringProperty("3");
		public StringProperty tf4                            = new SimpleStringProperty("4");
		public StringProperty tf5                            = new SimpleStringProperty("5");
		public StringProperty tf6                            = new SimpleStringProperty("6");
		public StringProperty tf7                            = new SimpleStringProperty("7");
		public StringProperty tf8                            = new SimpleStringProperty("8");
		public StringProperty tf9                            = new SimpleStringProperty("9");
		public StringProperty tf10                           = new SimpleStringProperty("10");
		public StringProperty tf11                           = new SimpleStringProperty("11");
		public StringProperty tf12                           = new SimpleStringProperty("12");
		
		
		public void reset()
		{
			tf1.set(TEXT_NULL);
			tf2.set(TEXT_NULL);
			tf3.set(TEXT_NULL);
			tf4.set(TEXT_NULL);
			tf5.set(TEXT_NULL);
			tf6.set(TEXT_NULL);
			tf7.set(TEXT_NULL);
			tf8.set(TEXT_NULL);
			tf9.set(TEXT_NULL);
			tf10.set(TEXT_NULL);
			tf11.set(TEXT_NULL);
			tf12.set(TEXT_NULL);
		}
			
		public void setSelfObject(Object selfObject)
		{
			if(selfObject != null)
			{
				if (selfObject instanceof SAInputSelf)
				{
					SAInputSelf inputSelf = (SAInputSelf) selfObject;
					
					tf1.set(String.format("Pi %+8.2f", inputSelf.pitch));
					tf5.set(String.format("Ro %+8.2f", inputSelf.roll));
					tf9.set(String.format("Ya %+8.2f", inputSelf.heading));
					
					tf2.set(String.format("LAT %8.4f", inputSelf.latitude));
					tf6.set(String.format("LON %8.4f", inputSelf.lontitude));
					tf10.set(String.format("ALT %5.1f", inputSelf.altitude));
					
					tf3.set(String.format("VN %+8.2f", inputSelf.velocity_n));
					tf7.set(String.format("VE %+8.2f", inputSelf.velocity_e));
					tf11.set(String.format("VU %+8.2f", inputSelf.velocity_u));
					
					tf4.set(TEXT_NULL);
					tf8.set(TEXT_NULL);
					tf12.set(TEXT_NULL);
				}
				else if (selfObject instanceof SAMember)
				{
					SAMember selfMember = (SAMember) selfObject;
					
					tf1.set(String.format("GID %8X", selfMember.globalId));
					tf5.set(String.format("ALT %5.0f", selfMember.altitude));
					tf9.set(String.format("HD %+8.2f", selfMember.heading));
					
					tf2.set(String.format("BIMG %4d", selfMember.bimg));
					tf6.set(String.format("TIMG %4d", selfMember.timg));
					tf10.set(String.format("SIMG %4d", selfMember.simg));
					
					tf3.set(String.format("VX %+8.2f", selfMember.velocityX));
					tf7.set(String.format("VY %+8.2f", selfMember.velocityY));
					tf11.set(String.format("VZ %+8.2f", selfMember.velocityZ));
					
					tf4.set(String.format("CS %s", selfMember.callsign));
					tf8.set(String.format("ID %4d", selfMember.callsignId));
					tf12.set(String.format("Q1 %4d", selfMember.q1LockDisplayId));
				}
			}
			else
			{
				reset();
			}
		}
		
		
		public void setSelectedObject(Object selectedObject)
		{
			if(selectedObject != null)
			{
				if (selectedObject instanceof SAInputTarget)
				{
					SAInputTarget inputTarget = (SAInputTarget) selectedObject;
					
					tf1.set(String.format("TID %4d",inputTarget.track_id));
					tf5.set(TEXT_NULL);
					tf9.set(TEXT_NULL);
					
					tf2.set(String.format("AZ %+8.2f",inputTarget.azimuth));
					tf6.set(String.format("EL %+8.2f",inputTarget.elevation));
					tf10.set(String.format("RNG %+8.2f",inputTarget.range));
					
					tf3.set(String.format("VX %+8.2f",inputTarget.velocity_x));
					tf7.set(String.format("VY %+8.2f",inputTarget.velocity_y));
					tf11.set(String.format("VZ %+8.2f",inputTarget.velocity_z));
					
					tf4.set(TEXT_NULL);
					tf8.set(TEXT_NULL);
					tf12.set(TEXT_NULL);
				}
				else if (selectedObject instanceof SAObject)
				{
					SAObject saObject = (SAObject) selectedObject;
					
					tf1.set(String.format("GID %8X", saObject.globalId));
					tf5.set(String.format("ALT %5.0f", saObject.altitude));
					tf9.set(String.format("HD %+8.2f", saObject.heading));
					
					tf2.set(String.format("AZ %+8.2f",saObject.azimuth));
					tf6.set(String.format("EL %+8.2f",saObject.elevation));
					tf10.set(String.format("RNG %+8.2f",saObject.range));
					
					tf3.set(String.format("VX %+8.2f", saObject.velocityX));
					tf7.set(String.format("VY %+8.2f", saObject.velocityY));
					tf11.set(String.format("VZ %+8.2f", saObject.velocityZ));
					
					if(selectedObject instanceof SATrack)
					{
						SATrack saTrack = (SATrack) selectedObject;
						
						tf4.set(String.format("LID %8X",saTrack.asLeaderGlobalId));
						tf8.set(String.format("SRC %6d",saTrack.targetSource));
						tf12.set(String.format("TAG %6d",saTrack.tag));
					}
					else if(selectedObject instanceof SAMember)
					{
						SAMember saMember = (SAMember) selectedObject;
						
						tf4.set(String.format("CS %s", saMember.callsign));
						tf8.set(String.format("ID %4d", saMember.callsignId));
						tf12.set(String.format("Q1 %4d", saMember.q1LockDisplayId));
					}
				}
			}
			else
			{
				reset();
			}
		}
		
	}

	
	
	
	private Text idText;
	private Aircraft aircraft;
	public JFXButton scenarioButton;
	public RadarCanvas radarView;
	public SaCanvas saView;
	public DualCanvas dualView;
	private StackPane center;
	private Pane right;
	public ToggleButton dualToggleButton;
	private CanvasView currentView;
	
	public TextFields guiSelf 										= new TextFields();
	public TextFields guiSelected									= new TextFields();
	public JFXButton zoomInButton;
	public JFXButton zoomOutButton;
	public JFXButton q1LockButton;
	public JFXButton q2LockButton;
	public ToggleButton radarToggleButton;
	public ToggleButton saToggleButton;
	public Text scenarioName;
	public JFXSpinner scenarioRunner;
	public JFXButton rightPanelButton;

	public AircraftView(Aircraft aircraft)
	{
		this.aircraft = aircraft;
		
		center 		= new StackPane();
		radarView 	= new RadarCanvas(aircraft, Color.BLACK,center);
		saView 		= new SaCanvas(aircraft, Color.BLACK,center);
		dualView 	= new DualCanvas(aircraft, Color.BLACK,center);
		center.getChildren().addAll(radarView, saView, dualView);
		
		center.minWidthProperty().bind(this.heightProperty().subtract(4));
		center.maxWidthProperty().bind(this.heightProperty().subtract(4));
		
		setCenter(Borders.wrap(center).etchedBorder().outerPadding(0,4,0,4).innerPadding(0).radius(0).buildAll());
		
		right = createRight();
		right.minWidthProperty().bind(widthProperty().subtract(heightProperty()).subtract(10));
		right.maxWidthProperty().bind(widthProperty().subtract(heightProperty()).subtract(10));		
		setRight(right);
		
	}
	
	
	private Pane createRight()
	{
	
		GridPane right = new GridPane();
		
		right.setBackground(Constants.BACKGOUND_35);
	//	right.setGridLinesVisible(true);
		double rows = 12.0;
		double cols = 8.0;
		
		RowConstraints r9 = new RowConstraints();
		r9.setPercentHeight(9);
		
		RowConstraints r5 = new RowConstraints();
		r5.setPercentHeight(5);
		
		RowConstraints r2 = new RowConstraints();
		r2.setPercentHeight(2);
		
		right.getRowConstraints().addAll(r9,r9,r9,r2,r9,r5,r9,r9,r9,r5,r9,r9,r9);
		
		
		
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(100.0/cols);
		
		
		for (int i = 0; i < cols; i++)
		{
			right.getColumnConstraints().add(c);
		}
		
		
		idText = new Text(aircraft.host);
		idText.setFill(Color.AQUA);
		idText.setFont(Font.font(12));
		
		
		ToggleGroup  group 	= new ToggleGroup();
		radarToggleButton 	= createButton(group, AircraftMode.RADAR, radarView , center);
		saToggleButton 		= createButton(group, AircraftMode.SA, saView , center);
		dualToggleButton 	= createButton(group, AircraftMode.DUAL, dualView, center);
		
		scenarioName	= new Text();
		GridPane.setMargin(scenarioName, new Insets(0, 0, 0, 10));
		scenarioName.setFill(Constants.COLOR_90);
		scenarioRunner = new JFXSpinner();
		scenarioRunner.setScaleX(0.5);
		scenarioRunner.setScaleY(0.5);
		
		
		zoomInButton = ViewUtils.jfxbutton(FontAwesomeIcon.MINUS_SQUARE,Color.BLACK,Color.WHITE,Color.AQUA,null,1);
		zoomOutButton = ViewUtils.jfxbutton(FontAwesomeIcon.PLUS_SQUARE,Color.BLACK,Color.WHITE,Color.AQUA,null,1);
		
		q1LockButton = ViewUtils.jfxbutton("Q1",Color.BLACK,Color.AQUA,null,1);
		q1LockButton.setOnAction(e ->
		{
			aircraft.setQ(true);
		});
		q2LockButton = ViewUtils.jfxbutton("Q2",Color.BLACK,Color.AQUA,null,1);
		q2LockButton.setOnAction(e ->
		{
			aircraft.setQ(false);
		});
		
		
		JFXButton button3 = ViewUtils.jfxbutton("3",Color.BLACK,Color.AQUA,null,1);
		JFXButton button4 = ViewUtils.jfxbutton("4",Color.BLACK,Color.AQUA,null,1);
		
		scenarioButton = ViewUtils.jfxbutton(FontAwesomeIcon.FOLDER,Color.BLACK,Color.WHITE,Color.AQUA,null,1);
		
		
		scenarioButton.setOnAction(e ->
		{
			File scenarioFile = AppManager.INSTANCE.showOpenFile(AppNco.class);
			if(scenarioFile != null)
			{				
				aircraft.reset();
				scenarioButton.setDisable(false);
				ScenarioManager.INSTANCE.readFile(scenarioFile, aircraft);
				aircraft.isScenarioExists.set(true);
				scenarioName.setText(scenarioFile.getName());
			}
		});
		
		rightPanelButton = ViewUtils.jfxbutton(FontAwesomeIcon.LIST,Color.BLACK,Color.WHITE,Color.AQUA,null,1);
		rightPanelButton.setOnAction(e ->
		{
			NcoManager.INSTANCE.showRightPanel(aircraft);
		});
		
		
		zoomInButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		q1LockButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		button3.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		button4.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		zoomOutButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		q2LockButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		scenarioButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		rightPanelButton.maxWidthProperty().bind(right.widthProperty().divide(cols/2).subtract(8));
		
				      
		Text my1 = createText(false);
		my1.textProperty().bind(guiSelf.tf1);
		Text my2 = createText(false);
		my2.textProperty().bind(guiSelf.tf2);
		Text my3 = createText(false);
		my3.textProperty().bind(guiSelf.tf3);
		Text my4 = createText(false);
		my4.textProperty().bind(guiSelf.tf4);
		Text my5 = createText(false);
		my5.textProperty().bind(guiSelf.tf5);
		Text my6 = createText(false);
		my6.textProperty().bind(guiSelf.tf6);
		Text my7 = createText(false);
		my7.textProperty().bind(guiSelf.tf7);
		Text my8 = createText(false);
		my8.textProperty().bind(guiSelf.tf8);
		Text my9 = createText(false);
		my9.textProperty().bind(guiSelf.tf9);
		Text my10 = createText(false);
		my10.textProperty().bind(guiSelf.tf10);
		Text my11 = createText(false);
		my11.textProperty().bind(guiSelf.tf11);
		Text my12 = createText(false);
		my12.textProperty().bind(guiSelf.tf12);
		
		
		
		Text selected1 = createText(true);
		selected1.textProperty().bind(guiSelected.tf1);
		Text selected2 = createText(true);
		selected2.textProperty().bind(guiSelected.tf2);
		Text selected3 = createText(true);
		selected3.textProperty().bind(guiSelected.tf3);
		Text selected4 = createText(true);
		selected4.textProperty().bind(guiSelected.tf4);
		Text selected5 = createText(true);
		selected5.textProperty().bind(guiSelected.tf5);
		Text selected6 = createText(true);
		selected6.textProperty().bind(guiSelected.tf6);
		Text selected7 = createText(true);
		selected7.textProperty().bind(guiSelected.tf7);
		Text selected8 = createText(true);
		selected8.textProperty().bind(guiSelected.tf8);
		Text selected9 = createText(true);
		selected9.textProperty().bind(guiSelected.tf9);
		Text selected10 = createText(true);
		selected10.textProperty().bind(guiSelected.tf10);
		Text selected11 = createText(true);
		selected11.textProperty().bind(guiSelected.tf11);
		Text selected12 = createText(true);
		selected12.textProperty().bind(guiSelected.tf12);
		

		
		GridPane.setConstraints(idText, 				0, 0, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(radarToggleButton, 		2, 0, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(saToggleButton, 		4, 0, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(dualToggleButton, 		6, 0, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(scenarioName, 			0, 1, 6, 1, HPos.LEFT, VPos.TOP, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(scenarioRunner, 		6, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
		
		GridPane.setConstraints(zoomInButton, 			0, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(q1LockButton, 			2, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(button3, 				4, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(scenarioButton, 		6, 2, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(zoomOutButton, 			0, 4, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(q2LockButton, 			2, 4, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(button4, 				4, 4, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(rightPanelButton, 		6, 4, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		
		
		GridPane.setConstraints(my1, 					0, 6, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my2, 					2, 6, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my3, 					4, 6, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my4, 					6, 6, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my5, 					0, 7, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my6, 					2, 7, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my7, 					4, 7, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my8, 					6, 7, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my9, 					0, 8, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my10, 					2, 8, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my11, 					4, 8, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(my12, 					6, 8, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		
		GridPane.setConstraints(selected1, 				0, 10, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected2, 				2, 10, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected3, 				4, 10, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected4, 				6, 10, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected5, 				0, 11, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected6, 				2, 11, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected7, 				4, 11, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected8, 				6, 11, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected9, 				0, 12, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected10, 			2, 12, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected11, 			4, 12, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		GridPane.setConstraints(selected12, 			6, 12, 2, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER);
				
		right.getChildren().addAll(idText, radarToggleButton,saToggleButton,dualToggleButton,scenarioName,scenarioRunner, zoomInButton, q1LockButton, button3, button4, zoomOutButton, q2LockButton, scenarioButton,rightPanelButton, my1, my2, my3, my4, my5, my6, my7,my8, my9, my10, my11, my12,   selected1, selected2, selected3, selected4, selected5, selected6, selected7,selected8, selected9, selected10, selected11, selected12);
		
		guiSelf.tf12.set(CanvasView.RADAR_RANGE / 2/ 5 + " km");
		zoomInButton.setOnAction(e ->
		{
			aircraft.setCurrentZoom(true);
		});
		
		zoomOutButton.setOnAction(e ->
		{
			aircraft.setCurrentZoom(false);
		});
		
		
		return right;
	}

	public void reset(boolean isFull) 
	{
		try 
		{
			guiSelf.reset();
			guiSelected.reset();
			
			ObservableList<Node> children = right.getChildren();
			for (Node node : children)
			{
				if (node instanceof Button)
				{
					Button button = (Button) node;
					button.setDisable(true);
				}
			}
			radarView.reset();
			saView.reset();
			dualView.reset();
			
			radarToggleButton.setSelected(false);
			saToggleButton.setSelected(false);
			dualToggleButton.setSelected(false);
			
			
			scenarioRunner.setVisible(false);
			if(isFull)
			{
				scenarioName.setText(null);
			}
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void drawView()
	{
		try 
		{			
			if(currentView != null)
			{
				
				if(currentView == radarView)
				{
					guiSelf.setSelfObject(aircraft.saInputSelf);
					guiSelected.setSelectedObject(aircraft.selectedSaInputTarget);
				}
				else if(currentView == saView)
				{
					SAMember selfObject = null;
					for (SAObject saObject : aircraft.saObjects.values())
					{
						if (saObject instanceof SAMember) 
						{
							SAMember saMember = (SAMember) saObject;
							if(saMember.motherAc == 1)
							{
								selfObject = saMember;
								break;
							}
						}
					}
					
					guiSelf.setSelfObject(selfObject);
					guiSelected.setSelectedObject(aircraft.selectedSaObject);
				}
				
				currentView.draw();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	private ToggleButton createButton(ToggleGroup toggleGroup, AircraftMode aircraftMode, CanvasView canvasView, StackPane container)
    {
    	ToggleButton toggleButton = new ToggleButton(aircraftMode.name())
    	{
    		@Override
    		public void fire()
    		{
    			if (getToggleGroup() == null || !isSelected()) { super.fire();} // we don't toggle from selected to not selected if part of a group
    		}
    		
    	};
    	
    	toggleButton.setAlignment(Pos.CENTER);
    	toggleButton.setTextAlignment(TextAlignment.CENTER);
    	
    	toggleButton.setId("toggle-button");
    	toggleGroup.getToggles().add(toggleButton);
    	toggleButton.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) ->
    	{
    		if(newValue)
    		{
    			ObservableList<Node> children = container.getChildren();
    			for (Node node : children) 
    			{
					node.setVisible(node == canvasView);
				}
    			
    			currentView = canvasView;
    			if(currentView instanceof RadarCanvas)
    			{
    				guiSelected.reset();
    				rightPanelButton.setDisable(! (NcoManager.INSTANCE.mode != Mode.SLAVE && aircraft.selectedSaInputTarget != null));
    			}
    			else if(currentView instanceof SaCanvas)
    			{
    				guiSelected.reset();
    				rightPanelButton.setDisable(!(NcoManager.INSTANCE.mode != Mode.SLAVE && aircraft.selectedSaObject != null));
    			}
    			else if(currentView instanceof DualCanvas)
    			{
    				rightPanelButton.setDisable(true);
    			}
    			
    			
    			
    			
    			if(aircraft.isScenarioRunning.get())
    			{
    				aircraft.isRadarHasChange.set(true);
    				aircraft.isSaHasChange.set(true);
    				drawView();
    			}
    		}
		});
    	return toggleButton;
    }
	
	private Text createText(boolean isSelected)
	{
		Text result = new Text();
		Color fill = (isSelected) ? Color.WHITE : Color.DEEPSKYBLUE;
		result.setFill(fill);
		return result;
	}
}
