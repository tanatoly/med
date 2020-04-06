package com.rafael.med;

import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainView extends BorderPane
{
	
	private static final String ENERGRNCY = "אדומים";
	public final StackPane center;
	private Text title;
	
	public final EmergencyView emergencyView;
	
	public MainView(Text title)
	{
		this.title = title;
		title.setText(ENERGRNCY);
		setBackground(Constants.BACKGOUND_10);
		center = new StackPane();
		BorderPane.setMargin(center, new Insets(5));
		center.setBackground(Constants.BACKGOUND_90);
		emergencyView = new EmergencyView();
	}
	
	
	
	
	public void buildView(MedData data)
	{
		VBox pane = new VBox(40);
		BorderPane.setMargin(pane, new Insets(5));
		pane.setBackground(Constants.BACKGOUND_60);
		pane.setAlignment(Pos.TOP_CENTER);
		pane.getChildren().add(ViewUtils.vspace(10));
		
		Button buttonw = ViewUtils.jfxbutton(ENERGRNCY, FontAwesomeIcon.WARNING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		pane.getChildren().add(buttonw);		
		pane.getChildren().add(ViewUtils.vspace());
		center.getChildren().add(emergencyView);
		buttonw.setOnAction(e ->
		{
			title.setText(ENERGRNCY);
			ObservableList<Node> children = center.getChildren();
			for (Node node : children)
			{
				node.setVisible(node == emergencyView);
			}
		});
		
		
		for (Department department : data.departments.values())
		{
			if(department != null)
			{
				Button button = ViewUtils.jfxbutton(department.id, FontAwesomeIcon.BUILDING, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
				pane.getChildren().add(button);
				
				DepartmentView departmentView = new DepartmentView();
				departmentView.setVisible(false);
				center.getChildren().add(departmentView);
				department.view = departmentView;				
				button.setOnAction(e ->
				{
					title.setText(department.id);
					ObservableList<Node> children = center.getChildren();
					for (Node node : children)
					{
						node.setVisible(node == departmentView);
					}
				});
			}
		}
			
		pane.getChildren().add(ViewUtils.vspace());
		Button button = ViewUtils.jfxbutton("הגדרות", FontAwesomeIcon.COG, 70, 70, Constants.COLOR_20, Color.GHOSTWHITE, Color.AQUA, "",2);
		pane.getChildren().add(button);		
		pane.getChildren().add(ViewUtils.vspace(10));
		pane.setMaxWidth(110);
		pane.setMinWidth(110);
		
		setRight(pane);	
		setCenter(center);
	}
}
