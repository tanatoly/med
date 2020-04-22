package com.rafael.med;

import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainView extends BorderPane
{
	private static final String STRING_SETTING 	= "הגדרות";
	private static final String STR_ENERGRNCY 	= "אדומים";
	private static final int MARGINS  			= 5;
	private static final int _110 				= 110;
	
	private final StackPane center;
	private final VBox right;
	private final Text title;
	
	public DetailsView detailsView;
	public SettingView settingView;
	public EmergencyView emergencyView;
	
	public CenterView currentView;
	
	public MainView(Text title, double screenWidth, double screenHeight)
	{
		this.title 				= title;
		
		title.setText(STR_ENERGRNCY);
		setBackground(Constants.BACKGOUND_10);
		
		center = new StackPane();
		BorderPane.setMargin(center, new Insets(MARGINS));
		center.setPadding(new Insets(2));
		center.setBackground(Constants.BACKGOUND_90);
		
		right = new VBox(20);
		BorderPane.setMargin(right, new Insets(MARGINS));
		right.setBackground(Constants.BACKGOUND_60);
		right.setAlignment(Pos.TOP_CENTER);
		
		right.setMaxWidth(_110);
		right.setMinWidth(_110);
		
		setRight(right);	
		setCenter(center);
	}
	
	
	public void buildView(MedData data)
	{
		
		addToCenter(emergencyView 	= new EmergencyView());
		addToCenter(detailsView 	= new DetailsView(data));
		addToCenter(settingView		= new SettingView());
		
		
		right.getChildren().add(ViewUtils.vspace(10));
		
		Button emergencyButton = ViewUtils.jfxbutton(STR_ENERGRNCY, FontAwesomeIcon.WARNING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		emergencyButton.setOnAction(e ->
		{
			showView(STR_ENERGRNCY, emergencyView, Color.RED);
		});
		
		right.getChildren().add(emergencyButton);		
		right.getChildren().add(ViewUtils.vspace());
		
		
		
		for (Department department : data.departments.values())
		{
			Button button = ViewUtils.jfxbutton(department.id, FontAwesomeIcon.BUILDING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
			right.getChildren().add(button);
			DepartmentView departmentView = new DepartmentView();
			department.view = departmentView;	
			addToCenter(departmentView);

			button.setOnAction(e ->
			{
				showView(department.id, departmentView, null);
			});
		}
			
		right.getChildren().add(ViewUtils.vspace());
		Button button = ViewUtils.jfxbutton(STRING_SETTING, FontAwesomeIcon.COG, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		button.setOnAction(e ->
		{
			showSettingsView();
		});
		
		right.getChildren().add(button);		
		right.getChildren().add(ViewUtils.vspace(10));
		
		showView(STR_ENERGRNCY, emergencyView, Color.RED);
	}
	
	private void addToCenter(CenterView centerView)
	{
		if (centerView instanceof ScrollPane)
		{
			ScrollPane scrollPane = (ScrollPane) centerView;
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
		}
		((Node)centerView).setVisible(false);
		center.getChildren().add((Node)centerView);
		
		((Node)centerView).visibleProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
		{
			if(newValue)
			{
				MedManager.INSTANCE.updateCenterView(0,true);
			}
			else
			{
				centerView.onToBack();
			}
		});
		
	}
	
	
	private void showView(String text, CenterView centerView, Color color)
	{
		currentView = centerView;
		if(color == null)
		{
			color = Color.WHITE;
		}
		title.setFill(color);
		title.setText(text);
		ObservableList<Node> children = center.getChildren();
		for (Node node : children)
		{
			node.setVisible(node == centerView);
		}
	}
	
	public void showDetailsView(String text)
	{
		showView(text, detailsView, Color.GREENYELLOW);
	}
	
	public void showSettingsView()
	{
		showView(STRING_SETTING, settingView, Constants.COLOR_95);
	}
}
